package com.iraimjanov.weather.retrofit

import com.iraimjanov.weather.model.city.CityData
import com.iraimjanov.weather.model.city_search.CitySearchData
import com.iraimjanov.weather.model.weather.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitService {
    @GET("onecall")
    fun getWeatherData(
        @Query("exclude") exclude: String,
        @Query("appid") api_key: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
    ): Call<WeatherData>

    @GET("reverse")
    fun getCityData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String,
        @Query("apiKey") apiKey: String,
    ): Call<CityData>

    @GET("autocomplete")
    fun getCitySearchData(
        @Header("x-rapidapi-host") host:String,
        @Header("x-rapidapi-key") key:String,
        @Query("limit") limit: Int,
        @Query("type") type: String,
        @Query("q") q: String
    ): Call<List<CitySearchData>>

}