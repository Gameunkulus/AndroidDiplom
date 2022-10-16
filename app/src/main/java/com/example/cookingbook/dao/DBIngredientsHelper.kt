package com.example.cookingbook.dao


import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.util.Pair
import androidx.core.content.PackageManagerCompat
import com.example.cookingbook.pojo.IngRecPair
import com.example.cookingbook.pojo.Ingredient
import java.util.*


class DBIngredientsHelper(context: Context?) : DBHelper(context) {
    var dbRecipesHelper: DBRecipesHelper

    /**
     * Возвращает список пар: ингредиент - его количество для указанного рецепта
     */
    fun getByRecipeId(recId: Long): List<Pair<Ingredient, String>>? {
        if (recId < 0) return null
        val db: SQLiteDatabase = getReadableDatabase()
        val FIND_QUERY =
            java.lang.String.format("SELECT * FROM %s WHERE %s = '%d'", TABLE_IR, IR_REC_ID, recId)
        val c = db.rawQuery(FIND_QUERY, null)
        val pairs: ArrayList<Pair<Ingredient, String>> = ArrayList<Pair<Ingredient, String>>()
        bindPairs(c, pairs)
        db.close()
        return pairs
    }

    fun getById(ingId: Long): Ingredient? {
        if (ingId < 0) return null
        val db: SQLiteDatabase = getReadableDatabase()
        val FIND_QUERY = java.lang.String.format(
            "SELECT * FROM %s WHERE %s = '%d'",
            TABLE_INGREDIENTS,
            ING_ID,
            ingId
        )
        val c = db.rawQuery(FIND_QUERY, null)
        val ings: ArrayList<Ingredient> = ArrayList<Ingredient>()
        bindIng(c, ings)
        db.close()
        return ings[0]
    }

    fun getByName(name: String): List<Ingredient> {
        if (name == "") return ArrayList<Ingredient>()
        val db: SQLiteDatabase = getReadableDatabase()
        val FIND_QUERY = java.lang.String.format(
            "SELECT * FROM %s WHERE %s like '%s%%'",
            TABLE_INGREDIENTS,
            ING_CAPTION,
            name.lowercase(Locale.getDefault())
        )
        val c = db.rawQuery(FIND_QUERY, null)
        val ings: ArrayList<Ingredient> = ArrayList<Ingredient>()
        bindIng(c, ings)
        db.close()
        return ings
    }

    fun addOrReplace(i: Ingredient?) {
        addOrReplace(object : ArrayList<Ingredient?>() {
            init {
                add(i)
            }
        })
    }

    @SuppressLint("RestrictedApi")
    fun addOrReplace(ingredients: List<Ingredient?>?) {
        if (ingredients == null) return
        val db: SQLiteDatabase = getWritableDatabase()
        val sql = "INSERT OR REPLACE INTO " + TABLE_INGREDIENTS.toString() + " VALUES (?,?);"
        val statement = db.compileStatement(sql)
        try {
            db.beginTransaction()
            for (i in ingredients) {
                statement.clearBindings()
                if (i != null) {
                    statement.bindLong(1, i.id)
                    statement.bindString(2, i.caption)
                }
                statement.execute()
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            Log.e(PackageManagerCompat.LOG_TAG, "Ошибка при обновлении ингредиента")
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    @SuppressLint("RestrictedApi")
    fun addOrReplacePairs(pairs: List<IngRecPair?>?) {
        if (pairs == null) return
        val db: SQLiteDatabase = getWritableDatabase()
        val sql = "INSERT OR REPLACE INTO " + TABLE_IR.toString() + " VALUES (?,?,?,?);"
        val statement = db.compileStatement(sql)
        try {
            db.beginTransaction()
            for (p in pairs) {
                statement.clearBindings()
                if (p != null) {
                    statement.bindLong(1, p.id)
                    statement.bindLong(2, p.ingId)
                    statement.bindLong(3, p.recId)
                    statement.bindString(4, p.quantity)
                }
                statement.execute()
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            Log.e(PackageManagerCompat.LOG_TAG, "Ошибка при обновлении пар ингредиент - рецепт")
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    @SuppressLint("Range", "RestrictedApi")
    private fun bindPairs(c: Cursor, pairs: ArrayList<Pair<Ingredient, String>>) {
        try {
            if (c.moveToFirst()) {
                do {
                    val quantity = c.getString(c.getColumnIndex(IR_QUANTITY))
                    val ingId = c.getLong(c.getColumnIndex(IR_ING_ID))
                    val ing: Ingredient? = getById(ingId)
                    pairs.add(Pair<Ingredient?, String>(ing, quantity))
                } while (c.moveToNext())
            }
        } catch (ex: Exception) {
            Log.e(PackageManagerCompat.LOG_TAG, "bindPairs error!")
        } finally {
            c.close()
        }
    }

    @SuppressLint("Range", "RestrictedApi")
    private fun bindIng(c: Cursor, ingredients: ArrayList<Ingredient>) {
        try {
            if (c.moveToFirst()) {
                do {
                    val id = c.getLong(c.getColumnIndex(ING_ID))
                    val name = c.getString(c.getColumnIndex(ING_CAPTION))
                    ingredients.add(Ingredient(id, name))
                } while (c.moveToNext())
            }
        } catch (ex: Exception) {
            Log.e(PackageManagerCompat.LOG_TAG, "bindIng error!")
        } finally {
            c.close()
        }
    }

    init {
        dbRecipesHelper = DBRecipesHelper(context)
    }
}