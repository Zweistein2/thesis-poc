package de.potionlabs.enums

enum class MatchingUGCType(val code: Int) {
    ITEMS(0),
    ITEMS_MTX(1),
    ITEMS_READY_TO_USE(2),
    COLLECTIONS(3),
    ARTWORK(4),
    VIDEOS(5),
    SCREENSHOTS(6),
    ALL_GUIDES(7),
    WEB_GUIDES(8),
    INTEGRATED_GUIDES(9),
    USABLE_IN_GAME(10),
    CONTROLLER_BINDINGS(11),
    GAME_MANAGED_ITEMS(12),
    ALL(-1);

    companion object {
        fun getByCode(code: Int): MatchingUGCType? = entries.firstOrNull { it.code == code }
    }
}