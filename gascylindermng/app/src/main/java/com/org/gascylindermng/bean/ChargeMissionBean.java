package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;
import com.loopj.android.http.PreemptiveAuthorizationHttpRequestInterceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class ChargeMissionBean implements Serializable {

    @SerializedName("id")
    private String missionId;

    @SerializedName("mediemId")
    private String mediemId;

    @SerializedName("mediemName")
    private String mediemName;

    @SerializedName("cylinderNumber")
    private String cylinderCount;

    @SerializedName("status")
    private String status; //1，充气中；2，已完成

    @SerializedName("beginDate")
    private String beginDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("remark")
    private String remark;

    @SerializedName("productionBatch")
    private String productionBatch;


    private ArrayList<String> cyPlatformIdList;


    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getMediemId() {
        return mediemId;
    }

    public void setMediemId(String mediemId) {
        this.mediemId = mediemId;
    }

    public String getMediemName() {
        return mediemName;
    }

    public void setMediemName(String mediemName) {
        this.mediemName = mediemName;
    }

    public String getCylinderCount() {
        return cylinderCount;
    }

    public void setCylinderCount(String cylinderCount) {
        this.cylinderCount = cylinderCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProductionBatch() {
        return productionBatch;
    }

    public void setProductionBatch(String productionBatch) {
        this.productionBatch = productionBatch;
    }


    public ArrayList<String> getCyPlatformIdList() {
        return cyPlatformIdList;
    }

    public void setCyPlatformIdList(ArrayList<String> cyPlatformIdList) {
        this.cyPlatformIdList = cyPlatformIdList;
    }
}