package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CyCategoryBean implements Serializable {


    @SerializedName("cylinderTypeId")
    private String categoryId;

    @SerializedName("cylinderTypeName")
    private String categoryName;

    private ArrayList<CyMediumBean> cyMediumList;

    public ArrayList<CyMediumBean> getCyMediumList() {
        return cyMediumList;
    }

    public void setCyMediumList(ArrayList<CyMediumBean> cyMediumList) {
        this.cyMediumList = cyMediumList;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
