package com.example.nagato.simplezhihudaily.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nagato hanj
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "daily_news_lists";
    public static final String ID = "_id";
    public static final String DATE = "date";
    public static final String CONTENT = "content";
    private static final String DATABASE_CREATE
            = "CREATE TABLE " + TABLE_NAME
            + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DATE + " CHAR(8) UNIQUE, "
            + CONTENT + " TEXT NOT NULL);";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
