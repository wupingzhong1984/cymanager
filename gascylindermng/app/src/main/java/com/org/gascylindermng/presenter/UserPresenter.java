package com.org.gascylindermng.presenter;


import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.org.gascylindermng.activity.MainActivity;
import com.org.gascylindermng.api.HttpResponseResult;
import com.org.gascylindermng.base.BasePresenter;
import com.org.gascylindermng.bean.ChargeMissionBean;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.CompanyInfoBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.SetBean;
import com.org.gascylindermng.bean.User;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.model.ChargeMission;
import com.org.gascylindermng.model.UserModel;
import com.org.gascylindermng.model.impl.ChargeMissionImpl;
import com.org.gascylindermng.model.impl.UserModelImpl;
import com.org.gascylindermng.tools.CommonTools;
import com.org.gascylindermng.tools.ServiceLogicUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class UserPresenter extends BasePresenter {

    private ApiCallback callback;
    private UserModel userModel;

    public UserPresenter(ApiCallback callback) {
        this.callback = callback;
        this.userModel = new UserModelImpl();
    }

    public User queryUser() {
        return userModel.queryUser();
    }

    public void deleteUser() {
        userModel.deleteUser();
    }

    public CompanyInfoBean querComapny() {
        return userModel.queryCompany();
    }

    public void deleteCompany() {
        userModel.deleteCompany();
    }


    private TreeMap<String,Object> addCommonParams(Map<String, Object> param) {

        TreeMap finalParams = new TreeMap<>();

        if (param != null) {
            String paramsStr = new Gson().toJson(param);
            finalParams.put("params", paramsStr);
        }

//        finalParams.put("appkey", Constants.appKey);
//
//        //test  only used in product
//        finalParams.put("version", APKVersionCodeUtils.getVerName(MyAppContext.getInstance().getApplicationContext()));
//
//        if (userModel.queryToken() != null) finalParams.put("token", userModel.queryToken());
//
//        String sigs = StringTools.keySort(finalParams);
//        finalParams.put("sign", sigs);
        return finalParams;
    }

    /***
     *用户登录
     * @param
     */
    public void login(final String username, final String pwd) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("userName", username);
        subParam.put("password", pwd);

        Observable<HttpResponseResult> userObservable = userModel.login(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            Type type = new TypeToken<User>(){}.getType();
                            Gson gson = new Gson();
                            JSONObject object=new JSONObject((LinkedTreeMap)httpResult.getData());
                            String mData = object.toString();
                            User user =gson.fromJson(mData,type);
                            user.setPwd(pwd);
                            userModel.updateUser(user);
                            callback.successful("login",user);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("login","needUpdate");
                        } else {
                            callback.failure("login",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("login",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *获取企业信息
     * @param
     */
    public void getCompanyById () {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId", queryUser().getUnitId());

        Observable<HttpResponseResult> userObservable = userModel.getCompanyById(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            Type type = new TypeToken<CompanyInfoBean>(){}.getType();
                            Gson gson = new Gson();
                            JSONObject object=new JSONObject((LinkedTreeMap)httpResult.getData());
                            String mData = object.toString();
                            CompanyInfoBean company =gson.fromJson(mData,type);
                            userModel.updateCompany(company);
                            callback.successful("getCompanyById",company);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getCompanyById","needUpdate");
                        } else {
                            callback.failure("getCompanyById",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("getCompanyById",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {

                        dispose();
                    }
                });
    }

    /***
     *模糊搜索客户
     * @param
     */
    public void searchCustomer(final String name) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("name", name);
        subParam.put("unitId", querComapny().getCompanyId());

        Observable<HttpResponseResult> userObservable = userModel.searchCustomer(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("searchCustomer",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("searchCustomer","needUpdate");
                        } else {
                            callback.failure("searchCustomer",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("searchCustomer",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *根据公司可用的气瓶类型
     * @param
     */
    public void getCyCategoryListByCompanyId() {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId", querComapny().getCompanyId());

        Observable<HttpResponseResult> userObservable = userModel.getCyCategoryListByCompanyId(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getCyCategoryListByCompanyId",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getCyCategoryListByCompanyId","needUpdate");
                        } else {
                            callback.failure("getCyCategoryListByCompanyId",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("getCyCategoryListByCompanyId",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *根据公司id获得配置的流程
     * @param
     */
    public void getCompanyProcessListByCompanyId() {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId", querComapny().getCompanyId());

        Observable<HttpResponseResult> userObservable = userModel.getCompanyProcessListByCompanyId(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getCompanyProcessListByCompanyId",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getCompanyProcessListByCompanyId","needUpdate");
                        } else {
                            callback.failure("getCompanyProcessListByCompanyId",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("getCompanyProcessListByCompanyId",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });

    }

    /***
     *根据检测流程获得之后的流向区域
     * @param
     */
    public void getNextAreaListByPreProcessId(String id) {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("companyProjectId", id);

        Observable<HttpResponseResult> userObservable = userModel.getNextAreaListByPreProcessId(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getNextAreaListByPreProcessId",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getNextAreaListByPreProcessId","needUpdate");
                        } else {
                            callback.failure("getNextAreaListByPreProcessId",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("getNextAreaListByPreProcessId",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });

    }

    /***
     *更新用户密码
     * @param
     */
    public void updatePassword(final String oldPwd, final String newPwd) {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("oldPassword", oldPwd);
        subParam.put("newPassword", newPwd);
        subParam.put("id", queryUser().getId());

        Observable<HttpResponseResult> userObservable = userModel.updatePassword(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            User user = queryUser();
                            user.setPwd(newPwd);
                            userModel.updateUser(user);
                            callback.successful("updatePassword",user);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("updatePassword","needUpdate");
                        } else {
                            callback.failure("updatePassword",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("updatePassword",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *通过标签码获取气瓶信息
     * @param
     */
    public void getCylinderInfoByPlatformCyNumber(final String platformCyNum) {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("cylinderNumber", platformCyNum);

        Observable<HttpResponseResult> userObservable = userModel.getCylinderInfoByPlatformCyNumber(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            if (httpResult.getData() == null)
                                callback.successful("getCylinderInfoByPlatformCyNumber",null);
                            Type type = new TypeToken<CylinderInfoBean>(){}.getType();
                            Gson gson = new Gson();
                            JSONObject object=new JSONObject((LinkedTreeMap)httpResult.getData());
                            String mData = object.toString();
                            CylinderInfoBean infoBean =gson.fromJson(mData,type);
                            callback.successful("getCylinderInfoByPlatformCyNumber",infoBean);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getCylinderInfoByPlatformCyNumber","needUpdate");
                        } else {
                            callback.failure("getCylinderInfoByPlatformCyNumber",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("getCylinderInfoByPlatformCyNumber",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *通过瓶号/企业内编号获取气瓶信息
     * @param
     */
    public void getCylinderInfoByCyCode(final String cyCode) {
        Map<String, Object> subParam = new HashMap<>();
        if (querComapny().getPinlessObject().equals("0")) {
            subParam.put("cylinderCode", cyCode);
        } else {
            subParam.put("ownCode", cyCode);
        }
        subParam.put("unitId", querComapny().getCompanyId());
        Observable<HttpResponseResult> userObservable = userModel.getCylinderInfoByCyCode(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            if (httpResult.getData() == null) {
                                callback.successful("getCylinderInfoByCyCode", null);
                            } else {
                                Type type = new TypeToken<CylinderInfoBean>() {
                                }.getType();
                                Gson gson = new Gson();
                                JSONObject object = new JSONObject((LinkedTreeMap) httpResult.getData());
                                String mData = object.toString();
                                CylinderInfoBean infoBean = gson.fromJson(mData, type);
                                callback.successful("getCylinderInfoByCyCode", infoBean);
                            }
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getCylinderInfoByCyCode","needUpdate");
                        } else {
                            callback.failure("getCylinderInfoByCyCode",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("getCylinderInfoByCyCode",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
    * 绑定气瓶
    * @param
    */
    public void addNumber(final String cyId, final String qrcode) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("cylinderId", cyId);
        subParam.put("number", qrcode);

        Observable<HttpResponseResult> userObservable = userModel.addNumber(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("addNumber",null);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("addNumber","needUpdate");
                        } else {
                            callback.failure("addNumber",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("addNumber",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     * 添加气瓶
     * @param
     */
    public void addCylinder(String cylinderOrOwnCode,
                            String categoryId,
                            String categoryName,
                            String mediumId,
                            String mediumName,
                            String manuId,
                            String manuName,
                            String manuDate,
                            String regularInspectionDate,
                            String setId,
                            String setNumber,
                            String pressure,
                            String weight,
                            String volume,
                            String wallthickness) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId", queryUser().getUnitId());
        subParam.put("fillingUnit", queryUser().getUnitName());

        if (querComapny().getPinlessObject().equals("0")) {
            subParam.put("cylinderCode", cylinderOrOwnCode);
        } else {
            subParam.put("ownCode", cylinderOrOwnCode);
        }
        subParam.put("employeeId",queryUser().getId());
        subParam.put("employeeName",queryUser().getEmployeeName());

        subParam.put("cylinderTypeId", categoryId);
        subParam.put("cylinderTypeName", categoryName);

        subParam.put("gasMediumId", mediumId);
        subParam.put("gasMediumName", mediumName);

        subParam.put("cylinderManufacturerId", manuId);
        subParam.put("cylinderManufacturerName", manuName);
        subParam.put("manufacturingDate", manuDate);
        if (!TextUtils.isEmpty(regularInspectionDate)) {
            subParam.put("regularInspectionDate", regularInspectionDate);
        }


        if (!TextUtils.isEmpty(setId)) {
            subParam.put("setId", setId);
        }
        if (!TextUtils.isEmpty(setNumber)) {
            subParam.put("setNumber", setNumber);
        }

        subParam.put("nominalTestPressure", !TextUtils.isEmpty(pressure)?pressure:"0");
        subParam.put("weight", !TextUtils.isEmpty(weight)?weight:"0");
        subParam.put("volume", !TextUtils.isEmpty(volume)?volume:"0");
        subParam.put("wallThickness", !TextUtils.isEmpty(wallthickness)?wallthickness:"0");

        Observable<HttpResponseResult> userObservable = userModel.addCylinder(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("addCylinder",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("addCylinder","needUpdate");
                        } else {
                            callback.failure("addCylinder",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("addCylinder",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *提交回厂验空结果
     * @param
     */
    public void submitPrechargeCheckResult(ArrayList<String> cyIdList,
                                           List<CheckItemBean> checkItems,
                                           boolean pass,
                                           String isEmptyCy,
                                           String nextAreaId,
                                           String remark) {

        Map<String, Object> subParam = new HashMap<>();

        StringBuilder cyStr = new StringBuilder();
        for(String cyId : cyIdList) {
            cyStr.append(cyId+",");
        }
        subParam.put("cylinderIdList", cyStr.substring(0,cyStr.length()-1));

        subParam.put("unitId",queryUser().getUnitId());
        subParam.put("ifPass", pass?"1":"0");
        subParam.put("empty",isEmptyCy);
        subParam.put("companyAreaId", nextAreaId);
        subParam.put("remark", remark);
        subParam.put("creator", queryUser().getEmployeeName());

        for (CheckItemBean c : checkItems) {

            subParam.put(c.getApiParam(), c.isState()?"1":"0");
        }

        Observable<HttpResponseResult> userObservable = userModel.submitPrechargeCheckResult(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("submitPrechargeCheckResult",httpResult);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("submitPrechargeCheckResult","needUpdate");
                        } else {
                            callback.failure("submitPrechargeCheckResult",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("submitPrechargeCheckResult",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *创建充装任务
     * @param
     */
    public void createChargeMission(Date beginTime,
                                    String productionBatch,
                                    String pureness,
//                                    String team,
                                    String remark,
                                    ArrayList<String> cyIdList) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId", queryUser().getUnitId());
        subParam.put("employeeId", queryUser().getId());
        subParam.put("creator", queryUser().getEmployeeName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        subParam.put("beginDate", sdf.format(beginTime));
        if (productionBatch != null) {
            subParam.put("productionBatch", productionBatch);
        }
        if (pureness != null) {
            subParam.put("pureness", pureness);
        }
        if (remark != null) {
            subParam.put("remark", remark);
        }
        StringBuilder cyStr = new StringBuilder();
        for(String cyId : cyIdList) {
            cyStr.append(cyId+",");
        }
        subParam.put("cylinderIdList", cyStr.substring(0,cyStr.length()-1));

        Observable<HttpResponseResult> userObservable = userModel.createChargeMission(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("createChargeMission",httpResult);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("createChargeMission","needUpdate");
                        } else {
                            callback.failure("createChargeMission",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("createChargeMission",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *获取从某天起的充装任务列表
     * @param
     */
    public void getChargeMissionList(String beginDate) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("employeeId", queryUser().getId());
        subParam.put("beginDate",beginDate);

        Observable<HttpResponseResult> userObservable = userModel.getChargeMissionList(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getChargeMissionList", httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getChargeMissionList","needUpdate");
                        } else {
                            callback.failure("getChargeMissionList",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("getChargeMissionList",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *获取从某时段内的充装任务列表
     * @param
     */
    public void getChargeMissionListNow() {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("employeeId", queryUser().getId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        subParam.put("begin", sdf.format(ServiceLogicUtils.getChargeClassBeginTime(new Date())));
        subParam.put("end", sdf.format(new Date()));

        Observable<HttpResponseResult> userObservable = userModel.getChargeMissionList(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getChargeMissionList", httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getChargeMissionList","needUpdate");
                        } else {
                            callback.failure("getChargeMissionList",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("getChargeMissionList",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *获取从某时段内的以及未完成的充装任务列表
     * @param
     */
    public void getChargeMissionListAndNotFinished() {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("employeeId", queryUser().getId());
        subParam.put("unitId",queryUser().getUnitId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        subParam.put("begin", sdf.format(ServiceLogicUtils.getChargeClassBeginTime(new Date())));
        subParam.put("end", sdf.format(new Date()));

        Observable<HttpResponseResult> userObservable = userModel.getChargeMissionListAndNotFinished(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getChargeMissionListAndNotFinished", httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getChargeMissionListAndNotFinished","needUpdate");
                        } else {
                            callback.failure("getChargeMissionListAndNotFinished",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("getChargeMissionListAndNotFinished",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }



    /***
     *获取的充装任务详情
     * @param
     */
    public void getChargeMissionByMissionId(String missionId) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("detectionMissionId",missionId);

        Observable<HttpResponseResult> userObservable = userModel.getChargeMissionByMissionId(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            if (httpResult.getData() == null)
                                callback.successful("getChargeMissionByMissionId",null);
                            Type type = new TypeToken<ChargeMissionBean>(){}.getType();
                            Gson gson = new Gson();
                            JSONObject object=new JSONObject((LinkedTreeMap)httpResult.getData());
                            String mData = object.toString();
                            ChargeMissionBean infoBean =gson.fromJson(mData,type);
                            callback.successful("getChargeMissionByMissionId",infoBean);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getChargeMissionByMissionId","needUpdate");
                        } else {
                            callback.failure("getChargeMissionByMissionId",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("getChargeMissionByMissionId",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *更新充装任务
     * @param
     */
    public void updateDetection(String missionId,
                                String remark,
                                String nextAreaId,
                                Date endTime,
                                boolean pass,
                                List<CheckItemBean> checkItems) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("detectionMissionId", missionId);
        subParam.put("remark", remark);
        subParam.put("companyAreaId", nextAreaId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        subParam.put("endDate", sdf.format(endTime));
        subParam.put("ifPass", pass?"1":"0");
        for (CheckItemBean c : checkItems) {
            subParam.put(c.getApiParam(), c.isState()?"1":"0");
        }

        Observable<HttpResponseResult> userObservable = userModel.updateChargeMission(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("updateDetection",httpResult);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("updateDetection","needUpdate");
                        } else {
                            callback.failure("updateDetection",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("updateDetection",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *更新充装任务
     * @param
     */
    public void updateChargeMissionV2(String missionId,
                                String remark,
                                String nextAreaId,
                                Date endTime,
                                List<Map> checkItems) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("detectionMissionId", missionId);
        if (!TextUtils.isEmpty(remark)) {
            subParam.put("remark", remark);
        } else {
            subParam.put("remark", "");
        }
    //    subParam.put("companyAreaId", nextAreaId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        subParam.put("endDate", sdf.format(endTime));

        Gson gson = new Gson();
        String jsonStr = gson.toJson(checkItems);
        subParam.put("cylinderCheckList",jsonStr);

        Observable<HttpResponseResult> userObservable = userModel.updateChargeMissionV2(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("updateChargeMissionV2",httpResult);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("updateChargeMissionV2","needUpdate");
                        } else {
                            callback.failure("updateChargeMissionV2",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("updateChargeMissionV2",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *提交充后验满结果
     * @param
     */
    public void submitPostchargeCheckResult(ArrayList<String> cyIdList,
                                            List<CheckItemBean> checkItems,
                                            boolean pass,
                                            String nextAreaId,
                                            String remark) {

        Map<String, Object> subParam = new HashMap<>();

        StringBuilder cyStr = new StringBuilder();
        for(String cyId : cyIdList) {
            cyStr.append(cyId+",");
        }
        subParam.put("cylinderIdList", cyStr.substring(0,cyStr.length()-1));

        subParam.put("unitId",queryUser().getUnitId());
        subParam.put("ifPass", pass?"1":"0");
        subParam.put("companyAreaId", nextAreaId);
        subParam.put("remark", remark);
        subParam.put("creator", queryUser().getEmployeeName());

        for (CheckItemBean c : checkItems) {
            subParam.put(c.getApiParam(), c.isState()?"1":"0");
        }

        Observable<HttpResponseResult> userObservable = userModel.submitPostchargeCheckResult(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("submitPostchargeCheckResult",httpResult);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("submitPostchargeCheckResult","needUpdate");
                        } else {
                            callback.failure("submitPostchargeCheckResult",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("submitPostchargeCheckResult",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *提交收发结果(客户、仓库)
     * @param
     */
    public void submitTransmitReceiveRecord(String isEmptyCy, //0否1是
                                            String actionType, //1，回收；2，发出
                                            String isFaceOk, //0否1是
                                            String transOrderId, //运单号
                                            String customerId, //客户id
                                            String warehouseId, //仓库id
                                            String driverId,
                                            String supercargoId,
                                            String carId,
                                            String remark,
                                            ArrayList<String> cyIdList,
                                            String lng,
                                            String lat) {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId", queryUser().getUnitId());
        if (!TextUtils.isEmpty(transOrderId)) {
            subParam.put("waybillNumber", transOrderId);
        }

        if(customerId != null && !customerId.equals("")) {
            subParam.put("destinationType", 1); //目的地类型（1客户，2仓库）
            subParam.put("customerId", customerId);
        } else {
            if(warehouseId != null && !warehouseId.equals("")) {
                subParam.put("destinationType", 2); //目的地类型（1客户，2仓库
                subParam.put("warehouseId", warehouseId);
            } else {
                subParam.put("destinationType", 0); //目的地类型（1客户，2仓库）
                subParam.put("customerId", 0);
                subParam.put("warehouseId", 0);
            }
        }

        if (!TextUtils.isEmpty(driverId)) {
            subParam.put("driverId", driverId);
        } else {
            subParam.put("driverId", 0);
        }

        if (!TextUtils.isEmpty(supercargoId)) {
            subParam.put("supercargoId", supercargoId);
        } else {
            subParam.put("supercargoId", 0);
        }

        if (!TextUtils.isEmpty(carId)) {
            subParam.put("carId", carId);
        } else {
            subParam.put("carId", 0);
        }

        if (!TextUtils.isEmpty(remark)) {
            subParam.put("remake", remark);
        } else {
            subParam.put("remake", "");
        }

        subParam.put("cylinderNum", cyIdList.size());
        subParam.put("actionType", actionType);
        subParam.put("creator", queryUser().getEmployeeName());


        ArrayList<Map> list = new ArrayList<>();
        for (String cy : cyIdList) {
            Map cyParams = new HashMap();
            cyParams.put("cylinderId",cy);
            cyParams.put("detection",isFaceOk);
            cyParams.put("empty",isEmptyCy);
            list.add(cyParams);
        }
        Gson gson = new Gson();
        String jsonStr = gson.toJson(list);
        subParam.put("cylinderRecordList",  jsonStr);

        if (!TextUtils.isEmpty(lat)) {
            subParam.put("lat", lat);
            subParam.put("lng", lng);
        }

        Observable<HttpResponseResult> userObservable = userModel.submitTransmitReceiveRecord(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("submitTransmitReceiveRecord",httpResult);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("submitTransmitReceiveRecord","needUpdate");
                        } else {
                            callback.failure("submitTransmitReceiveRecord",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("submitTransmitReceiveRecord",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *模糊搜索员工
     * @param
     */
    public void searchEmployee(String name) {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("name", name);
        subParam.put("unitId", querComapny().getCompanyId());

        Observable<HttpResponseResult> userObservable = userModel.searchEmployee(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("searchEmployee",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("searchEmployee","needUpdate");
                        } else {
                            callback.failure("searchEmployee",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("searchEmployee",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *模糊搜索车牌
     * @param
     */
    public void searchCarNumber(String name) {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("carNumber", name);
        subParam.put("unitId", querComapny().getCompanyId());

        Observable<HttpResponseResult> userObservable = userModel.searchCarNumber(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("searchCarNumber",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("searchCarNumber","needUpdate");
                        } else {
                            callback.failure("searchCarNumber",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("searchCarNumber",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *模糊搜索车牌
     * @param
     */
    public void searchWarehouse(String name) {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("name", name);
        subParam.put("unitId", querComapny().getCompanyId());

        Observable<HttpResponseResult> userObservable = userModel.searchWarehouse(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("searchWarehouse",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("searchWarehouse","needUpdate");
                        } else {
                            callback.failure("searchWarehouse",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.failure("searchWarehouse",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *添加定检记录
     * @param
     */
    public void submitCyRegularInspectionRecord(String cylinderId,
                                                boolean pass,
                                                String regularInspectionDate,
                                                String remark,
                                                List<CheckItemBean> checkItems) {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId",queryUser().getUnitId());
        subParam.put("cylinderId", cylinderId);
        subParam.put("result", pass?"1":"0");
        if (!TextUtils.isEmpty(regularInspectionDate)) {
            subParam.put("regularInspectionDate", regularInspectionDate);
        }
        subParam.put("remark", remark);
        subParam.put("creator", queryUser().getEmployeeName());

        for (CheckItemBean c : checkItems) {
            subParam.put(c.getApiParam(), c.isState()?"1":"0");
        }

        Observable<HttpResponseResult> userObservable = userModel.submitCyRegularInspectionRecord(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("submitCyRegularInspectionRecord",httpResult);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("submitCyRegularInspectionRecord","needUpdate");
                        } else {
                            callback.failure("submitCyRegularInspectionRecord",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("submitCyRegularInspectionRecord",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *添加维修记录
     * @param
     */
    public void submitCyRepairRecord(String cylinderId,
                                     String content,
                                     boolean pass,
                                     String nextAreaId) {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId",queryUser().getUnitId());
        subParam.put("cylinderId", cylinderId);
        subParam.put("content", content);
        subParam.put("result", pass?"1":"0");
        subParam.put("areaId", nextAreaId);
        subParam.put("creator", queryUser().getEmployeeName());

        Observable<HttpResponseResult> userObservable = userModel.submitCyRepairRecord(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("submitCyRepairRecord",httpResult);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("submitCyRepairRecord","needUpdate");
                        } else {
                            callback.failure("submitCyRepairRecord",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("submitCyRepairRecord",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *添加报废记录
     * @param
     */
    public void submitCyScrapRecord(String cylinderId,
                                    String reason) {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId",queryUser().getUnitId());
        subParam.put("cylinderId", cylinderId);
        subParam.put("reason", reason);
        subParam.put("creator", queryUser().getEmployeeName());

        Observable<HttpResponseResult> userObservable = userModel.submitCyScrapRecord(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("submitCyScrapRecord",httpResult);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("submitCyScrapRecord","needUpdate");
                        } else {
                            callback.failure("submitCyScrapRecord",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("submitCyScrapRecord",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *添加维护记录
     * @param
     */
    public void submitCyChangeMediumRecord(String cylinderId,
                                           String reason,
                                           String oldGasMediumId,
                                           String newGasMediumId,
                                           List<CheckItemBean> checkItems) {
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId",queryUser().getUnitId());
        subParam.put("cylinderId", cylinderId);
        subParam.put("reason", reason);
        subParam.put("creator", queryUser().getEmployeeName());

        if (!TextUtils.isEmpty(oldGasMediumId)) {
            subParam.put("oldGasMediumId", oldGasMediumId);
        }

        if (!TextUtils.isEmpty(newGasMediumId)) {
            subParam.put("newGasMediumId", newGasMediumId);
        }

        for (CheckItemBean c : checkItems) {
            subParam.put(c.getApiParam(), c.isState()?"1":"0");
        }

        Observable<HttpResponseResult> userObservable = userModel.submitCyChangeMediumRecord(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("submitCyChangeMediumRecord",httpResult);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("submitCyChangeMediumRecord","needUpdate");
                        } else {
                            callback.failure("submitCyChangeMediumRecord",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("submitCyChangeMediumRecord",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *查询集格
     * @param
     */
    public void searchUnitsSet(String setName, String setNumber) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId",queryUser().getUnitId());
        if (!TextUtils.isEmpty(setName)) {
            subParam.put("name", setName);
        }
        if (!TextUtils.isEmpty(setNumber)) {
            subParam.put("setNumber", setNumber);
        }

        Observable<HttpResponseResult> userObservable = userModel.searchUnitsSet(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("searchUnitsSet",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("searchUnitsSet","needUpdate");
                        } else {
                            callback.failure("searchUnitsSet",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("searchUnitsSet",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *查询集格下的气瓶
     * @param
     */
    public void getCylinderListBySetId(String setId) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId",queryUser().getUnitId());
        subParam.put("setId",setId);

        Observable<HttpResponseResult> userObservable = userModel.getCylinderListBySetId(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getCylinderListBySetId",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getCylinderListBySetId","needUpdate");
                        } else {
                            callback.failure("getCylinderListBySetId",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("getCylinderListBySetId",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }
    /***
     *查询气瓶制造商
     * @param
     */
    public void searchCylinderManufacturer(String manuCode) {

        String finaStr = manuCode.toUpperCase();
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId",queryUser().getUnitId());
        if (!TextUtils.isEmpty(finaStr)) {

            if(ServiceLogicUtils.isCyManufactureCode(finaStr)) {
                subParam.put("code", finaStr);
            } else {
                subParam.put("licenseNo", finaStr);
            }
        }

        Observable<HttpResponseResult> userObservable = userModel.searchCylinderManufacturer(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("searchCylinderManufacturer",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("searchCylinderManufacturer","needUpdate");
                        } else {
                            callback.failure("searchCylinderManufacturer",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("searchCylinderManufacturer",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *查询气瓶最后一次收发记录
     * @param
     */
    public void getCylinderLastTransmitRecord(String cyId, String transceiverType) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("cylinderId",cyId);
        subParam.put("transceiverType",transceiverType);

        Observable<HttpResponseResult> userObservable = userModel.getCylinderLastTransmitRecord(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getCylinderLastTransmitRecord",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getCylinderLastTransmitRecord","needUpdate");
                        } else {
                            callback.failure("getCylinderLastTransmitRecord",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("getCylinderLastTransmitRecord",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *查询气瓶最后一次充装记录
     * @param
     */
    public void getCylinderLastChargeRecord(String cyId) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("cylinderId",cyId);

        Observable<HttpResponseResult> userObservable = userModel.getCylinderLastChargeRecord(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getCylinderLastChargeRecord",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getCylinderLastChargeRecord","needUpdate");
                        } else {
                            callback.failure("getCylinderLastChargeRecord",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("getCylinderLastTransmitRecord",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *查询气瓶批次号
     * @param
     */
    public void searchBatchNumber(String batchNumber) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("batchNumber",batchNumber);

        Observable<HttpResponseResult> userObservable = userModel.searchBatchNumber(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("searchBatchNumber",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("searchBatchNumber","needUpdate");
                        } else {
                            callback.failure("searchBatchNumber",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("searchBatchNumber",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *查询集格和格中气瓶
     * @param
     */
    public void getSetWithCylinderListBySetId(String setId) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("setId",setId);

        Observable<HttpResponseResult> userObservable = userModel.getSetWithCylinderListBySetId(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            if (httpResult.getData() == null)
                                callback.successful("getSetWithCylinderListBySetId",null);
                            Type type = new TypeToken<SetBean>(){}.getType();
                            Gson gson = new Gson();
                            JSONObject object=new JSONObject((LinkedTreeMap)httpResult.getData());
                            String mData = object.toString();
                            SetBean setBean = gson.fromJson(mData,type);

                            callback.successful("getSetWithCylinderListBySetId",setBean);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getSetWithCylinderListBySetId","needUpdate");
                        } else {
                            callback.failure("getSetWithCylinderListBySetId",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("getSetWithCylinderListBySetId",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *删除任务
     * @param
     */
    public void deleteChargeMissionByMissionId(String missionId) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("id",missionId);

        Observable<HttpResponseResult> userObservable = userModel.deleteChargeMissionByMissionId(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            if (httpResult.getData() == null)
                                callback.successful("deleteChargeMissionByMissionId",null);
                            Type type = new TypeToken<ChargeMissionBean>(){}.getType();
                            Gson gson = new Gson();
                            JSONObject object=new JSONObject((LinkedTreeMap)httpResult.getData());
                            String mData = object.toString();
                            ChargeMissionBean infoBean =gson.fromJson(mData,type);
                            callback.successful("deleteChargeMissionByMissionId",infoBean);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("deleteChargeMissionByMissionId","needUpdate");
                        } else {
                            callback.failure("deleteChargeMissionByMissionId",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("deleteChargeMissionByMissionId",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }


    /***
     *查询订单号
     * @param
     */
    public void searchTransOrderNumber(String number) {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("number",number);

        Observable<HttpResponseResult> userObservable = userModel.searchTransOrderNumber(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("searchTransOrderNumber",httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("searchTransOrderNumber","needUpdate");
                        } else {
                            callback.failure("searchTransOrderNumber",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("searchTransOrderNumber",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *查询验空批次
     * @param
     */
    public void getPreChargeDetectionBatchList() {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId", queryUser().getUnitId());
        subParam.put("creator", queryUser().getEmployeeName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        subParam.put("begin", sdf.format(ServiceLogicUtils.getChargeClassBeginTime(new Date())));
        subParam.put("end", sdf.format(new Date()));

        Observable<HttpResponseResult> userObservable = userModel.getPreChargeDetectionBatchList(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getPreChargeDetectionBatchList", httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getPreChargeDetectionBatchList","needUpdate");
                        } else {
                            callback.failure("getPreChargeDetectionBatchList",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("getPreChargeDetectionBatchList",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *查询验满批次
     * @param
     */
    public void getPostChargeDetectionBatchList() {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId", queryUser().getUnitId());
        subParam.put("creator", queryUser().getEmployeeName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        subParam.put("begin", sdf.format(ServiceLogicUtils.getChargeClassBeginTime(new Date())));
        subParam.put("end", sdf.format(new Date()));

        Observable<HttpResponseResult> userObservable = userModel.getPostChargeDetectionBatchList(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getPostChargeDetectionBatchList", httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getPostChargeDetectionBatchList","needUpdate");
                        } else {
                            callback.failure("getPostChargeDetectionBatchList",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("getPostChargeDetectionBatchList",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }


    /***
     *提交出货记录
     * @param
     */
    public void submitStockOutRecord(ArrayList<String> cyIdList,
                                     String shipNumber,
                                     String remark) {

        Map<String, Object> subParam = new HashMap<>();

        subParam.put("unitId", queryUser().getUnitId());
        subParam.put("employeeId", queryUser().getId());
        if(cyIdList.size() == 1) {
            subParam.put("cylinderId", cyIdList.get(0));
        } else {
            StringBuilder cyStr = new StringBuilder();
            for(String cyId : cyIdList) {
                cyStr.append(cyId+",");
            }
            subParam.put("cylinderIdList", cyStr.substring(0,cyStr.length()-1));
        }
        subParam.put("shipNumber", shipNumber);

        if (!TextUtils.isEmpty(remark)) {
            subParam.put("remark", remark);
        }
        Observable<HttpResponseResult> userObservable = userModel.submitStockOutRecord(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("submitStockOutRecord",httpResult);
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("submitStockOutRecord","needUpdate");
                        } else {
                            callback.failure("submitStockOutRecord",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("submitStockOutRecord",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    /***
     *查询出货批次
     * @param
     */
    public void getStockOutRecord() {

        Map<String, Object> subParam = new HashMap<>();
        subParam.put("unitId", queryUser().getUnitId());
        subParam.put("employeeId", queryUser().getId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.roll(Calendar.DATE, -3);
        subParam.put("begin", sdf.format(calendar.getTime()));
        calendar.setTime(new Date());
        calendar.roll(Calendar.DATE, 1);
        subParam.put("end", sdf.format(calendar.getTime()));

        Observable<HttpResponseResult> userObservable = userModel.getStockOutRecord(subParam);
        userObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HttpResponseResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setDisposable(d);
                    }

                    @Override
                    public void onNext(HttpResponseResult httpResult) {
                        if (httpResult.getCode().equals("200")) {
                            callback.successful("getStockOutRecord", httpResult.getData());
                        } else if(httpResult.getCode().equals("501")){
                            callback.failure("getStockOutRecord","needUpdate");
                        } else {
                            callback.failure("getStockOutRecord",httpResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.failure("getStockOutRecord",errorMassage(e));
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }
}
