package com.maxgol.favdish.view.fragments

import com.maxgol.favdish.model.entities.FavDish

interface DeleteDishAction {
    fun deleteDish(favDish: FavDish)
}