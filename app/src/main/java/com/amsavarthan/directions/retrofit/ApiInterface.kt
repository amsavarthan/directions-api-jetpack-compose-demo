package com.amsavarthan.directions.retrofit

import com.amsavarthan.directions.model.Result
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("json")
    fun getDirection(
        @Query("origin") originPlaceId: String,
        @Query("destination") destinationPlaceId: String,
        @Query("key") key: String,
    ): Call<Result>

    companion object {

        fun create(): ApiInterface {
            return RetrofitClient.getInstance().create(ApiInterface::class.java)
        }

    }

}