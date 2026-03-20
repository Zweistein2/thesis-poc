package de.potionlabs.ffmlibrary

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Klasse die den [FFMProcessor] bereitstellt, damit dieser vom Kotlin Symbol Processing-Plugin gefunden und ausgeführt werden kann.
 */
class FFMProcessorProvider: SymbolProcessorProvider {
    /**
     * Methode die einen [FFMProcessor] erstellt und zurückgibt.
     *
     * @param[environment] die Umgebung, in der der Prozessor ausgeführt wird, enthält den CodeGenerator, das Logging und die Konfiguration.
     *
     * @return ein neuer [FFMProcessor] mit den entsprechenden Parametern.
     */
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return FFMProcessor(
            environment.codeGenerator,
            environment.logger,
            environment.options
        )
    }
}