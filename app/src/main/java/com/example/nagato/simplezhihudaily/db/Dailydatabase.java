package com.example.nagato.simplezhihudaily.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nagato.simplezhihudaily.db.bean.DailyStuff;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by nagato hanj
 */
public final class Dailydatabase {
    private static final String DATABASE_NAME = "simpleZhihuDaily.db";
    private static final Type type=new TypeToken<List<DailyStuff>>(){}.getType();
    private DBHelper DBHelper;
    private SQLiteDatabase mdatabase;

    public Dailydatabase(Context context) {
        DBHelper =new DBHelper(context,DATABASE_NAME,null,1);
        mdatabase= DBHelper.getWritableDatabase();
    }

    public List<DailyStuff> insert(String date, String content){
        ContentValues values=new ContentValues();
        values.put(DBHelper.DATE ,date);
        values.put(DBHelper.CONTENT,content);
        long rowid=mdatabase.insert(DBHelper.TABLE_NAME,null,values);
        Cursor cursor=mdatabase.query(DBHelper.TABLE_NAME,new String[]{DBHelper.ID,DBHelper.DATE,DBHelper.CONTENT},
                DBHelper.ID+"="+rowid,null,null,null,null);
        return ListByCursor(cursor);
    }

    public void update(String date,String content){
        ContentValues values=new ContentValues();
        values.put(DBHelper.DATE,date);
        values.put(DBHelper.CONTENT,content);
        mdatabase.update(DBHelper.TABLE_NAME, values,DBHelper.DATE+"="+date,null);
    }

    public List<DailyStuff> query(String date){
        Cursor cursor=mdatabase.query(DBHelper.TABLE_NAME,new String[]{DBHelper.ID,DBHelper.DATE,DBHelper.CONTENT},
                DBHelper.DATE+"="+date,null,null,null,null);
        return ListByCursor(cursor);
    }

    public void insertOrUpdateNewsList(String date, String content) {
        if (query(date) != null) {
            update(date, content);
        } else {
            insert(date, content);
        }
    }



    private List<DailyStuff> ListByCursor(Cursor cursor) {
        if (null!=cursor) {
            if(cursor.moveToFirst()){
                List<DailyStuff> list = new GsonBuilder().create().fromJson(cursor.getString(2),type);
                cursor.close();
                return list;
            } else{
                cursor.close();
                return null;
            }
        }else{
            return null;
        }
    }
}
