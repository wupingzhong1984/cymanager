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
import com.org.gascylindermng.adapter.CyListAdapter;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.ChargeMissionBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.ProcessNextAreaBean;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.view.WrapContentListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CyListActivity extends BaseActivity implements ApiCallback, CyListAdapter.AdapterClickListener {

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.listview)
    WrapContentListView listview;

    private CyListAdapter listAdapter;
    private UserPresenter userPresenter;

    private ArrayList<CylinderInfoBean> cyList;

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

        Object deleteFlag = getIntent().getSerializableExtra("canDeleteCy");
        if (deleteFlag != null && deleteFlag.equals("1")) {
            canDeleteCy = true;
        }

        listAdapter =  new CyListAdapter(this,userPresenter.querComapny().getPinlessObject(),canDeleteCy,this);
        listview.setAdapter(listAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(CyListActivity.this, CylinderInfoActivity.class);
                intent.putExtra("CylinderInfoBean", listAdapter.getData().get(position));
                startActivity(intent);
            }
        });

        Object cyInfoList = getIntent().getSerializableExtra("CyBeanlist");
        if (cyInfoList != null && ((ArrayList<CylinderInfoBean>)cyInfoList).size() > 0) {
            this.cyList = (ArrayList<CylinderInfoBean>)cyInfoList;
            listAdapter.updateData(cyList);
        }

        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("CyBeanlist", getCyList());
        resultIntent.putExtras(bundle);
        CyListActivity.this.setResult(0xA1, resultIntent);
    }

    @Override
    public <T> void successful(String api, T success) {


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

        AlertDialog.Builder builder = new AlertDialog.Builder(CyListActivity.this);
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
                CyListActivity.this.setResult(0xA1, resultIntent);
                if (getCyList().size() == 0) {
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

    public ArrayList<CylinderInfoBean> getCyList() {
        return cyList;
    }

    public void setCyList(ArrayList<CylinderInfoBean> cyList) {
        this.cyList = cyList;
    }

    public String getDeleteSetId() {
        return deleteSetId;
    }

    public void setDeleteSetId(String deleteSetId) {
        this.deleteSetId = deleteSetId;
    }
}
