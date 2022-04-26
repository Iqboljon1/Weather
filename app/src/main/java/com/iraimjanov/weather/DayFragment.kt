package com.iraimjanov.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iraimjanov.weather.adapters.RVDailyAdapter
import com.iraimjanov.weather.adapters.RVHourlyAdapter
import com.iraimjanov.weather.databinding.FragmentDayBinding
import com.iraimjanov.weather.datas.Constants
import com.iraimjanov.weather.datas.NetworkHelper
import com.iraimjanov.weather.model.city.CityData
import com.iraimjanov.weather.model.weather.Daily
import com.iraimjanov.weather.model.weather.Hourly
import com.iraimjanov.weather.model.weather.WeatherData

class DayFragment : Fragment() {
    private lateinit var binding: FragmentDayBinding
    private lateinit var weatherData: WeatherData
    private lateinit var cityName: String
    private lateinit var dailyList: List<Daily>
    private lateinit var hourlyList: List<Hourly>
    private lateinit var adapterHourly: RVHourlyAdapter
    private lateinit var adapterDaily: RVDailyAdapter
    private lateinit var networkHelper: NetworkHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDayBinding.inflate(layoutInflater)
        networkHelper = NetworkHelper(requireActivity())

        if (networkHelper.isNetworkConnected()){
            connection()
        }else{
            findNavController().navigate(R.id.action_dayFragment_to_noInternetFragment)
        }

        return binding.root
    }

    private fun connection() {
        loadData()

        binding.rvDaily.adapter = adapterDaily
        binding.rvHourly.adapter = adapterHourly
        binding.tvCityName.text = cityName

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadData() {
        weatherData = Constants.weatherData
        cityName = Constants.cityName
        dailyList = weatherData.daily
        hourlyList = weatherData.hourly
        adapterDaily = RVDailyAdapter(dailyList)
        adapterHourly = RVHourlyAdapter(hourlyList)
    }
}