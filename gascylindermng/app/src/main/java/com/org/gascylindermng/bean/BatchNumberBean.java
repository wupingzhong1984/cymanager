package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BatchNumberBean implements Serializable {

    @SerializedName("id")
    private String batchNumberId;

    @SerializedName("batchNumber") //批次号
    private String batchNumber;

    @SerializedName("productDate") //2020-02-13 00:00:00
    private String productDate;

    @SerializedName("pureness") //纯度id  1 2 3
    private String pureness;

    @SerializedName("team") //班组 ABCDE
    private String team;

    public String getBatchNumberId() {
        return batchNumberId;
    }

    public void setBatchNumberId(String batchNumberId) {
        this.batchNumberId = batchNumberId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getPureness() {
        return pureness;
    }

    public void setPureness(String pureness) {
        this.pureness = pureness;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return "BatchNumberBean{" +
                "batchNumberId='" + batchNumberId + '\'' +
                ", batchNumber='" + batchNumber + '\'' +
                ", productDate='" + productDate + '\'' +
                ", pureness='" + pureness + '\'' +
                ", team='" + team + '\'' +
                '}';
    }
}
