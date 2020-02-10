package com.org.gascylindermng.model.impl;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.org.gascylindermng.api.HttpRequestManage;
import com.org.gascylindermng.api.HttpResponseResult;
import com.org.gascylindermng.bean.ChargeMissionBean;
import com.org.gascylindermng.bean.CompanyInfoBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.User;
import com.org.gascylindermng.model.UserModel;
import com.org.gascylindermng.service.HttpRequestService;
import com.org.gascylindermng.tools.SharedPreTools;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;

public class UserModelImpl implements UserModel {

    private HttpRequestService httpRequestService;

    public UserModelImpl() {
        httpRequestService = HttpRequestManage.getInstanc().create(HttpRequestService.class);
    }

    @Override
    public User queryUser() {
        User user = (User) SharedPreTools.query("user", new User());
        return user;
    }

    @Override
    public void deleteUser() {
        SharedPreTools.remove("user");
    }

    @Override
    public void saveUser(User user) {
        SharedPreTools.save("user", user);
    }

    @Override
    public void updateUser(User user) {
        if (user != null) {
            SharedPreTools.remove("user");
            SharedPreTools.save("user", user);
        } else {
            User localuser = (User) SharedPreTools.query("user", new User());
            if (localuser.getId()!=null){
                SharedPreTools.remove("user");
            }
        }
    }


    @Override
    public CompanyInfoBean queryCompany() {
        CompanyInfoBean companyInfoBean = (CompanyInfoBean) SharedPreTools.query("company", new CompanyInfoBean());
        return companyInfoBean;
    }

    @Override
    public void deleteCompany() {
        SharedPreTools.remove("company");
    }

    @Override
    public void saveCompany(CompanyInfoBean company) {
        SharedPreTools.save("company", company);
    }

    @Override
    public void updateCompany(CompanyInfoBean company) {
        if (company != null) {
            SharedPreTools.remove("company");
            SharedPreTools.save("company", company);
        } else {
            CompanyInfoBean localcompany = (CompanyInfoBean) SharedPreTools.query("company", new CompanyInfoBean());
            if (localcompany.getCompanyId()!=null){
                SharedPreTools.remove("company");
            }
        }
    }

    @Override
    public Observable<HttpResponseResult> login(Map<String, Object> params) {
        return httpRequestService.login(params);
    }

    @Override
    public Observable<HttpResponseResult> getCompanyById(Map<String, Object> params) {
        return httpRequestService.getCompanyById(params);
    }

    @Override
    public Observable<HttpResponseResult> searchCustomer(Map<String, Object> params) {
        return httpRequestService.searchCustomer(params);
    }

    @Override
    public Observable<HttpResponseResult> getCyCategoryListByCompanyId(Map<String, Object> params) {
        return httpRequestService.getCyCategoryListByCompanyId(params);
    }

    @Override
    public Observable<HttpResponseResult> getCompanyProcessListByCompanyId(Map<String, Object> params) {
        return httpRequestService.getCompanyProcessListByCompanyId(params);
    }

    @Override
    public Observable<HttpResponseResult> getNextAreaListByPreProcessId(Map<String, Object> params) {
        return httpRequestService.getNextAreaListByPreProcessId(params);
    }

    @Override
    public Observable<HttpResponseResult> updatePassword(Map<String, Object> params) {
        return httpRequestService.updatePassword(params);
    }

    @Override
    public Observable<HttpResponseResult> getCylinderInfoByPlatformCyCode(Map<String, Object> params) {
        return httpRequestService.getCylinderInfoByPlatformCyCode(params);
    }

    @Override
    public Observable<HttpResponseResult> getCylinderInfoByCyCode(Map<String, Object> params) {
        return httpRequestService.getCylinderInfoByCyCode(params);
    }

    @Override
    public Observable<HttpResponseResult> getCylinderLastTransmitRecord(Map<String, Object> params) {
        return httpRequestService.getCylinderLastTransmitRecord(params);
    }

    @Override
    public Observable<HttpResponseResult> submitPrechargeCheckResult(Map<String, Object> params) {
        return httpRequestService.submitPrechargeCheckResult(params);
    }

    @Override
    public Observable<HttpResponseResult> createChargeMission(Map<String, Object> params) {
        return httpRequestService.createChargeMission(params);
    }

    @Override
    public Observable<HttpResponseResult> getChargeMissionList(Map<String, Object> params) {
        return httpRequestService.getChargeMissionList(params);
    }

    @Override
    public Observable<HttpResponseResult> updateChargeMission(Map<String, Object> params) {
        return httpRequestService.updateChargeMission(params);
    }

    @Override
    public Observable<HttpResponseResult> submitPostchargeCheckResult(Map<String, Object> params) {
        return httpRequestService.submitPostchargeCheckResult(params);
    }

    @Override
    public Observable<HttpResponseResult> submitTransmitReceiveRecord(Map<String, Object> params) {
        return httpRequestService.submitTransmitReceiveRecord(params);
    }

    @Override
    public Observable<HttpResponseResult> submitCyRegularInspectionRecord(Map<String, Object> params) {
        return httpRequestService.submitCyRegularInspectionRecord(params);
    }

    @Override
    public Observable<HttpResponseResult> submitCyRepairRecord(Map<String, Object> params) {
        return httpRequestService.submitCyRepairRecord(params);
    }

    @Override
    public Observable<HttpResponseResult> submitCyScrapRecord(Map<String, Object> params) {
        return httpRequestService.submitCyScrapRecord(params);
    }

    @Override
    public Observable<HttpResponseResult> submitCyChangeMediumRecord(Map<String, Object> params) {
        return httpRequestService.submitCyChangeMediumRecord(params);
    }
    @Override
    public Observable<HttpResponseResult> addCylinder(Map<String, Object> params) {
        return httpRequestService.addCylinder(params);
    }

    @Override
    public Observable<HttpResponseResult> addNumber(Map<String, Object> params) {
        return httpRequestService.addNumber(params);
    }

    @Override
    public Observable<HttpResponseResult> searchEmployee(Map<String, Object> params) {
        return httpRequestService.searchEmployee(params);
    }

    @Override
    public Observable<HttpResponseResult> searchCarNumber(Map<String, Object> params) {
        return httpRequestService.searchCarNumber(params);
    }

    @Override
    public Observable<HttpResponseResult> searchWarehouse(Map<String, Object> params) {
        return httpRequestService.searchWarehouse(params);
    }

    @Override
    public Observable<HttpResponseResult> searchUnitsSet(Map<String, Object> params) {
        return httpRequestService.searchUnitsSet(params);
    }

    @Override
    public Observable<HttpResponseResult> getCylinderListBySetId(Map<String, Object> params) {
        return httpRequestService.getCylinderListBySetId(params);
    }

    @Override
    public Observable<HttpResponseResult>searchCylinderManufacturer(Map<String, Object> params) {
        return httpRequestService.searchCylinderManufacturer(params);
    }
}