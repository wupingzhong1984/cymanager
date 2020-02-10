package com.org.gascylindermng.api;

import android.app.Application;
import com.xiasuhuei321.loadingdialog.manager.StyleManager;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import java.io.File;

public class MyAppContext extends Application {
    private static MyAppContext myAppContext;

    public static MyAppContext getInstance() {
        return myAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myAppContext = this;
        StyleManager s = new StyleManager();
        s.Anim(false).repeatTime(0).contentSize(-1).intercept(true);
        LoadingDialog.initStyle(s);
        File file = new File(Constants.savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
