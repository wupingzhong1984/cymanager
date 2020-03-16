package com.org.gascylindermng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.org.gascylindermng.R;
import com.org.gascylindermng.adapter.ChargeMissionListAdapter;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.ChargeMissionBean;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.CyChargeCheckRecordBean;
import com.org.gascylindermng.bean.SetBean;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.ServiceLogicUtils;
import com.org.gascylindermng.view.WrapContentListView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChargeMissionListActivity extends BaseActivity implements ApiCallback, ChargeMissionListAdapter.AdapterClickListener {


    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.listview)
    WrapContentListView listview;
    @BindView(R.id.today_bottle_count)
    TextView todayBottleCount;
    @BindView(R.id.right_text)
    TextView rightText;

    private ChargeMissionListAdapter listAdapter;
    private UserPresenter userPresenter;

    private ArrayList<ChargeMissionBean> missionList;

    private int lastDeleteMissionPosition = 0;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_charge_list);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("充装任务");
        rightText.setVisibility(View.VISIBLE);
        rightText.setText("刷新");
        missionList = new ArrayList<ChargeMissionBean>();
        userPresenter = new UserPresenter(this);
        listAdapter = new ChargeMissionListAdapter(this, this);
        listview.setAdapter(listAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ChargeMissionListActivity.this, ChargeActivity.class);
                intent.putExtra("ChargeMissionBean", listAdapter.getData().get(position));
                startActivityForResult(intent,0x01);
            }
        });
        requestMissionList();
    }

    private void requestMissionList() {
        loading("加载中","取得成功","取得失败");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        userPresenter.getChargeMissionListNow();
        //userPresenter.getChargeMissionList("2020-01-15"); //test
    }

    @Override
    public <T> void successful(String api, T success) {

        if (api.equals("deleteChargeMissionByMissionId")) {

            missionList.remove(lastDeleteMissionPosition);
            listAdapter.notifyDataSetChanged();

        } else if (api.equals("getChargeMissionList") && !(success instanceof String)) {

            loadingDialog.loadSuccess();
            missionList.clear();
            listAdapter.deleteAllData();
            todayBottleCount.setText("今日充装：" + 0 + "（集格以集格内瓶数算）");

            List<LinkedTreeMap> datas = (List<LinkedTreeMap>) success;
            if (datas != null && datas.size() > 0) {

                ArrayList<ChargeMissionBean> beans = new ArrayList<>();
                int cyCount = 0;
                for (LinkedTreeMap tm : datas) {
                    JSONObject object = new JSONObject((LinkedTreeMap) tm);
                    String mData = object.toString();
                    Type type = new TypeToken<ChargeMissionBean>() {
                    }.getType();
                    Gson gson = new Gson();
                    ChargeMissionBean mission = gson.fromJson(mData, type);
                    if (mission != null) {
                        mission.setCylinderIdList(new ArrayList<String>());

                        List<LinkedTreeMap> checkDataList = (List<LinkedTreeMap>) tm.get("yqDetectionVoList");
                        if (checkDataList != null && checkDataList.size() > 0) {
                            for (LinkedTreeMap cyCheckData : checkDataList) {
                                double id = (double)cyCheckData.get("cylinderId");
                                int i = (new Double(id)).intValue();
                                mission.getCylinderIdList().add(String.valueOf(i));

                                JSONObject object2 = new JSONObject(cyCheckData);
                                String mData2 = object2.toString();
                                Type type2 = new TypeToken<CyChargeCheckRecordBean>() {
                                }.getType();
                                Gson gson2 = new Gson();
                                CyChargeCheckRecordBean record = gson2.fromJson(mData2, type2);
                                if (record != null) {

                                    mission.getCyCheckRecordList().add(record);
                                    for (CheckItemBean checkItem : ServiceLogicUtils.getCheckListByProcessIdAndCyCategoryId(ServiceLogicUtils.process_id_charge)) {

                                        Object result = cyCheckData.get(checkItem.getApiParam());

                                        if (result != null && (double)result == 1) {
                                            checkItem.setState(true);
                                        } else {
                                            checkItem.setState(false);
                                        }
                                        record.getCheckItemResultList().add(checkItem);

                                    }
                                }
                            }
                        }
                        if(mission.getStatus().equals("1")) {
                            missionList.add(0,mission);
                        } else {
                            missionList.add(mission);
                        }
                        cyCount += Integer.valueOf(mission.getCylinderCount());
                    }
                }

                if (missionList.size() > 0) {
                    listAdapter.updateData(missionList);
                    todayBottleCount.setText("今日充装：" + cyCount + "（集格以集格内瓶数算）");
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

        if (api.equals("getChargeMissionList")) {
            loadingDialog.loadFailed();
            missionList.clear();
            listAdapter.deleteAllData();
            todayBottleCount.setText("今日充装：" + 0 + "（集格以集格内瓶数算）");
        }
    }

    @OnClick({R.id.back_img, R.id.create_mission, R.id.right_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.create_mission:
                Intent intent = new Intent(ChargeMissionListActivity.this, ChargeActivity.class);
                startActivityForResult(intent,0x01);
                break;
            case R.id.right_text:
                requestMissionList();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
                        requestMissionList();
                    }
                }.start();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void deleteClicked(int postision) {



        lastDeleteMissionPosition = postision;
        userPresenter.deleteChargeMissionByMissionId(missionList.get(postision).getMissionId());
    }
}
