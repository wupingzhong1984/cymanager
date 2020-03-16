package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;
import com.loopj.android.http.PreemptiveAuthorizationHttpRequestInterceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    @SerializedName("pureness")
    private String pureness; //纯度id  1 2 3

    @SerializedName("team")
    private String team; //班组  ABCDE

    @SerializedName("cylinderIdList")
    private ArrayList<String> cylinderIdList;

    @SerializedName("yqDetectionVoList")
    private ArrayList<LinkedHashMap> cyCheckList;

//    private ArrayList<String> cylinderNumberList; //0001xxxxx

    private ArrayList<CylinderInfoBean> cyInfoList;

    private ArrayList<CyChargeCheckRecordBean> cyCheckRecordList;

    public ChargeMissionBean(){

        this.cyInfoList = new ArrayList<CylinderInfoBean>();
        this.cyCheckRecordList = new ArrayList<CyChargeCheckRecordBean>();
    }


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

    public ArrayList<String> getCylinderIdList() {
        return cylinderIdList;
    }

    public void setCylinderIdList(ArrayList<String> cylinderIdList) {
        this.cylinderIdList = cylinderIdList;
    }

    public ArrayList<LinkedHashMap> getCyCheckList() {
        return cyCheckList;
    }

    public void setCyCheckList(ArrayList<LinkedHashMap> cyCheckList) {
        this.cyCheckList = cyCheckList;
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

    public ArrayList<CylinderInfoBean> getCyInfoList() {
        return cyInfoList;
    }

    public void setCyInfoList(ArrayList<CylinderInfoBean> cyInfoList) {
        this.cyInfoList = cyInfoList;
    }

//    public ArrayList<String> getCylinderNumberList() {
//        return cylinderNumberList;
//    }
//
//    public void setCylinderNumberList(ArrayList<String> cylinderNumberList) {
//        this.cylinderNumberList = cylinderNumberList;
//    }


    public ArrayList<CyChargeCheckRecordBean> getCyCheckRecordList() {
        return cyCheckRecordList;
    }

    public void setCyCheckRecordList(ArrayList<CyChargeCheckRecordBean> cyCheckRecordList) {
        this.cyCheckRecordList = cyCheckRecordList;
    }

    @Override
    public String toString() {
        return "ChargeMissionBean{" +
                "missionId='" + missionId + '\'' +
                ", mediemId='" + mediemId + '\'' +
                ", mediemName='" + mediemName + '\'' +
                ", cylinderCount='" + cylinderCount + '\'' +
                ", status='" + status + '\'' +
                ", beginDate='" + beginDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", remark='" + remark + '\'' +
                ", productionBatch='" + productionBatch + '\'' +
                ", pureness='" + pureness + '\'' +
                ", team='" + team + '\'' +
                ", cylinderIdList=" + cylinderIdList +
                ", cyCheckList=" + cyCheckList +
 //               ", cylinderNumberList=" + cylinderNumberList +
                ", cyInfoList=" + cyInfoList +
                '}';
    }
}