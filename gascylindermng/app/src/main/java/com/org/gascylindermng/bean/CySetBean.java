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

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
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

    @Override
    public String toString() {
        return "CySetBean{" +
                "setId='" + setId + '\'' +
                ", setNumber='" + setNumber + '\'' +
                ", unitId='" + unitId + '\'' +
                ", setName='" + setName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
