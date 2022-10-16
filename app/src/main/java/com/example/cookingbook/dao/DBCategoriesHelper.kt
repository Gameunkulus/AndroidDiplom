package com.example.cookingbook.dao


import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.cookingbook.pojo.Category


class DBCategoriesHelper(context: Context?) : DBHelper(context) {
    fun addOrUpdate(categories: List<Category?>?) {
        if (categories == null) return
        val db = writableDatabase
        val sql = "INSERT OR REPLACE INTO " + TABLE_CATEGORIES + " VALUES (?,?,?);"
        val statement = db.compileStatement(sql)
        try {
            db.beginTransaction()
            for (c in categories) {
                statement.clearBindings()
                if (c != null) {
                    statement.bindLong(1, c.id)
                }
                if (c != null) {
                    statement.bindString(2, c.name)
                }
                //statement.bindBlob(3, BitmapHelper.getBytes(c.icon));
                if (c != null) {
                    bindBitmapOrNull(statement, 3, c.icon)
                }
                statement.execute()
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "Ошибка при обновлении категории")
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun addOrUpdate(c: Category?) {
        addOrUpdate(object : ArrayList<Category?>() {
            init {
                add(c)
            }
        })
    }

    val all: List<Any>
        get() {
            val db = readableDatabase
            val FIND_REQUEST_QUERY = String.format("SELECT * FROM %s", TABLE_CATEGORIES)
            val c = db.rawQuery(FIND_REQUEST_QUERY, null)
            val categories: ArrayList<Category> = ArrayList<Category>()
            bindCategories(c, categories)
            db.close()
            return categories
        }

    @SuppressLint("Range")
    private fun bindCategories(c: Cursor, categories: ArrayList<Category>) {
        try {
            if (c.moveToFirst()) {
                do {
                    val id = c.getLong(c.getColumnIndex(CATEGORY_ID))
                    val name = c.getString(c.getColumnIndex(CATEGORY_CAPTION))
                    val iconBytes = c.getBlob(c.getColumnIndex(CATEGORY_ICON))
                    categories.add(Category(id, name, iconBytes))
                } while (c.moveToNext())
            }
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "bindCategories error!")
        } finally {
            c.close()
        }
    }
}