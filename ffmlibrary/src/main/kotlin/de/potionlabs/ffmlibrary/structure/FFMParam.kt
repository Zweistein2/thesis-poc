package de.potionlabs.ffmlibrary.structure

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import de.potionlabs.ffmlibrary.FFMProcessor.Companion.getTypeMapping

/**
 * Diese Klasse repräsentiert einen mit [de.potionlabs.ffmlibrary.annotations.NativeFunctionParam], [de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerParam], [de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerStringParam], oder [de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerStructParam] annotierten Parameter einer nativen Funktion.
 *
 * @param[paramName] Der Name des Parameters.
 * @param[paramType] Der Typ des Parameters.
 * @param[priority] Die Priorität des Parameters, welche angibt, in welcher Reihenfolge die Parameter an die native Funktion übergeben werden sollen.
 * @param[pointerValue] Der Wert, welcher in den Speicher geschrieben werden soll, auf welchen der Parameter zeigt, falls es sich um einen Zeiger-Parameter handelt.
 * @param[isPointer] Gibt an, ob es sich bei dem Parameter um einen Zeiger handelt.
 * @param[returnsStruct] Gibt an, ob der Parameter ein [de.potionlabs.ffmlibrary.utils.Struct] zurückgibt, welches aus dem Speicher gelesen werden muss, auf welchen der Parameter zeigt.
 * @param[isSequence] Gibt an, ob es sich bei dem Zeiger-Parameter um einen Zeiger auf eine Liste handelt.
 * @param[isCallbackId] Gibt an, ob es sich bei dem Parameter um die ID des Callback-[de.potionlabs.ffmlibrary.utils.Struct]s handelt, welcher von der nativen Funktion zurückgegeben werden soll.
 * @param[sequenceSizeOrSizeVariable] Gibt die Anzahl der Elemente der Liste an.
 * @param[stringSize] Gibt die Länge der Zeichenkette an, wenn der Parameter vom Typ [String] ist.
 */
data class FFMParam(
    val paramName: String,
    val paramType: KSType,
    val priority: Int,
    val pointerValue: String?,
    val isPointer: Boolean,
    val returnsStruct: Boolean,
    val isSequence: Boolean,
    val isCallbackId: Boolean,
    val sequenceSizeOrSizeVariable: String?,
    val stringSize: Int
): FFMObject {
    /**
     * Methode um aus dem FFMParam ein [ParameterSpec] zu generieren, welches den Parameter der nativen Funktion darstellt.
     *
     * @return[ParameterSpec] Das generierte [ParameterSpec], welches den Parameter darstellt.
     */
    fun toParameterSpec(): ParameterSpec {
        return ParameterSpec.builder(paramName, paramType.toTypeName()).build()
    }

    /**
     * Hilfsmethode, die von der [FFMMethod] aufgerufen wird, zu der der Parameter gehört.
     * Generiert den Binding-Code für die Speicherzuweisung des Parameters.
     *
     * @param[callbackIdParam] Der Parameter, welcher die ID des Callback-[de.potionlabs.ffmlibrary.utils.Struct]s enthält, sollte der Parameter ein Zeiger auf ein solches [de.potionlabs.ffmlibrary.utils.Struct] sein.
     *
     * @return[CodeBlock] Der generierte Binding-Code für die Speicherzuweisung des Parameters.
     */
    fun toVariableAllocationCodeBlock(callbackIdParam: FFMParam?): CodeBlock {
        val codeBlock = CodeBlock.builder()

        if(paramType.toString() == "String") {
            if(isPointer) {
                if(pointerValue.isNullOrBlank()) {
                    codeBlock.addStatement("val ${paramName}Memory = it.allocate(AddressLayout.ADDRESS, 1024)")
                } else {
                    codeBlock.addStatement("val ${paramName}Memory = it.allocateFrom(${pointerValue})")
                }
            } else {
                codeBlock.addStatement("val ${paramName}Memory = it.allocateFrom(${paramName})")
            }
        } else if(isPointer && returnsStruct) {
            val nameSuffix = if(isSequence) "Array" else ""

            if(!isSequence) {
                if(paramType.toString() == "Struct" && callbackIdParam != null) {
                    codeBlock.addStatement("val $paramName = it.allocate(CallbackStructFactory.getMemoryLayoutForId(${callbackIdParam.paramName}).byteSize(), CallbackStructFactory.getMemoryLayoutForId(${callbackIdParam.paramName}).byteAlignment())")
                } else {
                    codeBlock.addStatement("val $paramName = it.allocate(${paramType.toString().replaceFirstChar { char -> char.uppercase() }}Struct.LAYOUT.byteSize(), ${paramType.toString().replaceFirstChar { char -> char.uppercase() }}Struct.LAYOUT.byteAlignment())")
                }
            } else {
                if(sequenceSizeOrSizeVariable.isNullOrBlank() || (sequenceSizeOrSizeVariable.toIntOrNull() != null && sequenceSizeOrSizeVariable.toInt() == -1 )) {
                    codeBlock.addStatement("val ${paramName}${nameSuffix} = MemorySegment.NULL")
                } else if(sequenceSizeOrSizeVariable.toIntOrNull() != null) {
                    codeBlock.addStatement("val ${paramName}${nameSuffix} = it.allocate(MemoryLayout.sequenceLayout(${sequenceSizeOrSizeVariable}, ${paramType.toString().replaceFirstChar { char -> char.uppercase() }}Struct.LAYOUT))")
                } else {
                    codeBlock.addStatement("val ${paramName}${nameSuffix} = it.allocate(MemoryLayout.sequenceLayout(${sequenceSizeOrSizeVariable}.toLong(), ${paramType.toString().replaceFirstChar { char -> char.uppercase() }}Struct.LAYOUT))")
                }
            }
        } else if(isPointer) {
            val nameSuffix = if(isSequence) "Array" else ""

            if(!isSequence) {
                codeBlock.addStatement("val $paramName = it.allocate(ValueLayout.${getTypeMapping(paramType)}.byteSize(), ValueLayout.${getTypeMapping(paramType)}.byteAlignment())")

                if(!pointerValue.isNullOrBlank()) {
                    codeBlock.addStatement("${paramName}.set(ValueLayout.${getTypeMapping(paramType)}, 0, ${pointerValue})")
                }
            } else {
                if(sequenceSizeOrSizeVariable.isNullOrBlank() || (sequenceSizeOrSizeVariable.toIntOrNull() != null && sequenceSizeOrSizeVariable.toInt() == -1 )) {
                    codeBlock.addStatement("val ${paramName}${nameSuffix} = MemorySegment.NULL")
                } else if(sequenceSizeOrSizeVariable.toIntOrNull() != null) {
                    codeBlock.addStatement("val ${paramName}${nameSuffix} = it.allocate(MemoryLayout.sequenceLayout(${sequenceSizeOrSizeVariable}, ValueLayout.${getTypeMapping(paramType)}))")
                } else {
                    codeBlock.addStatement("val ${paramName}${nameSuffix} = it.allocate(MemoryLayout.sequenceLayout(${sequenceSizeOrSizeVariable}.toLong(), ValueLayout.${getTypeMapping(paramType)}))")
                }
            }
        }

        return codeBlock.build()
    }

    /**
     * Hilfsmethode, die von der [FFMMethod] aufgerufen wird, zu der der Zeiger-Parameter gehört.
     * Generiert den Binding-Code für die Auswertung des Zeiger-Parameters nach dem Aufruf der nativen Funktion.
     *
     * @param[callbackIdParam] Der Parameter, welcher die ID des Callback-[de.potionlabs.ffmlibrary.utils.Struct]s enthält, sollte der Parameter ein Zeiger auf ein solches [de.potionlabs.ffmlibrary.utils.Struct] sein.
     *
     * @return[CodeBlock] Der generierte Binding-Code für die Auswertung des Zeiger-Parameters.
     */
    fun toVariableInitialisationCodeBlock(callbackIdParam: FFMParam?): CodeBlock {
        val codeBlock = CodeBlock.builder()

        if(returnsStruct) {
            if(!isSequence) {
                if(paramType.toString() == "String") {
                    codeBlock.addStatement("val ${paramName}Value = MemorySegment.ofAddress(${paramName}Memory.address()).reinterpret(${stringSize}).getString(0)")
                } else if(paramType.toString() == "Struct" && callbackIdParam != null) {
                    codeBlock.addStatement("val ${paramName}Value = CallbackStructFactory.getStructForId(${callbackIdParam.paramName}, ${paramName}).mapToKotlinStruct()")
                } else {
                    codeBlock.addStatement("val ${paramName}Value = ${paramType.toString().replaceFirstChar { char -> char.uppercaseChar() }}Struct(${paramName}).mapToKotlinStruct()")
                }
            } else {
                if(sequenceSizeOrSizeVariable.isNullOrBlank() || (sequenceSizeOrSizeVariable.toIntOrNull() != null && sequenceSizeOrSizeVariable.toInt() == -1 )) {
                    codeBlock.addStatement("val ${paramType.toString().replaceFirstChar { char -> char.lowercaseChar() }}List = mutableListOf<${paramType.toString().replaceFirstChar { char -> char.uppercaseChar() }}>()")
                } else {
                    codeBlock.addStatement("val ${paramType.toString().replaceFirstChar { char -> char.lowercaseChar() }}List = ${paramName}Array.elements(${paramType.toString().replaceFirstChar { char -> char.uppercaseChar() }}Struct.LAYOUT).map { ${paramType.toString().replaceFirstChar { char -> char.uppercaseChar() }}Struct(it).mapToKotlinStruct() }.toList()")
                }
            }
        } else {
            if(!isSequence) {
                if(paramType.toString() == "String") {
                    codeBlock.addStatement("val ${paramName}Value = MemorySegment.ofAddress(${paramName}Memory.address()).reinterpret(${stringSize}).getString(0)")
                } else {
                    codeBlock.addStatement("val ${paramName}Value = ${paramName}.get(ValueLayout.${getTypeMapping(paramType)}, 0)")
                }
            } else {
                if(sequenceSizeOrSizeVariable.isNullOrBlank() || (sequenceSizeOrSizeVariable.toIntOrNull() != null && sequenceSizeOrSizeVariable.toInt() == -1 )) {
                    codeBlock.addStatement("val ${paramType.toString().replaceFirstChar { char -> char.lowercaseChar() }}List = mutableListOf<$paramType>()")
                } else {
                    val sequenceSize = if(sequenceSizeOrSizeVariable.toIntOrNull() != null) sequenceSizeOrSizeVariable else "${sequenceSizeOrSizeVariable}.toLong()"

                    codeBlock.addStatement("val ${paramType.toString().replaceFirstChar { char -> char.lowercaseChar() }}List = mutableListOf<$paramType>()")
                    codeBlock.beginControlFlow("(0..<${sequenceSize}).forEach {")
                    codeBlock.addStatement("val ${paramName}ArrayValue = ${paramName}Array.get(ValueLayout.${getTypeMapping(paramType)}, ValueLayout.${getTypeMapping(paramType)}.byteSize() * it)")
                    codeBlock.addStatement("${paramType.toString().replaceFirstChar { char -> char.lowercaseChar() }}List.add(${paramName}ArrayValue)")
                    codeBlock.endControlFlow()
                }
            }
        }

        return codeBlock.build()
    }
}

