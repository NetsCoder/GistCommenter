package com.netscoder.gistreader.mylib;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by ADMIN on 5/1/2019.
 */

public interface VolleyCallback {
    void onSuccess(JSONObject result);
    void onError(VolleyError error);
}
