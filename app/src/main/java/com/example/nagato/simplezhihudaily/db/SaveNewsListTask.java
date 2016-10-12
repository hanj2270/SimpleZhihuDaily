package com.example.nagato.simplezhihudaily.db;

import android.os.AsyncTask;

import com.example.nagato.simplezhihudaily.db.bean.DailyStuff;
import com.example.nagato.simplezhihudaily.global.MyApplication;
import com.google.gson.GsonBuilder;

import java.util.List;

public class SaveNewsListTask extends AsyncTask<Void, Void, Void> {
    private List<DailyStuff> newsList;

    public SaveNewsListTask(List<DailyStuff> newsList) {
        this.newsList = newsList;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (newsList != null && newsList.size() > 0) {
            saveNewsList(newsList);
        }

        return null;
    }

    private void saveNewsList(List<DailyStuff> newsList) {
        Dailydatabase dataSource = MyApplication.getmDailydatabase();
        String date = newsList.get(0).getDate();

        List<DailyStuff> originalData = dataSource.query(date);

        if (originalData == null || !originalData.equals(newsList)) {
            dataSource.insertOrUpdateNewsList(date, new GsonBuilder().create().toJson(newsList));
        }
    }
}