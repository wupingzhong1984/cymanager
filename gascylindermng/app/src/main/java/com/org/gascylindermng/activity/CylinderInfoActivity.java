package com.org.gascylindermng.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.CylinderInfoBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CylinderInfoActivity extends BaseActivity {

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.platform_cy_code)
    TextView platformCyCodeTV;
    @BindView(R.id.bottle_code)
    TextView bottleCodeTV;
    @BindView(R.id.owner_code)
    TextView ownerCodeTV;
    @BindView(R.id.medium)
    TextView mediumTV;
    @BindView(R.id.cy_category)
    TextView cyCategoryTV;
    @BindView(R.id.work_pressure)
    TextView workPressureTV;
    @BindView(R.id.volume)
    TextView volumeTV;
    @BindView(R.id.expiry_date)
    TextView expiryDateTV;
    @BindView(R.id.next_regular_date)
    TextView nextRegularDateTV;
    @BindView(R.id.manu_date)
    TextView manuDateTV;


    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cylinderinfo);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        CylinderInfoBean bean = (CylinderInfoBean) getIntent().getSerializableExtra("CylinderInfoBean");

        titleName.setText("气瓶详情");

        platformCyCodeTV.setText(bean.getPlatformCyCode());
        if (bean.getBottleCode() != null && bean.getBottleCode().length() > 0)
            bottleCodeTV.setText(bean.getBottleCode());
        if (bean.getCompanyRelateCode() != null && bean.getCompanyRelateCode().length() > 0)
            ownerCodeTV.setText(bean.getCompanyRelateCode());
        mediumTV.setText(bean.getCyMediumName());
        cyCategoryTV.setText(bean.getCyCategoryName());
        workPressureTV.setText(bean.getWorkPressure());
        volumeTV.setText(bean.getVolume());
        manuDateTV.setText(bean.getManuDate().substring(0,7));
        nextRegularDateTV.setText(bean.getNextRegularInspectionDate().substring(0,7));
        expiryDateTV.setText(bean.getScrapDate().substring(0,7));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
