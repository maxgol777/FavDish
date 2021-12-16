package com.maxgol.favdish.model.database

import androidx.room.Dao
import androidx.room.Insert
import com.maxgol.favdish.model.entities.FavDish

@Dao
interface FavDishDao {
    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)


}