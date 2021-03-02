package com.example.smd

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CompanyService {
    @GET("constituents?")
    fun getCompanyProfilesList(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Call<CompanyResponse>
}