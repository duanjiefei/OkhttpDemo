package com.github.duanjiefei.okhttpdemo.Bean;

public class NewsBody {
    private String resason;

    public String getResason() {
        return resason;
    }

    public void setResason(String resason) {
        this.resason = resason;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    private News news;
    private String error_code;
}
