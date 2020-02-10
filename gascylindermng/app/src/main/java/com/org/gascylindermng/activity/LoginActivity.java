package com.org.gascylindermng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.org.gascylindermng.BuildConfig;
import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.User;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements ApiCallback {

    @BindView(R.id.username)
    EditText usernameET;
    @BindView(R.id.password)
    EditText passwordET;


    UserPresenter userPresenter;
    @BindView(R.id.cur_version)
    TextView curVersion;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        userPresenter = new UserPresenter(this);
        curVersion.setText("V "+BuildConfig.VERSION_NAME);
    }

    @OnClick(R.id.login)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:

                String username = usernameET.getText().toString().trim();
                String pwd = passwordET.getText().toString().trim();
                if ("".equals(username) || null == username) {
                    showToast("用户名不能为空！");
                    return;
                }

                if ("".equals(pwd) || null == pwd) {
                    showToast("密码不能为空！");
                    return;
                }
                userPresenter.login(username, pwd);
                showLoading("登录中...", "登录成功", "登录失败");
                break;
        }
    }

    @Override
    public <T> void successful(String api, T success) {
        if (success instanceof User) {
            new Thread() {
                @Override
                public void run() {
                    //在子线程中进行下载操作
                    try {
                        userPresenter.getCompanyById();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            loadingDialog.loadSuccess();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public <T> void failure(String api, T failure) {
        loadingDialog.loadFailed();
        showToast("登录失败" + (String) failure);
        if (failure instanceof String && ((String) failure).equals("needUpdate")) {

            super.updateApp();
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
