package com.example.nagato.simplezhihudaily.db.bean;

/**
 * Created by nagato hanj
 */
public class Question {
    private final static String ZHIHU_QUESTION_URL_FLAG= "http://www.zhihu.com/question/";
    private String title;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    //避免出现非标准格式html
    public boolean isValidZhihuQuestion() {
        return url != null && url.startsWith(ZHIHU_QUESTION_URL_FLAG);
    }


}
