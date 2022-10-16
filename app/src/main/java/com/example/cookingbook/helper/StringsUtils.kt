package com.example.cookingbook.helper

import java.util.*

object StringsUtils {
    fun capitalize(str: String?): String? {
        return if (str == null || str.length == 0) {
            str
        } else str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1)
    }
}