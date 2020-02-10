package com.org.gascylindermng.model.impl;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.org.gascylindermng.bean.ChargeMissionBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.model.ChargeMission;
import com.org.gascylindermng.tools.SharedPreTools;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ChargeMissionImpl implements ChargeMission {

    public ChargeMissionImpl() {

    }


//    @Override
//    public ArrayList<ChargeMissionBean> queryChargeMissions() {
//        ArrayList<LinkedTreeMap> list = (ArrayList<LinkedTreeMap>) SharedPreTools.query("chargeMissionList", new ArrayList<>());
//        if (list != null && list.size() > 0) {
//
//            ArrayList<ChargeMissionBean> newList = new ArrayList<>();
//            for (LinkedTreeMap map : list) {
//                Type type = new TypeToken<ChargeMissionBean>() {
//                }.getType();
//                Gson gson = new Gson();
//                JSONObject jsonObject = new JSONObject(map);
//                String mData = jsonObject.toString();
//                ChargeMissionBean bean = gson.fromJson(mData, type);
//                bean.setCyList(new ArrayList<CylinderInfoBean>());
//
//                ArrayList<LinkedTreeMap> cylist = (ArrayList<LinkedTreeMap>)map.get("cyList");
//                for (LinkedTreeMap map2 : cylist) {
//                    Type type2 = new TypeToken<CylinderInfoBean>() {
//                    }.getType();
//                    Gson gson2 = new Gson();
//                    JSONObject jsonObject2 = new JSONObject(map2);
//                    String mData2 = jsonObject2.toString();
//                    CylinderInfoBean bean2 = gson2.fromJson(mData2, type2);
//                    bean.getCyList().add(bean2);
//                }
//
//                newList.add(bean);
//            }
//            return newList;
//
//        } else {
//
//            return null;
//        }
//    }
}
