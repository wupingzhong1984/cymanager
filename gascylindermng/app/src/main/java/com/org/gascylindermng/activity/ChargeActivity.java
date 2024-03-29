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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.activity.CaptureActivity;
import com.org.gascylindermng.R;
import com.org.gascylindermng.adapter.MySimpleSpinnerAdapter;
import com.org.gascylindermng.api.HttpResponseResult;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.BatchNumberBean;
import com.org.gascylindermng.bean.ChargeMissionBean;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.ProcessBean;
import com.org.gascylindermng.bean.ProcessNextAreaBean;
import com.org.gascylindermng.bean.Pureness;
import com.org.gascylindermng.bean.SetBean;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.CommonTools;
import com.org.gascylindermng.tools.PermissionPageUtils;
import com.org.gascylindermng.tools.ServiceLogicUtils;
import com.org.gascylindermng.view.WrapContentListView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_OTHER_SCAN_LIST;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN_SET_LIST;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN_CY_LIST;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST;

public class ChargeActivity extends BaseActivity implements ApiCallback {

    @BindView(R.id.title_name)
    TextView titleName;

    @BindView(R.id.create_mission_bottom)
    LinearLayout createMissionBottom;
    @BindView(R.id.close_mission_bottom)
    LinearLayout closeMissionBottom;
    @BindView(R.id.cy_count)
    TextView cyCountTV;
    @BindView(R.id.start_time)
    TextView startTimeTV;
    @BindView(R.id.end_time)
    TextView endTimeTV;
    @BindView(R.id.edittext_product_batch)
    EditText edittextProductBatch;
//    @BindView(R.id.edittext_charge_remark)
//    EditText edittextChargeRemark;

    @BindView(R.id.batch_listview)
    WrapContentListView batchListview;
    @BindView(R.id.pureness_spinner)
    Spinner purenessSpinner;
    //    @BindView(R.id.team_spinner)
//    Spinner teamSpinner;
//    @BindView(R.id.next_area_spinner)
//    Spinner nextAreaSpinner;
    @BindView(R.id.medium_name)
    TextView mediumName;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.submit2)
    Button submit2;


    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;

    //打开气瓶列表页-新任务
    private int REQUEST_CODE_2 = 0x02;
    //打开气瓶列表页-已开始任务
    private int REQUEST_CODE_3 = 0x03;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    private UserPresenter userPresenter;

    private int scanCount = 0;
    private ArrayList<CylinderInfoBean> newMissionCyList; //new mission cy
    private ArrayList<SetBean> newMissionSetList;
    private ArrayList<CylinderInfoBean> newMissionAllCyList; //new mission cy
    private ChargeMissionBean missionInfo;
    private String missionRemark;
    private Date beginTime;
    private Date endTime;

    private ArrayList<ProcessNextAreaBean> nextAreaList;

    private ArrayList<BatchNumberBean> productBatchList;
    private BatchNumberBean selectedProductBatch;

    MySimpleSpinnerAdapter batchListViewAdapter;
    MySimpleSpinnerAdapter purenessSpinnerAdapter;

    private boolean batchDidSelected = false;

    //charge check
    private ArrayList<String> nextAreaIdList;
    private ArrayList<String> checkOKList;
    private ArrayList<String> remarkList;
    private ArrayList<ArrayList<CheckItemBean>> checkItemResultListList;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_charge);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("充装");

        this.newMissionCyList = new ArrayList<CylinderInfoBean>();
        this.newMissionSetList = new ArrayList<SetBean>();
        this.newMissionAllCyList = new ArrayList<CylinderInfoBean>();
        this.nextAreaList = new ArrayList<ProcessNextAreaBean>();
        this.productBatchList = new ArrayList<BatchNumberBean>();
        this.checkOKList = new ArrayList<String>();
        this.nextAreaIdList = new ArrayList<String>();
        this.remarkList = new ArrayList<String>();
        this.checkItemResultListList = new ArrayList<ArrayList<CheckItemBean>>();

        userPresenter = new UserPresenter(this);

        purenessSpinnerAdapter = new MySimpleSpinnerAdapter(this);
        ArrayList<String> purenessTitleList = new ArrayList<String>();
        for (Pureness p : ServiceLogicUtils.getPurenessList()) {
            purenessTitleList.add(p.getText());
        }
        purenessSpinnerAdapter.addData(purenessTitleList);
        purenessSpinner.setAdapter(purenessSpinnerAdapter);


        final ChargeMissionBean missionInfo = (ChargeMissionBean) getIntent().getSerializableExtra("ChargeMissionBean");
        if (missionInfo == null) { //create mission
            createMissionBottom.setVisibility(View.VISIBLE);
            closeMissionBottom.setVisibility(View.GONE);
        } else {
            this.missionInfo = missionInfo;
            this.missionRemark = missionInfo.getRemark();

            for (String cyNumber : missionInfo.getCylinderNumberList()) {
                checkOKList.add("1");
                remarkList.add("");
                checkItemResultListList.add(ServiceLogicUtils.getCheckListByProcessIdAndCyCategoryId(ServiceLogicUtils.process_id_charge));
            }

            createMissionBottom.setVisibility(View.GONE);
            if (missionInfo.getStatus().equals("1")) {
                closeMissionBottom.setVisibility(View.VISIBLE);
            } else {
                closeMissionBottom.setVisibility(View.GONE);
            }
            edittextProductBatch.setText(missionInfo.getProductionBatch());
//            edittextChargeRemark.setText(missionInfo.getRemark());
            startTimeTV.setText(missionInfo.getBeginDate().substring("yyyy-MM-dd".length() + 1, missionInfo.getBeginDate().length()));
            if (missionInfo.getEndDate() != null) {
                endTimeTV.setText(missionInfo.getEndDate().substring("yyyy-MM-dd".length() + 1, missionInfo.getEndDate().length()));
            }
            cyCountTV.setText("扫描：" + scanCount + "，散瓶：" + getNewMissionCyList().size() + "，集格：" + getNewMissionSetList().size() + "，总气瓶数：" + missionInfo.getCylinderCount()+"  >");
            mediumName.setText(missionInfo.getMediemName());
            int purenessIndex = 0;
            for (Pureness p : ServiceLogicUtils.getPurenessList()) {
                if (p.getKeyValue().equals(missionInfo.getPureness())) {
                    purenessSpinner.setSelection(purenessIndex);
                }
                purenessIndex++;
            }
        }


        edittextProductBatch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (batchDidSelected) {
                    batchDidSelected = false;
                    return;
                }

                if (missionInfo != null) {
                    return;
                }

                if (!TextUtils.isEmpty(s) && s.toString().length() > 7) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.searchBatchNumber(s.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });

        batchListViewAdapter = new MySimpleSpinnerAdapter(this);
        batchListview.setAdapter(batchListViewAdapter);
        batchListview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                batchDidSelected = true;
                setSelectedProductBatch(productBatchList.get(position));
                edittextProductBatch.setText(getSelectedProductBatch().getBatchNumber());
                batchListViewAdapter.deleteAllData();
                batchListview.setVisibility(View.GONE);

                int purenessIndex = 0;
                for (Pureness p : ServiceLogicUtils.getPurenessList()) {
                    if (p.getKeyValue().equals(getSelectedProductBatch().getPureness())) {
                        purenessSpinner.setSelection(purenessIndex);
                        break;
                    }
                    purenessIndex++;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        userPresenter.getCompanyProcessListByCompanyId();
    }

    @OnClick({R.id.back_img, R.id.submit, R.id.submit2, R.id.start_scanner, R.id.cy_count, R.id.charge_start, R.id.charge_end})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.submit:
                hideSoftInput();
                createChargeMission();
                break;
            case R.id.submit2:
                hideSoftInput();
                closeChargeMission();
                break;
            case R.id.start_scanner:

                if (CommonTools.isCameraCanUse()) {
                    Intent intent = new Intent(ChargeActivity.this, CaptureActivity.class);
                    intent.putExtra("mode", ServiceLogicUtils.scan_multi);
                    intent.putExtra("isChargeScan", "1");
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
            case R.id.cy_count:
                if (missionInfo != null) {

                    Intent intent = new Intent(ChargeActivity.this, ChargeCyListActivity.class);
                    intent.putExtra("CyNumberlist", missionInfo.getCylinderNumberList());
                    if (missionInfo.getStatus().equals("1")) {
                        intent.putExtra("canCheck", "1");
                    }
                    intent.putExtra("nextAreaList", nextAreaList);
                    intent.putExtra("nextAreaIdList", nextAreaIdList);
                    intent.putExtra("checkOKList", checkOKList);
                    intent.putExtra("remarkList", remarkList);
                    intent.putExtra("checkItemResultListList", checkItemResultListList);
                    startActivityForResult(intent, REQUEST_CODE_3);

                } else if (newMissionAllCyList.size() > 0) {
                    Intent intent = new Intent(ChargeActivity.this, ChargeCyListActivity.class);
                    intent.putExtra("CyBeanlist", newMissionAllCyList);
                    intent.putExtra("canDeleteCy", "1");
                    startActivityForResult(intent, REQUEST_CODE_2);
                }
                break;
            case R.id.charge_start:
                if (missionInfo != null) {

                    LinearLayout llname = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.view_common_dialog, null);
                    final TextView textView = (TextView) llname.findViewById(R.id.message);
                    textView.setText("任务已开始，无法修改开始时间。");

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChargeActivity.this);
                    final AlertDialog dialog = builder.setView(llname).create();
                    dialog.show();
                    final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    beginTime = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    startTimeTV.setText(sdf.format(beginTime));
                }
                break;
            case R.id.charge_end:
                if (missionInfo == null) {
                    LinearLayout llname = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.view_common_dialog, null);
                    final TextView textView = (TextView) llname.findViewById(R.id.message);
                    textView.setText("请先创建任务再设定结束时间。");

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChargeActivity.this);
                    final AlertDialog dialog = builder.setView(llname).create();
                    dialog.show();
                    final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    endTime = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    endTimeTV.setText(sdf.format(endTime));
                }
                break;
        }
    }

    private void createChargeMission() {

        if (getNewMissionAllCyList().size() == 0) {

            showToast("请添加气瓶。");
            return;
        }

        if (beginTime == null) {

            showToast("请点击“开始”设置充装开始时间。");
            return;
        }

//        if (listAdapter.getProductBatch() == null || listAdapter.getProductBatch().equals("")) {
//
//            showToast("请正确设置生产批次号。");
//            return;
//        }

        LinearLayout llname = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.view_operation_dialog, null);
        final TextView textView = (TextView) llname.findViewById(R.id.message);
        textView.setText("请确认充装气瓶、开始时间和生产批次，任务开始后将无法修改。");

        final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
        final TextView btnCancel = (TextView) llname.findViewById(R.id.dialog_btn_cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(ChargeActivity.this);
        final AlertDialog dialog = builder.setView(llname).create();
        dialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> cyIdList = new ArrayList<String>();
                for (CylinderInfoBean c : getNewMissionAllCyList()) {
                    cyIdList.add(c.getCyId());
                }

                String batchStr = null;
                String pureness = null;
                if (selectedProductBatch != null) {
                    batchStr = selectedProductBatch.getBatchNumber();
                    pureness = selectedProductBatch.getPureness();
                } else {
                    batchStr = edittextProductBatch.getText().toString();
                    pureness = ServiceLogicUtils.getPurenessList().get(purenessSpinner.getSelectedItemPosition()).getKeyValue();
                }

                userPresenter.createChargeMission(beginTime,
                        batchStr,
                        pureness,
                        !TextUtils.isEmpty(missionRemark) ? missionRemark : null,
                        cyIdList);
                dialog.dismiss();
                submit.setClickable(false);
                showLoading("提交中...", "提交成功", "提交失败");
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void closeChargeMission() {

        if (endTime == null) {

            showToast("请点击“结束”设置充装结束时间。");
            return;
        }
        LinearLayout llname = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.view_operation_dialog, null);
        final TextView textView = (TextView) llname.findViewById(R.id.message);
        textView.setText("请确认结束时间和、检查项目、充后流量及备注，任务结束后将无法修改。");

        final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
        final TextView btnCancel = (TextView) llname.findViewById(R.id.dialog_btn_cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(ChargeActivity.this);
        final AlertDialog dialog = builder.setView(llname).create();
        dialog.show();

        final ArrayList<Map> aa = new ArrayList<>();
        for (int i = 0; i < missionInfo.getCylinderIdList().size(); i++) {
            String cyId = missionInfo.getCylinderIdList().get(i);
            HashMap c = new HashMap();
            c.put("cylinderId", cyId);
            c.put("companyAreaId", nextAreaIdList.get(i));
            c.put("ifPass", checkOKList.get(i));
            c.put("remark", remarkList.get(i));
            for (CheckItemBean checkItemBean : checkItemResultListList.get(i)) {
                c.put(checkItemBean.getApiParam(), checkItemBean.isState() ? "1" : "0");
            }
            aa.add(c);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userPresenter.updateChargeMissionV2(missionInfo.getMissionId(),
                        missionRemark,
                        "1", //test
                        //                      getNextAreaList().get(nextAreaSpinner.getSelectedItemPosition()).getAreaId(),
                        endTime,
                        aa);
                dialog.dismiss();
                submit2.setClickable(false);
                showLoading("提交中...", "提交成功", "提交失败");
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void closeAndRefreshMissionList() {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("needRefresh", "1");
        resultIntent.putExtras(bundle);
        this.setResult(0xA1, resultIntent);
        finish();
    }

    @Override
    public <T> void successful(String api, T success) {

        submit.setClickable(true);
        submit2.setClickable(true);
        if (api.equals("searchBatchNumber")) {

            selectedProductBatch = null;
            productBatchList.clear();

            ArrayList<String> batchCodeList = new ArrayList<>();
            List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
            for (LinkedTreeMap tm : list) {
                Type type = new TypeToken<BatchNumberBean>() {
                }.getType();
                Gson gson = new Gson();
                JSONObject object = new JSONObject(tm);
                String mData = object.toString();
                BatchNumberBean batchBean = gson.fromJson(mData, type);
                if (batchBean != null) {
                    productBatchList.add(batchBean);
                    batchCodeList.add(batchBean.getBatchNumber());
                }
            }

            if (batchCodeList.size() > 0) {
                batchListview.setVisibility(View.VISIBLE);
                batchListViewAdapter.updateData(batchCodeList);
            } else {
                batchListview.setVisibility(View.GONE);
                batchListViewAdapter.deleteAllData();
            }


        } else if (api.equals("createChargeMission")) {

            loadingDialog.loadSuccess();
            new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() { closeAndRefreshMissionList();
                    }
                }, 1500);

        } else if (api.equals("getCompanyProcessListByCompanyId")) {

            List<LinkedTreeMap> datas = (List<LinkedTreeMap>) success;
            if (datas != null && datas.size() > 0) {

                ArrayList<ProcessBean> beans = new ArrayList<>();
                for (LinkedTreeMap data : datas) {
                    JSONObject object = new JSONObject((LinkedTreeMap) data);
                    String mData = object.toString();
                    Type type = new TypeToken<ProcessBean>() {
                    }.getType();
                    Gson gson = new Gson();
                    final ProcessBean bean = gson.fromJson(mData, type);
                    if (Integer.valueOf(bean.getProcessId()) == ServiceLogicUtils.process_id_charge) {
                        new Thread() {
                            @Override
                            public void run() {
                                //在子线程中进行下载操作
                                try {
                                    userPresenter.getNextAreaListByPreProcessId(bean.getCompanyProcessId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        break;
                    }
                }
            }

        } else if (api.equals("getNextAreaListByPreProcessId")) {

            List<LinkedTreeMap> datas = (List<LinkedTreeMap>) success;
            if (datas != null && datas.size() > 0) {

                ArrayList<ProcessNextAreaBean> beans = new ArrayList<>();
                for (LinkedTreeMap data : datas) {
                    JSONObject object = new JSONObject((LinkedTreeMap) data);
                    String mData = object.toString();
                    Type type = new TypeToken<ProcessNextAreaBean>() {
                    }.getType();
                    Gson gson = new Gson();
                    ProcessNextAreaBean bean = gson.fromJson(mData, type);
                    if (bean != null) beans.add(bean);
                }
                if (beans.size() > 0) {
                    getNextAreaList().addAll(beans);
//                    ArrayList<String> adapterData = new ArrayList<String>();
//                    for (ProcessNextAreaBean p : nextAreaList) {
//                        adapterData.add(p.getAreaName());
//
//                    }
//                    nextAreaSpinnerAdapter.addData(adapterData);
                    if (missionInfo != null) {
                        for (String cyNumber : missionInfo.getCylinderNumberList()) {
                            nextAreaIdList.add(beans.get(0).getAreaId());
                        }
                    }
                }
            }


        } else if (api.equals("getCylinderListBySetId")) {
//            List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
//            for (LinkedTreeMap tm : list) {
//                Type type = new TypeToken<CylinderInfoBean>() {
//                }.getType();
//                Gson gson = new Gson();
//                JSONObject object = new JSONObject(tm);
//                String mData = object.toString();
//                CylinderInfoBean cyBean = gson.fromJson(mData, type);
//                if (cyBean != null) {
//                    getNewMissionCyList().add(cyBean);
//                }
//            }
//            lastScanCySetIdList.remove(0);
//            if (lastScanCySetIdList.size() > 0) {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        //在子线程中进行下载操作
//                        try {
//                            userPresenter.getCylinderListBySetId(lastScanCySetIdList.get(0));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();
//            }

        } else if (api.equals("getCylinderInfoByPlatformCyNumber")) {

//            if (success != null && success instanceof CylinderInfoBean) {
//
//                getNewMissionCyList().add((CylinderInfoBean) success);
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
//                } else if (lastScanCySetIdList.size() > 0) {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            //在子线程中进行下载操作
//                            try {
//                                userPresenter.getCylinderListBySetId(lastScanCySetIdList.get(0));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();
//                }
//            }

        } else if (api.equals("updateDetection")) {

            loadingDialog.loadSuccess();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeAndRefreshMissionList();
                }
            }, 1500);

        } else if (api.equals("updateChargeMissionV2")) {

            loadingDialog.loadSuccess();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeAndRefreshMissionList();
                }
            }, 1500);
        }
    }

    @Override
    public <T> void failure(String api, T failure) {

        submit.setClickable(true);
        submit2.setClickable(true);
        if (api.equals("updateDetection") || api.equals("updateChargeMissionV2")) {
            loadingDialog.setFailedText("提交失败，" + (String) failure);
            loadingDialog.loadFailed();
        } else {
            showToast("接口报错，" + (String) failure);
        }
        if (failure instanceof String && ((String) failure).equals("needUpdate")) {

            super.updateApp();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调

        Bundle bundle = data.getExtras();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> result = bundle.getStringArrayList(INTENT_EXTRA_KEY_OTHER_SCAN_LIST);
                if (result != null && result.size() > 0) {
                    scanCount += result.size();
                }

                ArrayList<SetBean> setList = (ArrayList<SetBean>) bundle.getSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST);
                if (setList != null && setList.size() > 0) {
                    getNewMissionSetList().addAll(setList);
                }

                ArrayList<CylinderInfoBean> cyList = (ArrayList<CylinderInfoBean>) bundle.getSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST);
                if (cyList != null && cyList.size() > 0) {
                    getNewMissionCyList().addAll(cyList);
                }

                ArrayList<CylinderInfoBean> allCyList = (ArrayList<CylinderInfoBean>) bundle.getSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST);
                if (allCyList != null && allCyList.size() > 0) {
                    getNewMissionAllCyList().addAll(allCyList);

                    if (TextUtils.isEmpty(mediumName.getText().toString())) {
                        mediumName.setText(getNewMissionAllCyList().get(0).getCyMediumName());
                    }
                }
                cyCountTV.setText("扫描：" + scanCount + "，散瓶：" + getNewMissionCyList().size() + "，集格：" + getNewMissionSetList().size() + "，总气瓶数：" + getNewMissionAllCyList().size()+"  >");
            } else {
                //showToast("扫描失败");
            }
        } else if (requestCode == REQUEST_CODE_2) {

            ArrayList<CylinderInfoBean> result = (ArrayList<CylinderInfoBean>) bundle.getSerializable("CyBeanlist");
            newMissionAllCyList.clear();
            if (result != null && result.size() > 0) {
                newMissionAllCyList.addAll(result);
            }
            if (newMissionAllCyList.size() > 0) {

                for (int i = newMissionSetList.size() - 1; i > -1; i--) {
                    boolean hasSet = false;
                    for (CylinderInfoBean c : newMissionAllCyList) {
                        if (!TextUtils.isEmpty(c.getSetId()) && c.getSetId().equals(newMissionSetList.get(i).getSetId())) {
                            hasSet = true;
                            break;
                        }
                    }
                    if (!hasSet) {
                        newMissionSetList.remove(i);
                    }
                }

                for (int i = newMissionCyList.size() - 1; i > -1; i--) {

                    boolean hasCy = false;
                    for (CylinderInfoBean c : newMissionAllCyList) {
                        if (c.getCyId().equals(newMissionCyList.get(i).getCyId())) {
                            hasCy = true;
                            break;
                        }
                    }
                    if (!hasCy) {
                        newMissionCyList.remove(i);
                    }
                }

                scanCount = newMissionSetList.size() + newMissionCyList.size();

            } else {
                newMissionCyList.clear();
                newMissionSetList.clear();
                scanCount = 0;
            }
            cyCountTV.setText("扫描：" + scanCount + "，散瓶：" + getNewMissionCyList().size() + "，集格：" + getNewMissionSetList().size() + "，总气瓶数：" + getNewMissionAllCyList().size()+"  >");
        } else if (requestCode == REQUEST_CODE_3) {

            this.nextAreaIdList = bundle.getStringArrayList("nextAreaIdList");
            this.checkOKList = bundle.getStringArrayList("checkOKList");
            this.remarkList = bundle.getStringArrayList("remarkList");
            this.checkItemResultListList = (ArrayList<ArrayList<CheckItemBean>>) bundle.getSerializable("checkItemResultListList");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

//    public ArrayList<String> getLastScanCyPlatformCodeList() {
//        return lastScanCyPlatformCodeList;
//    }
//
//    public void setLastScanCyPlatformCodeList(ArrayList<String> lastScanCyPlatformCodeList) {
//        this.lastScanCyPlatformCodeList = lastScanCyPlatformCodeList;
//    }
//
//    public ArrayList<String> getLastScanCySetIdList() {
//        return lastScanCySetIdList;
//    }
//
//    public void setLastScanCySetIdList(ArrayList<String> lastScanCySetIdList) {
//        this.lastScanCySetIdList = lastScanCySetIdList;
//    }

    public ArrayList<CylinderInfoBean> getNewMissionCyList() {
        return newMissionCyList;
    }

    public void setNewMissionCyList(ArrayList<CylinderInfoBean> newMissionCyList) {
        this.newMissionCyList = newMissionCyList;
    }

    public ArrayList<SetBean> getNewMissionSetList() {
        return newMissionSetList;
    }

    public void setNewMissionSetList(ArrayList<SetBean> newMissionSetList) {
        this.newMissionSetList = newMissionSetList;
    }

    public ArrayList<CylinderInfoBean> getNewMissionAllCyList() {
        return newMissionAllCyList;
    }

    public void setNewMissionAllCyList(ArrayList<CylinderInfoBean> newMissionAllCyList) {
        this.newMissionAllCyList = newMissionAllCyList;
    }

    public ArrayList<ProcessNextAreaBean> getNextAreaList() {
        return nextAreaList;
    }

    public void setNextAreaList(ArrayList<ProcessNextAreaBean> nextAreaList) {
        this.nextAreaList = nextAreaList;
    }

    public ChargeMissionBean getMissionInfo() {
        return missionInfo;
    }

    public void setMissionInfo(ChargeMissionBean missionInfo) {
        this.missionInfo = missionInfo;
    }

    public String getMissionRemark() {
        return missionRemark;
    }

    public void setMissionRemark(String missionRemark) {
        this.missionRemark = missionRemark;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public ArrayList<BatchNumberBean> getProductBatchList() {
        return productBatchList;
    }

    public void setProductBatchList(ArrayList<BatchNumberBean> productBatchList) {
        this.productBatchList = productBatchList;
    }

    public BatchNumberBean getSelectedProductBatch() {
        return selectedProductBatch;
    }

    public void setSelectedProductBatch(BatchNumberBean selectedProductBatch) {
        this.selectedProductBatch = selectedProductBatch;
    }

    public ArrayList<String> getNextAreaIdList() {
        return nextAreaIdList;
    }

    public void setNextAreaIdList(ArrayList<String> nextAreaIdList) {
        this.nextAreaIdList = nextAreaIdList;
    }

    public ArrayList<String> getCheckOKList() {
        return checkOKList;
    }

    public void setCheckOKList(ArrayList<String> checkOKList) {
        this.checkOKList = checkOKList;
    }

    public ArrayList<String> getRemarkList() {
        return remarkList;
    }

    public void setRemarkList(ArrayList<String> remarkList) {
        this.remarkList = remarkList;
    }

    public ArrayList<ArrayList<CheckItemBean>> getCheckItemResultListList() {
        return checkItemResultListList;
    }

    public void setCheckItemResultListList(ArrayList<ArrayList<CheckItemBean>> checkItemResultListList) {
        this.checkItemResultListList = checkItemResultListList;
    }
}
