package com.example.smd

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    @GET("company-news?")
    suspend fun getCompanyCandleListAsync(
        @Query("symbol") symbol: String,
        @Query("token") token: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): ArrayList<News>
}