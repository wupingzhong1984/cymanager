package com.org.gascylindermng.model;


import com.org.gascylindermng.api.HttpResponseResult;
import com.org.gascylindermng.bean.ChargeMissionBean;
import com.org.gascylindermng.bean.CompanyInfoBean;
import com.org.gascylindermng.bean.User;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;

public interface UserModel {

    //更新用户信息
    void updateUser(User user);
    //查询用户信息
    User queryUser();
    //删除用户信息
    void deleteUser();
    //保存用户信息
    void saveUser(User user);


    void updateCompany(CompanyInfoBean company);

    CompanyInfoBean queryCompany();

    void deleteCompany();

    void saveCompany(CompanyInfoBean company);

    Observable<HttpResponseResult> login(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> getCompanyById(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> getCyCategoryListByCompanyId(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> getCompanyProcessListByCompanyId(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> getNextAreaListByPreProcessId(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> updatePassword(@FieldMap Map<String, Object> params);

    Observable<HttpResponseResult> getCylinderInfoByPlatformCyNumber(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> getCylinderInfoByCyCode(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> getSetWithCylinderListBySetId(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> getCylinderLastTransmitRecord(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> getCylinderLastChargeRecord(@FieldMap Map<String, Object> params);

    Observable<HttpResponseResult> submitPrechargeCheckResult(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> createChargeMission(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> getChargeMissionList(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> getChargeMissionByMissionId(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> updateChargeMission(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> updateChargeMissionV2(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> deleteChargeMissionByMissionId(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> submitPostchargeCheckResult(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> submitTransmitReceiveRecord(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> submitCyRegularInspectionRecord(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> submitCyRepairRecord(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> submitCyScrapRecord(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> submitCyChangeMediumRecord(@FieldMap Map<String, Object> params);


    Observable<HttpResponseResult> addCylinder(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> addNumber(@FieldMap Map<String, Object> params);

    Observable<HttpResponseResult> searchCustomer(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> searchEmployee(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> searchCarNumber(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> searchWarehouse(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> searchUnitsSet(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> getCylinderListBySetId(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> searchCylinderManufacturer(@FieldMap Map<String, Object> params);
    Observable<HttpResponseResult> searchBatchNumber(@FieldMap Map<String, Object> params);
}
