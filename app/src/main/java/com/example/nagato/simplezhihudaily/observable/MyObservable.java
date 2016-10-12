package com.example.nagato.simplezhihudaily.observable;



import android.text.Html;

import com.annimon.stream.Optional;
import com.example.nagato.simplezhihudaily.db.bean.DailyStuff;
import com.example.nagato.simplezhihudaily.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by nagato hanj
 */
public class MyObservable {
    private static final Type type=new TypeToken<List<DailyStuff>>(){}.getType();
    static Observable<String> getHtml(String url) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(HttpUtils.get(url));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static Observable<String> getHtml(String url, String suffix) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(HttpUtils.get(url, suffix));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static Observable<String> getHtml(String url, int suffix) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(HttpUtils.get(url, suffix));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static Observable<String> getHtml(String baseUrl, String key, String value) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(HttpUtils.get(baseUrl, key, value));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static <T> Observable<T> toNonempty(Observable<Optional<T>> optionalObservable) {
        return optionalObservable.filter(Optional::isPresent).map(Optional::get);
    }

    static Observable<List<DailyStuff>> toDailyListObservable(Observable<String> htmlObservable) {
        return htmlObservable
                .map(MyObservable::decodeHtml)
                .flatMap(MyObservable::toJSONObject)
                .flatMap(MyObservable::getDailyNewsJSONArray)
                .map(MyObservable::reflectNewsListFromJSON);
    }

    private static Observable<JSONObject> toJSONObject(String data) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(new JSONObject(data));
                subscriber.onCompleted();
            } catch (JSONException e) {
                subscriber.onError(e);
            }
        });
    }

    private static Observable<JSONArray> getDailyNewsJSONArray(JSONObject dailyNewsJsonObject) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(dailyNewsJsonObject.getJSONArray("news"));
                subscriber.onCompleted();
            } catch (JSONException e) {
                subscriber.onError(e);
            }
        });
    }

    private static List<DailyStuff> reflectNewsListFromJSON(JSONArray newsListJsonArray) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(newsListJsonArray.toString(), type);
    }

    private static String decodeHtml(String in) {
        return Html.fromHtml(Html.fromHtml(in).toString()).toString();
    }

}
