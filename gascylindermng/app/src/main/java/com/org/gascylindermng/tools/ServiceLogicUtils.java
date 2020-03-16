package com.org.gascylindermng.tools;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.org.gascylindermng.R;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.Pureness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ServiceLogicUtils {

    public static String scan_single = "1"; //单扫
    public static String scan_multi = "2"; //连扫

    public static int process_id_precharge_check = 1; //回厂验空
    public static int process_id_charge = 2; //充装
    public static int process_id_postcharge_check = 3; //充后验满（质检）
    public static int process_id_send = 4; //发瓶
    public static int process_id_receive = 5; //收瓶
    public static int process_id_repair = 6; //维修
    public static int process_id_regular_inspection = 7; //定期检查
    public static int process_id_change_medium = 8; //气瓶维护
    public static int process_id_scrap = 9; //报废

    public static int user_position_siji = 1; //司机
    public static int user_position_yayunyuan = 2; //押运员
    public static int user_position_shoufa = 3; //收发
    public static int user_position_shenchan = 4; //生产
    public static int user_position_jiance = 5; //检测

    public static String getPositionNameByPositionInt(String position) {

        //1司机，2押运员，3收发，4生产，5检测，6气瓶管理员

        switch (position) {

            case "1":
                return "司机";
            case "2":
                return "押运员";
            case "3":
                return "收发";
            case "4":
                return "生产";
            case "5":
                return "分析";
            case "6":
                return "气瓶管理员";
            case "7":
                return "检测";
            case "8":
                return "办公";
                default:
                    return "未知";
        }
    }

    public static ArrayList<CheckItemBean> getCheckListByProcessIdAndCyCategoryId(int processId) {

        ArrayList<CheckItemBean> list = new ArrayList<CheckItemBean>();
        ArrayList<String> titleList = new ArrayList<String>();
        ArrayList<String> apiParamList = new ArrayList<String>();
        if (processId == process_id_precharge_check) {

            titleList.add("瓶身颜色");
            apiParamList.add("color");

            titleList.add("阀口螺纹");
            apiParamList.add("valve");

            titleList.add("瓶内余压");
            apiParamList.add("residualPressure");

            titleList.add("气瓶外观");
            apiParamList.add("appearance");

            titleList.add("安全附件");
            apiParamList.add("safety");

        } else if (processId == process_id_charge) {

            titleList.add("充前-瓶身颜色");
            apiParamList.add("beforeColor");

            titleList.add("充前-气瓶外观");
            apiParamList.add("beforeAppearance");

            titleList.add("充前-安全附件");
            apiParamList.add("beforeSafety");

            titleList.add("充前-检测日期");
            apiParamList.add("beforeRegularInspectionDate");

            titleList.add("充前-瓶内余压");
            apiParamList.add("beforeResidualPressure");


            titleList.add("充装-是否正常");
            apiParamList.add("fillingIfNormal");


            titleList.add("充后-压力复查");
            apiParamList.add("afterPressure");

            titleList.add("充后-阀门泄露");
            apiParamList.add("afterCheckLeak");

            titleList.add("充后-外观检查");
            apiParamList.add("afterAppearance");

            titleList.add("充后-温度检查");
            apiParamList.add("afterTemperature");

        } else if (processId == process_id_postcharge_check) {

            titleList.add("检测有效期");
            apiParamList.add("validityPeriod");

            titleList.add("检漏");
            apiParamList.add("checkLeak");

            titleList.add("测压");
            apiParamList.add("pressure");

            titleList.add("外观检查");
            apiParamList.add("appearance");

            titleList.add("温度无异常");
            apiParamList.add("temperature");

            titleList.add("合格证标签");
            apiParamList.add("certificate");

            titleList.add("生产安全标签");
            apiParamList.add("productionSafety");

        } else if (processId == process_id_regular_inspection) {

            titleList.add("外观检查");
            apiParamList.add("appearance");

            titleList.add("阀口螺纹");
            apiParamList.add("valve");

            titleList.add("测压");
            apiParamList.add("pressure");

            titleList.add("水容积");
            apiParamList.add("volume");

        } else if (processId == process_id_change_medium) {

            titleList.add("抽真空");
            apiParamList.add("vacuo");

            titleList.add("置换");
            apiParamList.add("update");

            titleList.add("修改外观");
            apiParamList.add("appearance");
        }

        for (int i = 0; i < titleList.size(); i++) {
            CheckItemBean b = new CheckItemBean();
            b.setTitle(titleList.get(i));
            b.setApiParam(apiParamList.get(i));
            if (processId == process_id_change_medium) {
                b.setState(false);
            }
            list.add(b);
        }

        return list;
    }

    public static ArrayList<Pureness> getPurenessList() {

        ArrayList<Pureness> list = new ArrayList<>();

        Pureness p1 = new Pureness();
        p1.setKeyValue("1");
        p1.setText("普");
        list.add(p1);

        Pureness p2 = new Pureness();
        p2.setKeyValue("2");
        p2.setText("2N");
        list.add(p2);

        Pureness p3 = new Pureness();
        p3.setKeyValue("3");
        p3.setText("3N");
        list.add(p3);

        Pureness p4 = new Pureness();
        p4.setKeyValue("4");
        p4.setText("4N");
        list.add(p4);

        Pureness p5 = new Pureness();
        p5.setKeyValue("5");
        p5.setText("5N");
        list.add(p5);

        Pureness p6 = new Pureness();
        p6.setKeyValue("6");
        p6.setText("6N");
        list.add(p6);

        return list;
    }

    public static Pureness getPurenessByKey(String key) {

        for (Pureness p : getPurenessList()) {
            if (p.getKeyValue().equals(key)) {
                return p;
            }
        }
        return null;
    }

    public static ArrayList<String> getTeamList() {

        ArrayList<String> list = new ArrayList<>();

        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");

        return list;
    }


    public static void getCylinderPlatformNumberOrSetIdFromScanResult(String result,
                                                          ArrayList<String> lastScanCySetIdList,   //1,2,3
                                                          ArrayList<String> lastScanCyPlatformNumberList) { //001xxxxxxxxx


        if (UrlUtils.strIsURL(result)) {

            if (result.contains("/set/code/")) {
                lastScanCySetIdList.add((result.split("/set/code/"))[1]);
            } else if (result.contains("t.ffqs.cn")) { // http://t.ffqs.cn/
                lastScanCyPlatformNumberList.add(result.substring(" http://t.ffqs.cn/".length()-1,result.length()));
            } else {
                String code = UrlUtils.parse(result).params.get("id");
                if (code != null && !code.equals("")) {
                    lastScanCyPlatformNumberList.add(code);
                }
            }
        } else {//条码

        }
    }

    //return 1,2,3 or  001xxxxxxxxx
    public static String getCylinderPlatformNumberOrSetIdFromScanResult(String result) {


        if (UrlUtils.strIsURL(result)) {

            if (result.contains("/set/code/")) {
                return (result.split("/set/code/"))[1];
            } else if (result.contains("t.ffqs.cn")) { // http://t.ffqs.cn/
                return result.substring(" http://t.ffqs.cn/".length()-1,result.length());
            } else {
                String code = UrlUtils.parse(result).params.get("id");
                if (code != null && !code.equals("")) {
                    return code;
                } else {
                    return "";
                }
            }
        } else {
            return result;
        }
    }

    public static Date getChargeClassBeginTime(Date time) {

        Calendar cal=Calendar.getInstance();

        cal.setTime(time);

        if (cal.get(Calendar.HOUR_OF_DAY) < 7) {

            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.HOUR_OF_DAY,15);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);

        } else if (cal.get(Calendar.HOUR_OF_DAY) < 19) {

            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.HOUR_OF_DAY,17);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);

        } else {
            cal.set(Calendar.HOUR_OF_DAY,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
        }
        Log.i("ChargeClassBeginTime:",cal.getTime().toString());
        return  cal.getTime();
    }

    public static Date getChargeClassEndTime(Date time) {

        Calendar cal=Calendar.getInstance();

        cal.setTime(time);

        if (cal.get(Calendar.HOUR_OF_DAY) < 7) {

            cal.set(Calendar.HOUR_OF_DAY,10);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);

        } else if (cal.get(Calendar.HOUR_OF_DAY) < 19) {

            cal.set(Calendar.HOUR_OF_DAY,22);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);

        } else {
            cal.add(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY,10);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
        }
        Log.i("ChargeClassEndTime:",cal.getTime().toString());
        return  cal.getTime();
    }

    //判断二维码是否合规有效
    public static boolean isValidCyNumber(String number, String unitId) {

        boolean valid = true;

        if (number.length() != 11) {
            valid = false;
        } else {

            String head = number.substring(0,4);
            if (Integer.valueOf(unitId) != Integer.valueOf(head)) {
                valid = false;
            }
        }

        return valid;
    }
}


