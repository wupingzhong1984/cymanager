package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("id")
    private String id;  //platform user id

    @SerializedName("userName")
    private String userName;
    @SerializedName("pwd")
    private String pwd;

    @SerializedName("name")
    private String employeeName;
    @SerializedName("position") //1司机，2押运员，3收发，4生产，5检测
    private String position;
    @SerializedName("code") //员工编号
    private String employeeCode;
    @SerializedName("unitId") //所属公司id
    private String unitId;
    @SerializedName("unitName") //所属用户公司名称
    private String unitName;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getEmployeeName() { return employeeName; }

    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeCode() { return employeeCode; }

    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userName=" + userName +
                ", pwd='" + pwd + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", position='" + position + '\'' +
                ", employeeCode='" + employeeCode + '\'' +
                ", unitId='" + unitId + '\'' +
                ", unitName='" + unitName + '\'' +
                '}';
    }
}
