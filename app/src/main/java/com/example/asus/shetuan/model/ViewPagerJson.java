package com.example.asus.shetuan.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by ASUS on 2017/3/27.
 */

public class ViewPagerJson {
    private JSONArray viewpager;

    public JSONArray jsonParser(String string){
        JSONTokener jsonTokener = new JSONTokener(string);
        try {
            viewpager = (JSONArray) jsonTokener.nextValue();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return viewpager;
    }
}
