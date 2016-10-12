package com.example.nagato.simplezhihudaily.observable;

import android.text.TextUtils;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.example.nagato.simplezhihudaily.db.bean.DailyStuff;
import com.example.nagato.simplezhihudaily.db.bean.Question;
import com.example.nagato.simplezhihudaily.db.bean.Story;
import com.example.nagato.simplezhihudaily.global.MyApplication;
import com.example.nagato.simplezhihudaily.utils.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by nagato hanj
 */
public class DataSourseObservable {
    private static final String QUESTION_SELECTOR = "div.question";
    private static final String QUESTION_TITLES_SELECTOR = "h2.question-title";
    private static final String QUESTION_LINKS_SELECTOR = "div.view-more a";

    public static Observable<List<DailyStuff>> dailyStuffFromSearch(String keywords){
        return MyObservable.toDailyListObservable(MyObservable.getHtml(API.SEARCH,"q",keywords));
    }

    public static Observable<List<DailyStuff>> dailyStuffFromDatabase(final String date){
        return Observable.create(new Observable.OnSubscribe<List<DailyStuff>>() {
            @Override
            public void call(Subscriber<? super List<DailyStuff>> subscriber) {
                List<DailyStuff> daliyList= MyApplication.getmDailydatabase().query(date);
                if(daliyList!=null){
                    subscriber.onNext(daliyList);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static Observable<List<DailyStuff>> dailyStuffFromZhihu(String date){
        Observable<Story> stories = MyObservable.getHtml(API.ZHIHU_DAILY_BEFORE, date)
                .flatMap(DataSourseObservable::getStoriesJsonArrayObservable)
                .flatMap(DataSourseObservable::getStoriesObservable);

        Observable<Document> documents = stories
                .flatMap(DataSourseObservable::getDocumentObservable);

        Observable<Optional<android.util.Pair<Story, Document>>> optionalStoryNDocuments
                = Observable.zip(stories, documents, DataSourseObservable::createPair);

        Observable<android.util.Pair<Story, Document>> storyNDocuments = MyObservable.toNonempty(optionalStoryNDocuments);

        return MyObservable.toNonempty(storyNDocuments.map(DataSourseObservable::convertToDailyNews))
                .doOnNext(news -> news.setDate(date))
                .toList();
    }

    private static Observable<JSONArray> getStoriesJsonArrayObservable(String html) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(new JSONObject(html).getJSONArray("stories"));
                subscriber.onCompleted();
            } catch (JSONException e) {
                subscriber.onError(e);
            }
        });
    }

    private static Observable<Story> getStoriesObservable(JSONArray newsArray) {
        return Observable.create(subscriber -> {
            try {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject newsJson = newsArray.getJSONObject(i);
                    subscriber.onNext(getStoryFromJSON(newsJson));
                }

                subscriber.onCompleted();
            } catch (JSONException e) {
                subscriber.onError(e);
            }
        });
    }

    private static Story getStoryFromJSON(JSONObject jsonStory) throws JSONException {
        Story story = new Story();

        story.setStoryId(jsonStory.getInt("id"));
        story.setDailyTitle(jsonStory.getString("title"));
        story.setThumbnailUrl(getThumbnailUrlForStory(jsonStory));

        return story;
    }

    private static String getThumbnailUrlForStory(JSONObject jsonStory) throws JSONException {
        if (jsonStory.has("images")) {
            return (String) jsonStory.getJSONArray("images").get(0);
        } else {
            return null;
        }
    }

    private static Observable<Document> getDocumentObservable(Story news) {
        return MyObservable.getHtml(API.ZHIHU_DAILY_OFFLINE_NEWS, news.getStoryId())
                .map(DataSourseObservable::getStoryDocument);
    }

    private static Document getStoryDocument(String json) {
        try {
            JSONObject newsJson = new JSONObject(json);
            return newsJson.has("body") ? Jsoup.parse(newsJson.getString("body")) : null;
        } catch (JSONException e) {
            return null;
        }
    }

    private static Optional<android.util.Pair<Story, Document>> createPair(Story story, Document document) {
        return Optional.ofNullable(document == null ? null : android.util.Pair.create(story, document));
    }

    private static Optional<DailyStuff> convertToDailyNews(android.util.Pair<Story, Document> pair) {
        DailyStuff result = null;

        Story story = pair.first;
        Document document = pair.second;
        String dailyTitle = story.getDailyTitle();

        List<Question> questions = getQuestions(document, dailyTitle);
        if (Stream.of(questions).allMatch(Question::isValidZhihuQuestion)) {
            result = new DailyStuff();

            result.setDailyTitle(dailyTitle);
            result.setThumbnailUrl(story.getThumbnailUrl());
            result.setQuestions(questions);
        }

        return Optional.ofNullable(result);
    }

    private static List<Question> getQuestions(Document document, String dailyTitle) {
        List<Question> result = new ArrayList<>();
        Elements questionElements = getQuestionElements(document);

        for (Element questionElement : questionElements) {
            Question question = new Question();

            String questionTitle = getQuestionTitleFromQuestionElement(questionElement);
            String questionUrl = getQuestionUrlFromQuestionElement(questionElement);
            // Make sure that the question's title is not empty.
            questionTitle = TextUtils.isEmpty(questionTitle) ? dailyTitle : questionTitle;

            question.setTitle(questionTitle);
            question.setUrl(questionUrl);

            result.add(question);
        }

        return result;
    }

    private static Elements getQuestionElements(Document document) {
        return document.select(QUESTION_SELECTOR);
    }

    private static String getQuestionTitleFromQuestionElement(Element questionElement) {
        Element questionTitleElement = questionElement.select(QUESTION_TITLES_SELECTOR).first();

        if (questionTitleElement == null) {
            return null;
        } else {
            return questionTitleElement.text();
        }
    }

    private static String getQuestionUrlFromQuestionElement(Element questionElement) {
        Element viewMoreElement = questionElement.select(QUESTION_LINKS_SELECTOR).first();

        if (viewMoreElement == null) {
            return null;
        } else {
            return viewMoreElement.attr("href");
        }
    }
}
