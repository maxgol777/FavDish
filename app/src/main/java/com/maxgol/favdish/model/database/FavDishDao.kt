package com.maxgol.favdish.model.database

import androidx.room.*
import com.maxgol.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {
    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)

    @Update
    suspend fun updateFaveDishDetails(favDish: FavDish)

    @Delete
    suspend fun deleteFavDish(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
    fun getAllDishesList(): Flow<List<FavDish>>

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE favorite_dish = 1")
    fun getFavoriteDishesList(): Flow<List<FavDish>>
}