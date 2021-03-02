package com.example.smd

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CompanyProfileService {
    @GET("profile2?")
    fun getCompanyProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Call<CompanyProfile>
}