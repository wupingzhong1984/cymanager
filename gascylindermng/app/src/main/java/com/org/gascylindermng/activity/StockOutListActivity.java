package com.org.gascylindermng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.org.gascylindermng.R;
import com.org.gascylindermng.adapter.StockOutListAdapter;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.StockOutListBean;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.view.WrapContentListView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class StockOutListActivity extends BaseActivity implements ApiCallback {

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.right_text)
    TextView rightText;
    @BindView(R.id.listview)
    WrapContentListView listview;
    @BindView(R.id.bottle_count)
    TextView bottleCount;

    private StockOutListAdapter listAdapter;
    private UserPresenter userPresenter;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_stockoutlist);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("出库批次");
        rightText.setVisibility(View.VISIBLE);
        rightText.setText("刷新");
        listAdapter = new StockOutListAdapter(this);
        listview.setAdapter(listAdapter);
        userPresenter = new UserPresenter(this);
        userPresenter.getStockOutRecord();
    }

    @Override
    public <T> void successful(String api, T success) {

        if (api.equals("getStockOutRecord")) {
            listAdapter.deleteAllData();
            bottleCount.setText("近期出库：" + 0 + "（集格以集格内瓶数算）");

            List<LinkedTreeMap> datas = (List<LinkedTreeMap>) success;
            if (datas != null && datas.size() > 0) {
                ArrayList<StockOutListBean> beans = new ArrayList<>();
                int cyCount = 0;
                for (LinkedTreeMap tm : datas) {
                    JSONObject object = new JSONObject((LinkedTreeMap) tm);
                    String mData = object.toString();
                    Type type = new TypeToken<StockOutListBean>() {
                    }.getType();
                    Gson gson = new Gson();
                    StockOutListBean batch = gson.fromJson(mData, type);
                    if (batch != null) {

                        beans.add(0,batch);
                        cyCount += Integer.valueOf(batch.getCylinderCount());
                    }
                }

                if (beans.size() > 0) {
                    listAdapter.updateData(beans);
                    bottleCount.setText("近期出库：" + cyCount + "（集格以集格内瓶数算）");
                }
            }
        }
    }

    @Override
    public <T> void failure(String api, T failure) {

        if (failure instanceof String && ((String) failure).equals("needUpdate")) {

            super.updateApp();
            return;
        }
        showToast("接口报错，" + (String) failure);
    }

    @OnClick({R.id.back_img, R.id.create, R.id.right_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.create:
                Intent intent = new Intent(StockOutListActivity.this, StockOutActivity.class);
                startActivityForResult(intent, 0x01);
                break;
            case R.id.right_text:
                userPresenter.getStockOutRecord();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {

            Bundle bundle = data.getExtras();
            String refresh = bundle.getString("needRefresh");
            if (refresh != null && refresh.equals("1")) {
                new Thread() {
                    @Override
                    public void run() {
                        //在子线程中进行下载操作
                        userPresenter.getStockOutRecord();
                    }
                }.start();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
