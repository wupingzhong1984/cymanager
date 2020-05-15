package com.org.gascylindermng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.activity.BindCodeToCyActivity;
import com.org.gascylindermng.activity.ChangeMediumActivity;
import com.org.gascylindermng.activity.ChargeMissionListActivity;
import com.org.gascylindermng.activity.PostchargeCheckActivity;
import com.org.gascylindermng.activity.PostchargeCheckBatchActivity;
import com.org.gascylindermng.activity.PrechargeCheckActivity;
import com.org.gascylindermng.activity.PrechargeCheckBatchActivity;
import com.org.gascylindermng.activity.ReceiveCylinderActivity;
import com.org.gascylindermng.activity.RegularInspectionActivity;
import com.org.gascylindermng.activity.RepairActivity;
import com.org.gascylindermng.activity.ScrapActivity;
import com.org.gascylindermng.activity.SendCylinderActivity;
import com.org.gascylindermng.activity.WarehouseTransActivity;
import com.org.gascylindermng.base.BaseFragment;
import com.org.gascylindermng.presenter.UserPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FunctionFragment extends BaseFragment {

    @BindView(R.id.bar_right_icon)
    ImageView barRightIcon;
    Unbinder unbinder;
    private UserPresenter userPresenter;
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.name)
    TextView name;

    @Override
    protected int initLayout() {
        return R.layout.fragment_function;
    }

    @Override
    protected void init() {

        userPresenter = new UserPresenter(null);
        backImg.setVisibility(View.GONE);
        titleName.setText("气瓶追溯系统");
        barRightIcon.setImageResource(R.mipmap.scanner_icon_white);
        barRightIcon.setVisibility(View.VISIBLE);
        name.setText("当前使用者："+userPresenter.queryUser().getEmployeeName());
    }

    private void showRules() {

        showToast("办公人员无权操作");
    }

    @OnClick({R.id.function_1, R.id.function_2, R.id.function_3, R.id.function_4, R.id.function_5,
            R.id.function_6, R.id.function_7, R.id.function_8, R.id.function_9, R.id.function_10, R.id.function_11, R.id.bar_right_icon})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.function_1:
                if (userPresenter.queryUser().getPosition().equals("8")) {
                    showRules();
                    return;
                }
                intent.setClass(getActivity(), PrechargeCheckBatchActivity.class);
                startActivity(intent);
                break;
            case R.id.function_2:
                if (userPresenter.queryUser().getPosition().equals("8")) {
                    showRules();
                    return;
                }
                intent.setClass(getActivity(), ChargeMissionListActivity.class);
                startActivity(intent);
                break;
            case R.id.function_3:
                if (userPresenter.queryUser().getPosition().equals("8")) {
                    showRules();
                    return;
                }
                intent.setClass(getActivity(), PostchargeCheckBatchActivity.class);
                startActivity(intent);
                break;
            case R.id.function_4:
                if (userPresenter.queryUser().getPosition().equals("8")) {
                    showRules();
                    return;
                }
                intent.setClass(getActivity(), SendCylinderActivity.class);
                startActivity(intent);
                break;
            case R.id.function_5:
                if (userPresenter.queryUser().getPosition().equals("8")) {
                    showRules();
                    return;
                }
                intent.setClass(getActivity(), ReceiveCylinderActivity.class);
                startActivity(intent);
                break;
            case R.id.function_6:
                intent.setClass(getActivity(), BindCodeToCyActivity.class);
                startActivity(intent);
                break;
            case R.id.function_7:
                if (userPresenter.queryUser().getPosition().equals("8")) {
                    showRules();
                    return;
                }
                intent.setClass(getActivity(), WarehouseTransActivity.class);
                startActivity(intent);
                break;
            case R.id.function_8:
                if (userPresenter.queryUser().getPosition().equals("8")) {
                    showRules();
                    return;
                }
                intent.setClass(getActivity(), RegularInspectionActivity.class);
                startActivity(intent);
                break;
            case R.id.function_9:
                if (userPresenter.queryUser().getPosition().equals("8")) {
                    showRules();
                    return;
                }
                intent.setClass(getActivity(), RepairActivity.class);
                startActivity(intent);
                break;
            case R.id.function_10:
                if (userPresenter.queryUser().getPosition().equals("8")) {
                    showRules();
                    return;
                }
                intent.setClass(getActivity(), ChangeMediumActivity.class);
                startActivity(intent);
                break;
            case R.id.function_11:
                if (userPresenter.queryUser().getPosition().equals("8")) {
                    showRules();
                    return;
                }
                intent.setClass(getActivity(), ScrapActivity.class);
                startActivity(intent);
                break;

            case R.id.bar_right_icon:
              //  intent.setClass(getActivity(), RegularInspectionActivity.class);
              //  startActivity(intent);
                //todo
                break;
        }
    }

}
