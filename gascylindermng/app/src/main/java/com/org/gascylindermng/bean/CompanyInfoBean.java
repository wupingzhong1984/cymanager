package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CompanyInfoBean implements Serializable {


    @SerializedName("id")
    private String companyId;

    @SerializedName("pinlessObject") //0，钢印号，1自编码
    private String pinlessObject;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getPinlessObject() {
        return pinlessObject;
    }

    public void setPinlessObject(String pinlessObject) {
        this.pinlessObject = pinlessObject;
    }

    @Override
    public String toString() {
        return "User{" +
                "companyId='" + companyId + '\'' +
                ", pinlessObject=" + pinlessObject +
                '}';
    }
}
