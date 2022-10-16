package com.example.cookingbook.dao


import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.core.content.PackageManagerCompat
import com.example.cookingbook.pojo.Ingredient
import com.example.cookingbook.pojo.Recipe
import java.util.*


class DBSearchHelper(context: Context?) : DBRecipesHelper(context) {
    class Builder(context: Context?) {
        private val instance: DBSearchHelper
        private var caption: String? = null
        private var ingredients: List<Ingredient>? = null
        private var categoryId = -1L
        private var cookingTime = -1
        fun withCaption(caption: String?): Builder {
            this.caption = caption
            return this
        }

        fun withIngredients(ingredients: List<Ingredient>?): Builder {
            this.ingredients = ingredients
            return this
        }

        fun inCategory(categoryId: Long): Builder {
            this.categoryId = categoryId
            return this
        }

        fun withCookingTime(cookingTime: Int): Builder {
            this.cookingTime = cookingTime
            return this
        }

        fun search(): Pair<Boolean, List<Long>> {
            return instance.findRecipes(caption, ingredients, categoryId, cookingTime)
        }

        init {
            instance = DBSearchHelper(context)
        }
    }

    fun findRecipes(
        recipeName: String?,
        ings: List<Ingredient>?,
        categoryId: Long,
        cookingTime: Int
    ): Pair<Boolean, List<Long>> {
        val recipes: MutableList<Recipe> =
            (ings?.let { getRecipes(it) }
                ?: getRecipes(recipeName)) as MutableList<Recipe> // находим рецепты с указанным именем или ингредиентами
        var exactlyFound = true
        val toRemove: MutableSet<Recipe> = HashSet<Recipe>()
        for (r: Recipe in recipes) {
            // если будут указаны и ингредиенты, и название рецепта, первоначальный поиск произойдет по ингредиентам
            // в таком случае, выполним фильтрацию по имени
            if (ings != null && recipeName != null && !r.name?.contains(recipeName)!!) toRemove.add(r)
            if (toRemove.size == recipes.size && ings != null) { // если после неё не осталось ниодного подходящего рецепта
                toRemove.clear() // игнорируем ограничение по имени
                exactlyFound = false //ставим соответствующий флаг
            }

            // производим поиск по доп. параметрам
            if (categoryId != -1L && r.categoryId !== categoryId) toRemove.add(r) else if (cookingTime != -1 && r.cookingTime > cookingTime) toRemove.add(
                r
            )
        }
        recipes.removeAll(toRemove)
        val result: MutableList<Long> = ArrayList()
        for (r: Recipe in recipes) {
            result.add(r.id)
        }
        return Pair(exactlyFound, result)
    }

    /**
     * поиск id рецептов по названию и с любыми ингредиентами
     */
    fun getRecipes(recipeName: String?): List<Recipe> {
        if (recipeName!!.isEmpty()) return ArrayList<Recipe>()
        val query = java.lang.String.format(
            "select * from %1\$s where %2\$s like '%%%3\$s%%' ORDER BY %2\$s",
            TABLE_RECIPES,
            RECIPE_CAPTION,
            recipeName
        )
        val db: SQLiteDatabase = getReadableDatabase()
        val c = db.rawQuery(query, null)
        val recipes: ArrayList<Recipe> = ArrayList<Recipe>()
        bindRecipes(c, recipes)
        db.close()
        return recipes
    }

    /**
     * поиск id рецептов с определенными компонентами
     */
    override fun findRecipes(ings: List<Ingredient>): List<Long> {
        val recipes: List<Recipe> = getRecipes(ings)
        val rec: MutableList<Long> = ArrayList()
        for (r: Recipe in recipes) rec.add(r.id)
        return rec
    }

    /**
     * поиска id рецептов по названию и с определенными ингредиентвми
     */
    override fun findRecipes(recipeCaption: String, ings: List<Ingredient>): List<Long> {
        val list: List<Recipe> = getRecipes(ings)
        val res: MutableList<Long> = ArrayList()
        for (r: Recipe in list) {
            if (r.name!!.lowercase(Locale.ROOT)
                    .contains(recipeCaption.lowercase(Locale.getDefault()))
            ) res.add(r.id)
        }
        return res
    }

    // поиск рецептов с любым названием, но определенным списком ингредиентов
    private fun getRecipes(ings: List<Ingredient>): MutableList<Recipe> {
        val res: ArrayList<Recipe> = ArrayList<Recipe>()
        val db: SQLiteDatabase = getReadableDatabase()
        db.execSQL("CREATE TEMP TABLE IF NOT EXISTS _ingFind(id integer)")
        for (i: Ingredient in ings) {
            db.execSQL(java.lang.String.format("insert into _ingFind(id) select %s ", i.id))
        }
        val RECIPE_FIELDS = java.lang.String.format(
            "%s, %s, %s, %s, %s, %s",
            RECIPE_ID,
            RECIPE_CAPTION,
            RECIPE_CATEGORY_ID,
            RECIPE_ICON,
            RECIPE_INSTRUCTION,
            RECIPE_TIME
        )
        val c = db.rawQuery(
            java.lang.String.format("select distinct %s from %s", RECIPE_FIELDS, TABLE_RECIPES) +
                    java.lang.String.format(
                        " join (select * from %s join _ingFind on (%s == _ingFind.id))",
                        TABLE_IR,
                        IR_ING_ID
                    ) +
                    java.lang.String.format(" on (%s == %s)", RECIPE_ID, IR_REC_ID), null
        )
        bindRecipes(c, res)
        db.execSQL("DELETE FROM _ingFind")
        db.close()
        return res
    }

    @SuppressLint("Range", "RestrictedApi")
    private fun bindLongs(c: Cursor, recipes: ArrayList<Long>) {
        try {
            if (c.moveToFirst()) {
                do {
                    val id = c.getLong(c.getColumnIndex(RECIPE_ID))
                    recipes.add(id)
                } while (c.moveToNext())
            }
        } catch (ex: Exception) {
            Log.e(PackageManagerCompat.LOG_TAG, "bindLongs error!")
        } finally {
            c.close()
        }
    }
}