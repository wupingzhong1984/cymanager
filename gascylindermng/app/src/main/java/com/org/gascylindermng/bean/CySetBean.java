package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CySetBean implements Serializable {

    @SerializedName("id") //集格ID
    private String setId;

    @SerializedName("setNumber") //集格编号
    private String setNumber;

    @SerializedName("unitId") //所属单位
    private String unitId;

    @SerializedName("name") //集格名字
    private String setName;

    @SerializedName("quantity") //气瓶数量
    private int quantity;

    @SerializedName("regularInspectionDate") //集格框过期时间，非必要
    private int regularInspectionDate;

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

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }

    public int getRegularInspectionDate() {
        return regularInspectionDate;
    }

    public void setRegularInspectionDate(int regularInspectionDate) {
        this.regularInspectionDate = regularInspectionDate;
    }

    @Override
    public String toString() {
        return "CySetBean{" +
                "setId='" + setId + '\'' +
                ", setNumber='" + setNumber + '\'' +
                ", unitId='" + unitId + '\'' +
                ", setName='" + setName + '\'' +
                ", quantity=" + quantity +
                ", regularInspectionDate=" + regularInspectionDate +
                '}';
    }
}
