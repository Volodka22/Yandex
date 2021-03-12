package com.example.smd

data class CompanyProfile (
    var currency: String,
    var price: Double,
    var name: String,
    var ticker: String = "",
    var logo: String = "",
    var changeM: Double = 0.0,
    var changeP: Double = 0.0,
    var isFavourite: Boolean = false
)