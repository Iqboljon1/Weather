package com.iraimjanov.weather.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iraimjanov.weather.R
import com.iraimjanov.weather.databinding.ItemPagerBinding
import com.iraimjanov.weather.datas.Constants
import com.iraimjanov.weather.db.CityDB
import com.iraimjanov.weather.model.weather.WeatherData
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class VPAdapter(
    private val listCity: List<CityDB>,
    private val listWeather: List<WeatherData>,
    private val navController: NavController,
) :
    RecyclerView.Adapter<VPAdapter.VH>() {

    inner class VH(private val itemRV: ItemPagerBinding) : RecyclerView.ViewHolder(itemRV.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            itemRV.apply {
                tvTime.text = getDateFormat(listWeather[position].current.dt)
                tvTemp.text = "${getIntegerNumber(listWeather[position].current.temp)}°"
                tvDescription.text = getDescription(listWeather[position].current.weather[0].description)
                tvDew.text = "${getIntegerNumber(listWeather[position].current.dew_point)}°"
                tvHumidity.text = "${listWeather[position].current.humidity} %"
                tvPressure.text = "${listWeather[position].current.pressure} hPa"
                tvCloudy.text = "${listWeather[position].current.clouds} %"
                tvUvIndex.text = listWeather[position].current.uvi.toString()
                tvVisibility.text = "${listWeather[position].current.visibility} m"
                tvSunrise.text = getTimeFormat(listWeather[position].current.sunrise)
                tvSunset.text = getTimeFormat(listWeather[position].current.sunset)
                rvHourly.adapter = RVHourlyAdapter(listWeather[position].hourly)
                Glide.with(itemRV.root).load(R.drawable.image_1).into(itemRV.imageWeatherBackground)

                itemRV.tvNextDay.setOnClickListener {
                    Constants.cityName = listCity[position].name.toString()
                    Constants.weatherData = listWeather[position]
                    navController.navigate(R.id.action_homeFragment_to_dayFragment)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(position)

    }

    override fun getItemCount(): Int = listCity.size

    private fun getDescription(description: String): String {
        return description[0].uppercase() + description.substring(1, description.length - 1)

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