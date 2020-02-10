package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EmployeeBean implements Serializable {

    @SerializedName("id")
    private String employeeId;

    @SerializedName("name")
    private String employeeName;

    @SerializedName("position")
    private String position; //1司机，2押运员，3收发，4生产，5检测

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
