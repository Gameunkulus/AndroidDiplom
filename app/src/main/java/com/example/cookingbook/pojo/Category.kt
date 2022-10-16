package com.example.cookingbook.pojo

import android.graphics.Bitmap
import com.example.cookingbook.helper.BitmapHelper.getImage
import com.example.cookingbook.helper.StringsUtils.capitalize
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty


class Category @JsonCreator constructor(
    @param:JsonProperty("id") var id: Long,
    @JsonProperty("name") name: String?,
    @JsonProperty("icon") iconBytes: ByteArray?
) {
    var name: String?

    @JsonIgnore
    var icon: Bitmap?

    interface CategoryClickListener {
        fun onClick(category: Category?)
    }

    init {
        this.name = capitalize(name)
        icon = getImage(iconBytes)
    }
}
