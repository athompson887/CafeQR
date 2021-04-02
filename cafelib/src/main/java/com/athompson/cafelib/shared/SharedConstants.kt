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

    const val VENUES: String = "venues"
    const val MENUS: String = "menus"
    const val FOOD_MENU_ITEM: String = "menu_item"
    const val USERS: String = "users"

    const val MENU_ITEMS: String = "menu_items"

    const val PREFERENCES: String = "CafeQrPreferences"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    // Intent extra constants.
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 2

    const val MALE: String = "Male"
    const val FEMALE: String = "Female"

    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val USER_ID: String = "user_id"
    const val IMAGE: String = "image"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val COMPLETE_PROFILE: String = "profileCompleted"
    const val ADD_ITEM_NAME: String = "add"

    const val VENUE_NAME: String = "name"
    const val VENUE_DESCRIPTION: String = "description"
    const val VENUE_LOCATION: String = "location"
    const val VENUE_MENU_ID: String = "selectedMenuId"
    const val VENUE_SELECTED_THEME: String = "selectedTheme"
    const val VENUE_IMAGE:String = "imageUrl"

    const val USER_PROFILE_IMAGE_SUFFIX:String = "User_Profile_Image"
    const val MENU_IMAGE_SUFFIX:String = "Menu_Image"
    const val VENUE_IMAGE_SUFFIX:String = "Venue_Image"
    const val MENU_ITEM_IMAGE_SUFFIX:String = "Menu_Item_Image"



    val FOOD_TYPES = arrayOf(
        "Appetizer","Starter", "Main", "Desert","Alchoholic Beverage", "Non Alcoholic Beverage", "Cocktail"
    )
    val THEMES = arrayOf(
        "Dark","Light"
    )
}