package com.example.cookingbook.dao


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import android.graphics.Bitmap
import android.util.Log
import com.cookbook.helpers.BitmapHelper


open class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, 1) {
    protected fun bindBitmapOrNull(statement: SQLiteStatement, index: Int, img: Bitmap?) {
        if (img == null) statement.bindNull(index) else statement.bindBlob(
            index,
            BitmapHelper.getBytes(img)
        )
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(LOG_TAG, "--- onCreate database ---")
        db.execSQL(CREATE_CATEGORIES_TABLE)
        db.execSQL(CREATE_INGREDIENTS_TABLE)
        db.execSQL(CREATE_RECIPES_TABLE)
        db.execSQL(CREATE_IR_TABLE)
        db.execSQL(CREATE_SHOP_LIST_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_CATEGORIES_TABLE)
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_INGREDIENTS_TABLE)
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_RECIPES_TABLE)
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_IR_TABLE)
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_SHOP_LIST_TABLE)
    }

    fun clearAll() {
        clearTable(TABLE_IR)
        clearTable(TABLE_RECIPES)
        clearTable(TABLE_INGREDIENTS)
        clearTable(TABLE_CATEGORIES)
        clearTable(TABLE_SHOP_LIST)
    }

    protected fun clearTable(tableName: String) {
        val db = writableDatabase
        db.execSQL("delete from $tableName")
    }

    companion object {
        // Названия таблиц
        @JvmStatic
        protected const val TABLE_CATEGORIES = "Categories"
        protected const val TABLE_INGREDIENTS = "Ingredients"
        @JvmStatic
        protected const val TABLE_RECIPES = "Recipes"
        @JvmStatic
        protected const val TABLE_IR = "IngRec"
        @JvmStatic
        protected const val TABLE_SHOP_LIST = "ShopList"

        // Заголовки полей таблиц
        @JvmStatic
        protected const val   //Категории
                CATEGORY_ID = "cat_id"
        @JvmStatic
        protected const val CATEGORY_CAPTION = "caption"
        @JvmStatic
        protected const val CATEGORY_ICON = "icon"

        //Ингредиенты
        protected const val ING_ID = "ing_id"
        protected const val ING_CAPTION = "ing_caption"

        //Рецепты
        @JvmStatic
        protected const val RECIPE_ID = "rec_id"
        @JvmStatic
        protected const val RECIPE_CAPTION = "rec_caption"
        @JvmStatic
        protected const val RECIPE_TIME = "rec_time"
        @JvmStatic
        protected const val RECIPE_CATEGORY_ID = "rec_category_id"
        @JvmStatic
        protected const val RECIPE_ICON = "rec_icon"
        @JvmStatic
        protected const val RECIPE_INSTRUCTION = "rec_instruction"

        //Связь Ингредиент - рецепт
        protected const val IR_ID = "ir_id"
        @JvmStatic
        protected const val IR_ING_ID = "ir_ing_id"
        @JvmStatic
        protected const val IR_REC_ID = "ir_rec_id"
        protected const val IR_QUANTITY = "ir_quantity"

        //Список покупок
        @JvmStatic
        protected const val SHOP_LIST_NAME = "sl_name"

        // Создание таблиц
        protected const val CREATE_CATEGORIES_TABLE = ("create table " + TABLE_CATEGORIES + " ("
                + CATEGORY_ID + " INTEGER PRIMARY KEY,"
                + CATEGORY_CAPTION + " varchar(255) NOT NULL,"
                + CATEGORY_ICON + " BLOB"
                + ");")
        protected const val CREATE_INGREDIENTS_TABLE = ("create table " + TABLE_INGREDIENTS + " ("
                + ING_ID + " INTEGER PRIMARY KEY,"
                + ING_CAPTION + " varchar(255) NOT NULL"
                + ");")
        protected const val CREATE_RECIPES_TABLE = ("create table " + TABLE_RECIPES + " ("
                + RECIPE_ID + " INTEGER PRIMARY KEY,"
                + RECIPE_CAPTION + " varchar(255) NOT NULL,"
                + RECIPE_TIME + " INTEGER NOT NULL,"
                + RECIPE_CATEGORY_ID + " INTEGER NOT NULL,"
                + RECIPE_ICON + " BLOB,"
                + RECIPE_INSTRUCTION + " TEXT NOT NULL,"
                + " FOREIGN KEY (" + RECIPE_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORY_ID + ")"
                + ");")
        protected const val CREATE_IR_TABLE = ("create table " + TABLE_IR + " ("
                + IR_ID + " INTEGER PRIMARY KEY,"
                + IR_ING_ID + " INTEGER NOT NULL,"
                + IR_REC_ID + " INTEGER NOT NULL,"
                + IR_QUANTITY + " TEXT NOT NULL,"
                + "FOREIGN KEY (" + IR_ING_ID + ") REFERENCES " + TABLE_INGREDIENTS + "(" + ING_ID + "),"
                + "FOREIGN KEY (" + IR_REC_ID + ") REFERENCES " + TABLE_RECIPES + "(" + RECIPE_ID + ")"
                + ");")
        protected const val CREATE_SHOP_LIST_TABLE = ("create table " + TABLE_SHOP_LIST + " ("
                + SHOP_LIST_NAME + " varchar(255) PRIMARY KEY );")
        @JvmStatic
        protected const val LOG_TAG = "dbCookbook"
        const val DB_NAME = "CookBook"
    }
}