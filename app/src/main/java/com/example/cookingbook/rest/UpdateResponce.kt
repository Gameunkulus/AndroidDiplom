package com.example.cookingbook.rest

import com.example.cookingbook.pojo.Category
import com.example.cookingbook.pojo.IngRecPair
import com.example.cookingbook.pojo.Ingredient
import com.example.cookingbook.pojo.Recipe
import com.fasterxml.jackson.annotation.JsonProperty


class UpdateResponse {
    @JsonProperty("recipes")
    var recipes: List<Recipe> = ArrayList()
    @JsonProperty("ingredients")
    var ingredients: List<Ingredient> = ArrayList()
    @JsonProperty("newUpdated")
    var newUpdated: Long? = null
    @JsonProperty("categories")
    var categories: List<Category> = ArrayList<Category>()
    @JsonProperty("recipeIngredients")
    var recIng: List<IngRecPair> = ArrayList()
}
