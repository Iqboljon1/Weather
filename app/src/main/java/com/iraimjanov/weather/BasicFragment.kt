package com.iraimjanov.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.iraimjanov.weather.adapters.VPAdapter
import com.iraimjanov.weather.databinding.FragmentBasicBinding
import com.iraimjanov.weather.datas.Constants
import com.iraimjanov.weather.datas.NetworkHelper
import com.iraimjanov.weather.db.AppDatabase
import com.iraimjanov.weather.db.CityDB
import com.iraimjanov.weather.model.weather.WeatherData

class BasicFragment : Fragment() {
    private lateinit var binding: FragmentBasicBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appDatabase: AppDatabase
    private lateinit var listCity: List<CityDB>
    private lateinit var listWeather: ArrayList<WeatherData>
    private lateinit var networkHelper: NetworkHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBasicBinding.inflate(layoutInflater)
        networkHelper = NetworkHelper(requireActivity())
        navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerViewParent) as NavHostFragment
        navController = navHostFragment.navController

        if (networkHelper.isNetworkConnected()){
            connection()
        }else{
            navController.navigate(R.id.action_homeFragment_to_noInternetFragment)
        }

        return binding.root
    }

    private fun connection() {
        loadData()

        binding.imageEdit.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_showCityFragment)
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tvCityName.text = listCity[position].name
            }
        })
    }

    private fun loadData() {
        appDatabase = AppDatabase.getInstance(requireActivity())
        listCity = appDatabase.myDao().getAllCityDB()
        listWeather = Constants.listWeatherData
        binding.viewPager.adapter = VPAdapter(listCity, listWeather , navController)
    }
}