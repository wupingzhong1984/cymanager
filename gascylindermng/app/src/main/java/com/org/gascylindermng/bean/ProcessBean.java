package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProcessBean implements Serializable {

    @SerializedName("id")
    private String companyProcessId; //每个公司不同

    @SerializedName("projectId") //每个公司一样
    private String processId;
    @SerializedName("projectName") //每个公司一样
    private String processName;

    public String getCompanyProcessId() {
        return companyProcessId;
    }

    public void setCompanyProcessId(String companyProcessId) {
        this.companyProcessId = companyProcessId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
