package com.macapp.carmusicapp.api

import com.macapp.carmusicapp.model.MyMusic
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiInterface {
    @Headers(
        "X-RapidAPI-Key: cbeb5b0286msh83c72714cf1b973p1eb59ajsn278f34a92e07",
        "X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com"
    )
    @GET("search")
    suspend fun getData(@Query("q") query: String): Response<MyMusic>

}