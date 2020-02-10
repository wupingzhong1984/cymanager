package com.org.gascylindermng.bean;

import com.google.gson.annotations.SerializedName;

public class WarehouseBean {

    @SerializedName("id")
    private String warehouseId;

    @SerializedName("name")
    private String warehouseName;

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
}
