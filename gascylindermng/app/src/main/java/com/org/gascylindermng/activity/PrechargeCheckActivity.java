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
import com.org.gascylindermng.adapter.PrechargeCheckAdapter;
import com.org.gascylindermng.api.HttpResponseResult;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.ProcessBean;
import com.org.gascylindermng.bean.ProcessNextAreaBean;
import com.org.gascylindermng.bean.SetBean;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

public class PrechargeCheckActivity extends BaseActivity implements ApiCallback {

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.listview)
    WrapContentListView listview;

    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    private int REQUEST_CODE_2 = 0x02;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    private PrechargeCheckAdapter listAdapter;
    private UserPresenter userPresenter;

    private ArrayList<CylinderInfoBean> newCyList; //new mission cy
    private ArrayList<SetBean> newSetList;
    private ArrayList<CylinderInfoBean> newAllCyList; //new mission cy


    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_prechargecheck);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("回厂验空");
        userPresenter = new UserPresenter(this);

        this.newCyList = new ArrayList<CylinderInfoBean>();
        this.newSetList = new ArrayList<SetBean>();
        this.newAllCyList = new ArrayList<CylinderInfoBean>();

        listAdapter = new PrechargeCheckAdapter(this,ServiceLogicUtils.getCheckListByProcessIdAndCyCategoryId(ServiceLogicUtils.process_id_precharge_check));
        listview.setAdapter(listAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    if (listAdapter.allCyCount == 0) return;
                    Intent intent = new Intent(PrechargeCheckActivity.this, CyListActivity.class);
                    intent.putExtra("CyBeanlist", newAllCyList);
                    intent.putExtra("canDeleteCy", "1");
                    startActivityForResult(intent,REQUEST_CODE_2);

                }
            }
        });

        userPresenter.getCompanyProcessListByCompanyId();
    }

    @OnClick({R.id.back_img, R.id.submit, R.id.start_scanner})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.start_scanner:
                if (CommonTools.isCameraCanUse()) {
                    Intent intent = new Intent(PrechargeCheckActivity.this, CaptureActivity.class);
                    intent.putExtra("mode", ServiceLogicUtils.scan_multi);
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
                if (listAdapter.allCyCount == 0) {
                    showToast("请先扫描检测对象气瓶二维码");
                    return;
                }
                hideSoftInput();

                LinearLayout llname = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.view_operation_dialog, null);
                final TextView textView = (TextView) llname.findViewById(R.id.message);
                textView.setText("是否确认信息正确并提交？");

                final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
                btnConfirm.setText("确认");
                final TextView btnCancel = (TextView) llname.findViewById(R.id.dialog_btn_cancel);

                AlertDialog.Builder builder = new AlertDialog.Builder(PrechargeCheckActivity.this);
                final AlertDialog dialog = builder.setView(llname).create();
                dialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<String> cyIdList = new ArrayList<String>();
                        for (CylinderInfoBean c : newAllCyList) {
                            cyIdList.add(c.getCyId());
                        }

                        userPresenter.submitPrechargeCheckResult(
                                cyIdList,
                                listAdapter.getData(),
                                listAdapter.checkOK,
                                listAdapter.isEmptyCy ? "1" : "0",
                                listAdapter.nextAreaId,
                                listAdapter.remark);
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

        if (api.equals("submitPrechargeCheckResult")) {
            HttpResponseResult httpResponseResult = (HttpResponseResult) success;
            if (!httpResponseResult.getCode().equals("200")) {
                showToast("提交失败，" + httpResponseResult.getMessage() + "，请重新尝试提交。");
            } else {
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
                        dialog.dismiss();
                        closeAndRefreshBatchList();
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        closeAndRefreshBatchList();
                    }
                }, 1500);

            }
        } else if (api.equals("getCompanyProcessListByCompanyId")) {

            List<LinkedTreeMap> datas = (List<LinkedTreeMap>) success;
            if (datas != null && datas.size() > 0) {

                ArrayList<ProcessBean> beans = new ArrayList<>();
                for (LinkedTreeMap data : datas) {
                    JSONObject object=new JSONObject((LinkedTreeMap)data);
                    String mData = object.toString();
                    Type type = new TypeToken<ProcessBean>(){}.getType();
                    Gson gson = new Gson();
                    final ProcessBean bean =gson.fromJson(mData,type);
                    if (Integer.valueOf(bean.getProcessId()) == ServiceLogicUtils.process_id_precharge_check) {
                        new Thread(){
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
                    JSONObject object=new JSONObject((LinkedTreeMap)data);
                    String mData = object.toString();
                    Type type = new TypeToken<ProcessNextAreaBean>(){}.getType();
                    Gson gson = new Gson();
                    ProcessNextAreaBean bean =gson.fromJson(mData,type);
                    if (bean != null) beans.add(bean);
                }
                if (beans.size() > 0) {
                    listAdapter.nextAreaList.addAll(beans);
                    listAdapter.nextAreaId = beans.get(0).getAreaId();
                    listAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public <T> void failure(String api, T failure) {
        if (failure instanceof String && ((String)failure).equals("needUpdate")) {

            super.updateApp();
            return;
        }
        if (api.equals("submitPrechargeCheckResult")) {
            showToast("提交失败，" + (String) failure + "，请重新尝试提交。");
        } else {
            showToast("接口报错，" + (String) failure);
        }
    }

    private void closeAndRefreshBatchList() {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("needRefresh", "1");
        resultIntent.putExtras(bundle);
        this.setResult(0xA1, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                ArrayList<String> result = bundle.getStringArrayList("qr_scan_result");
                if (result != null && result.size() > 0) {
                    listAdapter.scanCount += result.size();
                }

                ArrayList<SetBean> setList = (ArrayList<SetBean>) bundle.getSerializable("qr_scan_result_set_list");
                if (setList != null && setList.size() > 0) {
                    getNewSetList().addAll(setList);
                    listAdapter.setCount = getNewSetList().size();
                }

                ArrayList<CylinderInfoBean> cyList = (ArrayList<CylinderInfoBean>) bundle.getSerializable("qr_scan_result_cy_list");
                if (cyList != null && cyList.size() > 0) {
                    getNewCyList().addAll(cyList);
                    listAdapter.cyCount = getNewCyList().size();
                }

                ArrayList<CylinderInfoBean> allCyList = (ArrayList<CylinderInfoBean>) bundle.getSerializable("qr_scan_result_all_cy_list");
                if (allCyList != null && allCyList.size() > 0) {
                    getNewAllCyList().addAll(allCyList);
                    listAdapter.allCyCount = getNewAllCyList().size();

                }
                listAdapter.notifyDataSetChanged();

            } else {
                //showToast("扫描失败");
            }
        } else if (requestCode == REQUEST_CODE_2) {

            ArrayList<CylinderInfoBean> result = (ArrayList<CylinderInfoBean>)bundle.getSerializable("CyBeanlist");
            newAllCyList.clear();
            if (result != null && result.size() > 0) {
                newAllCyList.addAll(result);
            }
            if (newAllCyList.size() > 0) {

                for (int i = newSetList.size()-1; i > -1; i--) {
                    boolean hasSet = false;
                    for (CylinderInfoBean c : newAllCyList) {
                        if (!TextUtils.isEmpty(c.getSetId()) && c.getSetId().equals(newSetList.get(i).getSetId())) {
                            hasSet = true;
                            break;
                        }
                    }
                    if (!hasSet) {
                        newSetList.remove(i);
                    }
                }

                for (int i = newCyList.size()-1; i > -1; i--) {

                    boolean hasCy = false;
                    for (CylinderInfoBean c : newAllCyList) {
                        if (c.getCyId().equals(newCyList.get(i).getCyId())) {
                            hasCy = true;
                            break;
                        }
                    }
                    if (!hasCy) {
                        newCyList.remove(i);
                    }
                }

            } else {
                newCyList.clear();
                newSetList.clear();
            }

            listAdapter.scanCount = newSetList.size() + newCyList.size();
            listAdapter.setCount = newSetList.size();
            listAdapter.cyCount = newCyList.size();
            listAdapter.allCyCount = newAllCyList.size();
            listAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<CylinderInfoBean> getNewCyList() {
        return newCyList;
    }

    public void setNewCyList(ArrayList<CylinderInfoBean> newCyList) {
        this.newCyList = newCyList;
    }

    public ArrayList<SetBean> getNewSetList() {
        return newSetList;
    }

    public void setNewSetList(ArrayList<SetBean> newSetList) {
        this.newSetList = newSetList;
    }

    public ArrayList<CylinderInfoBean> getNewAllCyList() {
        return newAllCyList;
    }

    public void setNewAllCyList(ArrayList<CylinderInfoBean> newAllCyList) {
        this.newAllCyList = newAllCyList;
    }
}
