package de.potionlabs.ffmlibrary

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.writeTo
import de.potionlabs.ffmlibrary.annotations.NativeClass
import de.potionlabs.ffmlibrary.annotations.NativeStruct
import de.potionlabs.ffmlibrary.annotations.NativeTuple
import de.potionlabs.ffmlibrary.structure.FFMLibrary
import java.lang.foreign.ValueLayout
import kotlin.reflect.KClass
import kotlin.to

/**
 * Klasse die den KSP-Prozessor für die FFM-Bibliothek implementiert.
 * Sie verarbeitet die mit den Annotationen versehenen Klassen, Strukturen und Tupel und generiert die entsprechenden Dateien für die FFM-Bibliothek.
 * Der Prozessor verwendet den [FFMVisitor], um die zu untersuchenden Dateien zu "besuchen" und die Informationen zu sammeln, die für die Generierung der Binding-Dateien benötigt werden.
 * Am Ende werden die generierten Dateien in das Zielverzeichnis geschrieben.
 *
 * @param[codeGenerator] Der CodeGenerator, der zum Schreiben der generierten Dateien verwendet wird.
 * @param[logger] Der KSPLogger, der zum Protokollieren von Informationen und Fehlern während der Verarbeitung verwendet wird.
 * @param[options] Die Konfiguration, die während der Verarbeitung verwendet wird.
 */
class FFMProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {
    private val visitor = FFMVisitor(logger, options)

    companion object {
        private val typeMapping = mapOf(
            "Byte" to (ValueLayout.JAVA_BYTE to "JAVA_BYTE"),
            "Boolean" to (ValueLayout.JAVA_BOOLEAN to "JAVA_BOOLEAN"),
            "Char" to (ValueLayout.JAVA_CHAR to "JAVA_CHAR"),
            "Short" to (ValueLayout.JAVA_SHORT to "JAVA_SHORT"),
            "Int" to (ValueLayout.JAVA_INT to "JAVA_INT"),
            "Long" to (ValueLayout.JAVA_LONG to "JAVA_LONG"),
            "Float" to (ValueLayout.JAVA_FLOAT to "JAVA_FLOAT"),
            "Double" to (ValueLayout.JAVA_DOUBLE to "JAVA_DOUBLE"),
            "String" to (ValueLayout.ADDRESS to "ADDRESS"),
        )

        /**
         * Hilfsmethode die für ein [KSType] den entsprechenden FFM-Typen als String zurückgibt.
         * Wenn der Typ nicht in der [typeMapping] enthalten ist oder ein Zeiger ist, wird "ADDRESS" zurückgegeben.
         *
         * @param[type] Der KSType, für den der FFM-Typ ermittelt werden soll.
         * @param[isPointer] Gibt an, ob der Typ ein Zeiger ist.
         *
         * @return[String] Der entsprechende FFM-Typ als String.
         */
        fun getTypeMapping(type: KSType?, isPointer: Boolean = false): String {
            if(type == null || typeMapping[type.toString()] == null || isPointer) {
                return "ADDRESS"
            }

            return typeMapping[type.toString()]!!.second
        }

        /**
         * Hilfsmethode die für ein [KSType] die entsprechende Byte-Ausrichtung zurückgibt.
         * Wenn der Typ nicht in der [typeMapping] enthalten ist, wird die Byte-Ausrichtung von "ADDRESS" zurückgegeben.
         *
         * @param[type] Der KSType, für den die Byte-Ausrichtung ermittelt werden soll.
         *
         * @return[Long] Die Byte-Ausrichtung des Typs.
         */
        fun getTypeAlignment(type: KSType): Long {
            return typeMapping[type.toString()]?.first?.byteAlignment() ?: ValueLayout.ADDRESS.byteAlignment()
        }
    }

    /**
     * Methode, die vom KSP-Plugin aufgerufen wird, um die Verarbeitung der Symbole zu starten.
     * Sie ermittelt die zu untersuchenden Dateien anhand der gesetzten Annotationen und übergibt diese an den [FFMVisitor], um die Informationen zu sammeln, die für die Generierung der Binding-Dateien benötigt werden.
     * Stößt am Ende für die [FFMLibrary] die Generierung der Dateien an und schreibt diese in das Zielverzeichnis.
     *
     * @param[resolver] Der Resolver, der zum Ermitteln der Symbole verwendet wird.
     *
     * @return[List] Eine Liste von [KSAnnotated], die angibt, welche Symbole erneut verarbeitet werden müssen. In diesem Fall wird immer eine leere Liste zurückgegeben, da alle Symbole in einem Durchgang verarbeitet werden.
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        visitor.setBuiltIns(resolver.builtIns)

        val library = FFMLibrary("")

        val interfaces = resolver.getSymbolsWithAnnotation(NativeClass::class.qualifiedName.orEmpty()).toList()
        val structs = resolver.getSymbolsWithAnnotation(NativeStruct::class.qualifiedName.orEmpty()).toList()
        val tuples = resolver.getSymbolsWithAnnotation(NativeTuple::class.qualifiedName.orEmpty()).toList()

        val symbols = listOf(interfaces, structs, tuples).flatten().distinct().asSequence()

        if (symbols.count() == 0) {
            logger.info("No more symbols to process, ending")
            return emptyList()
        } else {
            logger.info("Processing ${symbols.count()} symbols")
            symbols.forEach {
                visitor.visitFile(it.containingFile!!, library)

                logger.logging("Visitor visited ${visitor.getVisitedSymbols().size} symbols")
            }
        }

        if (library.platform.isBlank()) {
            logger.error("No platform specified, aborting")
            return emptyList()
        }

        val files = library.toFiles()

        files.forEach {
            it.writeTo(codeGenerator, false)
        }

        return emptyList()
    }
}

/*
 * -------------------------- Extension Functions --------------------------
 */

/**
 * Erweiterungs-Methode um dem [FileSpec.Builder] die benötigten Imports hinzuzufügen.
 *
 * @param[classes] Die Klassen, deren Imports hinzugefügt werden sollen.
 */
fun FileSpec.Builder.addImports(vararg classes: KClass<*>) {
    classes.forEach { clazz -> this.addImport(clazz.asClassName().packageName, clazz.asClassName().simpleName)}
}