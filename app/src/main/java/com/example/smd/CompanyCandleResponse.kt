package com.example.smd

import com.google.gson.annotations.SerializedName

class CompanyCandleResponse {
    @SerializedName("o")
    var candleList = ArrayList<Double>()

}