package com.iraimjanov.weather.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MyDao {
    @Insert
    fun addCityDB(cityDB: CityDB)

    @Delete
    fun deleteCityDB(cityDB: CityDB)

    @Query("select * from CityDB")
    fun getAllCityDB(): List<CityDB>
}