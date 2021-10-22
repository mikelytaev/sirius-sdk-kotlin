package com.sirius.library.mobile.utils;




import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;



public class JSONUtilsAndroid {
        public static String JSONObjectToString(JSONObject obj) {
            return JSONObjectToString(obj, false);
        }

        public static String JSONObjectToString(JSONObject obj, boolean sortKeys) {
            Iterator<String> keys = obj.keys();
           /* if (sortKeys)
                Collections.sort(keys);*/
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{");
            while (keys.hasNext()) {
                String key = keys.next();
                stringBuilder.append("\"").append(key).append("\"").append(":");
                try {
                    Object  val = obj.get(key);
                    stringBuilder.append(JSONFieldToString(val)).append(',');
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (stringBuilder.charAt(stringBuilder.length()-1) == ',') {
                stringBuilder.deleteCharAt(stringBuilder.length()-1);
            }
            stringBuilder.append("}");
            return stringBuilder.toString();
        }

        private static String JSONArrayToString(JSONArray arr) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (int i=0;i<arr.length();i++) {
                try {
                    Object o =  arr.get(i);
                    stringBuilder.append(JSONFieldToString(o)).append(',');
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (stringBuilder.charAt(stringBuilder.length()-1) == ',') {
                stringBuilder.deleteCharAt(stringBuilder.length()-1);
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }

        private static String JSONFieldToString(Object o) {
            if (o == null || o == JSONObject.NULL) {
                return "null";
            }
            if (!(o instanceof JSONObject || o instanceof JSONArray)) {
                boolean needQuotes = !(o instanceof Number || o instanceof Boolean);
                if (needQuotes) {
                    return JSONObject.quote(o.toString());
                } else {
                    return o.toString();
                }
            }
            if (o instanceof JSONObject) {
                return JSONObjectToString((JSONObject) o);
            }
            if (o instanceof JSONArray) {
                return JSONArrayToString((JSONArray) o);
            }
            return "";
        }

}
