package com.maxgol.favdish.model.database

import androidx.annotation.WorkerThread
import com.maxgol.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {

    val allDishesList: Flow<List<FavDish>> = favDishDao.getAllDishesList()
    val favoriteDishes: Flow<List<FavDish>> = favDishDao.getFavoriteDishesList()

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish) {
        favDishDao.insertFavDishDetails(favDish)
    }

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish) {
        favDishDao.updateFaveDishDetails(favDish)
    }

    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDish) {
        favDishDao.deleteFavDish(favDish)
    }

    fun filteredListDishes(value: String): Flow<List<FavDish>> =
        favDishDao.getFilteredDishesList(value)
}