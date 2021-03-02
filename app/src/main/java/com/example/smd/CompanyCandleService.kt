package com.example.smd

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CompanyCandleService {
    @GET("candle?")
    fun getCompanyCandleList(
        @Query("symbol") symbol: String,
        @Query("token") token: String,
        @Query("resolution") resolution: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): Call<CompanyCandleResponse>
}