package com.iraimjanov.weather.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CityDB {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var name: String? = null
    var latitude: String? = null
    var longitude: String? = null

    constructor(id: Int?, name: String?, latitude: String?, longitude: String?) {
        this.id = id
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor(name: String?, latitude: String?, longitude: String?) {
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor()

}