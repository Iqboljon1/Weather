package com.iraimjanov.weather

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.iraimjanov.weather.databinding.FragmentSplashBinding
import com.iraimjanov.weather.datas.Constants
import com.iraimjanov.weather.datas.Constants.API_KEY_WEATHER
import com.iraimjanov.weather.datas.Constants.exclude
import com.iraimjanov.weather.datas.Constants.firstTime
import com.iraimjanov.weather.datas.Constants.language
import com.iraimjanov.weather.datas.Constants.units
import com.iraimjanov.weather.datas.NetworkHelper
import com.iraimjanov.weather.db.AppDatabase
import com.iraimjanov.weather.db.CityDB
import com.iraimjanov.weather.model.weather.WeatherData
import com.iraimjanov.weather.retrofit.RetrofitClientWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var appDatabase: AppDatabase
    private lateinit var listCity: List<CityDB>
    private lateinit var networkHelper: NetworkHelper
    private var position = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        networkHelper = NetworkHelper(requireActivity())

        if (networkHelper.isNetworkConnected()){
            connection()
        }else{
            findNavController().navigate(R.id.action_splashFragment_to_noInternetFragment)
        }



        return binding.root
    }

    private fun connection() {
        appDatabase = AppDatabase.getInstance(requireActivity())
        listCity = appDatabase.myDao().getAllCityDB()
        Constants.listWeatherData.clear()

        if (listCity.isNotEmpty()){
            getWeather(listCity[position])
        }else{
            firstTime = true
            findNavController().navigate(R.id.action_splashFragment_to_searchCityFragment)
        }
    }

    private fun getWeather(cityDB: CityDB){
        RetrofitClientWeather.retrofitService().getWeatherData(
            exclude,
            API_KEY_WEATHER,
            cityDB.latitude!!.toDouble(),
            cityDB.longitude!!.toDouble(),
            units,
            language
        ).enqueue(object : Callback<WeatherData> {
            override fun onResponse(
                call: Call<WeatherData>,
                response: Response<WeatherData>,
            ) {
                if (response.isSuccessful) {
                    Log.d("Main!!" , listCity[position].name.toString())
                    Constants.listWeatherData.add(response.body()!!)
                    if (Constants.listWeatherData.size == listCity.size){
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    }else{
                        position++
                        getWeather(listCity[position])
                    }
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Log.d("Main!!", t.message.toString())
            }
        })
    }

}