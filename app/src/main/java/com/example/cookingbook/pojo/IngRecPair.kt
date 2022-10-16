package com.example.cookingbook.pojo

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


class IngRecPair @JsonCreator constructor(
    @param:JsonProperty("id") var id: Long,
    @param:JsonProperty("resId") var recId: Long,
    @param:JsonProperty("ingId") var ingId: Long,
    @param:JsonProperty("quantity") var quantity: String
)