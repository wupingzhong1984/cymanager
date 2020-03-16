package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CyChargeCheckRecordBean implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("detectionMissionId")
    private String missionId;

    @SerializedName("cylinderId")
    private String cylinderId;

    @SerializedName("cylinderNumber")
    private String cylinderNumber;

    @SerializedName("mediemName")
    private String mediemName;

    @SerializedName("pureness")
    private String pureness;

    @SerializedName("team")
    private String team;

    @SerializedName("productionBatch")
    private String productionBatch;

    @SerializedName("ifPass")
    private String ifPass;

    @SerializedName("remark")
    private String remark;

    @SerializedName("companyAreaId")
    private String nextAreaId;

    @SerializedName("areaName")
    private String nextAreaName;



    private ArrayList<CheckItemBean> checkItemResultList;
//            "beforeColor":1,
//            "beforeAppearance":1,
//            "beforeSafety":1,
//            "beforeRegularInspectionDate":1,
//            "beforeResidualPressure":1,
//            "fillingIfNormal":1,
//            "afterPressure":1,
//            "afterCheckLeak":1,
//            "afterAppearance":1,
//            "afterTemperature":1,

    public CyChargeCheckRecordBean(){

        this.checkItemResultList = new ArrayList<CheckItemBean>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getCylinderId() {
        return cylinderId;
    }

    public void setCylinderId(String cylinderId) {
        this.cylinderId = cylinderId;
    }

    public String getCylinderNumber() {
        return cylinderNumber;
    }

    public void setCylinderNumber(String cylinderNumber) {
        this.cylinderNumber = cylinderNumber;
    }

    public String getMediemName() {
        return mediemName;
    }

    public void setMediemName(String mediemName) {
        this.mediemName = mediemName;
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

    public String getProductionBatch() {
        return productionBatch;
    }

    public void setProductionBatch(String productionBatch) {
        this.productionBatch = productionBatch;
    }

    public String getIfPass() {
        return ifPass;
    }

    public void setIfPass(String ifPass) {
        this.ifPass = ifPass;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getNextAreaId() {
        return nextAreaId;
    }

    public void setNextAreaId(String nextAreaId) {
        this.nextAreaId = nextAreaId;
    }

    public String getNextAreaName() {
        return nextAreaName;
    }

    public void setNextAreaName(String nextAreaName) {
        this.nextAreaName = nextAreaName;
    }

    public ArrayList<CheckItemBean> getCheckItemResultList() {
        return checkItemResultList;
    }

    public void setCheckItemResultList(ArrayList<CheckItemBean> checkItemResultList) {
        this.checkItemResultList = checkItemResultList;
    }
}
