package de.potionlabs.ffmlibrary.structure

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.toTypeName
import de.potionlabs.ffmlibrary.FFMProcessor.Companion.getTypeMapping

/**
 * Diese Klasse repräsentiert eine mit [de.potionlabs.ffmlibrary.annotations.NativeFunction] annotierte Funktion der nativen Bibliothek.
 *
 * @param[methodName] Der Name der Methode, welche die Funktion repräsentiert.
 * @param[symbolName] Der Name der nativen Funktion.
 * @param[packageName] Der Name des Packages, in welchem die Methode generiert wird.
 * @param[selfReferencing] Gibt an, ob die Methode eine Referenz auf die Adresse der eigenen Klasse enthält.
 * @param[customReturnType] Der Rückgabetyp der Methode, falls dieser aufgrund der Nutzung eines [de.potionlabs.ffmlibrary.utils.ReturnTuple]s nicht von der Methodensignatur abgeleitet werden kann.
 * @param[returnType] Der Rückgabetyp der Methode, welcher aus der Methodensignatur abgeleitet wird.
 * @param[parameters] Eine Liste der Parameter, welche die Methode enthält.
 * @param[hasPointerParams] Gibt an, ob die Methode Zeiger-Parameter enthält, welche speziell behandelt werden müssen.
 */
data class FFMMethod(
    val methodName: String,
    val symbolName: String,
    val packageName: String,
    val selfReferencing: Boolean,
    val customReturnType: KSType?,
    var returnType: KSType? = null,
    val parameters: MutableList<FFMParam> = mutableListOf(),
    var hasPointerParams: Boolean,
): FFMObject {
    /**
     * Methode um aus der FFMMethod ein [FunSpec] zu generieren, welches die native Funktion darstellt.
     * Dabei werden alle Parameter der Funktion berücksichtigt.
     *
     * @return[FunSpec] Das generierte [FunSpec], welches die native Funktion darstellt.
     */
    fun toFunSpec(): FunSpec {
        val function = FunSpec.builder(methodName)

        function.addModifiers(KModifier.OVERRIDE)
        function.addParameters(parameters.filter { !it.isPointer }.map { it.toParameterSpec() })
        if(hasPointerParams) {
            function.returns(ClassName.bestGuess("$packageName.${methodName.replaceFirstChar { it.uppercase() }}ReturnTuple"))
        } else if(returnType != null && returnType.toString() != "Unit") {
            function.returns(returnType!!.toTypeName())
        } else {
            function.returns(UNIT)
        }

        val returnText = if(customReturnType != null) "ValueLayout.${getTypeMapping(customReturnType)}"
            else if(returnType != null && returnType.toString() != "Unit") "ValueLayout.${getTypeMapping(returnType)}"
            else ""
        val selfReferencingText = if(selfReferencing) "ValueLayout.ADDRESS" else ""
        val parametersText = if(parameters.isNotEmpty()) parameters.sortedBy { it.priority }.filter { it.priority >= 0 }.map { "ValueLayout.${getTypeMapping(it.paramType, it.isPointer)}" }.reduceRightOrNull { s, acc -> "$s, $acc" } ?: "" else ""
        val nativeParameterNames = mutableListOf<String>()
        val convertParameterNames = mutableListOf<String>()
        parameters.filter { it.isPointer }.forEach {
            if(!it.isSequence) {
                convertParameterNames.add("${it.paramName}Value")
            } else {
                convertParameterNames.add("${it.paramType.toString().replaceFirstChar { char -> char.lowercaseChar() }}List")
            }
        }
        parameters.sortedBy { it.priority }.forEach {
            if(it.paramType.toString() == "String") {
                nativeParameterNames.add("${it.paramName}Memory")
            } else if(it.isPointer) {
                val nameSuffix = if(it.isSequence) "Array" else ""
                nativeParameterNames.add("${it.paramName}$nameSuffix")
            } else {
                if(it.priority != -1) {
                    nativeParameterNames.add(it.paramName)
                }
            }
        }

        function.addStatement("val function = FunctionDescriptor.of%1L(%2L)",
            if((!customReturnType.isEmpty() && customReturnType.toString() != "Unit") || (!returnType.isEmpty() && returnType.toString() != "Unit")) "" else "Void",
            listOf(returnText, selfReferencingText, parametersText).filter { it.isNotBlank() }.reduceRightOrNull { s, acc -> "$s, $acc" } ?: "")
        function.addStatement("val symbolName = %1S", symbolName)
        function.addStatement("val method = loaderLookup.find(symbolName).or { stdLibLookup.find(symbolName) }.map { symbolSegment -> nativeLinker.downcallHandle(symbolSegment, function) }.orElseThrow()")

        val callbackIdParam = parameters.firstOrNull { it.isCallbackId }

        function.addCode(CodeBlock.builder()
            .beginControlFlow("Arena.ofConfined().use")
            .add(parameters.sortedBy { it.priority }.map{ it.toVariableAllocationCodeBlock(callbackIdParam) }.filter { it.isNotEmpty() }.joinToCode(""))
            .add(toBodyCodeBlock(nativeParameterNames))
            .add(parameters.sortedBy { it.priority }.filter { it.isPointer }.joinToCode("") { it.toVariableInitialisationCodeBlock(callbackIdParam) })
            .addStatement(if(hasPointerParams) "return ${methodName.replaceFirstChar { it.uppercase() }}ReturnTuple(result, ${convertParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""})" else "")
            .endControlFlow()
            .build()
        )

        return function.build()
    }

    /**
     * Methode um den Binding-Code für den Aufruf der nativen Funktion und der Rückgabe des Aufruf-Ergebnisses zu generieren, basierend auf den Parametern der Methode und deren Rückgabetyp.
     *
     * @param[nativeParameterNames] Eine Liste der Parameter, welche an die native Funktion übergeben werden.
     *
     * @return[CodeBlock] Der generierte Binding-Code für den Aufruf der nativen Funktion und der Rückgabe des Aufruf-Ergebnisses.
     */
    private fun toBodyCodeBlock(nativeParameterNames: MutableList<String>): CodeBlock {
        val codeBlock = CodeBlock.builder()

        if(hasPointerParams) {
            if(selfReferencing) {
                if (returnType.toString() == "String") {
                    if(nativeParameterNames.isNotEmpty()) {
                        codeBlock.addStatement("val result = MemorySegment.ofAddress((method.invoke(address, ${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as MemorySegment).address(), 256).getString(0)")
                    } else {
                        codeBlock.addStatement("val result = MemorySegment.ofAddress((method.invoke(address) as MemorySegment).reinterpret(256).address()).getString(0)")
                    }
                } else {
                    if(nativeParameterNames.isNotEmpty()) {
                        if (!customReturnType.isEmpty()) {
                            codeBlock.addStatement("val result = method.invoke(address, ${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as $customReturnType")
                        } else if (returnType.toString().endsWith("Spec")) {
                            codeBlock.addStatement("val result = ${returnType.toString().substringBefore("Spec")}(method.invoke(address, ${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as MemorySegment)")
                        } else {
                            codeBlock.addStatement("val result = method.invoke(address, ${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as $returnType")
                        }
                    } else {
                        if (!customReturnType.isEmpty()) {
                            codeBlock.addStatement("val result = method.invoke(address) as $customReturnType")
                        } else if (returnType.toString().endsWith("Spec")) {
                            codeBlock.addStatement("val result = ${returnType.toString().substringBefore("Spec")}(method.invoke(address) as MemorySegment)")
                        } else {
                            codeBlock.addStatement("val result = method.invoke(address) as $returnType")
                        }
                    }
                }
            } else {
                if (returnType.toString() == "String") {
                    if(nativeParameterNames.isNotEmpty()) {
                        codeBlock.addStatement("val result = MemorySegment.ofAddress((method.invoke(${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as MemorySegment).address()).reinterpret(256).getString(0)")
                    } else {
                        codeBlock.addStatement("val result = MemorySegment.ofAddress((method.invoke() as MemorySegment).address()).reinterpret(256).getString(0)")
                    }
                } else {
                    if (!customReturnType.isEmpty()) {
                        codeBlock.addStatement("val result = method.invoke(${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as $customReturnType")
                    } else if (returnType.toString().endsWith("Spec")) {
                        codeBlock.addStatement("val result = ${returnType.toString().substringBefore("Spec")}(method.invoke(${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as MemorySegment)")
                    } else {
                        codeBlock.addStatement("val result = method.invoke(${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as $returnType")
                    }
                }
            }
        } else {
            if(selfReferencing) {
                if (returnType.toString() == "String") {
                    if(nativeParameterNames.isNotEmpty()) {
                        codeBlock.addStatement("return MemorySegment.ofAddress((method.invoke(address, ${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as MemorySegment).address(), 256).getString(0)")
                    } else {
                        codeBlock.addStatement("return (method.invoke(address) as MemorySegment).reinterpret(256).getString(0)")
                    }
                } else {
                    if(nativeParameterNames.isNotEmpty()) {
                        if (!customReturnType.isEmpty()) {
                            codeBlock.addStatement("return method.invoke(address, ${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as $customReturnType")
                        } else if (returnType.toString().endsWith("Spec")) {
                            codeBlock.addStatement("return ${returnType.toString().substringBefore("Spec")}(method.invoke(address, ${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as MemorySegment)")
                        } else {
                            codeBlock.addStatement("return method.invoke(address, ${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as $returnType")
                        }
                    } else {
                        if (!customReturnType.isEmpty()) {
                            codeBlock.addStatement("return method.invoke(address) as $customReturnType")
                        } else if (returnType.toString().endsWith("Spec")) {
                            codeBlock.addStatement("return ${returnType.toString().substringBefore("Spec")}(method.invoke(address) as MemorySegment)")
                        } else {
                            codeBlock.addStatement("return method.invoke(address) as $returnType")
                        }
                    }
                }
            } else {
                if (returnType.toString() == "String") {
                    if(nativeParameterNames.isNotEmpty()) {
                        codeBlock.addStatement("return MemorySegment.ofAddress((method.invoke(${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as MemorySegment).address()).reinterpret(256).getString(0)")
                    } else {
                        codeBlock.addStatement("return MemorySegment.ofAddress((method.invoke() as MemorySegment).address()).reinterpret(256).getString(0)")
                    }
                } else {
                    if (!customReturnType.isEmpty()) {
                        codeBlock.addStatement("return method.invoke(${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as $customReturnType")
                    } else if (returnType.toString().endsWith("Spec")) {
                        codeBlock.addStatement("return ${returnType.toString().substringBefore("Spec")}(method.invoke(${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as MemorySegment)")
                    } else {
                        codeBlock.addStatement("return method.invoke(${nativeParameterNames.reduceRightOrNull { s, acc -> "$s, $acc" } ?: ""}) as $returnType")
                    }
                }
            }
        }

        return codeBlock.build()
    }
}

/*
 * -------------------------- Extension Functions --------------------------
 */

/**
 * Erweiterungs-Methode um zu überprüfen, ob ein [KSType] leer ist, also entweder null oder "null" als String darstellt.
 *
 * @return[Boolean] true, wenn der [KSType] leer ist, sonst false.
 */
private fun KSType?.isEmpty(): Boolean {
    return this == null || this.toString() == "null"
}
