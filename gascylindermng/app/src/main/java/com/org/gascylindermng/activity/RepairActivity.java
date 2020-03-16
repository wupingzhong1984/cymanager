package com.org.gascylindermng.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.activity.CaptureActivity;
import com.org.gascylindermng.R;
import com.org.gascylindermng.adapter.RepairAdapter;
import com.org.gascylindermng.api.HttpResponseResult;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.ProcessBean;
import com.org.gascylindermng.bean.ProcessNextAreaBean;
import com.org.gascylindermng.bean.UrlEntity;
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
import butterknife.OnClick;

public class RepairActivity extends BaseActivity implements ApiCallback{

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.listview)
    WrapContentListView listview;

    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    private RepairAdapter listAdapter;
    private UserPresenter userPresenter;
    private String lastScanCyPlatformCode;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_repair);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("维修");
        userPresenter = new UserPresenter(this);
        listAdapter = new RepairAdapter(this);
        listview.setAdapter(listAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    if (listAdapter.cyInfo == null) {
                        if (CommonTools.isCameraCanUse()) {
                            Intent intent = new Intent(RepairActivity.this, CaptureActivity.class);
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
                    } else {
                        if (listAdapter.cyInfo == null) return;
                        Intent intent = new Intent(RepairActivity.this, CylinderInfoActivity.class);
                        intent.putExtra("CylinderInfoBean", listAdapter.cyInfo);
                        startActivity(intent);
                    }
                }
            }
        });

        userPresenter.getCompanyProcessListByCompanyId();
    }

    @OnClick({R.id.back_img, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.submit:
                if (listAdapter.cyInfo == null) {
                    showToast("请先扫描检测对象气瓶二维码");
                    return;
                }
                hideSoftInput();

                if (TextUtils.isEmpty(listAdapter.remark)) {
                    showToast("请在填写维修内容/维修失败理由。");
                    return;
                }

                LinearLayout llname = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.view_operation_dialog, null);
                final TextView textView = (TextView) llname.findViewById(R.id.message);
                textView.setText("是否确认信息正确并提交？");

                final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
                btnConfirm.setText("确认");
                final TextView btnCancel = (TextView) llname.findViewById(R.id.dialog_btn_cancel);

                AlertDialog.Builder builder = new AlertDialog.Builder(RepairActivity.this);
                final AlertDialog dialog = builder.setView(llname).create();
                dialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        userPresenter.submitCyRepairRecord(
                                listAdapter.cyInfo.getCyId(),
                                listAdapter.remark,
                                listAdapter.repairOK,
                                listAdapter.nextAreaId);
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

    @Override
    public <T> void successful(String api, T success) {

        if (api.equals("getCompanyProcessListByCompanyId")) {

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
                    if (Integer.valueOf(bean.getProcessId()) == ServiceLogicUtils.process_id_repair) {
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
                    listAdapter.nextAreaList.addAll(beans);
                    listAdapter.nextAreaId = beans.get(0).getAreaId();
                }
            }

        } else if (api.equals("getCylinderInfoByPlatformCyNumber")) {

            if (success != null && success instanceof CylinderInfoBean) {
                listAdapter.cyInfo = (CylinderInfoBean) success;
                listAdapter.notifyDataSetChanged();
            }

        } else if (api.equals("submitCyRepairRecord")) {
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
        if (api.equals("submitCyRepairRecord")) {
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
            this.lastScanCyPlatformCode = result.get(0);
            if (TextUtils.isEmpty(lastScanCyPlatformCode)) {
                showToast("异常二维码");
            } else {
                new Thread(){
                    @Override
                    public void run() {
                        //在子线程中进行下载操作
                        try {
                            userPresenter.getCylinderInfoByPlatformCyNumber(lastScanCyPlatformCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        } else {
            //showToast("扫描失败");
        }
    }
}
