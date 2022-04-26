package com.iraimjanov.weather.datas

import com.iraimjanov.weather.model.weather.WeatherData

object Constants {

    //Weather API settings
    const val units: String = "metric"
    var language: String = "en"

    //City API settings
    const val format: String = "json"

    //Weather API settings constants
    const val BASE_URL_WEATHER = "https://api.openweathermap.org/data/2.5/"
    const val API_KEY_WEATHER = "cb3ac4397750f1a567633506ff32c63c"
    const val exclude = "minutely,alerts"

    //City API settings constants
    const val BASE_URL_CITY = "https://api.geoapify.com/v1/geocode/"
    const val API_KEY_CITY = "22fbd49d946142f99450810a8b648d6a"

    //City Search API settings constants
    const val BASE_URL_CITY_SEARCH = "https://spott.p.rapidapi.com/places/"
    const val API_KEY_CITY_SEARCH = "df0a7e3ba1msh3db8fb6650227fap1ffd3ejsn1de59e4fbd0f"
    const val HOST = "spott.p.rapidapi.com"
    const val limit = 10
    const val type = "CITY"

    lateinit var weatherData: WeatherData
    var cityName = ""
    val listWeatherData = ArrayList<WeatherData>()
    var firstTime = false

}