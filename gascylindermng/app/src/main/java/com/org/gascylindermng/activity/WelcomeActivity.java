package com.org.gascylindermng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.User;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class WelcomeActivity extends BaseActivity implements ApiCallback {
    @BindView(R.id.welcome_images)
    ImageView welcomeImages;
    private UserPresenter userPresenter;

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        userPresenter = new UserPresenter(this);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.welcome);
        welcomeImages.startAnimation(anim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//                User user = (User) SharedPreTools.query("user",new User());
                User user = userPresenter.queryUser();
                Intent intent = new Intent();
                if (user.getId() == null) {
                    intent.setClass(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    intent.setClass(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }

    @Override
    public <T> void successful(String api, T success) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public <T> void failure(String api, T failure) {
        if (failure instanceof String && ((String)failure).equals("needUpdate")) {

            super.updateApp();
            return;
        }
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
