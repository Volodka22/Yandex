package com.example.smd

import retrofit2.http.GET
import retrofit2.http.Query

interface CompanyService {
    @GET("constituents?")
    suspend fun getCompanyProfilesListAsync(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): CompanyResponse
}