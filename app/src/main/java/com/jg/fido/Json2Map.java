package com.jg.fido;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Json2Map {
    /**
     * 将json 数组转换为Map 对象
     *
     * @param jsonString
     * @return
     */

    public static Map<Integer, String> getMap(String jsonString)

    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            @SuppressWarnings("unchecked")
            Iterator<String> keyIter = jsonObject.keys();
            int key;
            String value;
            Map<Integer, String> valueMap = new HashMap<Integer, String>();
            while (keyIter.hasNext()) {
                key = Integer.parseInt(keyIter.next());
                //key = (int)keyIter.next();
                value = (String)jsonObject.get(keyIter.next());
                valueMap.put(key, value);
            }
            return valueMap;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }


    /**
     * 把json 转换为ArrayList 形式
     *
     * @return
     */
    public static List<Map<Integer, String>> getList(String jsonString) {
        List<Map<Integer, String>> list = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject;
            list = new ArrayList<Map<Integer, String>>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list.add(getMap(jsonObject.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }
}
