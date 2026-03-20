package de.potionlabs.ffmlibrary

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSBuiltIns
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import de.potionlabs.ffmlibrary.annotations.*
import de.potionlabs.ffmlibrary.structure.*

/**
 * Klasse, die die KSP-Symbole "besucht" und die Informationen für die FFM-Objekte sammelt.
 * Symbole sind hierbei Klassen, Parameter, Typen, etc.
 * Es werden nur die relevanten Symbole für die FFM-Generierung untersucht, um die Performance zu verbessern.
 * Die gesammelten Informationen werden in den FFM-Objekten gespeichert, die später für die Code-Generierung verwendet werden.
 *
 * @param[logger] der KSP-Logger, um Informationen während des Besuchs zu protokollieren.
 * @param[options] die Konfiguration, die über die KSP-Optionen bereitgestellt wird.
 */
@OptIn(KspExperimental::class)
class FFMVisitor(
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : KSVisitor<FFMObject, Unit> {
    private val visited = HashSet<KSNode>()
    private val libraryClasses = mutableMapOf<String, MutableList<KSFile>>()
    private lateinit var builtIns: KSBuiltIns

    /**
     * Methode, die die bereits besuchten Symbole zurückgibt.
     *
     * @return[HashSet] die Menge der bereits besuchten Symbole.
     */
    fun getVisitedSymbols(): HashSet<KSNode> {
        return visited
    }

    /**
     * Methode, die prüft, ob das Symbol bereits besucht wurde.
     * Wurde das Symbol bisher noch nicht besucht, so wird es auf "besucht" gesetzt.
     *
     * @param[symbol] das zu prüfende Symbol.
     *
     * @return[Boolean] true, wenn das Symbol bereits besucht wurde, sonst false.
     */
    private fun checkIfVisited(symbol: KSNode): Boolean {
        return if (visited.contains(symbol)) {
            true
        } else {
            visited.add(symbol); false
        }
    }

    /**
     * Nicht genutzt.
     */
    override fun visitAnnotated(annotated: KSAnnotated, data: FFMObject) {
        if (checkIfVisited(annotated)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitAnnotation(annotation: KSAnnotation, data: FFMObject) {
        if (checkIfVisited(annotation)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitCallableReference(reference: KSCallableReference, data: FFMObject) {
        if (checkIfVisited(reference)) return
    }

    /**
     * Besucht eine Klassendeklaration, sammelt die Informationen über die Klasse und ihre Annotationen.
     *
     * @param[classDeclaration] die zu besuchende Klassendeklaration.
     * @param[data] das FFM-Objekt, in dem die gesammelten Informationen gespeichert werden sollen.
     */
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: FFMObject) {
        if (checkIfVisited(classDeclaration)) return

        classDeclaration.annotations.forEach {
            it.accept(this, data)
        }
        classDeclaration.declarations.forEach {
            it.accept(this, data)
        }
    }

    /**
     * Nicht genutzt.
     */
    override fun visitClassifierReference(reference: KSClassifierReference, data: FFMObject) {
        if (checkIfVisited(reference)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitDeclaration(declaration: KSDeclaration, data: FFMObject) {
        if (checkIfVisited(declaration)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitDeclarationContainer(declarationContainer: KSDeclarationContainer, data: FFMObject) {
        if (checkIfVisited(declarationContainer)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitDefNonNullReference(reference: KSDefNonNullReference, data: FFMObject) {
        if (checkIfVisited(reference)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitDynamicReference(reference: KSDynamicReference, data: FFMObject) {
        if (checkIfVisited(reference)) return
    }

    /**
     * Besucht eine Datei, sammelt die Informationen über die darin enthaltenen Klassen und deren Annotationen.
     * Je nach Art der Datei, bzw. deren Annotation wird ein [FFMInterface], [FFMStruct] oder [FFMTuple] erstellt und mit den gesammelten Informationen gefüllt.
     *
     * @param[file] die zu besuchende Datei.
     * @param[data] das FFM-Objekt, in dem die gesammelten Informationen gespeichert werden sollen.
     */
    override fun visitFile(file: KSFile, data: FFMObject) {
        if (data !is FFMLibrary) return
        if (checkIfVisited(file)) return

        val nativeClassAnnotation = file.declarations
            .filterIsInstance(KSClassDeclaration::class.java)
            .filter { it.isAnnotationPresent(NativeClass::class) }
            .flatMap { it.getAnnotationsByType(NativeClass::class) }
            .firstOrNull()

        val nativeStructAnnotation = file.declarations
            .filterIsInstance(KSClassDeclaration::class.java)
            .filter { it.isAnnotationPresent(NativeStruct::class) }
            .flatMap { it.getAnnotationsByType(NativeStruct::class) }
            .firstOrNull()

        val nativeTupleAnnotation = file.declarations
            .filterIsInstance(KSClassDeclaration::class.java)
            .filter { it.isAnnotationPresent(NativeTuple::class) }
            .flatMap { it.getAnnotationsByType(NativeTuple::class) }
            .firstOrNull()

        if(nativeClassAnnotation != null) {
            libraryClasses.putIfAbsent(nativeClassAnnotation.libraryName, mutableListOf())
            libraryClasses[nativeClassAnnotation.libraryName]!!.add(file)
            val className = nativeClassAnnotation.className
            val packageName = file.packageName.asString()

            val kInterface = FFMInterface(
                libraryName = nativeClassAnnotation.libraryName,
                fileName = file.fileName,
                className = className,
                packageName = packageName,
                path = nativeClassAnnotation.path,
                externalLinker = nativeClassAnnotation.externalLinker,
                containsLinker = nativeClassAnnotation.containsLinker,
                isCClass = nativeClassAnnotation.isCClass,
            )

            file.annotations.forEach {
                it.accept(this, kInterface)
            }

            file.declarations.forEach {
                it.accept(this, kInterface)
            }

            if (data.platform.isBlank()) {
                data.platform = nativeClassAnnotation.platform
            }
            data.interfaces.add(kInterface)
        } else if(nativeStructAnnotation != null) {
            libraryClasses.putIfAbsent(nativeStructAnnotation.libraryName, mutableListOf())
            libraryClasses[nativeStructAnnotation.libraryName]!!.add(file)

            val kStruct = FFMStruct(
                libraryName = nativeStructAnnotation.libraryName,
                fileName = file.fileName,
                className = file.fileName.substringBefore(".kt") + "Struct",
                packageName = file.packageName.asString(),
                callbackId = nativeStructAnnotation.id
            )

            file.declarations.forEach {
                it.accept(this, kStruct)
            }

            data.structs.add(kStruct)
        } else if(nativeTupleAnnotation != null) {
            val kTuple = FFMTuple(
                libraryName = nativeTupleAnnotation.libraryName,
                className = nativeTupleAnnotation.className,
                fileName = file.fileName.substringBefore(".kt"),
                packageName = file.packageName.asString()
            )

            data.tuples.add(kTuple)
        }
    }

    /**
     * Besucht eine Methodendeklaration, sammelt die Informationen über die Methode und ihre Annotationen, sowie Parameter.
     * Es werden nur abstrakte Methoden berücksichtigt, da nur diese für die FFM-Generierung relevant sind.
     * Für die Methode wird ein [FFMMethod] und für die Parameter jeweils ein [FFMParam] erstellt.
     *
     * @param[function] die zu besuchende Methodendeklaration.
     * @param[data] das FFM-Objekt, in dem die gesammelten Informationen gespeichert werden sollen.
     */
    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: FFMObject) {
        if (data !is FFMInterface) return
        if (checkIfVisited(function)) return

        // filtering default methods
        if (!function.isAbstract) return

        val nativeFunctionAnnotation = if (function.isAnnotationPresent(NativeFunction::class)) {
            function.getAnnotationsByType(NativeFunction::class).first()
        } else {
            null
        }

        val nativeFunctionReturnTypeAnnotation = function.annotations.toList()
            .filter { ksAnnotation ->
                ksAnnotation.annotationType.toString() == NativeFunctionReturnType::class.simpleName && ksAnnotation.arguments
                    .any { ksValueArgument -> ksValueArgument.name?.getShortName() == "type" }
            }.toList().firstOrNull()

        val methodName = function.simpleName.getShortName()
        val symbolName = nativeFunctionAnnotation?.name ?: function.simpleName.getShortName()

        val kMethod = FFMMethod(
            methodName = methodName,
            symbolName = symbolName,
            packageName = data.packageName,
            customReturnType = getKSType(nativeFunctionReturnTypeAnnotation?.arguments?.find { it.name?.getShortName() == "type" }?.value as KSClassDeclaration?),
            selfReferencing = nativeFunctionAnnotation?.selfReferencing ?: false,
            hasPointerParams = false
        )

        // Dirty Workaround because "Object::class"-constructs inside annotations are not available at ksp-runtime via "getAnnotationsByType"
        val nativeFunctionPointerParamAnnotations = function.annotations.toList()
            .filter { ksAnnotation ->
                ksAnnotation.annotationType.toString() == NativeFunctionPointerParam::class.simpleName && ksAnnotation.arguments
                    .any { ksValueArgument -> ksValueArgument.name?.getShortName() == "type" }
            }.toList()

        if (nativeFunctionPointerParamAnnotations.isNotEmpty()) {
            val pointerParams = nativeFunctionPointerParamAnnotations.map { ksAnnotation ->
                FFMParam(
                    paramName = ksAnnotation.arguments.find { it.name?.getShortName() == "name" }?.value.toString(),
                    paramType = getKSType(ksAnnotation.arguments.find { it.name?.getShortName() == "type" }?.value as KSClassDeclaration)!!,
                    priority = ksAnnotation.arguments.find { it.name?.getShortName() == "priority" }?.value.toString().toIntOrNull() ?: -1,
                    pointerValue = ksAnnotation.arguments.find { it.name?.getShortName() == "value" }?.value.toString(),
                    isPointer = true,
                    returnsStruct = false,
                    isSequence = false,
                    isCallbackId = false,
                    sequenceSizeOrSizeVariable = null,
                    stringSize = 0
                )
            }.toList()

            kMethod.hasPointerParams = true
            kMethod.parameters.addAll(pointerParams)
        }

        // Dirty Workaround because "Object::class"-constructs inside annotations are not available at ksp-runtime via "getAnnotationsByType"
        val nativeFunctionPointerStringParamAnnotations = function.annotations.toList()
            .filter { ksAnnotation -> ksAnnotation.annotationType.toString() == NativeFunctionPointerStringParam::class.simpleName && ksAnnotation.arguments
                .any { ksValueArgument -> ksValueArgument.name?.getShortName() == "type" }
            }.toList()

        if (nativeFunctionPointerStringParamAnnotations.isNotEmpty()) {
            val pointerParams = nativeFunctionPointerStringParamAnnotations.map { ksAnnotation ->
                FFMParam(
                    paramName = ksAnnotation.arguments.find { it.name?.getShortName() == "name" }?.value.toString(),
                    paramType = getKSType(ksAnnotation.arguments.find { it.name?.getShortName() == "type" }?.value as KSClassDeclaration)!!,
                    priority = ksAnnotation.arguments.find { it.name?.getShortName() == "priority" }?.value.toString().toIntOrNull() ?: -1,
                    pointerValue = "",
                    isPointer = true,
                    returnsStruct = false,
                    isSequence = false,
                    isCallbackId = false,
                    sequenceSizeOrSizeVariable = null,
                    stringSize = ksAnnotation.arguments.find { it.name?.getShortName() == "size" }?.value.toString().toIntOrNull() ?: -1,
                )
            }.toList()

            kMethod.hasPointerParams = true
            kMethod.parameters.addAll(pointerParams)
        }


        // Dirty Workaround because "Object::class"-constructs inside annotations are not available at ksp-runtime via "getAnnotationsByType"
        val nativeFunctionPointerStructParamAnnotations = function.annotations.toList()
            .filter { ksAnnotation ->
                ksAnnotation.annotationType.toString() == NativeFunctionPointerStructParam::class.simpleName && ksAnnotation.arguments
                    .any { ksValueArgument -> ksValueArgument.name?.getShortName() == "type" }
            }.toList()

        if (nativeFunctionPointerStructParamAnnotations.isNotEmpty()) {
            val pointerStructParams = nativeFunctionPointerStructParamAnnotations.map { ksAnnotation ->
                FFMParam(
                    paramName = ksAnnotation.arguments.find { it.name?.getShortName() == "name" }?.value.toString(),
                    paramType = ksAnnotation.arguments.find { it.name?.getShortName() == "type" }?.value as KSType,
                    priority = ksAnnotation.arguments.find { it.name?.getShortName() == "priority" }?.value.toString().toIntOrNull() ?: -1,
                    pointerValue = null,
                    isPointer = true,
                    returnsStruct = true,
                    isSequence = ksAnnotation.arguments.find { it.name?.getShortName() == "isSequence" }?.value.toString() == "true",
                    isCallbackId = false,
                    sequenceSizeOrSizeVariable = ksAnnotation.arguments.find { it.name?.getShortName() == "sequenceSize" }?.value.toString(),
                    stringSize = 0
                )
            }.toList()

            kMethod.hasPointerParams = true
            kMethod.parameters.addAll(pointerStructParams)
        }

        function.declarations.forEach {
            it.accept(this, kMethod)
        }
        function.parameters.forEach {
            it.accept(this, kMethod)
        }
        function.returnType?.accept(this, kMethod)

        data.methods.add(kMethod)
    }

    /**
     * Nicht genutzt.
     */
    override fun visitModifierListOwner(modifierListOwner: KSModifierListOwner, data: FFMObject) {
        if (checkIfVisited(modifierListOwner)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitNode(node: KSNode, data: FFMObject) {
        if (checkIfVisited(node)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitParenthesizedReference(reference: KSParenthesizedReference, data: FFMObject) {
        if (checkIfVisited(reference)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitPropertyAccessor(accessor: KSPropertyAccessor, data: FFMObject) {
        if (checkIfVisited(accessor)) return
    }

    /**
     * Besucht eine Attributdeklaration, sammelt die Informationen über das Attribut und seine Annotationen, sowie den Typ des Attributs.
     *
     * @param[property] die zu besuchende Attributdeklaration.
     * @param[data] das FFM-Objekt, in dem die gesammelten Informationen gespeichert werden sollen.
     */
    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: FFMObject) {
        if (checkIfVisited(property)) return

        if (data is FFMStruct) {
            val nativeStructStringSizeAnnotation = if(property.isAnnotationPresent(NativeStructStringSize::class)) {
                property.getAnnotationsByType(NativeStructStringSize::class).first()
            } else {
                null
            }
            data.fields.add(FFMStructField(property.toString(), property.type.resolve(), nativeStructStringSizeAnnotation?.size))
        }
    }

    /**
     * Nicht genutzt.
     */
    override fun visitPropertyGetter(getter: KSPropertyGetter, data: FFMObject) {
        if (checkIfVisited(getter)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitPropertySetter(setter: KSPropertySetter, data: FFMObject) {
        if (checkIfVisited(setter)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitReferenceElement(element: KSReferenceElement, data: FFMObject) {
        if (checkIfVisited(element)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitTypeAlias(typeAlias: KSTypeAlias, data: FFMObject) {
        if (checkIfVisited(typeAlias)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitTypeArgument(typeArgument: KSTypeArgument, data: FFMObject) {
        if (checkIfVisited(typeArgument)) return
    }

    /**
     * Nicht genutzt.
     */
    override fun visitTypeParameter(typeParameter: KSTypeParameter, data: FFMObject) {
        if (checkIfVisited(typeParameter)) return
    }

    /**
     * Besucht einen Typenverweis, sammelt die Informationen über den Rückgabetyp einer Methode.
     * 
     * @param[typeReference] der zu besuchende Typenverweis.
     * @param[data] das FFM-Objekt, in dem die gesammelten Informationen gespeichert werden sollen.
     */
    override fun visitTypeReference(typeReference: KSTypeReference, data: FFMObject) {
        if (data !is FFMMethod) return
        if (checkIfVisited(typeReference)) return

        data.returnType = typeReference.resolve()
    }

    /**
     * Nicht genutzt.
     */
    override fun visitValueArgument(valueArgument: KSValueArgument, data: FFMObject) {
        if (checkIfVisited(valueArgument)) return
    }

    /**
     * Besucht eine Parameterdeklaration, sammelt die Informationen über den Parameter und seine Annotationen, sowie den Typ des Parameters.
     * Erzeugt ein [FFMParam] und fügt es der Liste der Parameter der Methode hinzu.
     *
     * @param[valueParameter] die zu besuchende Parameterdeklaration.
     * @param[data] das FFM-Objekt, in dem die gesammelten Informationen gespeichert werden sollen.
     */
    override fun visitValueParameter(valueParameter: KSValueParameter, data: FFMObject) {
        if (data !is FFMMethod) return
        if (checkIfVisited(valueParameter)) return

        val nativeFunctionParamAnnotation = if (valueParameter.isAnnotationPresent(NativeFunctionParam::class)) {
            valueParameter.getAnnotationsByType(NativeFunctionParam::class).first()
        } else {
            null
        }

        val kParam = FFMParam(
            paramName = valueParameter.name?.getShortName() ?: valueParameter.toString(),
            paramType = valueParameter.type.resolve(),
            priority = nativeFunctionParamAnnotation?.priority ?: -1,
            pointerValue = null,
            isPointer = false,
            returnsStruct = false,
            isSequence = false,
            isCallbackId = nativeFunctionParamAnnotation?.isCallbackId ?: false,
            sequenceSizeOrSizeVariable = null,
            stringSize = 0
        )

        data.parameters.add(kParam)
    }

    /**
     * Hilfsmethode um den [KSType] eines Datentyps ausgehend vom Wert des in der Annotation genutzten [de.potionlabs.ffmlibrary.utils.Datatype]-Eintrags zu erhalten
     *
     * @param[enumValue] die Klassendeklaration des Datentyps in der Annotation.
     *
     * @return[KSType] der entsprechende KSType oder null, wenn der Typ nicht erkannt wurde.
     */
    private fun getKSType(enumValue: KSClassDeclaration?): KSType? {
        if(enumValue == null) return null

        return when(enumValue.simpleName.getShortName().lowercase()) {
            "byte" -> KSBuiltIns::byteType.get(builtIns)
            "char" -> KSBuiltIns::charType.get(builtIns)
            "short" -> KSBuiltIns::shortType.get(builtIns)
            "integer" -> KSBuiltIns::intType.get(builtIns)
            "long" -> KSBuiltIns::longType.get(builtIns)
            "float" -> KSBuiltIns::floatType.get(builtIns)
            "double" -> KSBuiltIns::doubleType.get(builtIns)
            "boolean" -> KSBuiltIns::booleanType.get(builtIns)
            "string" -> KSBuiltIns::stringType.get(builtIns)
            else -> null
        }
    }

    /**
     * Hilfsmethode um die [KSBuiltIns] zu setzen, da diese nicht direkt im Konstruktor des Visitors verfügbar sind, aber für die Umwandlung der Datentypen benötigt werden.
     */
    fun setBuiltIns(builtIns: KSBuiltIns) {
        this.builtIns = builtIns
    }
}