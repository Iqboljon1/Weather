package com.iraimjanov.weather.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iraimjanov.weather.databinding.ItemCitySearchBinding
import com.iraimjanov.weather.model.city_search.CitySearchData

class RVCitySearchAdapter(private val listCitySearch: ArrayList<CitySearchData> , private val rvClickCitySearch: RVClickCitySearch) :
    RecyclerView.Adapter<RVCitySearchAdapter.VH>() {

    inner class VH(var itemRV: ItemCitySearchBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(citySearchData: CitySearchData) {
            itemRV.tvCityName.text = citySearchData.name
            itemRV.imageIcAdd.setOnClickListener {
                rvClickCitySearch.add(citySearchData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemCitySearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(listCitySearch[position])

    }

    override fun getItemCount(): Int = listCitySearch.size

    interface RVClickCitySearch {
        fun add(citySearchData: CitySearchData)
    }

}