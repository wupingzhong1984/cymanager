package com.org.gascylindermng.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.adapter.ChargeCyListAdapter;
import com.org.gascylindermng.adapter.CyListAdapter;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.ProcessNextAreaBean;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.ServiceLogicUtils;
import com.org.gascylindermng.view.WrapContentListView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class ChargeCyListActivity extends BaseActivity implements ApiCallback, ChargeCyListAdapter.AdapterClickListener {

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.listview)
    WrapContentListView listview;

    private ChargeCyListAdapter listAdapter;
    private UserPresenter userPresenter;

    private ArrayList<String> cyNumberList; //new mission
    private ArrayList<CylinderInfoBean> cyList; //created mission

    private boolean canCheck = false;
    private boolean canDeleteCy = false;

    private String deleteSetId;

    private ArrayList<ProcessNextAreaBean> nextAreaList;
    private ArrayList<String> checkOKList;
    private ArrayList<String> nextAreaIdList;
    private ArrayList<String> remarkList;
    private ArrayList<ArrayList<CheckItemBean>> checkItemResultListList;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cylist);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("气瓶列表");
        this.cyList = new ArrayList<CylinderInfoBean>();
        userPresenter = new UserPresenter(this);

        Object idList = getIntent().getSerializableExtra("CyNumberlist");
        if (idList != null) {
            this.cyNumberList = (ArrayList<String>) getIntent().getSerializableExtra("CyNumberlist");

            Object checkFlag = getIntent().getSerializableExtra("canCheck");
            if (checkFlag != null && checkFlag.equals("1")) {
                canCheck = true;
            }

            this.nextAreaList = (ArrayList<ProcessNextAreaBean>) getIntent().getSerializableExtra("nextAreaList");
            this.checkOKList = (ArrayList<String>) getIntent().getSerializableExtra("checkOKList");
            this.nextAreaIdList = (ArrayList<String>) getIntent().getSerializableExtra("nextAreaIdList");
            this.remarkList = (ArrayList<String>) getIntent().getSerializableExtra("remarkList");
            this.checkItemResultListList =(ArrayList<ArrayList<CheckItemBean>>) getIntent().getSerializableExtra("checkItemResultListList");
            userPresenter.getCylinderInfoByPlatformCyNumber(cyNumberList.get(0));
        } else {
            this.cyList = (ArrayList<CylinderInfoBean>) getIntent().getSerializableExtra("CyBeanlist");
            listAdapter.updateData(cyList);

            Object deleteFlag = getIntent().getSerializableExtra("canDeleteCy");
            if (deleteFlag != null && deleteFlag.equals("1")) {
                canDeleteCy = true;
            }

        }

        listAdapter =  new ChargeCyListAdapter(this,userPresenter.querComapny().getPinlessObject(),canCheck,canDeleteCy, this);
        listview.setAdapter(listAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ChargeCyListActivity.this, CylinderInfoActivity.class);
                intent.putExtra("CylinderInfoBean", listAdapter.getData().get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public <T> void successful(String api, T success) {

        if (api.equals("getCylinderInfoByPlatformCyNumber")) {

            if (success != null && success instanceof CylinderInfoBean) {

                cyList.add((CylinderInfoBean)success);
                listAdapter.updateData(cyList);
                cyNumberList.remove(0);
                if (cyNumberList.size() > 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.getCylinderInfoByPlatformCyNumber(cyNumberList.get(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {

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
        showToast("接口报错，" + (String) failure);
    }

    @OnClick(R.id.back_img)
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.back_img:
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("CyBeanlist", getCyList());
                bundle.putStringArrayList("nextAreaIdList", nextAreaIdList);
                bundle.putStringArrayList("checkOKList", checkOKList);
                bundle.putStringArrayList("remarkList",remarkList);
                bundle.putSerializable("checkItemResultListList", checkItemResultListList);
                resultIntent.putExtras(bundle);
                ChargeCyListActivity.this.setResult(0xA1, resultIntent);
                finish();
                break;
        }
    }

    @Override
    public void deleteClicked(final int postision) {

        CylinderInfoBean cylinderInfoBean = cyList.get(postision);
        if (!TextUtils.isEmpty(cylinderInfoBean.getSetId())) {
            setDeleteSetId(cylinderInfoBean.getSetId());
        } else {
            setDeleteSetId(null);
        }

        LinearLayout llname = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.view_operation_dialog, null);
        final TextView textView = (TextView) llname.findViewById(R.id.message);
        textView.setText(getDeleteSetId() != null?"确定删除该集格码？":"确定删除该气瓶码？");

        final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
        final TextView btnCancel = (TextView) llname.findViewById(R.id.dialog_btn_cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(ChargeCyListActivity.this);
        final AlertDialog dialog = builder.setView(llname).create();
        dialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getDeleteSetId() != null) {
                    for (int i = getCyList().size()-1; i > -1; i--) {

                        if (getCyList().get(i).getSetId() != null && getCyList().get(i).getSetId().equals(getDeleteSetId())) {
                            getCyList().remove(i);
                        }
                    }

                } else {
                    getCyList().remove(postision);
                }

                if(getCyList().size() == 0) {
                    listAdapter.deleteAllData();
                } else {
                    listAdapter.updateData(getCyList());
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
    }

    @Override
    public void checkEditClicked(final int postision) {

        CylinderInfoBean cylinderInfoBean = cyList.get(postision);

        Intent intent = new Intent(ChargeCyListActivity.this, ChargeCheckActivity.class);
        if (!TextUtils.isEmpty(cylinderInfoBean.getSetNumber())) {
            intent.putExtra("setNumber", cylinderInfoBean.getSetNumber());
        } else {
            intent.putExtra("cyNumber", cylinderInfoBean.getPlatformCyCode());
        }
        intent.putExtra("nextAreaList", nextAreaList);
        intent.putExtra("checkOK", checkOKList.get(postision));
        intent.putExtra("remark", remarkList.get(postision));
        intent.putExtra("nextAreaId", nextAreaIdList.get(postision));
        intent.putExtra("checkItemList", checkItemResultListList.get(postision));
        startActivityForResult(intent,ChargeCheckActivity.RESULT_CODE_CHARGE_CHECK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调

        Bundle bundle = data.getExtras();
        if (requestCode == ChargeCheckActivity.RESULT_CODE_CHARGE_CHECK) {

            String setNumber = bundle.getString("setNumber");
            String cyNumber = bundle.getString("cyNumber");
            String checkOK = bundle.getString("checkOK");
            String nextAreaId = bundle.getString("nextAreaId");
            String remark = bundle.getString("remark");
            ArrayList<CheckItemBean> checkItemList = (ArrayList<CheckItemBean>)bundle.getSerializable("checkItemList");

            for (int i = 0; i < cyList.size(); i++) {

                if ((!TextUtils.isEmpty(setNumber) && cyList.get(i).getSetNumber().equals(setNumber)) ||
                        (!TextUtils.isEmpty(cyNumber) && cyList.get(i).getPlatformCyCode().equals(cyNumber))) {
                    checkOKList.set(i,checkOK);
                    nextAreaIdList.set(i,nextAreaId);
                    remarkList.set(i,remark);
                    checkItemResultListList.set(i,checkItemList);
                }
            }
        }
    }


    public String getDeleteSetId() {
        return deleteSetId;
    }

    public void setDeleteSetId(String deleteSetId) {
        this.deleteSetId = deleteSetId;
    }

    public ArrayList<CylinderInfoBean> getCyList() {
        return cyList;
    }

    public void setCyList(ArrayList<CylinderInfoBean> cyList) {
        this.cyList = cyList;
    }


}
