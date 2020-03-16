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
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.ServiceLogicUtils;
import com.org.gascylindermng.view.WrapContentListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class ChargeCyListActivity extends BaseActivity implements ApiCallback, ChargeCyListAdapter.AdapterClickListener {

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.listview)
    WrapContentListView listview;

    private ChargeCyListAdapter listAdapter;
    private UserPresenter userPresenter;

    private ArrayList<String> platformIdList;
    private ArrayList<CylinderInfoBean> cyList;

    private boolean canCheck = false;
    private boolean canDeleteCy = false;

    private String deleteSetId;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cylist);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("气瓶列表");
        this.cyList = new ArrayList<CylinderInfoBean>();
        userPresenter = new UserPresenter(this);

        Object checkFlag = getIntent().getSerializableExtra("canCheck");
        if (checkFlag != null && checkFlag.equals("1")) {
            canCheck = true;
        }

        Object deleteFlag = getIntent().getSerializableExtra("canDeleteCy");
        if (deleteFlag != null && deleteFlag.equals("1")) {
            canDeleteCy = true;
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

        Object idList = getIntent().getSerializableExtra("CyPlatformIdlist");
        if (idList != null) {
            this.platformIdList = (ArrayList<String>) getIntent().getSerializableExtra("CyPlatformIdlist");
            userPresenter.getCylinderInfoByPlatformCyNumber(platformIdList.get(0));
        } else {
            this.cyList = (ArrayList<CylinderInfoBean>) getIntent().getSerializableExtra("CyBeanlist");
            listAdapter.updateData(cyList);
        }

        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("CyBeanlist", getCyList());
        resultIntent.putExtras(bundle);
        ChargeCyListActivity.this.setResult(0xA1, resultIntent);

    }

    @Override
    public <T> void successful(String api, T success) {

        if (api.equals("getCylinderInfoByPlatformCyNumber")) {

            if (success != null && success instanceof CylinderInfoBean) {

                cyList.add((CylinderInfoBean)success);
                listAdapter.updateData(cyList);
                platformIdList.remove(0);
                if (platformIdList.size() > 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.getCylinderInfoByPlatformCyNumber(platformIdList.get(0));
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
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("CyBeanlist", getCyList());
                resultIntent.putExtras(bundle);
                ChargeCyListActivity.this.setResult(0xA1, resultIntent);
                listAdapter.updateData(getCyList());
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
