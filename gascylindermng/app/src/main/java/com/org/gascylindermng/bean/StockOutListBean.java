package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StockOutListBean implements Serializable {

    @SerializedName("date")
    private String time;

    @SerializedName("ship_number")
    private String shipNumber;

    @SerializedName("cylinderCount")
    private String cylinderCount;

    @SerializedName("batch")
    private String batch;

    @SerializedName("remark")
    private String remark;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getShipNumber() {
        return shipNumber;
    }

    public void setShipNumber(String shipNumber) {
        this.shipNumber = shipNumber;
    }

    public String getCylinderCount() {
        return cylinderCount;
    }

    public void setCylinderCount(String cylinderCount) {
        this.cylinderCount = cylinderCount;
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
        return "StockOutListBean{" +
                "time='" + time + '\'' +
                ", shipNumber='" + shipNumber + '\'' +
                ", cylinderCount='" + cylinderCount + '\'' +
                ", batch='" + batch + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
