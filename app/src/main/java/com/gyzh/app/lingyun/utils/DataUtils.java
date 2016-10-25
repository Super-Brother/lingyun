package com.gyzh.app.lingyun.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/7/15.
 */
public class DataUtils {
    public static JSONArray getImagePager() {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < 10; i++) {
                JSONObject object = new JSONObject();
                object.put("ImgUrl", "http://pic1.nipic.com/2008-09-08/200898163242920_2.jpg");
                array.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static JSONArray getRecomments() {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < 10; i++) {
                JSONObject object = new JSONObject();
                object.put("ImgUrl", "http://pic1.nipic.com/2008-09-08/200898163242920_2.jpg");
                object.put("ProductName", "中方园" + "i");
                object.put("AreaName", "中方园");
                object.put("Type", "三室两厅");
                object.put("Area", "70");
                object.put("SellPrice", "60");
                //                object.put("IsCollect", i % 2);
                array.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static JSONArray getProducts() {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < 10; i++) {
                JSONObject object = new JSONObject();
                object.put("ImgUrl", "http://pic1.nipic.com/2008-09-08/200898163242920_2.jpg");
                object.put("ProductName", "中方园" + "i");
                object.put("AreaName", "中方园");
                object.put("Type", "三室两厅");
                object.put("Area", "70");
                object.put("SellPrice", "60");
                object.put("IsCollect", i % 2);
                array.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static JSONArray getNews() {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < 10; i++) {
                JSONObject object = new JSONObject();
                object.put("ImgUrl", "http://pic1.nipic.com/2008-09-08/200898163242920_2.jpg");
                object.put("Title", "嘎嘎嘎嘎嘎嘎嘎嘎" + i);
                object.put("Url", "http://www.baidu.com");
                object.put("Desc", "嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎嘎");
                array.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static JSONArray getPushHistory() {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < 10; i++) {
                JSONObject object = new JSONObject();
                object.put("Id", i);
                object.put("Title", "嘎嘎嘎嘎嘎嘎嘎嘎" + i);
                object.put("Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                array.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static JSONArray getTsakList() {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < 10; i++) {
                JSONObject object = new JSONObject();
                object.put("Id", i);
                object.put("Context", "嘎嘎嘎嘎嘎嘎嘎嘎" + i);
                object.put("Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                array.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static JSONArray getMsgList() {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < 10; i++) {
                JSONObject object = new JSONObject();
                object.put("Id", i);
                object.put("Context", "嘎嘎嘎嘎嘎嘎嘎嘎" + i);
                object.put("Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                array.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static JSONArray getInformationList() {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < 10; i++) {
                JSONObject object = new JSONObject();
                object.put("Id", i);
                object.put("Context", "嘎嘎嘎嘎嘎嘎嘎嘎" + i);
                object.put("Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                array.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }
}
