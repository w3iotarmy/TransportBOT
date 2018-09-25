package org.droidplanner.android.activities.helpers;

import android.util.Log;

import org.droidplanner.android.SocketDataReceiver;
import org.droidplanner.android.constant.code.ConstantCode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Borhan Uddin on 7/11/2018.
 */
public class JsonBuilder {
    public static JSONObject getModeChanger(String mode) {
        JSONObject json = null;
        try {
            json = new JSONObject("{\"" + ConstantCode.USERNAME_KEY + "\":" + "\"" + ConstantCode.USER+ "\""+ ","
                    + "\"" +  ConstantCode.ACTION_TYPE + "\":" + "\"" + ConstantCode.MODE_CHANGE+ "\""+","
                    + "\"" + ConstantCode.DATA_STRING+ "\":" + "\"" + mode+ "\"" + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    public static JSONObject armed_or_disarmed(String value)
    {
        JSONObject json = null;
        try {
            json = new JSONObject("{\"" + ConstantCode.USERNAME_KEY + "\":" + "\"" + ConstantCode.USER+ "\""+ ","
                    + "\"" +  ConstantCode.ACTION_TYPE + "\":" + "\"" + value+ "\"" + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
