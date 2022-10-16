package com.example.cookingbook.dao

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.core.content.PackageManagerCompat


class DBShopListHelper(context: Context?) : DBHelper(context) {
    val all: List<String>
        get() {
            val db: SQLiteDatabase = getReadableDatabase()
            val FIND_QUERY = java.lang.String.format("SELECT * FROM %s", TABLE_SHOP_LIST)
            val c = db.rawQuery(FIND_QUERY, null)
            val lines = ArrayList<String>()
            bindLines(c, lines)
            db.close()
            return lines
        }

    @SuppressLint("Range", "RestrictedApi")
    private fun bindLines(c: Cursor, lines: ArrayList<String>) {
        try {
            if (c.moveToFirst()) {
                do {
                    val name = c.getString(c.getColumnIndex(SHOP_LIST_NAME))
                    lines.add(name)
                } while (c.moveToNext())
            }
        } catch (ex: Exception) {
            Log.e(PackageManagerCompat.LOG_TAG, "bindLines error!")
        } finally {
            c.close()
        }
    }

    fun add(ing: String?) {
        if (ing == null) return
        val db: SQLiteDatabase = getWritableDatabase()
        val sql = "INSERT OR REPLACE INTO " + TABLE_SHOP_LIST.toString() + " VALUES (?);"
        val statement = db.compileStatement(sql)
        try {
            db.beginTransaction()
            statement.bindString(1, ing)
            statement.execute()
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            Log.e(PackageManagerCompat.LOG_TAG, "Ошибка при обновлении списка покупок", ex)
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun remove(ing: String) {
        val db: SQLiteDatabase = getWritableDatabase()
        try {
            if (db.delete(
                    TABLE_SHOP_LIST,
                    SHOP_LIST_NAME.toString() + "='" + ing + "'",
                    null
                ) != 1
            ) {
                throw Exception(String.format("Not deleted ing = %s", ing))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }
}