package com.example.smd

import com.google.gson.annotations.SerializedName

class CompanyProfile {
    @SerializedName("currency")
    var currency = ""
    var price = 0.0
    @SerializedName("name")
    var name = ""
    @SerializedName("ticker")
    var ticker = ""
    @SerializedName("logo")
    var logo = ""
    var changeM = 0.0
    var changeP = 0.0
    var isFavourite = false
}