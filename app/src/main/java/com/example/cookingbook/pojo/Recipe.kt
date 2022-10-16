package com.example.cookingbook.pojo


import android.graphics.Bitmap
import com.example.cookingbook.helper.BitmapHelper.getImage
import com.example.cookingbook.helper.StringsUtils.capitalize
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty


class Recipe @JsonCreator constructor(
    @param:JsonProperty("id") var id: Long,
    @JsonProperty("name") name: String?,
    @JsonProperty("time") cookingTime: Int,
    @JsonProperty("category") categoryId: Long,
    @JsonProperty("instruction") instruction: String,
    @JsonProperty("icon") iconBytes: ByteArray?
) {
    var name: String?
    var cookingTime: Int
    var categoryId: Long
    var instruction: String

    @JsonIgnore
    var icon: Bitmap?

    interface RecipeClickListener {
        fun onClick(recipe: Recipe?)
    }

    init {
        this.name = capitalize(name)
        this.cookingTime = cookingTime
        this.categoryId = categoryId
        this.instruction = instruction
        icon = getImage(iconBytes)
    }
}