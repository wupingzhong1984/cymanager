package com.org.gascylindermng.service;

import com.org.gascylindermng.api.HttpResponseResult;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.User;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface HttpRequestService {

    /***
     * 用户登录
     * @param
     * @return
     */
    @FormUrlEncoded
    @POST("login")
    Observable<HttpResponseResult> login(@FieldMap Map<String, Object> params);


    @FormUrlEncoded
    @POST("getCompanyCylinderTypeVoListByUnitId")
    Observable<HttpResponseResult> getCyCategoryListByCompanyId(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getCompanyProjectByCompanyId")
    Observable<HttpResponseResult> getCompanyProcessListByCompanyId(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getCompanyProjectAreaByCompanyProjectId")
    Observable<HttpResponseResult> getNextAreaListByPreProcessId(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getCompanyById")
    Observable<HttpResponseResult> getCompanyById(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("updatePassword")
    Observable<HttpResponseResult> updatePassword(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getCylinderByNumber")
    Observable<HttpResponseResult> getCylinderInfoByPlatformCyCode(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getCylinderByCode")
    Observable<HttpResponseResult> getCylinderInfoByCyCode(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("addPreDetection")
    Observable<HttpResponseResult> submitPrechargeCheckResult(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("addDetection")
    Observable<HttpResponseResult> createChargeMission(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getDetectionMissionVoListByEmployeeId")
    Observable<HttpResponseResult> getChargeMissionList(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("updateDetection")
    Observable<HttpResponseResult> updateChargeMission(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("addAfterDetection")
    Observable<HttpResponseResult> submitPostchargeCheckResult(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("addTransmitReceiveRecord")
    Observable<HttpResponseResult> submitTransmitReceiveRecord(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("addNumber")
    Observable<HttpResponseResult> addNumber(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("addCylinder")
    Observable<HttpResponseResult> addCylinder(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getCustomerByName")
    Observable<HttpResponseResult> searchCustomer(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getEmployeeByName")
    Observable<HttpResponseResult> searchEmployee(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getCarByCarNumber")
    Observable<HttpResponseResult> searchCarNumber(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getWarehouseByName")
    Observable<HttpResponseResult> searchWarehouse(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("addCylinderTimingDetectionRecord")
    Observable<HttpResponseResult> submitCyRegularInspectionRecord(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("addCylinderMaintainRecord")
    Observable<HttpResponseResult> submitCyRepairRecord(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("addCylinderScrapRecord")
    Observable<HttpResponseResult> submitCyScrapRecord(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("addCylinderUpdateRecord")
    Observable<HttpResponseResult> submitCyChangeMediumRecord(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getSetInfoByUnitId")
    Observable<HttpResponseResult> searchUnitsSet(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getCylinderBySetId")
    Observable<HttpResponseResult> getCylinderListBySetId(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getCylinderManufacturer")
    Observable<HttpResponseResult> searchCylinderManufacturer(@FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("getCylinderLastTransmitRecord")
    Observable<HttpResponseResult> getCylinderLastTransmitRecord(@FieldMap Map<String, Object> params);
}
