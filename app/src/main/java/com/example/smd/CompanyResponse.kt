package com.example.smd

import com.google.gson.annotations.SerializedName

data class CompanyResponse(
    @SerializedName("constituents")
    var companyArr: ArrayList<String> =  ArrayList()
)