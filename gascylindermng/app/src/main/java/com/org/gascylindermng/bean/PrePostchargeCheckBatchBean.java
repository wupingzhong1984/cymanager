package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PrePostchargeCheckBatchBean implements Serializable {

    @SerializedName("date")
    private String date;

    @SerializedName("cylinderCount")
    private String cylinderCount;

    @SerializedName("empty")
    private String empty;

    @SerializedName("batch")
    private String batch;

    @SerializedName("remark")
    private String remark;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCylinderCount() {
        return cylinderCount;
    }

    public void setCylinderCount(String cylinderCount) {
        this.cylinderCount = cylinderCount;
    }

    public String getEmpty() {
        return empty;
    }

    public void setEmpty(String empty) {
        this.empty = empty;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "PrePostchargeCheckBatchBean{" +
                "date='" + date + '\'' +
                ", cylinderCount='" + cylinderCount + '\'' +
                ", empty='" + empty + '\'' +
                ", batch='" + batch + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
