package com.example.nagato.simplezhihudaily.observable;

import android.util.Log;

import com.example.nagato.simplezhihudaily.db.Dailydatabase;
import com.example.nagato.simplezhihudaily.db.bean.DailyStuff;
import com.example.nagato.simplezhihudaily.global.MyApplication;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by nagato hanj
 */
public class SaveDataObservable {

    public static void saveDailylist(List<DailyStuff> dailyList){
        saveData(dailyList)
                .subscribeOn(Schedulers.computation())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String date) {
                        Log.d("saveDailylistdata",date);
                    }
                });

    }




    private static Observable<String> saveData(final List<DailyStuff> dailyList){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(saveNewsList(dailyList));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static String saveNewsList(List<DailyStuff> dailyList)throws IOException {
        Dailydatabase dataSource = MyApplication.getmDailydatabase();
        String date = dailyList.get(0).getDate();

        List<DailyStuff> originalData = dataSource.query(date);

        if (originalData == null ) {
            dataSource.insert(date, new GsonBuilder().create().toJson(dailyList));
        }else if(!originalData.equals(dailyList)){
            dataSource.update(date, new GsonBuilder().create().toJson(dailyList));
        }
        return date;
    }
    
}
