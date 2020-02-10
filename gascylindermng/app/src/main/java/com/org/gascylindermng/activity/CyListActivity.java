package com.org.gascylindermng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CyListActivity extends BaseActivity implements ApiCallback {

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.listview)
    WrapContentListView listview;

    private CyListAdapter listAdapter;
    private UserPresenter userPresenter;

    private ArrayList<String> platformIdList;
    private ArrayList<CylinderInfoBean> cyList;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cylist);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("气瓶列表");
        cyList = new ArrayList<CylinderInfoBean>();
        userPresenter = new UserPresenter(this);
        listAdapter =  new CyListAdapter(this,userPresenter.querComapny().getPinlessObject());
        listview.setAdapter(listAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(CyListActivity.this, CylinderInfoActivity.class);
                intent.putExtra("CylinderInfoBean", listAdapter.getData().get(position));
                startActivity(intent);
            }
        });

        platformIdList = (ArrayList<String>) getIntent().getSerializableExtra("CyPlatformIdlist");
        if (platformIdList != null) {

            userPresenter.getCylinderInfoByPlatformCyCode(platformIdList.get(0));

        } else {
            ArrayList<CylinderInfoBean> list = (ArrayList<CylinderInfoBean>) getIntent().getSerializableExtra("CyBeanlist");
            listAdapter.updateData(list);
        }
    }

    @Override
    public <T> void successful(String api, T success) {

        if (api.equals("getCylinderInfoByPlatformCyCode")) {

            if (success != null && success instanceof CylinderInfoBean) {

                cyList.add((CylinderInfoBean)success);
                platformIdList.remove(0);
                if (platformIdList.size() > 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            //在子线程中进行下载操作
                            try {
                                userPresenter.getCylinderInfoByPlatformCyCode(platformIdList.get(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    listAdapter.updateData(cyList);
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
}
