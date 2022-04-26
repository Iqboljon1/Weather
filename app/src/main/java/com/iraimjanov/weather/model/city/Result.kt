package com.iraimjanov.weather.model.city

data class Result(
    val address_line1: String,
    val address_line2: String,
    val bbox: Bbox,
    val city: String,
    val country: String,
    val country_code: String,
    val county: String,
    val datasource: Datasource,
    val distance: Double,
    val formatted: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val place_id: String,
    val postcode: String,
    val rank: Rank,
    val result_type: String,
    val state: String,
    val street: String
)