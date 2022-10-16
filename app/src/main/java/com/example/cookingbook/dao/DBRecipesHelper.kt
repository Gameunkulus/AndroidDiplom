package com.example.cookingbook.dao


import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.Log
import androidx.core.content.PackageManagerCompat
import com.cookbook.pojo.Recipe


open class DBRecipesHelper(context: Context?) : DBHelper(context) {
    fun addOrUpdate(recipes: List<Recipe?>?) {
        if (recipes == null) return
        val db: SQLiteDatabase = getWritableDatabase()
        val sql = "INSERT OR REPLACE INTO " + TABLE_RECIPES.toString() + " VALUES (?,?,?,?,?,?);"
        val statement = db.compileStatement(sql)
        try {
            db.beginTransaction()
            for (r in recipes) {
                statement.clearBindings()
                statement.bindLong(1, r.id)
                statement.bindString(2, r.name)
                statement.bindLong(3, r.cookingTime)
                statement.bindLong(4, r.categoryId)
                bindBitmapOrNull(statement, 5, r.icon)
                statement.bindString(6, r.instruction)
                statement.execute()
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            Log.e(PackageManagerCompat.LOG_TAG, "Ошибка при обновлении рецептов")
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getByCategory(categoryId: Long): List<Recipe>? {
        if (categoryId < 0) return null
        val db: SQLiteDatabase = getReadableDatabase()
        val FIND_QUERY = java.lang.String.format(
            "SELECT * FROM %s WHERE %s = '%d'",
            TABLE_RECIPES,
            RECIPE_CATEGORY_ID,
            categoryId
        )
        val c = db.rawQuery(FIND_QUERY, null)
        val recipes: ArrayList<Recipe> = ArrayList<Recipe>()
        bindRecipes(c, recipes)
        db.close()
        return recipes
    }

    val count: Long
        get() {
            val db: SQLiteDatabase = getReadableDatabase()
            return DatabaseUtils.queryNumEntries(db, TABLE_RECIPES)
        }

    fun getById(recipeId: Long): Recipe? {
        if (recipeId < 0) return null
        val db: SQLiteDatabase = getReadableDatabase()
        val FIND_QUERY = java.lang.String.format(
            "SELECT * FROM %s WHERE %s = '%d'",
            TABLE_RECIPES,
            RECIPE_ID,
            recipeId
        )
        val c = db.rawQuery(FIND_QUERY, null)
        val recipes: ArrayList<Recipe> = ArrayList<Recipe>()
        bindRecipes(c, recipes)
        if (recipes.size != 1) {
            Log.e(
                PackageManagerCompat.LOG_TAG,
                String.format("В базе найдено %d рецептов с id = %d", recipes.size, recipeId)
            )
        }
        db.close()
        return recipes[0]
    }

    fun getById(ids: List<Long?>?): List<Recipe>? {
        if (ids == null) return null
        val idsLine = TextUtils.join(", ", ids)
        val db: SQLiteDatabase = getReadableDatabase()
        val FIND_QUERY = java.lang.String.format(
            "SELECT * FROM %s WHERE %s IN (%s)",
            TABLE_RECIPES,
            RECIPE_ID,
            idsLine
        )
        val c = db.rawQuery(FIND_QUERY, null)
        val recipes: ArrayList<Recipe> = ArrayList<Recipe>()
        bindRecipes(c, recipes)
        db.close()
        return recipes
    }

    fun addOrUpdate(r: Recipe?) {
        addOrUpdate(object : ArrayList<Recipe?>() {
            init {
                add(r)
            }
        })
    }

    protected fun bindRecipes(c: Cursor, recipes: ArrayList<Recipe>) {
        try {
            if (c.moveToFirst()) {
                do {
                    val id = c.getLong(c.getColumnIndex(RECIPE_ID))
                    val name = c.getString(c.getColumnIndex(RECIPE_CAPTION))
                    val time = c.getInt(c.getColumnIndex(RECIPE_TIME))
                    val catId = c.getLong(c.getColumnIndex(RECIPE_CATEGORY_ID))
                    val instruction = c.getString(c.getColumnIndex(RECIPE_INSTRUCTION))
                    val iconBytes = c.getBlob(c.getColumnIndex(RECIPE_ICON))
                    recipes.add(Recipe(id, name, time, catId, instruction, iconBytes))
                } while (c.moveToNext())
            }
        } catch (ex: Exception) {
            Log.e(PackageManagerCompat.LOG_TAG, "bindRecipes error!")
        } finally {
            c.close()
        }
    }
}