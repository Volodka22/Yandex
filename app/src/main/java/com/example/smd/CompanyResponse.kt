package com.example.smd

import com.google.gson.annotations.SerializedName

class CompanyResponse {
    @SerializedName("constituents")
    var companyArr = ArrayList<String>()
}