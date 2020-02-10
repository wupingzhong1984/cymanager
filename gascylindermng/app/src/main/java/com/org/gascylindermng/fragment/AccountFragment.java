package com.org.gascylindermng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.activity.LoginActivity;
import com.org.gascylindermng.base.BaseFragment;
import com.org.gascylindermng.bean.User;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.APKVersionCodeUtils;
import com.org.gascylindermng.tools.ServiceLogicUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AccountFragment extends BaseFragment {

    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.job)
    TextView job;
    @BindView(R.id.code)
    TextView code;
    @BindView(R.id.cur_version)
    TextView curVersion;

    private UserPresenter userPresenter;

    @Override
    protected int initLayout() {
        return R.layout.fragment_account;
    }

    @Override
    protected void init() {

        userPresenter = new UserPresenter(null);

        backImg.setVisibility(View.GONE);
        titleName.setText("设置");

        User user = userPresenter.queryUser();
        name.setText(user.getEmployeeName());
        job.setText(ServiceLogicUtils.getPositionNameByPositionInt(user.getPosition()));
        code.setText(user.getEmployeeCode());

        curVersion.setText(APKVersionCodeUtils.getVerName(this.getContext()));
    }

    @OnClick(R.id.logout)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.logout:
                userPresenter.deleteUser();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }
}
