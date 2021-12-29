package com.maxgol.favdish.utils

object Constants {
    const val DISH_TYPE = "DishType"
    const val DISH_CATEGORY = "DishCategory"
    const val DISH_COOKING_TIME = "DishCookingTime"

    const val DISH_IMAGE_SOURCE_LOCAL = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE = "Online"
    const val EXTRA_DISH_DETAILS = "DishDetails"

    const val ALL_ITEMS: String = "ALL"
    const val FILTER_SELECTION: String = "FilterSelection"

    const val API_ENDPOINT: String = "recipes/random"

    const val API_KEY: String = "apiKey"
    const val API_KEY_VALUE: String = "42905ae0a8b9427f8e56d8ae91924b5c"

    const val LIMIT_LICENSE: String = "limitLicense"
    const val LIMIT_LICENSE_VALUE: Boolean = true

    const val TAGS: String = "tags"
    const val TAGS_VALUE: String = "vegetarian, dessert"

    const val NUMBER: String = "number"
    const val NUMBER_VALUE: Int = 1

    const val BASE_URL = "https://spoonacular.com/"

    fun dishTypes(): ArrayList<String> = arrayListOf(
        "breakfast",
        "lunch",
        "snacks",
        "dinner",
        "salad",
        "side dish",
        "dessert",
        "other"
    )

    fun dishCategories(): ArrayList<String> = arrayListOf(
        "Pizza",
        "BBQ",
        "Bakery",
        "Burger",
        "Cafe",
        "Chicken",
        "Dessert",
        "Drinks"
    )

    fun dishCookTime(): ArrayList<String> = arrayListOf(
        "10",
        "20",
        "30",
        "40",
        "50",
        "60",
        "90",
        "120"
    )
}