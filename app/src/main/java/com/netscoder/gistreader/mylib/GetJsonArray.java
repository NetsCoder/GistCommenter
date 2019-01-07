package com.netscoder.gistreader.mylib;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ADMIN on 5/1/2019.
 */

//volley
public class GetJsonArray {
    private RequestQueue mRequestQueue;
    private String url ;
    private Context myContext;
    private JSONObject params;
    private JsonArrayRequest jsonArrayRequest;

    public  GetJsonArray(Context context, String url,JSONObject params){
        this.myContext = context;
        this.url=url;
        this.params=params;
    }
    public void  getResponse(final VolleyJSONArrayCallback callback) {
        final String TAG = this.getClass().getName();

        //1 RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this.myContext);

        //2 String Request initialized (create request)
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("Response :" + response.toString());
                callback.onSuccess(response);
            }
        },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        error.printStackTrace();
                        callback.onError(error);
                        // hide the progress dialog
                        // pDialog.hide();
                    }
        });

        //3 add in queue
        mRequestQueue.add(jsonArrayRequest);
    }

}
