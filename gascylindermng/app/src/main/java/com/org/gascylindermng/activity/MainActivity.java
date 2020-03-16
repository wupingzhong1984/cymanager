package com.org.gascylindermng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.fragment.AccountFragment;
import com.org.gascylindermng.fragment.FunctionFragment;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.ServiceLogicUtils;

import java.util.ArrayList;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements TabHost.OnTabChangeListener  {
    @BindView(R.id.realcontent)
    FrameLayout realcontent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost mTabHost;
    private int mImageViewArray[] = {R.mipmap.function_icon01, R.mipmap.account_icon01};
    private int mImageViewArray_highlight[] = {R.mipmap.function_icon02, R.mipmap.account_icon02};
    private String mTextviewArray[] = {"功能","设置"};
    private Class fragmentArray[] = {FunctionFragment.class, AccountFragment.class};

    @Override
    protected void onRestart() {
        super.onRestart();
        mTabHost.setCurrentTab(0);
    }

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        mTabHost.setup(this, getSupportFragmentManager(), R.id.realcontent);
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
                    .setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }
        mTabHost.setCurrentTabByTag(mTextviewArray[0]);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setOnTabChangedListener(this);
    }

    private void updateTab() {
        TabWidget tabw = mTabHost.getTabWidget();
        for (int i = 0; i < tabw.getChildCount(); i++) {
            View view = tabw.getChildAt(i);
            ImageView iv = (ImageView) view.findViewById(R.id.item_icon);
            if (i == mTabHost.getCurrentTab()) {
                ((TextView) view.findViewById(R.id.item_name)).setTextColor(getResources().getColor(R.color.myblue));
                iv.setImageResource(mImageViewArray_highlight[i]);
            } else {
                ((TextView) view.findViewById(R.id.item_name)).setTextColor(getResources().getColor(R.color.text_color));
                iv.setImageResource(mImageViewArray[i]);
            }

        }
    }

    @Override
    public void onTabChanged(String tabId) {
        updateTab();
    }

    private View getTabItemView(int index) {
        View view = getLayoutInflater().inflate(R.layout.tabbar_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.item_icon);
        TextView textView = (TextView) view.findViewById(R.id.item_name);
        textView.setText(mTextviewArray[index]);
        if (index == 0) {
            textView.setTextColor(getResources().getColor(R.color.myblue));
            imageView.setImageResource(R.mipmap.function_icon02);
        } else {
            textView.setTextColor(getResources().getColor(R.color.text_color));
            imageView.setImageResource(mImageViewArray[index]);
        }


        return view;
    }


}
