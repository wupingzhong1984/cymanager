package com.org.gascylindermng.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.activity.CaptureActivity;
import com.org.gascylindermng.R;
import com.org.gascylindermng.adapter.ChargeAdapter;
import com.org.gascylindermng.api.HttpResponseResult;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.ChargeMissionBean;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.ProcessBean;
import com.org.gascylindermng.bean.ProcessNextAreaBean;
import com.org.gascylindermng.bean.UrlEntity;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.model.ChargeMission;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.CommonTools;
import com.org.gascylindermng.tools.PermissionPageUtils;
import com.org.gascylindermng.tools.ServiceLogicUtils;
import com.org.gascylindermng.tools.UrlUtils;
import com.org.gascylindermng.view.WrapContentListView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChargeActivity extends BaseActivity implements ApiCallback {

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.listview)
    WrapContentListView listview;
    @BindView(R.id.create_mission_bottom)
    LinearLayout createMissionBottom;
    @BindView(R.id.close_mission_bottom)
    LinearLayout closeMissionBottom;

    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    private ChargeAdapter listAdapter;
    private UserPresenter userPresenter;
    private ArrayList<String> lastScanCyPlatformCodeList;
    private ArrayList<String> lastScanCySetPlatformIdList;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_charge);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("充装");
        lastScanCyPlatformCodeList = new ArrayList<String>();
        lastScanCySetPlatformIdList = new ArrayList<String>();

        userPresenter = new UserPresenter(this);
        listAdapter = new ChargeAdapter(this, this);
        ChargeMissionBean missionInfo = (ChargeMissionBean) getIntent().getSerializableExtra("ChargeMissionBean");
        if (missionInfo == null) { //create mission
            createMissionBottom.setVisibility(View.VISIBLE);
            closeMissionBottom.setVisibility(View.GONE);
        } else {
            createMissionBottom.setVisibility(View.GONE);
            closeMissionBottom.setVisibility(View.VISIBLE);
            listAdapter.setMissionInfo(missionInfo);
            if (listAdapter.getData().size() == 0) {
                listAdapter.addData(ServiceLogicUtils.getCheckListByProcessIdAndCyCategoryId(ServiceLogicUtils.process_id_charge,null));
            }
            //userPresenter.getCylinderInfoByPlatformCyCode(missionInfo.getCyPlatformIdList());
        }

        listview.setAdapter(listAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (listAdapter.getMissionInfo() != null) {

                        Intent intent = new Intent(ChargeActivity.this, CyListActivity.class);
                        intent.putExtra("CyPlatformIdlist", listAdapter.getMissionInfo().getCyPlatformIdList());
                        startActivity(intent);

                    } else if (listAdapter.getNewMissionCyList().size() > 0) {
                        Intent intent = new Intent(ChargeActivity.this, CyListActivity.class);
                        intent.putExtra("CyBeanlist", listAdapter.getNewMissionCyList());
                        startActivity(intent);
                    }
                }
            }
        });

        userPresenter.getCompanyProcessListByCompanyId();
    }

    @OnClick({R.id.back_img, R.id.submit, R.id.submit2, R.id.start_scanner})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.start_scanner:

                    if (CommonTools.isCameraCanUse()) {
                        Intent intent = new Intent(ChargeActivity.this, CaptureActivity.class);
                        intent.putExtra("mode",ServiceLogicUtils.scan_multi);
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
                hideSoftInput();
                createChargeMission();
                break;
            case R.id.submit2:
                hideSoftInput();
                closeChargeMission();
                break;
        }
    }

    private void createChargeMission() {

        if (listAdapter.getNewMissionCyList().size() == 0) {

            showToast("请添加气瓶。");
            return;
        }

        if (listAdapter.getBeginTime() == null) {

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
                for (CylinderInfoBean c : listAdapter.getNewMissionCyList()) {
                    cyIdList.add(c.getCyId());
                }
                userPresenter.createChargeMission(listAdapter.getBeginTime(),
                        !TextUtils.isEmpty(listAdapter.getProductBatch())?listAdapter.getProductBatch():null,
                        !TextUtils.isEmpty(listAdapter.getRemark())?listAdapter.getRemark():null,
                        cyIdList);
                dialog.dismiss();
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

        if (listAdapter.getEndTime() == null) {

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

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userPresenter.updateDetection(listAdapter.getMissionInfo().getMissionId(),
                        listAdapter.getMissionInfo().getRemark(),
                        listAdapter.getEndTime(),
                        true,
                        listAdapter.getData());
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public <T> void successful(String api, T success) {

        if (api.equals("createChargeMission")) {

            HttpResponseResult httpResponseResult = (HttpResponseResult) success;
            if (!httpResponseResult.getCode().equals("200")) {
                super.showToast(httpResponseResult.getMessage());
            } else {
                showToast("创建成功");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            }

        } if (api.equals("getCompanyProcessListByCompanyId")) {

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
                    listAdapter.getNextAreaList().addAll(beans);
                    listAdapter.setNextAreaId(beans.get(0).getAreaId());
                }
                listAdapter.notifyDataSetChanged();
            }

        } else if (api.equals("getCylinderListBySetId")) {
            List<LinkedTreeMap> list = (List<LinkedTreeMap>) success;
            for (LinkedTreeMap tm : list) {
                Type type = new TypeToken<CylinderInfoBean>() {
                }.getType();
                Gson gson = new Gson();
                JSONObject object = new JSONObject(tm);
                String mData = object.toString();
                CylinderInfoBean cyBean = gson.fromJson(mData, type);
                if (cyBean != null) {
                    if (listAdapter.getData().size() == 0) {
                        listAdapter.addData(ServiceLogicUtils.getCheckListByProcessIdAndCyCategoryId(ServiceLogicUtils.process_id_charge, cyBean.getCyCategoryId()));
                    }
                    listAdapter.getNewMissionCyList().add(cyBean);
                }
            }
            listAdapter.notifyDataSetChanged();
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

        } else if (api.equals("getCylinderInfoByPlatformCyCode")) {

            if (success != null && success instanceof CylinderInfoBean) {

                listAdapter.getNewMissionCyList().add((CylinderInfoBean)success);
                if (listAdapter.getData().size() == 0) {
                    listAdapter.addData(ServiceLogicUtils.getCheckListByProcessIdAndCyCategoryId(ServiceLogicUtils.process_id_charge, ((CylinderInfoBean)success).getCyCategoryId()));
                }
                listAdapter.notifyDataSetChanged();
                lastScanCyPlatformCodeList.remove(0);
                if (lastScanCyPlatformCodeList.size() > 0) {

                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.getCylinderInfoByPlatformCyCode(lastScanCyPlatformCodeList.get(0));
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

        } else if (api.equals("updateDetection")) {

            HttpResponseResult httpResponseResult = (HttpResponseResult) success;
            if (!httpResponseResult.getCode().equals("200")) {
                super.showToast(httpResponseResult.getMessage());
            } else {
                showToast("提交成功");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            }
        }
    }

    @Override
    public <T> void failure(String api, T failure) {
        if (failure instanceof String && ((String)failure).equals("needUpdate")) {

            super.updateApp();
            return;
        }
        if (api.equals("updateDetection")) {
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

            if (result == null || result.size() == 0) {
                return;
            }
            for (String r: result) {
                ServiceLogicUtils.getCylinderPlatformCyCodeFromScanResult(r, lastScanCySetPlatformIdList, lastScanCyPlatformCodeList);
            }

//            if (UrlUtils.strIsURL(result)) {
//
//                if (result.contains("/set/code/")) {
//                    lastScanCySetPlatformIdList.add((result.split("/set/code/"))[1]);
//                } else {
//                    String code = UrlUtils.parse(result).params.get("id");
//                    if (code != null && !code.equals("")) {
//                        lastScanCyPlatformCodeList.add(code);
//                    }
//                }
//            } else {
//                lastScanCyPlatformCodeList.add(result);
//            }

            if (lastScanCyPlatformCodeList.size() > 0) {
                new Thread() {
                    @Override
                    public void run() {
                        //在子线程中进行下载操作
                        try {
                            userPresenter.getCylinderInfoByPlatformCyCode(lastScanCyPlatformCodeList.get(0));
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
            } else {
                showToast("无有效气瓶二维码");
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

    public void showCyListActivity() {

        if (listAdapter.getMissionInfo() != null) {

            Intent intent = new Intent(ChargeActivity.this, CyListActivity.class);
            intent.putExtra("CyPlatformIdlist", listAdapter.getMissionInfo().getCyPlatformIdList());
            startActivity(intent);

        } else if (listAdapter.getNewMissionCyList().size() > 0) {
            Intent intent = new Intent(ChargeActivity.this, CyListActivity.class);
            intent.putExtra("CyBeanlist", listAdapter.getNewMissionCyList());
            startActivity(intent);
        }

        Log.i("11","11");
    }
}
