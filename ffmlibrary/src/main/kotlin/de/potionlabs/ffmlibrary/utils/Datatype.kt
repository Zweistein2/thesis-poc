package de.potionlabs.ffmlibrary.utils

import kotlin.reflect.KClass

/**
 * Hilfsklasse für die Einschränkung auf die nutzbaren Datentypen in den bereitgestellten Annotationen.
 */
enum class Datatype(val clazz: KClass<*>) {
    STRING(String::class),
    BOOLEAN(Boolean::class),
    SHORT(Short::class),
    INTEGER(Int::class),
    LONG(Long::class),
    FLOAT(Float::class),
    DOUBLE(Double::class),
    BYTE(Byte::class);
}