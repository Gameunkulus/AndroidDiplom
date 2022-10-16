package com.example.cookingbook.rest

import com.example.cookingbook.pojo.Category
import com.example.cookingbook.pojo.IngRecPair
import com.example.cookingbook.pojo.Ingredient
import com.example.cookingbook.pojo.Recipe
import com.fasterxml.jackson.annotation.JsonProperty
import retrofit2.http.GET

public abstract class DeltaResponse {
    @GET:JsonProperty("delta")
    public abstract var delta :Double ;
}
