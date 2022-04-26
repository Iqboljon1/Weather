package com.iraimjanov.weather.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iraimjanov.weather.databinding.ItemHourlyRvBinding
import com.iraimjanov.weather.model.weather.Hourly
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class RVHourlyAdapter(private val listHourly: List<Hourly>) :
    RecyclerView.Adapter<RVHourlyAdapter.VH>() {

    inner class VH(var itemRV: ItemHourlyRvBinding) : RecyclerView.ViewHolder(itemRV.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(hourly: Hourly) {
            itemRV.imageIcon.setAnimation("${hourly.weather[0].icon}.json")
            itemRV.tvTime.text = getTimeFormat(hourly.dt)
            itemRV.tvTemp.text = "${getIntegerNumber(hourly.temp)}°"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemHourlyRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(listHourly[position])

    }

    override fun getItemCount(): Int = 24

    @SuppressLint("SimpleDateFormat")
    fun getTimeFormat(unixTime:Int):String{
        return SimpleDateFormat("HH:mm").format(Date(unixTime*1000L))
    }

    fun getIntegerNumber(num:Double):String{
        return DecimalFormat("#").format(num)
    }

}