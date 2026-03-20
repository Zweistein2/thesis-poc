package de.potionlabs.enums

enum class UserUGCList(val code: Int) {
    PUBLISHED(0),
    VOTED_ON(1),
    VOTED_UP(2),
    VOTED_DOWN(3),
    WILL_VOTE_LATER(4),
    FAVORITED(5),
    SUBSCRIBED(6),
    USED_OR_PLAYED(7),
    FOLLOWED(8);

    companion object {
        fun getByCode(code: Int): UserUGCList? = entries.firstOrNull { it.code == code }
    }
}