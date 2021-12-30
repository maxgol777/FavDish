package com.maxgol.favdish.model.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.maxgol.favdish.model.entities.RandomDish
import com.maxgol.favdish.utils.Constants
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class RandomDishApiService {

    var api: RandomDishAPI

    init {

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        api = Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(RandomDishAPI::class.java)
    }


    fun getRandomDish(): Single<RandomDish.Recipes> = api.getRandomDish(
        Constants.API_KEY_VALUE,
        Constants.LIMIT_LICENSE_VALUE,
        Constants.TAGS_VALUE,
        Constants.NUMBER_VALUE
    )
}