package com.org.gascylindermng.tools;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.org.gascylindermng.api.MyAppContext;
import java.util.ArrayList;
import java.util.List;

/***
 * 本地数据保存工具
 */
public class SharedPreTools {
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    static {
        sp = MyAppContext.getInstance().getSharedPreferences("gas_cylinder_data", MyAppContext.getInstance().MODE_MULTI_PROCESS);
        editor = sp.edit();
    }

    /***
     *保存对象
     * @param key
     * @param value
     * @return
     */
    public static void save(String key, Object value) {
        String type = value.getClass().getSimpleName();
        try {
            switch (type) {
                case "Boolean":
                    editor.putBoolean(key, (Boolean) value);
                    break;
                case "Long":
                    editor.putLong(key, (Long) value);
                    break;
                case "Float":
                    editor.putFloat(key, (Float) value);
                    break;
                case "String":
                    editor.putString(key, (String) value);
                    break;
                case "Integer":
                    editor.putInt(key, (Integer) value);
                    break;
                default:
                    Gson gson = new Gson();
                    String json = gson.toJson(value);
                    editor.putString(key, json);
                    break;
            }
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static Object result;

    public static Object query(String key, Object defaultValue) {
        String type = defaultValue.getClass().getSimpleName();
        try {
            switch (type) {
                case "Boolean":
                    result = sp.getBoolean(key, (Boolean) defaultValue);
                    break;
                case "Long":
                    result = sp.getLong(key, (Long) defaultValue);
                    break;
                case "Float":
                    result = sp.getFloat(key, (Float) defaultValue);
                    break;
                case "String":
                    result = sp.getString(key, (String) defaultValue);
                    break;
                case "Integer":
                    result = sp.getInt(key, (Integer) defaultValue);
                    break;
                default:
                    Gson gson = new Gson();
                    String json = sp.getString(key, "");
                    if (!json.equals("") && json.length() > 0) {
                        result = gson.fromJson(json, defaultValue.getClass());
                    } else {
                        result = defaultValue;
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }


    public static <T> void saveDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();

    }
    public static <T> List<T> queryDataList(String tag) {
        List<T> datalist=new ArrayList<T>();
        String strJson = sp.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
        }.getType());
        return datalist;

    }



    /**
     * 删除指定key
     *
     * @param key
     */
    public static void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /***
     * 清空所有
     */
    public static void remove() {
        editor.clear();
        editor.commit();
    }
}
