package com.org.gascylindermng.bean;

import com.bumptech.glide.util.Synthetic;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CylinderInfoBean implements Serializable {

    @SerializedName("id")
    private String cyId;

    @SerializedName("cylinderNumber")
    private String platformCyCode;
    @SerializedName("cylinderCode")
    private String bottleCode;
    @SerializedName("ownCode")
    private String companyRelateCode;

    @SerializedName("gasMediumName")
    private String cyMediumName;
    @SerializedName("gasMediumId")
    private String cyMediumId;
    @SerializedName("cylinderTypeName")
    private String cyCategoryName;
    @SerializedName("cylinderTypeId")
    private String cyCategoryId;
    @SerializedName("nominalTestPressure") //公称压力
    private String workPressure;
    @SerializedName("weight") //钢瓶重量
    private String weight;
    @SerializedName("volume") //容积、水容积
    private String volume;
    @SerializedName("wallThickness") //壁厚
    private String wallThickness;

    @SerializedName("setId") //集格id
    private String setId;
    @SerializedName("setNumber") //集格编号
    private String setNumber;

    @SerializedName("cylinderManufacturerId") //生产单位id
    private String manufacturerId;
    @SerializedName("cylinderManufacturerCode") //生产单位编号
    private String manufacturerCode;
    @SerializedName("cylinderManufacturerName") //生产单位名称
    private String manufacturerName;
    @SerializedName("cylinderManufacturingDate") //生产日期
    private String manuDate;
    @SerializedName("cylinderScrapDate") //报废日期
    private String scrapDate;

    @SerializedName("regularInspectionDate") //下次定检日期
    private String nextRegularInspectionDate;


    @SerializedName("lastFillTime") //最近一次充装日期
    private String lastFillTime;

    @SerializedName("unitId") //产权（托管）公司id
    private String unitId;
    @SerializedName("unitName") //产权（托管）公司
    private String unitName;


    public String getCyId() {
        return cyId;
    }

    public void setCyId(String cyId) {
        this.cyId = cyId;
    }

    public String getPlatformCyCode() {
        return platformCyCode;
    }

    public void setPlatformCyCode(String platformCyCode) {
        this.platformCyCode = platformCyCode;
    }

    public String getBottleCode() {
        return bottleCode;
    }

    public void setBottleCode(String bottleCode) {
        this.bottleCode = bottleCode;
    }

    public String getCompanyRelateCode() {
        return companyRelateCode;
    }

    public void setCompanyRelateCode(String companyRelateCode) {
        this.companyRelateCode = companyRelateCode;
    }

    public String getCyMediumName() {
        return cyMediumName;
    }

    public void setCyMediumName(String cyMediumName) {
        this.cyMediumName = cyMediumName;
    }

    public String getCyMediumId() {
        return cyMediumId;
    }

    public void setCyMediumId(String cyMediumId) {
        this.cyMediumId = cyMediumId;
    }

    public String getCyCategoryName() {
        return cyCategoryName;
    }

    public void setCyCategoryName(String cyCategoryName) {
        this.cyCategoryName = cyCategoryName;
    }

    public String getCyCategoryId() {
        return cyCategoryId;
    }

    public void setCyCategoryId(String cyCategoryId) {
        this.cyCategoryId = cyCategoryId;
    }

    public String getWorkPressure() {
        return workPressure;
    }

    public void setWorkPressure(String workPressure) {
        this.workPressure = workPressure;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getWallThickness() {
        return wallThickness;
    }

    public void setWallThickness(String wallThickness) {
        this.wallThickness = wallThickness;
    }

    public String getSetId() {

        if(setId.equals("0"))
            return null;

        return setId;
    }

    public void setSetId(String setId) {

        if (setId.equals("0")) {
            this.setId = null;
            return;
        }

        this.setId = setId;
    }

    public String getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getManuDate() {
        return manuDate;
    }

    public void setManuDate(String manuDate) {
        this.manuDate = manuDate;
    }

    public String getScrapDate() {
        return scrapDate;
    }

    public void setScrapDate(String scrapDate) {
        this.scrapDate = scrapDate;
    }

    public String getNextRegularInspectionDate() {
        return nextRegularInspectionDate;
    }

    public void setNextRegularInspectionDate(String nextRegularInspectionDate) {
        this.nextRegularInspectionDate = nextRegularInspectionDate;
    }

    public String getLastFillTime() {
        return lastFillTime;
    }

    public void setLastFillTime(String lastFillTime) {
        this.lastFillTime = lastFillTime;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }




    @Override
    public String toString() {
        return "CylinderInfoBean{" +
                "cyId='" + cyId + '\'' +
                ", platformCyCode='" + platformCyCode + '\'' +
                ", bottleCode='" + bottleCode + '\'' +
                ", companyRelateCode='" + companyRelateCode + '\'' +
                ", cyMediumName='" + cyMediumName + '\'' +
                ", cyMediumId='" + cyMediumId + '\'' +
                ", cyCategoryName='" + cyCategoryName + '\'' +
                ", cyCategoryId='" + cyCategoryId + '\'' +
                ", workPressure='" + workPressure + '\'' +
                ", weight='" + weight + '\'' +
                ", volume='" + volume + '\'' +
                ", wallThickness='" + wallThickness + '\'' +
                ", setId='" + setId + '\'' +
                ", setNumber='" + setNumber + '\'' +
                ", manufacturerId='" + manufacturerId + '\'' +
                ", manufacturerCode='" + manufacturerCode + '\'' +
                ", manufacturerName='" + manufacturerName + '\'' +
                ", manuDate='" + manuDate + '\'' +
                ", scrapDate='" + scrapDate + '\'' +
                ", nextRegularInspectionDate='" + nextRegularInspectionDate + '\'' +
                ", lastFillTime='" + lastFillTime + '\'' +
                ", unitId='" + unitId + '\'' +
                ", unitName='" + unitName + '\'' +
                '}';
    }
}
