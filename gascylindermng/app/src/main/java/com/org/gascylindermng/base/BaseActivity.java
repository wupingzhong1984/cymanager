package com.org.gascylindermng.base;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.org.gascylindermng.R;
import com.org.gascylindermng.tools.PermissionPageUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    protected RxPermissions rxPermission;
    protected LoadingDialog loadingDialog;

    public void updateApp() {

        int permission = ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // 2、提示  3、禁止
        if (permission != PackageManager.PERMISSION_GRANTED) {
            showToast("请打开此应用的存储权限！");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PermissionPageUtils u = new PermissionPageUtils(getBaseContext());
                    u.jumpPermissionPage();
                }
            }, 1000);

        } else {
            LinearLayout llname = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.view_common_dialog, null);
            final TextView textView = (TextView) llname.findViewById(R.id.message);
            textView.setText("新版本已上线，需要下载安装后才可继续使用。");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.setView(llname).create();
            dialog.show();

            final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
            btnConfirm.setText("开始下载");
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downLoadApk("http://www.ffqs.cn/cymanager.apk");
                }
            });
        }


    }
    private void downLoadApk(final String downloadUrl) {
        //进度条
        final ProgressDialog pd;
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(downloadUrl, pd);
                    //安装APK
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                }
            }}.start();

    }

    private static File getFileFromServer(String path, ProgressDialog pd) throws Exception{
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "cymanager.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len ;
            int total=0;
            while((len =bis.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                total+= len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        }
        else{
            return null;
        }
    }

    private void installApk(File file) {

        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    this, "com.org.gascylindermng", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean hasInstallPermission = this.getPackageManager().canRequestPackageInstalls();
                if (!hasInstallPermission) {
                    startInstallPermissionSettingActivity();
                }
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        try {
            this.startActivity(intent);
        } catch (Exception e) {
            Log.d("error:", e.toString());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() { //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPermission = new RxPermissions(this);
        initLayout(savedInstanceState);
        ButterKnife.bind(this);
        setStatusBar(getStatusBarColor());
        init(savedInstanceState);


    }

    protected  void showLoading(String loadingMessage) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setLoadingText(loadingMessage)
                .show();
    }



    protected void showLoading(String loadingMessage, String SuccessMessage, String failedMessage) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setLoadingText(loadingMessage)
                .setFailedText(failedMessage)
                .setSuccessText(SuccessMessage)
                .show();
    }


    /**
     * Android 6.0 以上设置状态栏颜色
     */
    protected void setStatusBar(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().setStatusBarColor(color);

            // 如果亮色，设置状态栏文字为黑色
            if (isLightColor(color)) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }

    }

    /**
     * 判断颜色是不是亮色
     *
     * @param color
     * @return
     * @from https://stackoverflow.com/questions/24260853/check-if-color-is-dark-or-light-in-android
     */
    private boolean isLightColor(@ColorInt int color) {
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }

    /**
     * 获取StatusBar颜色，默认白色
     *
     * @return
     */
    protected @ColorInt
    int getStatusBarColor() {
        return Color.WHITE;
    }


    protected void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

    protected void loading(String content) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setLoadingText(content).show();
    }

    protected void loading(String content, String success, String failed) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setLoadingText(content).setFailedText(failed).setSuccessText(success).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null)
            loadingDialog.close();
    }

    protected abstract void initLayout(Bundle savedInstanceState);

    protected abstract void init(Bundle savedInstanceState);

    protected void hideSoftInput () {
        View peekDecorView = getWindow().peekDecorView();
        if (peekDecorView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(peekDecorView.getWindowToken(),0);
        }
    }
}
