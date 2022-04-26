package com.iraimjanov.weather.retrofit

import com.iraimjanov.weather.datas.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientCitySearch {
    fun retrofitService(): RetrofitService {
        return getRetrofit().create(RetrofitService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_CITY_SEARCH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}