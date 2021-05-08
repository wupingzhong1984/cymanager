package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SetBean implements Serializable {

    @SerializedName("id")
    private String setId; //2

    @SerializedName("setNumber")
    private String setNumber; //pj28013

    @SerializedName("cylinderList")
    private ArrayList<CylinderInfoBean> cylinderList;

    public SetBean() {
        this.cylinderList = new ArrayList<>();
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

    public ArrayList<CylinderInfoBean> getCylinderList() {
        return cylinderList;
    }

    public void setCylinderList(ArrayList<CylinderInfoBean> cylinderList) {
        this.cylinderList = cylinderList;
    }
}
