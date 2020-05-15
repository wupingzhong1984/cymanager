package com.org.gascylindermng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.adapter.ChargeCheckAdapter;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.ProcessNextAreaBean;
import com.org.gascylindermng.view.WrapContentListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChargeCheckActivity extends BaseActivity {

    public static final int RESULT_CODE_CHARGE_CHECK = 0xA1;

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.listview)
    WrapContentListView listview;

    private ChargeCheckAdapter listAdapter;

    private String setNumber;
    private String cylinderNumber;

    private boolean check;
    private String nextAreaId;
    private ArrayList<ProcessNextAreaBean> nextAreaList;
    private String remark;
    private ArrayList<CheckItemBean> checkItemList;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_charge_check);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("充装检测");

        this.setNumber = (String) getIntent().getStringExtra("setNumber");
        this.cylinderNumber = (String) getIntent().getStringExtra("cyNumber");
        String checkOK = (String) getIntent().getStringExtra("checkOK");
        if (!TextUtils.isEmpty(checkOK) && checkOK.equals("1")) {
            check = true;
        } else {
            check = false;
        }
        this.checkItemList = (ArrayList<CheckItemBean>) getIntent().getSerializableExtra("checkItemList");
        this.remark = (String) getIntent().getStringExtra("remark");
        this.nextAreaId = (String) getIntent().getStringExtra("nextAreaId");
        this.nextAreaList = (ArrayList<ProcessNextAreaBean>) getIntent().getSerializableExtra("nextAreaList");

        listAdapter = new ChargeCheckAdapter(this,
                checkItemList,
                nextAreaList,
                cylinderNumber,
                setNumber,
                check,
                nextAreaId,
                remark);
        listview.setAdapter(listAdapter);
    }

    @OnClick({R.id.back_img, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.submit:

                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                //set and cy number
                bundle.putString("setNumber",setNumber);
                bundle.putString("cyNumber",cylinderNumber);
                //checkOK
                bundle.putString("checkOK",listAdapter.checkOK?"1":"0");
                //next area id
                bundle.putString("nextAreaId",listAdapter.nextAreaId);
                //remark
                bundle.putString("remark",listAdapter.remark);
                //check item list
                bundle.putSerializable("checkItemList",(ArrayList<CheckItemBean>)listAdapter.getData());
                resultIntent.putExtras(bundle);
                this.setResult(RESULT_CODE_CHARGE_CHECK, resultIntent);

                finish();
                break;
        }
    }

    public String getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }

    public String getCylinderNumber() {
        return cylinderNumber;
    }

    public void setCylinderNumber(String cylinderNumber) {
        this.cylinderNumber = cylinderNumber;
    }

    public String getNextAreaId() {
        return nextAreaId;
    }

    public void setNextAreaId(String nextAreaId) {
        this.nextAreaId = nextAreaId;
    }

    public ArrayList<ProcessNextAreaBean> getNextAreaList() {
        return nextAreaList;
    }

    public void setNextOptionList(ArrayList<ProcessNextAreaBean> nextAreaList) {
        this.nextAreaList = nextAreaList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ArrayList<CheckItemBean> getCheckItemList() {
        return checkItemList;
    }

    public void setCheckItemList(ArrayList<CheckItemBean> checkItemList) {
        this.checkItemList = checkItemList;
    }
}
