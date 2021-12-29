package com.maxgol.favdish.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maxgol.favdish.model.entities.RandomDish
import com.maxgol.favdish.model.network.RandomDishApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomDishViewModel : ViewModel() {

    private val randomRecipeApiService = RandomDishApiService()

    private val compositeDisposable = CompositeDisposable()

    private val loadRandomDish = MutableLiveData<Boolean>()
    val randomDishResponse = MutableLiveData<RandomDish.Recipes>()
    val randomDishLoadingError = MutableLiveData<Boolean>()

    fun getRandomRecipeFromApi() {
        loadRandomDish.value = true
        compositeDisposable.add(
            randomRecipeApiService.getRandomDish()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<RandomDish.Recipes>() {
                    override fun onSuccess(recipes: RandomDish.Recipes?) {
                        loadRandomDish.value = false
                        recipes?.let { randomDishResponse.value = it }
                        randomDishLoadingError.value = false
                    }

                    override fun onError(e: Throwable?) {
                        loadRandomDish.value = false
                        randomDishLoadingError.value = true
                        e?.printStackTrace()
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}