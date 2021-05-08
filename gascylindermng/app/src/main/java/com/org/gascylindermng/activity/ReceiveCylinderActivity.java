package com.org.gascylindermng.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.org.gascylindermng.tools.PermissionPageUtils;
import com.org.gascylindermng.tools.ServiceLogicUtils;
import com.org.gascylindermng.tools.UrlUtils;
import com.org.gascylindermng.view.WrapContentListView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_OTHER_SCAN_LIST;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN_SET_LIST;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN_CY_LIST;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST;

public class ReceiveCylinderActivity extends BaseActivity implements ApiCallback {
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.empty_cy)
    ImageView emptyCy;
    @BindView(R.id.full_cy)
    ImageView fullCy;
    @BindView(R.id.edittext_trans_order)
    EditText edittextTransOrder;
//    @BindView(R.id.edittext_customer_order)
//    EditText edittextCustomerOrder;
//    @BindView(R.id.edittext_customer)
//    EditText edittextCustomer;
//    @BindView(R.id.customer_listview)
//    WrapContentListView customerListview;
//    @BindView(R.id.edittext_car_number)
//    EditText edittextCarNumber;
//    @BindView(R.id.car_number_listview)
//    WrapContentListView carNumberListview;
//    @BindView(R.id.edittext_driver)
//    EditText edittextDriver;
//    @BindView(R.id.driver_listview)
//    WrapContentListView driverListview;
//    @BindView(R.id.edittext_supercargo)
//    EditText edittextSupercargo;
//    @BindView(R.id.supercargo_listview)
//    WrapContentListView supercargoListview;
    @BindView(R.id.trans_order_listview)
    WrapContentListView transOrderListview;
    @BindView(R.id.facecheck_result_checkbox)
    ImageView faceCheckResultCheckbox;
    @BindView(R.id.cy_count_textview)
    TextView cyCountTextview;

    private UserPresenter userPresenter;
//    private ArrayList<String> lastScanCyPlatformCodeList;
//    private ArrayList<String> lastScanCySetPlatformIdList;
    private ArrayList<CylinderInfoBean> cyList;


    //打开扫描界面请求码
    private int REQUEST_CODE_1 = 0x01;
    private int REQUEST_CODE_2 = 0x02;
    private int REQUEST_CODE_3 = 0x03;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    private boolean didSelected = false;
    private boolean isEmptyCy = true;
    private boolean faceCheckOk = false;
    private boolean isSearchingSupercargo = false; //false:driver   true:supercargo

    private ArrayList<CustomerInfoBean> customerList;
    private CustomerInfoBean selectedCustomer;
//    MySimpleSpinnerAdapter customerListAdapter;

    private ArrayList<EmployeeBean> driverList;
    private EmployeeBean selectedDriver;
//    MySimpleSpinnerAdapter driverListAdapter;

    private ArrayList<EmployeeBean> supercargoList;
    private EmployeeBean selectedSupercargo;
//    MySimpleSpinnerAdapter supercargoListAdapter;

    private ArrayList<CarBean> carList;
    private CarBean selectedCar;
//    MySimpleSpinnerAdapter carListAdapter;

    private ArrayList<String> transOrderList;
    private String selectedTransOrder;
    MySimpleSpinnerAdapter transOrderListAdapter;


    boolean getOrderInfoFromERP = false;
    LinkedTreeMap erpOrderInfo;
    ArrayList<String> driverAndSurNameList;

    private LocationManager gpsManager;
    private String lat;
    private String lng;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_receive);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("客户回瓶");
        userPresenter = new UserPresenter(this);
        customerList = new ArrayList<CustomerInfoBean>();
        driverList = new ArrayList<EmployeeBean>();
        supercargoList = new ArrayList<EmployeeBean>();
        carList = new ArrayList<CarBean>();
        transOrderList = new ArrayList<String>();
        driverAndSurNameList = new ArrayList<String>();
//        lastScanCyPlatformCodeList = new ArrayList<String>();
//        lastScanCySetPlatformIdList = new ArrayList<String>();
        cyList = new ArrayList<CylinderInfoBean>();

        transOrderListAdapter = new MySimpleSpinnerAdapter(this);
        transOrderListview.setAdapter(transOrderListAdapter);
        transOrderListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                didSelected = true;
                selectedTransOrder = transOrderList.get(position);
                edittextTransOrder.setText(selectedTransOrder);
                transOrderListAdapter.deleteAllData();
                transOrderListview.setVisibility(View.GONE);

            }
        });

        edittextTransOrder.addTextChangedListener(new TextWatcher() {
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

                transOrderList.clear();
                transOrderListAdapter.deleteAllData();
                transOrderListview.setVisibility(View.GONE);
                selectedTransOrder = null;
                if (!TextUtils.isEmpty(s) && s.toString().length()>5) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.searchTransOrderNumber(s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
            }
        });

//        customerListAdapter = new MySimpleSpinnerAdapter(this);
//        customerListview.setAdapter(customerListAdapter);
//        customerListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                didSelected = true;
//                selectedCustomer = customerList.get(position);
//                edittextCustomer.setText(selectedCustomer.getCustomerName());
//                customerListAdapter.deleteAllData();
//                customerListview.setVisibility(View.GONE);
//
//            }
//        });
//
//        driverListAdapter = new MySimpleSpinnerAdapter(this);
//        driverListview.setAdapter(driverListAdapter);
//        driverListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                didSelected = true;
//                selectedDriver = driverList.get(position);
//                edittextDriver.setText(selectedDriver.getEmployeeName());
//                driverListAdapter.deleteAllData();
//                driverListview.setVisibility(View.GONE);
//
//            }
//        });
//
//        supercargoListAdapter = new MySimpleSpinnerAdapter(this);
//        supercargoListview.setAdapter(supercargoListAdapter);
//        supercargoListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                didSelected = true;
//                selectedSupercargo = supercargoList.get(position);
//                edittextSupercargo.setText(selectedSupercargo.getEmployeeName());
//                supercargoListAdapter.deleteAllData();
//                supercargoListview.setVisibility(View.GONE);
//
//            }
//        });
//
//        carListAdapter = new MySimpleSpinnerAdapter(this);
//        carNumberListview.setAdapter(carListAdapter);
//        carNumberListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                didSelected = true;
//                selectedCar = carList.get(position);
//                edittextCarNumber.setText(selectedCar.getCarNumber());
//                carListAdapter.deleteAllData();
//                carNumberListview.setVisibility(View.GONE);
//
//            }
//        });
//
//        edittextCustomer.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(final Editable s) {
//                if (didSelected) {
//                    didSelected = false;
//                    return;
//                }
//
//                if (!TextUtils.isEmpty(s)) {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            //在子线程中进行下载操作
//                            try {
//                                userPresenter.searchCustomer(s.toString());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();
//                } else {
//                    customerList.clear();
//                    customerListAdapter.deleteAllData();
//                    customerListview.setVisibility(View.GONE);
//                    selectedCustomer = null;
//                }
//            }
//        });
//
//        edittextDriver.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(final Editable s) {
//                isSearchingSupercargo = false;
//                if (didSelected) {
//                    didSelected = false;
//                    return;
//                }
//
//                if (!TextUtils.isEmpty(s)) {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            //在子线程中进行下载操作
//                            try {
//                                userPresenter.searchEmployee(s.toString());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();
//                } else {
//                    driverList.clear();
//                    driverListAdapter.deleteAllData();
//                    driverListview.setVisibility(View.GONE);
//                    selectedDriver = null;
//                }
//            }
//        });
//
//        edittextSupercargo.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(final Editable s) {
//                isSearchingSupercargo = true;
//                if (didSelected) {
//                    didSelected = false;
//                    return;
//                }
//
//                if (!TextUtils.isEmpty(s)) {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            //在子线程中进行下载操作
//                            try {
//                                userPresenter.searchEmployee(s.toString());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();
//                } else {
//                    supercargoList.clear();
//                    supercargoListAdapter.deleteAllData();
//                    supercargoListview.setVisibility(View.GONE);
//                    selectedSupercargo = null;
//                }
//            }
//        });
//
//        edittextCarNumber.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(final Editable s) {
//
//                if (didSelected) {
//                    didSelected = false;
//                    return;
//                }
//
//                if (!TextUtils.isEmpty(s)) {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            //在子线程中进行下载操作
//                            try {
//                                userPresenter.searchCarNumber(s.toString());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();
//                } else {
//                    carList.clear();
//                    carListAdapter.deleteAllData();
//                    carNumberListview.setVisibility(View.GONE);
//                    selectedCar = null;
//                }
//            }
//        });

        gpsManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showToast("请开启此应用的位置权限...");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PermissionPageUtils u = new PermissionPageUtils(getBaseContext());
                    u.jumpPermissionPage();
                }
            }, 1000);
        } else {
            startGps();
        }
    }

    //开始GPS定位
    @SuppressLint("MissingPermission")
    private void startGps() {

        //1.获取到LocationManager对象
        gpsManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //2.得到定位服务者provider:provider可为gps定位，也可为为基站和WIFI定位
        String provider = gpsManager.getProvider(LocationManager.GPS_PROVIDER).getName();

        //3.设置位置监听器：3000ms为定位的间隔时间，10m为距离变化阀值，gpsListener为回调接口
        gpsManager.requestLocationUpdates(provider, 1000, 0, gpsListener);
    }

    //停止GPS定位
    private void stopGps() {
        gpsManager.removeUpdates(gpsListener);
    }

    private final LocationListener gpsListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        public void onProviderEnabled(String provider) {

        }

        // GPS禁用时触发
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            lat = ""+location.getLatitude();
            lng = ""+location.getLongitude();
            showToast("onLocationChanged. " + "lat:"+lat+"   lng:"+lng);
            stopGps();
        }
    };

    @OnClick({R.id.back_img,
            R.id.empty_cy,
            R.id.full_cy,
//            R.id.customer_order_scanner,
            R.id.trans_order_scanner,
            R.id.facecheck_result_checkbox,
            R.id.cy_scanner,
            R.id.submit,
            R.id.cy_count_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                stopGps();
                finish();
                break;
            case R.id.empty_cy:
                isEmptyCy = true;
                fullCy.setImageResource(R.mipmap.item_check_off);
                emptyCy.setImageResource(R.mipmap.item_check_on);
                break;
            case R.id.full_cy:
                isEmptyCy = false;
                fullCy.setImageResource(R.mipmap.item_check_on);
                emptyCy.setImageResource(R.mipmap.item_check_off);
                break;
//            case R.id.customer_order_scanner:
//                if (CommonTools.isCameraCanUse()) {
//                    Intent intent = new Intent(ReceiveCylinderActivity.this, CaptureActivity.class);
//                    startActivityForResult(intent, REQUEST_CODE_2);
//                } else {
//                    showToast("请打开此应用的摄像头权限！");
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            PermissionPageUtils u = new PermissionPageUtils(getBaseContext());
//                            u.jumpPermissionPage();
//                        }
//                    }, 1000);
//                }
//                break;
            case R.id.trans_order_scanner:
                if (CommonTools.isCameraCanUse()) {
                    Intent intent = new Intent(ReceiveCylinderActivity.this, CaptureActivity.class);
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
            case R.id.facecheck_result_checkbox:
                if (faceCheckOk == true) {
                    faceCheckOk = false;
                    faceCheckResultCheckbox.setImageResource(R.mipmap.item_check_off);
                } else {
                    faceCheckOk = true;
                    faceCheckResultCheckbox.setImageResource(R.mipmap.item_check_on);
                }
                break;
            case R.id.cy_scanner:

                if(TextUtils.isEmpty(lat))  {
                    startGps();
                }

                if (CommonTools.isCameraCanUse()) {
                    Intent intent = new Intent(ReceiveCylinderActivity.this, CaptureActivity.class);
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
                Intent intent = new Intent(ReceiveCylinderActivity.this, CyListActivity.class);
                intent.putExtra("CyBeanlist", cyList);
                startActivity(intent);
                break;
            case R.id.submit:

              //  edittextTransOrder.setText("XS-170509-0003"); //test

                if (cyList.size() == 0) {
                    showToast("您还未扫描任何气瓶！");
                    return;
                }

                if (TextUtils.isEmpty(edittextTransOrder.getText().toString())) {
                    showToast("请输入发货单号！");
                    return;
                }


                if (!faceCheckOk) {
                    showToast("请先完成外观检查！");
                    return;
                }

                LinearLayout llname = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.view_operation_dialog, null);
                final TextView textView = (TextView) llname.findViewById(R.id.message);
                textView.setText("是否确认信息正确并提交？");

                final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
                btnConfirm.setText("确认");
                final TextView btnCancel = (TextView) llname.findViewById(R.id.dialog_btn_cancel);

                AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveCylinderActivity.this);
                final AlertDialog dialog = builder.setView(llname).create();
                dialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //get info ERP
                        requestSalesOrderInfoFromERPBySalesOrderCode();

                        /*
                        ArrayList<String> cyIdList = new ArrayList<>();
                        for (CylinderInfoBean info : receiveCyList) {
                            cyIdList.add(info.getCyId());
                        }

                        userPresenter.submitTransmitReceiveRecord(isEmptyCy ? "1" : "0", //0否1是
                                "1", //1，回收；2，发出
                                faceCheckOk ? "1" : "0", //0否1是
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
    //    String mData = "transId=" + "XS-170509-0003"; //test
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
                        setErpOrderInfo((LinkedTreeMap) bean.getData());
                        // "transId":"XS-170509-0003"
                        // "warehouseName":"化工区基地",

                        // "customerName":"上海应用技术学院",
                        // "driverName":"刘林",
                        // "updateBy":"陈杰",
                        // "carNumber":"沪D49596",
                        getOrderInfoFromERP = true;
                        driverAndSurNameList.clear();
                        String drivers = erpOrderInfo.get("driverName").toString();
                        if (!TextUtils.isEmpty(drivers)) {
                            if (drivers.contains("，")) {
                                driverAndSurNameList.add(drivers.split("，")[0]);
                                driverAndSurNameList.add(drivers.split("，")[1]);
                            } else {
                                driverAndSurNameList.add(drivers);
                            }
                        }
                        String supercargos = erpOrderInfo.get("supercargo").toString();
                        if (!TextUtils.isEmpty(supercargos)) {
                            if (supercargos.contains("，")) {
                                driverAndSurNameList.add(supercargos.split("，")[0]);
                                driverAndSurNameList.add(supercargos.split("，")[1]);
                            } else {
                                driverAndSurNameList.add(supercargos);
                            }
                        }
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

        if (api.equals("searchTransOrderNumber")) {

            selectedTransOrder = null;
            transOrderList.clear();

            List<String> list = (List<String>) success;
            for (String tm : list) {
                transOrderList.add(tm);
            }

            if (transOrderList.size() > 0) {
                transOrderListview.setVisibility(View.VISIBLE);
                transOrderListAdapter.updateData(transOrderList);
            } else {
                transOrderListview.setVisibility(View.GONE);
                transOrderListAdapter.deleteAllData();
            }

        } else
//            if (api.equals("getCylinderListBySetId")) {
//
//            List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
//            for (LinkedTreeMap tm : list) {
//                Type type = new TypeToken<CylinderInfoBean>() {
//                }.getType();
//                Gson gson = new Gson();
//                JSONObject object = new JSONObject(tm);
//                String mData = object.toString();
//                CylinderInfoBean cyBean = gson.fromJson(mData, type);
//                if (cyBean != null) {
//                    cyList.add(cyBean);
//                    cyCountTextview.setText("已扫描气瓶 " + cyList.size() + " 个");
//                }
//            }
//            lastScanCySetPlatformIdList.remove(0);
//            if (lastScanCySetPlatformIdList.size() > 0) {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        //在子线程中进行下载操作
//                        try {
//                            userPresenter.getCylinderListBySetId(lastScanCySetPlatformIdList.get(0));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();
//            }
//
//        } else if (api.equals("getCylinderInfoByPlatformCyNumber")) {
//
//            if (success != null && success instanceof CylinderInfoBean) {
//
//                cyList.add((CylinderInfoBean) success);
//                cyCountTextview.setText("已扫描气瓶 "+cyList.size()+" 个");
//                lastScanCyPlatformCodeList.remove(0);
//                if (lastScanCyPlatformCodeList.size() > 0) {
//
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
//                }
//            }
//        } else
            if (api.equals("submitTransmitReceiveRecord")) {

            LinearLayout llname = (LinearLayout) this.getLayoutInflater()
                    .inflate(R.layout.view_common_dialog, null);
            final TextView textView = (TextView) llname.findViewById(R.id.message);
            textView.setText("提交成功。");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.setView(llname).create();
            dialog.show();
            final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2500);

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

            if(getOrderInfoFromERP) {
                if (customerList.size() > 0) {
                    this.selectedCustomer = customerList.get(0);
                }
                new Thread() {
                    @Override
                    public void run() {
                        //在子线程中进行下载操作
                        try {
                            userPresenter.searchEmployee(driverAndSurNameList.get(0));
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
//                if (cusList.size() > 0) {
//                    customerListview.setVisibility(View.VISIBLE);
//                    customerListAdapter.updateData(cusList);
//                } else {
//                    customerListview.setVisibility(View.GONE);
//                    customerListAdapter.deleteAllData();
//                }
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
                                    userPresenter.searchEmployee(driverAndSurNameList.get(driverAndSurNameList.size()-1));
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
//                    if (nameList.size() > 0) {
//                        supercargoListview.setVisibility(View.VISIBLE);
//                        supercargoListAdapter.updateData(nameList);
//                    } else {
//                        supercargoListview.setVisibility(View.GONE);
//                        supercargoListAdapter.deleteAllData();
//                    }

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
//                    if (nameList.size() > 0) {
//                        driverListview.setVisibility(View.VISIBLE);
//                        driverListAdapter.updateData(nameList);
//                    } else {
//                        driverListview.setVisibility(View.GONE);
//                        driverListAdapter.deleteAllData();
//                    }
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
//                if (carNumberList.size() > 0) {
//                    carNumberListview.setVisibility(View.VISIBLE);
//                    carListAdapter.updateData(carNumberList);
//                } else {
//                    carNumberListview.setVisibility(View.GONE);
//                    carListAdapter.deleteAllData();
//                }
            }

        }
    }

    private void submitTransRecord(){

                new Thread() {
                    @Override
                    public void run() {
                        //在子线程中进行下载操作
                        try {
                            ArrayList<String> cyIdList = new ArrayList<>();
                            for (CylinderInfoBean info : cyList) {
                                cyIdList.add(info.getCyId());
                            }

                            userPresenter.submitTransmitReceiveRecord(isEmptyCy ? "1" : "0", //0否1是
                                    "1", //1，回收；2，发出
                                    faceCheckOk ? "1" : "0", //0否1是
                                    edittextTransOrder.getText().toString(),
                                    selectedCustomer!=null?selectedCustomer.getCustomerId():null,
                                    "",
                                    selectedDriver!=null?selectedDriver.getEmployeeId():null,
                                    selectedSupercargo!=null?selectedSupercargo.getEmployeeId():null,
                                    selectedCar!=null?selectedCar.getCarId():null,
                                    "",
                                    cyIdList,
                                    lng,
                                    lat);
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
            ArrayList<String> result = bundle.getStringArrayList(INTENT_EXTRA_KEY_OTHER_SCAN_LIST);

            if (requestCode == REQUEST_CODE_1) {
                if(result != null && result.size() > 0) {

                    edittextTransOrder.setText(result.get(0));
                }
            } else if (requestCode == REQUEST_CODE_2) {
                if(result != null && result.size() > 0) {

                    //edittextCustomerOrder.setText(result.get(0));
                }
            } else if (requestCode == REQUEST_CODE_3) {

                ArrayList<CylinderInfoBean> allCyList = (ArrayList<CylinderInfoBean>)bundle.getSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST);
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

    public LinkedTreeMap getErpOrderInfo() {
        return erpOrderInfo;
    }

    public void setErpOrderInfo(LinkedTreeMap erpOrderInfo) {
        this.erpOrderInfo = erpOrderInfo;
    }
}
