package com.iraimjanov.weather.retrofit

import com.iraimjanov.weather.datas.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientCity {

    fun retrofitService(): RetrofitService {
        return getRetrofit().create(RetrofitService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_CITY)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}