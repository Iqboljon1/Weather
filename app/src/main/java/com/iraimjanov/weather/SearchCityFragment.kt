package com.iraimjanov.weather

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iraimjanov.weather.adapters.RVCitySearchAdapter
import com.iraimjanov.weather.databinding.FragmentSearchCityBinding
import com.iraimjanov.weather.datas.Constants
import com.iraimjanov.weather.datas.Constants.API_KEY_CITY_SEARCH
import com.iraimjanov.weather.datas.Constants.HOST
import com.iraimjanov.weather.datas.Constants.limit
import com.iraimjanov.weather.datas.Constants.type
import com.iraimjanov.weather.datas.NetworkHelper
import com.iraimjanov.weather.db.AppDatabase
import com.iraimjanov.weather.db.CityDB
import com.iraimjanov.weather.model.city_search.CitySearchData
import com.iraimjanov.weather.model.weather.WeatherData
import com.iraimjanov.weather.retrofit.RetrofitClientCitySearch
import com.iraimjanov.weather.retrofit.RetrofitClientWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchCityFragment : Fragment() {
    private lateinit var binding: FragmentSearchCityBinding
    private lateinit var appDatabase: AppDatabase
    private lateinit var networkHelper: NetworkHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchCityBinding.inflate(layoutInflater)
        networkHelper = NetworkHelper(requireActivity())

        if (networkHelper.isNetworkConnected()) {
            connection()
        } else {
            findNavController().navigate(R.id.action_searchCityFragment_to_noInternetFragment)
        }

        return binding.root
    }

    private fun connection() {
        appDatabase = AppDatabase.getInstance(requireActivity())
        binding.swipeRefreshLayout.isEnabled = false
        settingsSearchView()
        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun searchCity(cityName: String) {
        RetrofitClientCitySearch.retrofitService()
            .getCitySearchData(HOST, API_KEY_CITY_SEARCH, limit, type, cityName)
            .enqueue(object : Callback<List<CitySearchData>> {
                override fun onResponse(
                    call: Call<List<CitySearchData>>,
                    response: Response<List<CitySearchData>>,
                ) {
                    if (response.isSuccessful) {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.rvCitySearch.adapter =
                            RVCitySearchAdapter(response.body()!! as ArrayList<CitySearchData>,
                                object : RVCitySearchAdapter.RVClickCitySearch {
                                    override fun add(citySearchData: CitySearchData) {
                                        val citySearchDataDb = CityDB(citySearchData.name,
                                            citySearchData.coordinates.latitude.toString(),
                                            citySearchData.coordinates.longitude.toString())
                                        binding.rvCitySearch.adapter =
                                            RVCitySearchAdapter(ArrayList(),
                                                object : RVCitySearchAdapter.RVClickCitySearch {
                                                    override fun add(citySearchData: CitySearchData) {}
                                                })
                                        binding.searchView.visibility = View.GONE
                                        binding.imageBack.visibility = View.GONE
                                        binding.swipeRefreshLayout.isRefreshing = true
                                        addWeatherData(citySearchDataDb)
                                    }
                                })
                    }
                }

                override fun onFailure(call: Call<List<CitySearchData>>, t: Throwable) {
                    Log.d("Main!!", t.message.toString())
                }
            })
    }

    private fun addWeatherData(cityDB: CityDB) {
        RetrofitClientWeather.retrofitService().getWeatherData(
            Constants.exclude,
            Constants.API_KEY_WEATHER,
            cityDB.latitude!!.toDouble(),
            cityDB.longitude!!.toDouble(),
            Constants.units,
            Constants.language
        ).enqueue(object : Callback<WeatherData> {
            override fun onResponse(
                call: Call<WeatherData>,
                response: Response<WeatherData>,
            ) {
                if (response.isSuccessful) {
                    Constants.listWeatherData.add(response.body()!!)
                    appDatabase.myDao().addCityDB(cityDB)
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (isVisible) {
                        if (Constants.firstTime) {
                            Constants.firstTime = false
                            findNavController().navigate(R.id.action_searchCityFragment_to_homeFragment)
                        } else {
                            findNavController().popBackStack()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Log.d("Main!!", t.message.toString())
            }
        })
    }

    private fun settingsSearchView() {
        binding.searchView.requestFocus()
        binding.searchView.setOnQueryTextFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.showSoftInput(view.findFocus(), 0)
            }
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.toString().trim().isNotEmpty()) {
                    binding.swipeRefreshLayout.isRefreshing = true
                    searchCity(query.toString())
                } else {
                    binding.rvCitySearch.adapter = RVCitySearchAdapter(ArrayList(),
                        object : RVCitySearchAdapter.RVClickCitySearch {
                            override fun add(citySearchData: CitySearchData) {}
                        })
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
}