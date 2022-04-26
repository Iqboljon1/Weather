package com.iraimjanov.weather.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iraimjanov.weather.databinding.ItemDailyRvBinding
import com.iraimjanov.weather.model.weather.Daily
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class RVDailyAdapter(private val listDaily: List<Daily>) :
    RecyclerView.Adapter<RVDailyAdapter.VH>() {

    inner class VH(var itemRV: ItemDailyRvBinding) : RecyclerView.ViewHolder(itemRV.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(daily: Daily) {
            itemRV.imageIcon.setAnimation("${daily.weather[0].icon}.json")
            itemRV.tvTemp.text = "${getIntegerNumber(daily.temp.day)}°"
            itemRV.tvDay.text = buildDayOfMonth(daily.dt)
            itemRV.tvDayName.text = buildDayOfWeek(daily.dt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemDailyRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(listDaily[position])

    }

    override fun getItemCount(): Int = 7

    fun getIntegerNumber(num:Double):String{
        return DecimalFormat("#").format(num)
    }

    @SuppressLint("SimpleDateFormat")
    private fun buildDayOfWeek(unixTime: Int): String {
        return SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date(unixTime * 1000L))
    }

    @SuppressLint("SimpleDateFormat")
    private fun buildDayOfMonth(unixTime: Int): String {
        return SimpleDateFormat("dd MMMM", Locale.ENGLISH).format(Date(unixTime * 1000L))
    }

}