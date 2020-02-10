package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomerInfoBean implements Serializable {

    @SerializedName("unitId")
    private String customerId;
    @SerializedName("name")
    private String customerName;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
