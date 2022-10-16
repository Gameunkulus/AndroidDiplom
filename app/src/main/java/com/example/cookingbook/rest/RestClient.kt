package com.example.cookingbook.rest

import android.content.Context
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory


object RestClient {
    private const val ENDPOINT = "https://cookbook-rest.herokuapp.com/api/"
    private var sRestService: IRestApi? = null
    fun getClient(context: Context?): IRestApi? {
        if (sRestService == null) {
            val client: OkHttpClient = Builder().addInterceptor(new MockInterceptor())
                .build()
            val mapper = ObjectMapper()
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            val retrofit: Retrofit = Builder()
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .baseUrl(ENDPOINT)
                .client(client)
                .build()
            sRestService = retrofit.create(IRestApi::class.java)
        }
        return sRestService
    }
}
