package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckItemBean implements Serializable {


    private String title;

    private String apiParam;

    private boolean state = true; //false NO， true OK


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }


    public String getApiParam() {
        return apiParam;
    }

    public void setApiParam(String apiParam) {
        this.apiParam = apiParam;
    }

    @Override
    public String toString() {
        return "CheckItemBean{" +
                "title='" + title + '\'' +
                ", apiParam='" + apiParam + '\'' +
                ", state=" + state +
                '}';
    }
}
