package com.example.smd

import com.google.gson.annotations.SerializedName

data class CompanyCandleResponse (
    @SerializedName("o")
    var candleList: ArrayList<Double> = ArrayList()
)