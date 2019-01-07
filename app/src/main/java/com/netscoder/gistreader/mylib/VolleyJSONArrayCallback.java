package com.netscoder.gistreader.mylib;

import com.android.volley.VolleyError;

import org.json.JSONArray;

/**
 * Created by ADMIN on 5/1/2019.
 */

public interface VolleyJSONArrayCallback {
    void onSuccess(JSONArray result);
    void onError(VolleyError error);
}
