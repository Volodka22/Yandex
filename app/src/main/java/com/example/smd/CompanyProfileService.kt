package com.example.smd


import retrofit2.http.GET
import retrofit2.http.Query

interface CompanyProfileService {
    @GET("profile2?")
    suspend fun getCompanyProfileAsync(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): CompanyProfile
}