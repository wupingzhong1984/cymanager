package com.org.gascylindermng.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.activity.CaptureActivity;
import com.org.gascylindermng.R;
import com.org.gascylindermng.adapter.MySimpleSpinnerAdapter;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.CustomerInfoBean;
import com.org.gascylindermng.bean.CyCategoryBean;
import com.org.gascylindermng.bean.CyManufacturerBean;
import com.org.gascylindermng.bean.CyMediumBean;
import com.org.gascylindermng.bean.CySetBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.CommonTools;
import com.org.gascylindermng.tools.PermissionPageUtils;
import com.org.gascylindermng.tools.ServiceLogicUtils;
import com.org.gascylindermng.view.WrapContentListView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindCodeToCyActivity extends BaseActivity implements ApiCallback {

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.bottleorcompanyrelatecode)
    EditText bottleorcompanyrelatecode;
    @BindView(R.id.cycategory_spinner)
    Spinner cycategorySpinner;
    @BindView(R.id.medium_spinner)
    Spinner mediumSpinner;
    @BindView(R.id.year)
    EditText year;
    @BindView(R.id.month)
    EditText month;
    @BindView(R.id.day)
    EditText day;
    @BindView(R.id.platform_cy_code)
    TextView platformCyCode;
    @BindView(R.id.cy_owner_comany)
    ImageView cyOwnerComany;
    @BindView(R.id.cy_owner_customer)
    ImageView cyOwnerCustomer;
    @BindView(R.id.cy_owner)
    EditText cyOwner;
    @BindView(R.id.cy_set)
    EditText cySet;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.customer_listview)
    WrapContentListView customerListview;
    @BindView(R.id.cySet_listview)
    WrapContentListView cySetListview;
    @BindView(R.id.cy_manufacturer)
    EditText cyManufacturer;
    @BindView(R.id.work_pressure)
    EditText cyWorkPressure;
    @BindView(R.id.cy_weight)
    EditText cyWeight;
    @BindView(R.id.cy_volume)
    EditText cyVolume;
    @BindView(R.id.cy_wallthickness)
    EditText cyWallthickness;
    @BindView(R.id.cyManu_listview)
    WrapContentListView cyManuListview;
    @BindView(R.id.next_year)
    EditText nextYear;
    @BindView(R.id.next_month)
    EditText nextMonth;
    @BindView(R.id.next_day)
    EditText nextDay;


    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    private boolean customerOwnCy = false;
    private UserPresenter userPresenter;
    private CylinderInfoBean cyInfo;
    private CustomerInfoBean selectedCustomer;
    private CyManufacturerBean selectedCyManu;
    private CySetBean selectedCySet;

    private ArrayList<CyCategoryBean> cyCategoryList;
    private ArrayList<CustomerInfoBean> customerList;
    private ArrayList<CyManufacturerBean> cyManuList;
    private ArrayList<CySetBean> cySetList;

    MySimpleSpinnerAdapter cyCategorySpinnerAdapter;
    MySimpleSpinnerAdapter cyMediumSpinnerAdapter;
    MySimpleSpinnerAdapter customerListAdapter;
    MySimpleSpinnerAdapter cyManuListAdapter;
    MySimpleSpinnerAdapter cySetListAdapter;

    private boolean customerDidSelected = false;
    private boolean cyManuDidSelected = false;
    private boolean cySetDidSelected = false;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bindcodetocy);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        this.cyCategoryList = new ArrayList<CyCategoryBean>();
        this.customerList = new ArrayList<CustomerInfoBean>();
        this.cyManuList = new ArrayList<CyManufacturerBean>();
        this.cySetList = new ArrayList<CySetBean>();

        titleName.setText("气瓶绑码");
        userPresenter = new UserPresenter(this);

        cyCategorySpinnerAdapter = new MySimpleSpinnerAdapter(this);
        cycategorySpinner.setAdapter(cyCategorySpinnerAdapter);
        cycategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CyCategoryBean c = cyCategoryList.get(position);

                ArrayList<String> mList = new ArrayList<String>();
                for (CyMediumBean m : c.getCyMediumList()) {
                    mList.add(m.getMediumName());
                }
                if (mList.size() > 0) {
                    cyMediumSpinnerAdapter.updateData(mList);
                } else {
                    cyMediumSpinnerAdapter.deleteAllData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cyMediumSpinnerAdapter = new MySimpleSpinnerAdapter(this);
        mediumSpinner.setAdapter(cyMediumSpinnerAdapter);

        customerListAdapter = new MySimpleSpinnerAdapter(this);
        customerListview.setAdapter(customerListAdapter);
        customerListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                customerDidSelected = true;
                selectedCustomer = customerList.get(position);
                cyOwner.setText(selectedCustomer.getCustomerName());
                customerListAdapter.deleteAllData();
                customerListview.setVisibility(View.GONE);

            }
        });

        cySetListAdapter = new MySimpleSpinnerAdapter(this);
        cySetListview.setAdapter(cySetListAdapter);
        cySetListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cySetDidSelected = true;
                selectedCySet = cySetList.get(position);
                cySet.setText(selectedCySet.getSetNumber());
                cySetListAdapter.deleteAllData();
                cySetListview.setVisibility(View.GONE);

            }
        });

        cyManuListAdapter = new MySimpleSpinnerAdapter(this);
        cyManuListview.setAdapter(cyManuListAdapter);
        cyManuListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cyManuDidSelected = true;
                selectedCyManu = cyManuList.get(position);
                cyManufacturer.setText(selectedCyManu.getCode());
                cyManuListAdapter.deleteAllData();
                cyManuListview.setVisibility(View.GONE);

            }
        });

        bottleorcompanyrelatecode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.getCylinderInfoByCyCode(s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });

        cyOwner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (customerDidSelected) {
                    customerDidSelected = false;
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
                    selectedCustomer = null;
                }
            }
        });

        cySet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (cySetDidSelected) {
                    cySetDidSelected = false;
                    return;
                }

                if (!TextUtils.isEmpty(s)) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.searchUnitsSet(null, s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    cySetList.clear();
                    cySetListAdapter.deleteAllData();
                    selectedCySet = null;
                }
            }
        });

        cyManufacturer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (cyManuDidSelected) {
                    cyManuDidSelected = false;
                    return;
                }

                if (!TextUtils.isEmpty(s)) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.searchCylinderManufacturer(s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    cyManuList.clear();
                    cyManuListAdapter.deleteAllData();
                    selectedCyManu = null;
                }
            }
        });

        userPresenter.getCyCategoryListByCompanyId();
    }

    @OnClick({R.id.back_img, R.id.cy_owner_comany, R.id.cy_owner_customer, R.id.platform_cy_code, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.cy_owner_comany:

//                customerOwnCy = false;
//                cyOwnerComany.setImageResource(R.mipmap.item_check_on);
//                cyOwnerCustomer.setImageResource(R.mipmap.item_check_off);
//                cyOwner.setVisibility(View.INVISIBLE);
                break;
            case R.id.cy_owner_customer:
                customerOwnCy = true;
                cyOwnerComany.setImageResource(R.mipmap.item_check_off);
                cyOwnerCustomer.setImageResource(R.mipmap.item_check_on);
                cyOwner.setVisibility(View.VISIBLE);
                break;
            case R.id.platform_cy_code:
                if (CommonTools.isCameraCanUse()) {
                    Intent intent = new Intent(BindCodeToCyActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
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
            case R.id.submit:

                if (cyInfo != null && cyInfo.getPlatformCyCode() != null) {

                    showToast("该气瓶已绑定标签码。");
                    return;
                }

                if (TextUtils.isEmpty(bottleorcompanyrelatecode.getText().toString())) {
                    showToast("请输入气瓶号。");
                    return;
                }


                if (selectedCyManu == null) {
                    showToast("请设置气瓶生产商。");
                    return;
                }

                if (TextUtils.isEmpty(year.getText().toString()) ||
                        year.getText().toString().length() < 4 ||
                        TextUtils.isEmpty(month.getText().toString()) ||
                        TextUtils.isEmpty(day.getText().toString())) {
                    showToast("请正确输入气瓶生产日期。");
                    return;
                }

                if(!TextUtils.isEmpty(nextYear.getText().toString()) ||
                        !TextUtils.isEmpty(nextMonth.getText().toString()) ||
                        !TextUtils.isEmpty(nextDay.getText().toString())) {

                    if (TextUtils.isEmpty(nextYear.getText().toString()) ||
                            TextUtils.isEmpty(nextMonth.getText().toString()) ||
                            TextUtils.isEmpty(nextDay.getText().toString())) {
                        showToast("请完整输入下检日期。");
                        return;
                    }

                }

//                if (customerOwnCy) {
//                    if (TextUtils.isEmpty(cyOwner.getText().toString())) {
//                        showToast("请输入气瓶托管所属客户名。");
//                    } else if (selectedCustomer == null) {
//                        showToast("该客户不存在，请先在后台录入。");
//                    }
//                    return;
//                }

                if (platformCyCode.getText().toString().equals("")) {
                    showToast("请扫描条码/二维码。");
                    return;
                }
                LinearLayout llname = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.view_operation_dialog, null);
                final TextView textView = (TextView) llname.findViewById(R.id.message);
                textView.setText("是否确认信息正确并提交？");

                final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
                btnConfirm.setText("确认");
                final TextView btnCancel = (TextView) llname.findViewById(R.id.dialog_btn_cancel);

                AlertDialog.Builder builder = new AlertDialog.Builder(BindCodeToCyActivity.this);
                final AlertDialog dialog = builder.setView(llname).create();
                dialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoading("绑定中...", "绑定成功", "绑定失败");
                        if (cyInfo == null) {
                            //create cy
                            String code = bottleorcompanyrelatecode.getText().toString();

                            String yearStr = year.getText().toString();
                            String monthStr, dayStr;
                            int monthInt = Integer.valueOf(month.getText().toString());
                            int dayInt = Integer.valueOf(day.getText().toString());
                            if (monthInt > 9) {
                                monthStr = String.valueOf(monthInt);
                            } else {
                                monthStr = "0" + String.valueOf(monthInt);
                            }
                            if (dayInt > 9) {
                                dayStr = String.valueOf(dayInt);
                            } else {
                                dayStr = "0" + String.valueOf(dayInt);
                            }

                            String nextYearStr = nextYear.getText().toString();
                            String nextMonthStr = "";
                            String nextDayStr = "";
                            if (!TextUtils.isEmpty(nextYearStr)) {
                                monthInt = Integer.valueOf(nextMonth.getText().toString());
                                dayInt = Integer.valueOf(nextDay.getText().toString());
                                if (monthInt > 9) {
                                    nextMonthStr = String.valueOf(monthInt);
                                } else {
                                    nextMonthStr = "0" + String.valueOf(monthInt);
                                }
                                if (dayInt > 9) {
                                    nextDayStr = String.valueOf(dayInt);
                                } else {
                                    nextDayStr = "0" + String.valueOf(dayInt);
                                }
                            }

                            userPresenter.addCylinder(bottleorcompanyrelatecode.getText().toString(),
                                    cyCategoryList.get(cycategorySpinner.getSelectedItemPosition()).getCategoryId(),
                                    cyCategoryList.get(cycategorySpinner.getSelectedItemPosition()).getCategoryName(),
                                    cyCategoryList.get(cycategorySpinner.getSelectedItemPosition()).getCyMediumList().get(mediumSpinner.getSelectedItemPosition()).getMediumId(),
                                    cyCategoryList.get(cycategorySpinner.getSelectedItemPosition()).getCyMediumList().get(mediumSpinner.getSelectedItemPosition()).getMediumName(),
                                    selectedCyManu.getId(),
                                    selectedCyManu.getName(),
                                    yearStr + "-" + monthStr + "-" + dayStr,
                                    (nextYearStr != null ? nextYearStr + "-" + nextMonthStr + "-" + nextDayStr : null),
                                    (selectedCySet != null ? selectedCySet.getSetId() : null),
                                    (selectedCySet != null ? selectedCySet.getSetNumber() : null),
                                    cyWorkPressure.getText().toString(),
                                    cyWeight.getText().toString(),
                                    cyVolume.getText().toString(),
                                    cyWallthickness.getText().toString());

                        } else {
                            //relation
                            userPresenter.addNumber(cyInfo.getCyId(), platformCyCode.getText().toString());
                        }
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

    private void cleanAllInfoInUI(boolean cleanCyRelateCode) {

        this.cyInfo = null;
        if (cleanCyRelateCode) {
            bottleorcompanyrelatecode.setText("");
        }
        platformCyCode.setText("");
        cyCategorySpinnerAdapter.deleteAllData();
        cyMediumSpinnerAdapter.deleteAllData();

        ArrayList cateList = new ArrayList();
        for (CyCategoryBean cate : cyCategoryList) {
            cateList.add(cate.getCategoryName());
        }
        cyCategorySpinnerAdapter.updateData(cateList);

        ArrayList mediumList = new ArrayList();
        for (CyMediumBean medium : cyCategoryList.get(0).getCyMediumList()) {
            mediumList.add(medium.getMediumName());
        }
        cyMediumSpinnerAdapter.updateData(mediumList);

        year.setText("");
        month.setText("");
        day.setText("");
        customerOwnCy = false;
        cyOwnerComany.setImageResource(R.mipmap.item_check_on);
        cyOwnerCustomer.setImageResource(R.mipmap.item_check_off);
        cyOwner.setText("");
        cyOwner.setVisibility(View.INVISIBLE);

        customerList.clear();
        selectedCustomer = null;
        customerListAdapter.deleteAllData();
        customerListview.setVisibility(View.GONE);

        cyManufacturer.setText("");
        cyManuList.clear();
        selectedCyManu = null;
        cyManuListAdapter.deleteAllData();
        cyManuListview.setVisibility(View.GONE);

        cySet.setText("");
        cySetList.clear();
        selectedCySet = null;
        cySetListAdapter.deleteAllData();
        cySetListview.setVisibility(View.GONE);

        cyWorkPressure.setText("");
        cyWeight.setText("");
        cyVolume.setText("");
        cyWallthickness.setText("");
    }

    @Override
    public <T> void successful(String api, T success) {
        if (api.equals("addNumber")) {

            loadingDialog.loadSuccess();
            LinearLayout llname = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.view_operation_dialog, null);
            final TextView textView = (TextView) llname.findViewById(R.id.message);
            textView.setText("绑定成功。是否继续绑定？");

            final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
            btnConfirm.setText("继续绑定");
            final TextView btnCancel = (TextView) llname.findViewById(R.id.dialog_btn_cancel);

            AlertDialog.Builder builder = new AlertDialog.Builder(BindCodeToCyActivity.this);
            final AlertDialog dialog = builder.setView(llname).create();
            dialog.show();

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cleanAllInfoInUI(true);
                    dialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                }
            });

        } else if (api.equals("addCylinder")) {

            Type type = new TypeToken<CylinderInfoBean>() {
            }.getType();
            Gson gson = new Gson();
            JSONObject object = new JSONObject((LinkedTreeMap) success);
            String mData = object.toString();
            final CylinderInfoBean cy = gson.fromJson(mData, type);
            this.cyInfo = cy;
            new Thread() {
                @Override
                public void run() {
                    //在子线程中进行下载操作
                    try {
                        userPresenter.addNumber(cy.getCyId(), platformCyCode.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();


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

        } else if (api.equals("searchCylinderManufacturer")) {

            selectedCyManu = null;
            cyManuList.clear();

            List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
            for (LinkedTreeMap tm : list) {
                Type type = new TypeToken<CyManufacturerBean>() {
                }.getType();
                Gson gson = new Gson();
                JSONObject object = new JSONObject(tm);
                String mData = object.toString();
                CyManufacturerBean manuInfoBean = gson.fromJson(mData, type);
                cyManuList.add(manuInfoBean);
            }
//            if (customerList.size() == 1 &&  customerList.get(0).getCustomerName().equals(cyOwner.getText().toString())) {
//                return;
//            }
            ArrayList<String> manuList = new ArrayList<String>();
            for (CyManufacturerBean bean : cyManuList) {
                manuList.add(bean.getCode() + "-" + bean.getName());
            }
            if (manuList.size() > 0) {
                cyManuListview.setVisibility(View.VISIBLE);
                cyManuListAdapter.updateData(manuList);
            } else {
                cyManuListview.setVisibility(View.GONE);
                cyManuListAdapter.deleteAllData();
            }

        } else if (api.equals("searchUnitsSet")) {

            selectedCySet = null;
            cySetList.clear();

            List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
            for (LinkedTreeMap tm : list) {
                Type type = new TypeToken<CySetBean>() {
                }.getType();
                Gson gson = new Gson();
                JSONObject object = new JSONObject(tm);
                String mData = object.toString();
                CySetBean setInfoBean = gson.fromJson(mData, type);
                cySetList.add(setInfoBean);
            }
            ArrayList<String> setList = new ArrayList<String>();
            for (CySetBean bean : cySetList) {
                setList.add(bean.getSetNumber() + "-" + bean.getSetName());
            }
            if (setList.size() > 0) {
                cySetListview.setVisibility(View.VISIBLE);
                cySetListAdapter.updateData(setList);
            } else {
                cySetListview.setVisibility(View.GONE);
                cySetListAdapter.deleteAllData();
            }

        } else if (api.equals("getCyCategoryListByCompanyId")) {

            if (success instanceof List) {

                List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
                for (LinkedTreeMap tm : list) {
                    Type type = new TypeToken<CyCategoryBean>() {
                    }.getType();
                    Gson gson = new Gson();
                    JSONObject object = new JSONObject(tm);
                    String mData = object.toString();
                    CyCategoryBean categoryBean = gson.fromJson(mData, type);
                    categoryBean.setCyMediumList(new ArrayList<CyMediumBean>());

                    List<LinkedTreeMap> list2 = (List<LinkedTreeMap>) tm.get("gasMediumList");
                    for (LinkedTreeMap tm2 : list2) {
                        Type type2 = new TypeToken<CyMediumBean>() {
                        }.getType();
                        Gson gson2 = new Gson();
                        JSONObject object2 = new JSONObject(tm2);
                        String mData2 = object2.toString();
                        CyMediumBean mediumBean = gson2.fromJson(mData2, type2);
                        categoryBean.getCyMediumList().add(mediumBean);
                    }

                    cyCategoryList.add(categoryBean);
                }
                ArrayList<String> cateList = new ArrayList<String>();
                for (CyCategoryBean bean : cyCategoryList) {
                    cateList.add(bean.getCategoryName());
                }
                if (cateList.size() > 0) {
                    cyCategorySpinnerAdapter.updateData(cateList);
                    CyCategoryBean firstCate = cyCategoryList.get(0);
                    ArrayList<String> mList = new ArrayList<String>();
                    for (CyMediumBean m : firstCate.getCyMediumList()) {
                        mList.add(m.getMediumName());
                    }
                    if (mList.size() > 0) {
                        cyMediumSpinnerAdapter.updateData(mList);
                    } else {
                        cyMediumSpinnerAdapter.deleteAllData();
                    }
                } else {
                    cyCategorySpinnerAdapter.deleteAllData();
                    cyMediumSpinnerAdapter.deleteAllData();
                }
            }

        } else if (api.equals("getCylinderInfoByCyCode")) {
            if (success != null && success instanceof CylinderInfoBean) {
                this.cyInfo = (CylinderInfoBean) success;

                ArrayList cateList = new ArrayList();
                cateList.add(cyInfo.getCyCategoryName());
                cyCategorySpinnerAdapter.updateData(cateList);

                ArrayList mediumList = new ArrayList();
                mediumList.add(cyInfo.getCyMediumName());
                cyMediumSpinnerAdapter.updateData(mediumList);

                if (!TextUtils.isEmpty(cyInfo.getPlatformCyCode())) {
                    cyManufacturer.setText(cyInfo.getManufacturerCode());
                }

                String[] strs1 = cyInfo.getManuDate().split(" ");
                String[] strs2 = strs1[0].split("-");
                year.setText(strs2[0]);
                month.setText(strs2[1]);
                day.setText(strs2[2]);

                if (cyInfo.getUnitId().equals(userPresenter.querComapny().getCompanyId())) { //我司自有瓶子
                    customerOwnCy = false;
                    cyOwnerComany.setImageResource(R.mipmap.item_check_on);
                    cyOwnerCustomer.setImageResource(R.mipmap.item_check_off);
                    cyOwner.setText("");
                    cyOwner.setVisibility(View.INVISIBLE);
                } else {
                    customerOwnCy = true;
                    cyOwnerComany.setImageResource(R.mipmap.item_check_off);
                    cyOwnerCustomer.setImageResource(R.mipmap.item_check_on);
                    cyOwner.setText(cyInfo.getUnitName());
                    cyOwner.setVisibility(View.VISIBLE);
                }
                customerList.clear();
                selectedCustomer = null;
                customerListAdapter.deleteAllData();
                customerListview.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(cyInfo.getSetNumber())) {
                    cySet.setText(cyInfo.getSetNumber());
                }

                if (!TextUtils.isEmpty(cyInfo.getWorkPressure())) {
                    cyWorkPressure.setText(cyInfo.getWorkPressure());
                }

                if (!TextUtils.isEmpty(cyInfo.getWeight())) {
                    cyWeight.setText(cyInfo.getWeight());
                }

                if (!TextUtils.isEmpty(cyInfo.getVolume())) {
                    cyVolume.setText(cyInfo.getVolume());
                }

                if (!TextUtils.isEmpty(cyInfo.getWallThickness())) {
                    cyWallthickness.setText(cyInfo.getWallThickness());
                }

                if (!TextUtils.isEmpty(cyInfo.getPlatformCyCode())) {
                    platformCyCode.setText(cyInfo.getPlatformCyCode());
                }


            } else {
                cleanAllInfoInUI(false);
            }
        }
    }

    @Override
    public <T> void failure(String api, T failure) {

        if (failure instanceof String && ((String) failure).equals("needUpdate")) {

            super.updateApp();
            return;
        }

        loadingDialog.loadFailed();
        showToast("接口报错，" + (String) failure);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (resultCode == RESULT_OK) { //RESULT_OK = -1F
            Bundle bundle = data.getExtras();
            ArrayList<String> result = bundle.getStringArrayList("qr_scan_result");
            if (result == null || result.size() == 0) {
                return;
            }
            String lastScanCyPlatformCode = ServiceLogicUtils.getCylinderPlatformCyCodeFromScanResult(result.get(0));
            if (TextUtils.isEmpty(lastScanCyPlatformCode)) {
                showToast("异常二维码");
            } else {
                platformCyCode.setText(lastScanCyPlatformCode);
            }

        } else {
            showToast("扫描失败");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
