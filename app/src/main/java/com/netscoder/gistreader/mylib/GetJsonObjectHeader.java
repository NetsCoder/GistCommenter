package com.netscoder.gistreader.mylib;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ADMIN on 7/1/2019.
 */

public class GetJsonObjectHeader {
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonObjectRequest;
    private String url ;
    private Context myContext;
    private JSONObject params;
    private String credentials;
    private String requestBody;
    public GetJsonObjectHeader(Context context, String url, JSONObject params,String credentials,String requestBody){
        this.myContext = context;
        this.url=url;
        this.params=params;
        this.credentials = credentials;
        this.requestBody=requestBody;
    }
    public void  getResponse(final VolleyCallback callback) {
        final String TAG = this.getClass().getName();

        //1 RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this.myContext);

        //2 String Request initialized (create request)
         mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,params,
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
        })

         {

             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 Map<String, String> headers = new HashMap<>();
                 //String credentials = "username:password";
                 String auth = "Basic "
                         + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                 headers.put("Content-Type", "application/json");
                 headers.put("Authorization", auth);
                 return headers;
             }

             @Override
             public String getBodyContentType() {
                 return "application/json; charset=utf-8";
             }

             @Override
             public byte[] getBody()  {

                 try {
                     return requestBody == null ? null : requestBody.getBytes("utf-8");
                 } catch (UnsupportedEncodingException uee) {
                     VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                     return null;
                 }
             }
         };

         mRequestQueue.add(mJsonObjectRequest);

    }
}
