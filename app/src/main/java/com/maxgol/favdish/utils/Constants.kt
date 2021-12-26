package com.maxgol.favdish.utils

object Constants {
    const val DISH_TYPE = "DishType"
    const val DISH_CATEGORY = "DishCategory"
    const val DISH_COOKING_TIME = "DishCookingTime"

    const val DISH_IMAGE_SOURCE_LOCAL = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE = "Online"
    const val EXTRA_DISH_DETAILS = "DishDetails"


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