package com.maxgol.favdish.viewmodel

import androidx.lifecycle.*
import com.maxgol.favdish.model.database.FavDishRepository
import com.maxgol.favdish.model.entities.FavDish
import kotlinx.coroutines.launch

class FavDishViewModel(private val repository: FavDishRepository) : ViewModel() {

    val allDishesList: LiveData<List<FavDish>> = repository.allDishesList.asLiveData()
    val favoriteDishes: LiveData<List<FavDish>> = repository.favoriteDishes.asLiveData()

    fun insert(dish: FavDish) = viewModelScope.launch {
        repository.insertFavDishData(dish)
    }

    fun update(dish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(dish)
    }

    fun delete(dish: FavDish) = viewModelScope.launch {
        repository.deleteFavDishData(dish)
    }
}

@Suppress("UNCHECKED_CAST")
class FavDishViewModelFactory(private val repository: FavDishRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavDishViewModel::class.java)) {
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}