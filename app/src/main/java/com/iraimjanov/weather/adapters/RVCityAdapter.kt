package com.iraimjanov.weather.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iraimjanov.weather.databinding.ItemCityBinding
import com.iraimjanov.weather.db.CityDB

class RVCityAdapter(
    private val listCity: List<CityDB>,
    private val rvClickCity: RVClickCity,
) :
    RecyclerView.Adapter<RVCityAdapter.VH>() {

    inner class VH(var itemRV: ItemCityBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(cityDB: CityDB, position: Int) {
            itemRV.tvCityName.text = cityDB.name
            itemRV.imageIcDelete.setOnClickListener {
                rvClickCity.delete(cityDB , position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(listCity[position] , position)

    }

    override fun getItemCount(): Int = listCity.size

    interface RVClickCity {
        fun delete(cityDB: CityDB, position: Int)
    }

}