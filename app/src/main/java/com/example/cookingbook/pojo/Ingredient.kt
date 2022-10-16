package com.example.cookingbook.pojo


import com.example.cookingbook.helper.StringsUtils.capitalize
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


class Ingredient @JsonCreator constructor(
    @param:JsonProperty("id") var id: Long,
    @JsonProperty("name") caption: String?
) {
    var caption: String?
    override fun equals(obj: Any?): Boolean {
        if (obj is Ingredient) {
            return obj.caption == caption
        }
        return false
    }

    interface IngredientClickListener {
        fun onClick(i: Ingredient?)
    }

    init {
        this.caption = capitalize(caption)
    }
}