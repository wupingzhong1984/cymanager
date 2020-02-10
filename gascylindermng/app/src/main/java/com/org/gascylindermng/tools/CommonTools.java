package com.org.gascylindermng.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.View;

import com.org.gascylindermng.activity.PrechargeCheckActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommonTools {

    public static void measureWidthAndHeight(View view) {

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        view.measure(widthMeasureSpec, heightMeasureSpec);

    }


    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse) {
            if (mCamera != null)
                mCamera.release();
            mCamera = null;
        }
        return canUse;
    }

    public static boolean isAppInstalled(Context mContext, String packageName) {
        PackageManager manager = mContext.getPackageManager();
        List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (PackageInfo info : installedPackages) {
                if (info.packageName.equals(packageName))
                    return true;
            }
        }
        return false;
    }

    public static String getLastDayDateWithYearAndMonth(String year, String tempMonth) {

        //year="2018" month="2"
        Calendar calendar = Calendar.getInstance();
        // 设置时间,当前时间不用设置
        String month;
        if (tempMonth.contains("0")) {
            month = tempMonth.substring(1);
        } else {
            month = tempMonth;
        }

        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month));

        // System.out.println(calendar.getTime());

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
    }

    //date format must be "yyyy-MM-dd"
    public static <T> boolean date1BeforeNow(String date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        boolean before = false;
        try {
            Date date1 = format.parse(date);
            before = date1.before(new Date());
            System.out.println(before);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return before;
    }
}
