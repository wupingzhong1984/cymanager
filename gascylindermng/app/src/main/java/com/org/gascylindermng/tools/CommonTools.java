package com.org.gascylindermng.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.text.TextUtils;
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

    /**
     * 得到指定月的天数
     * */
    public static int getMonthLastDay(int year, int month)
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }
    public static boolean containPointAndIsInt(String str) {
        if (str.contains(".0")) {
            return true;
        }
        return false;
    }

    public static boolean lessFourHourBetweenNowAndDate(String time)  {

        if(TextUtils.isEmpty(time)) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTimeStr = sdf.format(System.currentTimeMillis());
        //每天毫秒数
        long nd = 1000 * 24 * 60 * 60;
        //每小时毫秒数
        long nh = 1000 * 60 * 60;
        //每分钟毫秒数
        long nm = 1000 * 60;

        try {
            Date nowDate = sdf.parse(nowTimeStr);
            Date date = sdf.parse(time);

            long diff = nowDate.getTime() - date.getTime();

            // 计算差多少天
            long day = diff / nd;
            // 计算差多少小时
            long hour = diff % nd / nh;
            // 计算差多少分钟
            long min = diff % nd % nh / nm;

            if(day == 0 && hour < 4){
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }

    }
}
