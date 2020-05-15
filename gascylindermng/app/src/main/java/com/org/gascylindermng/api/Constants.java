package com.org.gascylindermng.api;

import android.os.Environment;

public interface Constants {

    String Base_Url = "http://47.101.208.226:18090/api/";

    String Base_Url2 = "http://47.101.208.226:18080/api/"; //中台
    //TEST URL
    // String Base_Url = "http://47.101.47.89:18090/api/"

    //保存位置
    String savePath = Environment.getExternalStorageDirectory() + "/gas_cylinder_mng/";

    String sign = "199bc872ad9a4954b17fd9e937bccaa0";
    String appkey = "776c666db24d46708aca388ff6888685";
}
