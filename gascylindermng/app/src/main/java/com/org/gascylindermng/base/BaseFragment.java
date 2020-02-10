package com.org.gascylindermng.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import butterknife.ButterKnife;

/***
 * fragment基类
 */
public abstract class BaseFragment extends Fragment {
    protected BaseActivity activity;
    protected View mContentView;
    protected RxPermissions rxPermission;
    protected LoadingDialog loadingDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BaseActivity) context;
        rxPermission = new RxPermissions(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(initLayout(), container, false);
            ButterKnife.bind(this, mContentView);
            init();
        }
        return mContentView;
    }

    protected void loading(String content) {
        loadingDialog = new LoadingDialog(activity);
        loadingDialog.setLoadingText(content).show();
    }

    protected void loading(String content, String success, String failed) {
        loadingDialog = new LoadingDialog(activity);
        loadingDialog.setLoadingText(content).setFailedText(failed).setSuccessText(success).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(loadingDialog!=null)
            loadingDialog.close();

    }

    protected void showToast(String content) {
        Toast.makeText(activity, content, Toast.LENGTH_LONG).show();
    }

    protected abstract int initLayout();

    protected abstract void init();


}
