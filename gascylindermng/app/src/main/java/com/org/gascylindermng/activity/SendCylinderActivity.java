package com.org.gascylindermng.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.activity.CaptureActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.org.gascylindermng.R;
import com.org.gascylindermng.adapter.MySimpleSpinnerAdapter;
import com.org.gascylindermng.api.HttpResponseResult;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.CarBean;
import com.org.gascylindermng.bean.CustomerInfoBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.EmployeeBean;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.CommonTools;
import com.org.gascylindermng.tools.ParseXMLWithPull;
import com.org.gascylindermng.tools.PermissionPageUtils;
import com.org.gascylindermng.tools.ServiceLogicUtils;
import com.org.gascylindermng.tools.UrlUtils;
import com.org.gascylindermng.view.WrapContentListView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class SendCylinderActivity extends BaseActivity implements ApiCallback {
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.full_cy)
    ImageView fullCy;
    @BindView(R.id.empty_cy)
    ImageView emptyCy;
    @BindView(R.id.edittext_trans_order)
    EditText edittextTransOrder;
    @BindView(R.id.edittext_customer_order)
    EditText edittextCustomerOrder;
    @BindView(R.id.edittext_customer)
    EditText edittextCustomer;
    @BindView(R.id.customer_listview)
    WrapContentListView customerListview;
    @BindView(R.id.edittext_car_number)
    EditText edittextCarNumber;
    @BindView(R.id.car_number_listview)
    WrapContentListView carNumberListview;
    @BindView(R.id.edittext_driver)
    EditText edittextDriver;
    @BindView(R.id.driver_listview)
    WrapContentListView driverListview;
    @BindView(R.id.edittext_supercargo)
    EditText edittextSupercargo;
    @BindView(R.id.supercargo_listview)
    WrapContentListView supercargoListview;
    @BindView(R.id.cy_count_textview)
    TextView cyCountTextview;

    private UserPresenter userPresenter;
    private ArrayList<String> lastScanCyPlatformCodeList;
    private ArrayList<String> lastScanCySetPlatformIdList;
    private ArrayList<CylinderInfoBean> cyList;

    //打开扫描界面请求码
    private int REQUEST_CODE_1 = 0x01;
    private int REQUEST_CODE_2 = 0x02;
    private int REQUEST_CODE_3 = 0x03;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    private boolean didSelected = false;
    private boolean isEmptyCy = false;
    private boolean isSearchingSupercargo = false; //false:driver   true:supercargo

    private ArrayList<CustomerInfoBean> customerList;
    private CustomerInfoBean selectedCustomer;
    MySimpleSpinnerAdapter customerListAdapter;

    private ArrayList<EmployeeBean> driverList;
    private EmployeeBean selectedDriver;
    MySimpleSpinnerAdapter driverListAdapter;

    private ArrayList<EmployeeBean> supercargoList;
    private EmployeeBean selectedSupercargo;
    MySimpleSpinnerAdapter supercargoListAdapter;

    private ArrayList<CarBean> carList;
    private CarBean selectedCar;
    MySimpleSpinnerAdapter carListAdapter;

    boolean getOrderInfoFromERP = false;
    LinkedTreeMap erpOrderInfo;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_send);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("发瓶卸货");
        userPresenter = new UserPresenter(this);
        customerList = new ArrayList<CustomerInfoBean>();
        driverList = new ArrayList<EmployeeBean>();
        supercargoList = new ArrayList<EmployeeBean>();
        carList = new ArrayList<CarBean>();
        lastScanCyPlatformCodeList = new ArrayList<String>();
        lastScanCySetPlatformIdList = new ArrayList<String>();
        cyList = new ArrayList<CylinderInfoBean>();

        customerListAdapter = new MySimpleSpinnerAdapter(this);
        customerListview.setAdapter(customerListAdapter);
        customerListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                didSelected = true;
                selectedCustomer = customerList.get(position);
                edittextCustomer.setText(selectedCustomer.getCustomerName());
                customerListAdapter.deleteAllData();
                customerListview.setVisibility(View.GONE);

            }
        });

        driverListAdapter = new MySimpleSpinnerAdapter(this);
        driverListview.setAdapter(driverListAdapter);
        driverListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                didSelected = true;
                selectedDriver = driverList.get(position);
                edittextDriver.setText(selectedDriver.getEmployeeName());
                driverListAdapter.deleteAllData();
                driverListview.setVisibility(View.GONE);

            }
        });

        supercargoListAdapter = new MySimpleSpinnerAdapter(this);
        supercargoListview.setAdapter(supercargoListAdapter);
        supercargoListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                didSelected = true;
                selectedSupercargo = supercargoList.get(position);
                edittextSupercargo.setText(selectedSupercargo.getEmployeeName());
                supercargoListAdapter.deleteAllData();
                supercargoListview.setVisibility(View.GONE);

            }
        });

        carListAdapter = new MySimpleSpinnerAdapter(this);
        carNumberListview.setAdapter(carListAdapter);
        carNumberListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                didSelected = true;
                selectedCar = carList.get(position);
                edittextCarNumber.setText(selectedCar.getCarNumber());
                carListAdapter.deleteAllData();
                carNumberListview.setVisibility(View.GONE);

            }
        });

        edittextCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (didSelected) {
                    didSelected = false;
                    return;
                }

                if (!TextUtils.isEmpty(s)) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.searchCustomer(s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    customerList.clear();
                    customerListAdapter.deleteAllData();
                    customerListview.setVisibility(View.GONE);
                    selectedCustomer = null;
                }
            }
        });

        edittextDriver.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                isSearchingSupercargo = false;
                if (didSelected) {
                    didSelected = false;
                    return;
                }

                if (!TextUtils.isEmpty(s)) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.searchEmployee(s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    driverList.clear();
                    driverListAdapter.deleteAllData();
                    driverListview.setVisibility(View.GONE);
                    selectedDriver = null;
                }
            }
        });

        edittextSupercargo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                isSearchingSupercargo = true;
                if (didSelected) {
                    didSelected = false;
                    return;
                }

                if (!TextUtils.isEmpty(s)) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.searchEmployee(s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    supercargoList.clear();
                    supercargoListAdapter.deleteAllData();
                    supercargoListview.setVisibility(View.GONE);
                    selectedSupercargo = null;
                }
            }
        });

        edittextCarNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {

                if (didSelected) {
                    didSelected = false;
                    return;
                }

                if (!TextUtils.isEmpty(s)) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.searchCarNumber(s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    carList.clear();
                    carListAdapter.deleteAllData();
                    carNumberListview.setVisibility(View.GONE);
                    selectedCar = null;
                }
            }
        });
    }

    @OnClick({R.id.back_img,
            R.id.full_cy,
            R.id.empty_cy,
            R.id.trans_order_scanner,
            R.id.customer_order_scanner,
            R.id.cy_scanner,
            R.id.submit,
            R.id.cy_count_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.full_cy:
                isEmptyCy = false;
                fullCy.setImageResource(R.mipmap.item_check_on);
                emptyCy.setImageResource(R.mipmap.item_check_off);
                break;
            case R.id.empty_cy:
                isEmptyCy = true;
                fullCy.setImageResource(R.mipmap.item_check_off);
                emptyCy.setImageResource(R.mipmap.item_check_on);
                break;
            case R.id.trans_order_scanner:
                if (CommonTools.isCameraCanUse()) {
                    Intent intent = new Intent(SendCylinderActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_1);
                } else {
                    showToast("请打开此应用的摄像头权限！");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PermissionPageUtils u = new PermissionPageUtils(getBaseContext());
                            u.jumpPermissionPage();
                        }
                    }, 1000);
                }
                break;
            case R.id.customer_order_scanner:
                if (CommonTools.isCameraCanUse()) {
                    Intent intent = new Intent(SendCylinderActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_2);
                } else {
                    showToast("请打开此应用的摄像头权限！");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PermissionPageUtils u = new PermissionPageUtils(getBaseContext());
                            u.jumpPermissionPage();
                        }
                    }, 1000);
                }
                break;
            case R.id.cy_scanner:
                if (CommonTools.isCameraCanUse()) {
                    Intent intent = new Intent(SendCylinderActivity.this, CaptureActivity.class);
                    intent.putExtra("mode", ServiceLogicUtils.scan_multi);
                    startActivityForResult(intent, REQUEST_CODE_3);
                } else {
                    showToast("请打开此应用的摄像头权限！");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PermissionPageUtils u = new PermissionPageUtils(getBaseContext());
                            u.jumpPermissionPage();
                        }
                    }, 1000);
                }
                break;
            case R.id.cy_count_layout:
                Intent intent = new Intent(SendCylinderActivity.this, CyListActivity.class);
                intent.putExtra("CyBeanlist", cyList);
                startActivity(intent);
                break;
            case R.id.submit:

                if (cyList.size() == 0) {
                    showToast("您还未扫描任何气瓶！");
                    return;
                }

                if (TextUtils.isEmpty(edittextTransOrder.getText().toString())) {
                    showToast("请输入运单号！");
                    return;
                }

//                if (selectedCustomer == null) {
//
//                    if (TextUtils.isEmpty(edittextCustomer.getText().toString())) {
//                        showToast("请输入客户名！");
//                    } else {
//                        showToast("该客户不存在，请先在后台录入！");
//                    }
//                    return;
//                }

//                if (selectedDriver == null) {
//
//                    if (TextUtils.isEmpty(edittextDriver.getText().toString())) {
//                        showToast("请输入司机名！");
//                    } else {
//                        showToast("该员工不存在，请先在后台录入！");
//                    }
//                    return;
//                }
//
//                if (selectedSupercargo == null) {
//
//                    if (TextUtils.isEmpty(edittextSupercargo.getText().toString())) {
//                        showToast("请输入押运员名！");
//                    } else {
//                        showToast("该员工不存在，请先在后台录入！");
//                    }
//                    return;
//                }
//
//                if (selectedCar == null) {
//
//                    if (TextUtils.isEmpty(edittextCarNumber.getText().toString())) {
//                        showToast("请输入车牌号！");
//                    } else {
//                        showToast("该车牌号不存在，请先在后台录入！");
//                    }
//                    return;
//                }

                LinearLayout llname = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.view_operation_dialog, null);
                final TextView textView = (TextView) llname.findViewById(R.id.message);
                textView.setText("是否确认信息正确并提交？");

                final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
                btnConfirm.setText("确认");
                final TextView btnCancel = (TextView) llname.findViewById(R.id.dialog_btn_cancel);

                AlertDialog.Builder builder = new AlertDialog.Builder(SendCylinderActivity.this);
                final AlertDialog dialog = builder.setView(llname).create();
                dialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //get info ERP
                        requestSalesOrderInfoFromERPBySalesOrderCode();

                        /*
                        ArrayList<String> cyIdList = new ArrayList<>();
                        for (CylinderInfoBean info : sendCyList) {
                            cyIdList.add(info.getCyId());
                        }

                        userPresenter.submitTransmitReceiveRecord(isEmptyCy ? "1" : "0", //0否1是
                                "2", //1，回收；2，发出
                                "1", //0否1是
                                edittextTransOrder.getText().toString().trim(),
                                selectedCustomer!=null?selectedCustomer.getCustomerId():null,
                                "",
                                selectedDriver!=null?selectedDriver.getEmployeeId():null,
                                selectedSupercargo!=null?selectedSupercargo.getEmployeeId():null,
                                selectedCar!=null?selectedCar.getCarId():null,
                                "",
                                cyIdList);

                               */

                        dialog.dismiss();

                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                break;
        }
    }

    private void requestSalesOrderInfoFromERPBySalesOrderCode() {

        String mData = "transId=" + edittextTransOrder.getText().toString().trim();
//        String mData = "transId=" + "XS-170509-0003"; //test
        StringEntity entity = null;

        try {
            entity = new StringEntity(mData, "utf-8");
            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8"));
        } catch (Exception e) {
        }

        String url = "http://gas777.iask.in:89/ServiceControl/JavaService.ashx";

        new AsyncHttpClient().post(null, url, entity, "application/x-www-form-urlencoded; charset=utf-8", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                HttpResponseResult bean = null;
                try {
                    Type type = new TypeToken<HttpResponseResult>() {
                    }.getType();
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String mData = jsonObject.toString();
                    bean = gson.fromJson(mData, type);

                    if (bean == null) {

                        submitTransRecord();

                    } else {
                        erpOrderInfo = (LinkedTreeMap) bean.getData();
                        // "transId":"XS-170509-0003"
                        // "warehouseName":"化工区基地",

                        // "customerName":"上海应用技术学院",
                        // "driverName":"刘林",
                        // "updateBy":"陈杰",
                        // "carNumber":"沪D49596",
                        getOrderInfoFromERP = true;
                        userPresenter.searchCustomer(erpOrderInfo.get("customerName").toString());
                    }

                } catch (Exception e) {
                    submitTransRecord();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showToast("非凡通连接异常，无法取得发货单信息！");
                submitTransRecord();
            }
        });

    }


    @Override
    public <T> void successful(String api, T success) {
        if (api.equals("getCylinderListBySetId")) {

            List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
            for (LinkedTreeMap tm : list) {
                Type type = new TypeToken<CylinderInfoBean>() {
                }.getType();
                Gson gson = new Gson();
                JSONObject object = new JSONObject(tm);
                String mData = object.toString();
                CylinderInfoBean cyBean = gson.fromJson(mData, type);
                if (cyBean != null) {
                    cyList.add(cyBean);
                    cyCountTextview.setText("已扫描气瓶 " + cyList.size() + " 个");
                }
            }
            lastScanCySetPlatformIdList.remove(0);
            if (lastScanCySetPlatformIdList.size() > 0) {
                new Thread() {
                    @Override
                    public void run() {
                        //在子线程中进行下载操作
                        try {
                            userPresenter.getCylinderListBySetId(lastScanCySetPlatformIdList.get(0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

        } else if (api.equals("getCylinderInfoByPlatformCyNumber")) {

            if (success != null && success instanceof CylinderInfoBean) {

                cyList.add((CylinderInfoBean) success);
                cyCountTextview.setText("已扫描气瓶 "+cyList.size()+" 个");
                lastScanCyPlatformCodeList.remove(0);
                if (lastScanCyPlatformCodeList.size() > 0) {

                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.getCylinderInfoByPlatformCyNumber(lastScanCyPlatformCodeList.get(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else if (lastScanCySetPlatformIdList.size() > 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.getCylinderListBySetId(lastScanCySetPlatformIdList.get(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        } else if (api.equals("submitTransmitReceiveRecord")) {

            showToast("提交成功");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);

        } else if (api.equals("searchCustomer")) {

            selectedCustomer = null;
            customerList.clear();

            List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
            for (LinkedTreeMap tm : list) {
                Type type = new TypeToken<CustomerInfoBean>() {
                }.getType();
                Gson gson = new Gson();
                JSONObject object = new JSONObject(tm);
                String mData = object.toString();
                CustomerInfoBean customerInfoBean = gson.fromJson(mData, type);
                customerList.add(customerInfoBean);
            }
//            if (customerList.size() == 1 &&  customerList.get(0).getCustomerName().equals(cyOwner.getText().toString())) {
//                return;
//            }

            if(getOrderInfoFromERP) {
                if (customerList.size() > 0) {
                    this.selectedCustomer = customerList.get(0);
                }
                new Thread() {
                    @Override
                    public void run() {
                        //在子线程中进行下载操作
                        try {
                            userPresenter.searchEmployee(erpOrderInfo.get("driverName").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();


            } else {

                ArrayList<String> cusList = new ArrayList<String>();
                for (CustomerInfoBean bean : customerList) {
                    cusList.add(bean.getCustomerName());
                }
                if (cusList.size() > 0) {
                    customerListview.setVisibility(View.VISIBLE);
                    customerListAdapter.updateData(cusList);
                } else {
                    customerListview.setVisibility(View.GONE);
                    customerListAdapter.deleteAllData();
                }
            }

        } else if (api.equals("searchEmployee")) {

            if (getOrderInfoFromERP) {

                List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
                if (list == null || list.size() == 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.searchCarNumber(erpOrderInfo.get("carNumber").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    return;
                }
                Type type = new TypeToken<EmployeeBean>() {
                }.getType();
                Gson gson = new Gson();
                JSONObject object = new JSONObject(list.get(0));
                String mData = object.toString();
                EmployeeBean infoBean = gson.fromJson(mData, type);
                if (infoBean.getPosition().equals("1") || infoBean.getPosition().equals("2")) {
                    if (selectedDriver == null) {
                        this.selectedDriver = infoBean;
                        new Thread() {
                            @Override
                            public void run() {
                                //在子线程中进行下载操作
                                try {
                                    userPresenter.searchEmployee(erpOrderInfo.get("updateBy").toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else {
                        this.selectedSupercargo = infoBean;
                        new Thread() {
                            @Override
                            public void run() {
                                //在子线程中进行下载操作
                                try {
                                    userPresenter.searchCarNumber(erpOrderInfo.get("carNumber").toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }


            } else {

                if (isSearchingSupercargo) {

                    selectedSupercargo = null;
                    supercargoList.clear();

                    List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
                    for (LinkedTreeMap tm : list) {
                        Type type = new TypeToken<EmployeeBean>() {
                        }.getType();
                        Gson gson = new Gson();
                        JSONObject object = new JSONObject(tm);
                        String mData = object.toString();
                        EmployeeBean infoBean = gson.fromJson(mData, type);
                        if (infoBean.getPosition().equals("1") || infoBean.getPosition().equals("2")) {
                            supercargoList.add(infoBean);
                        }
                    }

                    ArrayList<String> nameList = new ArrayList<String>();
                    for (EmployeeBean bean : supercargoList) {
                        nameList.add(bean.getEmployeeName());
                    }
                    if (nameList.size() > 0) {
                        supercargoListview.setVisibility(View.VISIBLE);
                        supercargoListAdapter.updateData(nameList);
                    } else {
                        supercargoListview.setVisibility(View.GONE);
                        supercargoListAdapter.deleteAllData();
                    }

                } else {

                    selectedDriver = null;
                    driverList.clear();

                    List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
                    for (LinkedTreeMap tm : list) {
                        Type type = new TypeToken<EmployeeBean>() {
                        }.getType();
                        Gson gson = new Gson();
                        JSONObject object = new JSONObject(tm);
                        String mData = object.toString();
                        EmployeeBean infoBean = gson.fromJson(mData, type);
                        if (infoBean.getPosition().equals("1") || infoBean.getPosition().equals("2")) {
                            driverList.add(infoBean);
                        }
                    }
                    ArrayList<String> nameList = new ArrayList<String>();
                    for (EmployeeBean bean : driverList) {
                        nameList.add(bean.getEmployeeName());
                    }
                    if (nameList.size() > 0) {
                        driverListview.setVisibility(View.VISIBLE);
                        driverListAdapter.updateData(nameList);
                    } else {
                        driverListview.setVisibility(View.GONE);
                        driverListAdapter.deleteAllData();
                    }
                }
            }
        } else if (api.equals("searchCarNumber")) {

            selectedCar = null;
            carList.clear();

            List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
            for (LinkedTreeMap tm : list) {
                Type type = new TypeToken<CarBean>() {
                }.getType();
                Gson gson = new Gson();
                JSONObject object = new JSONObject(tm);
                String mData = object.toString();
                CarBean carInfoBean = gson.fromJson(mData, type);
                carList.add(carInfoBean);
            }

            if (getOrderInfoFromERP) {
                if (carList.size() > 0) {
                    this.selectedCar = carList.get(0);
                }
                getOrderInfoFromERP = false;
                submitTransRecord();

            } else {
                ArrayList<String> carNumberList = new ArrayList<String>();
                for (CarBean bean : carList) {
                    carNumberList.add(bean.getCarNumber());
                }
                if (carNumberList.size() > 0) {
                    carNumberListview.setVisibility(View.VISIBLE);
                    carListAdapter.updateData(carNumberList);
                } else {
                    carNumberListview.setVisibility(View.GONE);
                    carListAdapter.deleteAllData();
                }
            }

        }
    }

    private void submitTransRecord() {

            new Thread() {
                @Override
                public void run() {
                    //在子线程中进行下载操作
                    try {
                        final ArrayList<String> cyIdList = new ArrayList<>();
                            for (CylinderInfoBean info : cyList) {
                                cyIdList.add(info.getCyId());
                            }
                    userPresenter.submitTransmitReceiveRecord(isEmptyCy ? "1" : "0", //0否1是
                            "2", //1，回收；2，发出
                            "1", //0否1是
                            edittextTransOrder.getText().toString(),
                            selectedCustomer!=null?selectedCustomer.getCustomerId():null,
                            "",
                            selectedDriver!=null?selectedDriver.getEmployeeId():null,
                            selectedSupercargo!=null?selectedSupercargo.getEmployeeId():null,
                            selectedCar!=null?selectedCar.getCarId():null,
                            "",
                            cyIdList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public <T> void failure(String api, T failure) {
        if (failure instanceof String && ((String)failure).equals("needUpdate")) {

            super.updateApp();
            return;
        }
        if (api.equals("submitTransmitReceiveRecord")) {
            showToast("提交失败，" + (String) failure);
        } else {
            showToast("接口报错，" + (String) failure);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (resultCode == RESULT_OK) { //RESULT_OK = -1

            Bundle bundle = data.getExtras();
            ArrayList<String> result = bundle.getStringArrayList("qr_scan_result");

            if (requestCode == REQUEST_CODE_1) {
                if(result != null && result.size() > 0) {

                    edittextTransOrder.setText(result.get(0));
                }
            } else if (requestCode == REQUEST_CODE_2) {
                if(result != null && result.size() > 0) {

                    //edittextCustomerOrder.setText(result.get(0));
                }
            } else if (requestCode == REQUEST_CODE_3) {

                ArrayList<CylinderInfoBean> allCyList = (ArrayList<CylinderInfoBean>)bundle.getSerializable("qr_scan_result_all_cy_list");
                if (allCyList != null && allCyList.size() > 0) {
                    cyList.addAll(allCyList);
                }
                cyCountTextview.setText("已扫描气瓶 " + cyList.size() + " 个");

//                for (String r: result) {
//                    ServiceLogicUtils.getCylinderPlatformNumberOrSetIdFromScanResult(r, lastScanCySetPlatformIdList, lastScanCyPlatformCodeList);
//                }

//                if (lastScanCyPlatformCodeList.size() > 0) {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            //在子线程中进行下载操作
//                            try {
//                                userPresenter.getCylinderInfoByPlatformCyNumber(lastScanCyPlatformCodeList.get(0));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();
//                } else if (lastScanCySetPlatformIdList.size() > 0) {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            //在子线程中进行下载操作
//                            try {
//                                userPresenter.getCylinderListBySetId(lastScanCySetPlatformIdList.get(0));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();
//                } else {
//                    showToast("无有效气瓶二维码");
//                }
            }
        } else {
            //showToast("扫描失败");
        }
    }
}
