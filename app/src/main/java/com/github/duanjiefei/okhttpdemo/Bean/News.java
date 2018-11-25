package com.github.duanjiefei.okhttpdemo.Bean;

import java.util.ArrayList;

public class News {
    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public ArrayList<NewsData> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<NewsData> arrayList) {
        this.arrayList = arrayList;
    }

    private String stat;
    private ArrayList<NewsData> arrayList;
}
