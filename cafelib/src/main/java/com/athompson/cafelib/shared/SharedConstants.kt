package com.athompson.cafelib.shared

object SharedConstants {
    const val LOGGING_ON = true
    const val CAFE_QR_ID = 66
    const val ERROR_RETURN = -1

    const val USER_DB = "qrusers"

    const val EMAIL_FIELD = "email"
    const val FIRST_NAME_FIELD = "firstName"
    const val LAST_NAME_FIELD = "lastName"
    const val DISPLAY_NAME_FIELD = "displayName"
    const val UID_FIELD = "uid"

    const val ORGANISATIONS: String = "organisations"
    const val VENUES: String = "venues"
    const val MENUS: String = "menus"
    const val MENU_ITEM: String = "menu_item"
    const val USERS: String = "users"


    val FOOD_TYPES = arrayOf(
        "Appetizer","Starter", "Main", "Desert","Alchoholic Beverage", "Non Alcoholic Beverage", "Cocktail"
    )
    val THEMES = arrayOf(
        "Dark","Light"
    )
}