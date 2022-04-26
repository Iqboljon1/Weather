package com.iraimjanov.weather

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.iraimjanov.weather.adapters.RVHourlyAdapter
import com.iraimjanov.weather.databinding.FragmentLocationBinding
import com.iraimjanov.weather.datas.Constants
import com.iraimjanov.weather.datas.Constants.API_KEY_CITY
import com.iraimjanov.weather.datas.Constants.cityName
import com.iraimjanov.weather.datas.Constants.format
import com.iraimjanov.weather.datas.Constants.weatherData
import com.iraimjanov.weather.datas.NetworkHelper
import com.iraimjanov.weather.datas.PermissionsService
import com.iraimjanov.weather.model.city.CityData
import com.iraimjanov.weather.model.weather.Current
import com.iraimjanov.weather.model.weather.Hourly
import com.iraimjanov.weather.model.weather.WeatherData
import com.iraimjanov.weather.retrofit.RetrofitClientCity
import com.iraimjanov.weather.retrofit.RetrofitClientWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class LocationFragment : Fragment() {
    private lateinit var binding: FragmentLocationBinding
    private lateinit var permissionsService: PermissionsService
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var networkHelper: NetworkHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLocationBinding.inflate(layoutInflater)
        networkHelper = NetworkHelper(requireActivity())
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerViewParent) as NavHostFragment
        navController = navHostFragment.navController

        if (networkHelper.isNetworkConnected()) {
            connection()
        } else {
            navController.navigate(R.id.action_homeFragment_to_noInternetFragment)
        }

        return binding.root
    }

    private fun connection() {
        permissionsService = PermissionsService()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.lyRefresh.isRefreshing = true
        checkForLocationAccess()

        binding.lyRefresh.setOnRefreshListener {
            checkForLocationAccess()
        }

        binding.tvNextDay.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_dayFragment)
        }
    }

    private fun checkForLocationAccess() {
        if (permissionsService.checkPermission(requireActivity())) {
            checkLocationEnabled()
        } else {
            requestPermissions()
        }
    }

    private fun checkLocationEnabled() {
        if (permissionsService.isLocationEnabled(requireActivity())) {
            getMyLocation()
        } else {
            buildAlertMessageNoGps()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            val result = it.result
            if (result == null) {
                getNewLocation()
            } else {
                getLocationWeatherData(result.latitude, result.longitude)
                println("Message!! ${result.accuracy}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.myLooper()!!)
    }

    @SuppressLint("ShowToast")
    private fun requestPermissions() {
        val mPermissionResult = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                checkLocationEnabled()
            } else {
                buildAlertMessageNoPermissions()
            }
        }
        mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun buildAlertMessageNoPermissions() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Allow the app to access location information for the app to work properly.\n" +
                "Go to Permission>Location to open it")
            .setCancelable(false)
            .setPositiveButton("To Open It") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") {
                    _, _,
                ->
                launchLocationEnabled.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun getLocationWeatherData(latitude: Double, longitude: Double) {
        RetrofitClientWeather.retrofitService().getWeatherData(
            Constants.exclude,
            Constants.API_KEY_WEATHER,
            latitude,
            longitude,
            Constants.units,
            Constants.language
        ).enqueue(object : Callback<WeatherData> {
            override fun onResponse(
                call: Call<WeatherData>,
                response: Response<WeatherData>,
            ) {
                if (response.isSuccessful) {
                    weatherData = response.body()!!
                    buildRVHourly(response.body()!!.hourly)
                    showActivity(response.body()!!.current,
                        response.body()!!.lat,
                        response.body()!!.lon)
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Log.d("Main!!", t.message.toString())
            }
        })
    }

    private fun buildRVHourly(listHourly: List<Hourly>) {
        binding.rvHourly.adapter = RVHourlyAdapter(listHourly)
    }

    @SuppressLint("SetTextI18n")
    private fun showActivity(current: Current, lat: Double, lon: Double) {
        binding.apply {
            tvTime.text = getDateFormat(current.dt)
            tvTemp.text = "${getIntegerNumber(current.temp)}°"
            tvDescription.text = getDescription(current.weather[0].description)
            tvDew.text = "${getIntegerNumber(current.dew_point)}°"
            tvHumidity.text = "${current.humidity} %"
            tvPressure.text = "${current.pressure} hPa"
            tvCloudy.text = "${current.clouds} %"
            tvUvIndex.text = current.uvi.toString()
            tvVisibility.text = "${current.visibility} m"
            tvSunrise.text = getTimeFormat(current.sunrise)
            tvSunset.text = getTimeFormat(current.sunset)
            Glide.with(binding.root).load(R.drawable.image_1).into(binding.imageWeatherBackground)
            getCityName(lat, lon)
        }
    }

    private fun getDescription(description: String): String {
        return description[0].uppercase() + description.substring(1, description.length - 1)

    }

    private fun getCityName(lat: Double, lon: Double) {
        RetrofitClientCity.retrofitService().getCityData(lat, lon, format, API_KEY_CITY)
            .enqueue(object : Callback<CityData> {
                override fun onResponse(call: Call<CityData>, response: Response<CityData>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.results[0].state != null) {
                            binding.tvCityName.text = response.body()!!.results[0].state
                            cityName = response.body()!!.results[0].state
                        }

                        if (response.body()!!.results[0].city != null) {
                            binding.tvCityName.text = response.body()!!.results[0].city
                            cityName = response.body()!!.results[0].city
                        }

                        if (response.body()!!.results[0].street != null) {
                            binding.tvCityName.text = response.body()!!.results[0].street
                            cityName = response.body()!!.results[0].street
                        }

                        binding.lyRefresh.isRefreshing = false
                        binding.lyParent.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<CityData>, t: Throwable) {
                    Log.d("Main!!", t.message.toString())
                }
            })
    }

    private val launchLocationEnabled =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            checkLocationEnabled()
        }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val result = p0.lastLocation
            getLocationWeatherData(result.latitude, result.longitude)
        }
    }

    private fun getIntegerNumber(num: Double): String {
        return DecimalFormat("#").format(num)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTimeFormat(unixTime: Int): String {
        return SimpleDateFormat("HH:mm").format(Date(unixTime * 1000L))
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateFormat(unixTime: Int): String {
        return SimpleDateFormat("E, dd MMMM", Locale.ENGLISH).format(Date(unixTime * 1000L))
    }
}