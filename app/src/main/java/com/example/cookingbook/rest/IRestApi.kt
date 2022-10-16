package com.example.cookingbook.rest


import android.telecom.Call
import retrofit2.http.GET


interface IRestApi {0
    @GET(UPDATE_URL)
    fun update(@retrofit2.http.Query("lastUpdated") lastUpdate: Long?): Call<UpdateResponse?>?

    @GET(GET_UPDATE_SIZE_URL)
    fun getDelta(@retrofit2.http.Query("lastUpdated") lastUpdate: Long?): Call<DeltaResponse?>?

    companion object {
        const val UPDATE_URL = "update"
        const val GET_UPDATE_SIZE_URL = "delta"
    }
}
