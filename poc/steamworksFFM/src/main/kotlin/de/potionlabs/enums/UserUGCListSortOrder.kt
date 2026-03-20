package de.potionlabs.enums

enum class UserUGCListSortOrder(val code: Int) {
    CREATION_ORDER_DESC(0),
    CREATION_ORDER_ASC(1),
    TITLE_ASC(2),
    LAST_UPDATED_DESC(3),
    SUBSCRIPTION_DATE_DESC(4),
    VOTE_SCORE_DESC(5),
    FOR_MODERATION(6);

    companion object {
        fun getByCode(code: Int): UserUGCListSortOrder? = entries.firstOrNull { it.code == code }
    }
}