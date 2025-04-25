package com.example.forecanow.data.network


import com.example.forecanow.pojo.NominatimLocation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApiService {
    @GET("search")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5
    ): Response<List<NominatimLocation>>
}