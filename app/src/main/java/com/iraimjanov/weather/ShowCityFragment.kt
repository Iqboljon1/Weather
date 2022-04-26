package com.iraimjanov.weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.iraimjanov.weather.adapters.RVCityAdapter
import com.iraimjanov.weather.databinding.FragmentShowCityBinding
import com.iraimjanov.weather.datas.Constants
import com.iraimjanov.weather.datas.NetworkHelper
import com.iraimjanov.weather.db.AppDatabase
import com.iraimjanov.weather.db.CityDB

class ShowCityFragment : Fragment() {
    private lateinit var binding: FragmentShowCityBinding
    private lateinit var appDatabase: AppDatabase
    private lateinit var listCity: List<CityDB>
    private lateinit var networkHelper: NetworkHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentShowCityBinding.inflate(layoutInflater)
        networkHelper = NetworkHelper(requireActivity())

        if (networkHelper.isNetworkConnected()){
            connection()
        }else{
            findNavController().navigate(R.id.action_showCityFragment_to_noInternetFragment)
        }

        return binding.root
    }

    private fun connection() {
        binding.lySearchView.setOnClickListener {
            findNavController().navigate(R.id.action_showCityFragment_to_searchCityFragment)
        }

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        buildRV()
    }

    private fun buildRV() {
        appDatabase = AppDatabase.getInstance(requireActivity())
        listCity = appDatabase.myDao().getAllCityDB()
        binding.rvCity.adapter = RVCityAdapter(listCity, object : RVCityAdapter.RVClickCity {
            override fun delete(cityDB: CityDB, position: Int) {
                if (listCity.size != 1){
                    appDatabase.myDao().deleteCityDB(cityDB)
                    Constants.listWeatherData.removeAt(position)
                    buildRV()
                }else{
                    val snackbar = Snackbar.make(binding.root, "Leave at least on place!", Snackbar.LENGTH_LONG)
                    snackbar.setAction("Ok"
                    ) {}
                    snackbar.show()
                }
            }
        })
    }
}