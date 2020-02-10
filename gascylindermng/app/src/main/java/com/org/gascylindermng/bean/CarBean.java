package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CarBean implements Serializable {

    @SerializedName("id")
    private String carId;

    @SerializedName("carNumber")
    private String carNumber;

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }
}
