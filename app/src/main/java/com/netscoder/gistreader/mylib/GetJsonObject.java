package com.netscoder.gistreader.mylib;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by ADMIN on 5/1/2019.
 */

public class GetJsonObject {
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonObjectRequest;
    private String url ;
    private Context myContext;
    private JSONObject params;

    public GetJsonObject(Context context, String url, JSONObject params){
        this.myContext = context;
        this.url=url;
        this.params=params;
    }
    public void  getResponse(final VolleyCallback callback) {
        final String TAG = this.getClass().getName();

        //1 RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this.myContext);

        //2 String Request initialized (create request)
         mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("Response :" + response.toString());
                        callback.onSuccess(response);
                        //  pDialog.hide();

                    }}, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();

                // hide the progress dialog
                // pDialog.hide();
            }
        });

         mRequestQueue.add(mJsonObjectRequest);

    }
}
