package de.potionlabs.enums

enum class InitResult(val code: Int) {
    /**
     * Success.
     */
    OK(0),

    /**
     * Some other failure.
     */
    FAILED_GENERIC(1),

    /**
     * Cannot connect to Steam, steam probably isn't running.
     */
    NO_STEAM_CLIENT(2),

    /**
     * Steam client appears to be out of date.
     */
    VERSION_MISMATCH(3);

    companion object {
        fun getByCode(code: Int): InitResult? = entries.firstOrNull { it.code == code }
    }
}