package com.netscoder.gistreader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.netscoder.gistreader.mylib.*;
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Button click method
    public void onApply(View v)
    {

        {

            String gist_url =String.format("https://api.github.com/gists/279de5bf320469b7c708afe13f4d8411");


            gist_url = gist_url.replace("+","%2B");
            System.out.println("1. URLPassed gist_url:"+gist_url);

            //Show progress dialog 042418
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();

            GetJsonObject report=new GetJsonObject(this.getApplicationContext(),gist_url,null);
            report.getResponse(new VolleyCallback() {
                
                @Override
                public void onSuccess(JSONObject myGist) {
                    pDialog.hide();
                    try {
                        System.out.println(new StringBuilder().append("2. ****** Result from MainActivity :   ******** \n\n").append(myGist));
                        if (myGist != null) {
                            JSONObject owner = myGist.getJSONObject("owner");
                            String login = owner.getString("login");
                            String avatar_url = owner.getString("avatar_url");


                            String description = myGist.getString("description");

                            JSONObject files1 = myGist.getJSONObject("files").getJSONObject("gistfile1.txt");//file name hardcoded need to change
                            String filename = files1.getString("filename");
                            String content = files1.getString("content");
                            String last_active = myGist.getString("updated_at");
                            String comments_url = myGist.getString("comments_url");
                            System.out.println(new StringBuilder().append("---------------- description:\t").append(description)
                                    .append("  filename:\t").append(filename)
                                    .append("  content:\t").append(content)
                                    .append("  comments_url:\t").append(comments_url)
                                    .append("----------------------\n\n"));
                           Intent intent = new Intent(getApplicationContext(), GistCommenter.class);
                            intent.putExtra("avatar_url", avatar_url);
                            intent.putExtra("login", login);
                            intent.putExtra("description", description);
                            intent.putExtra("filename", filename);
                            intent.putExtra("content", content);
                            intent.putExtra("comments_url", comments_url);
                            intent.putExtra("last_active", last_active);
                            startActivity(intent);
                            //showOkAlert("Done :"+myGist);
                        }else{
                            showOkAlert("Not Done :"+myGist);
                        }
//

                    } catch (JSONException e) {
                        System.out.println("error.getClass() :"+e.getStackTrace());
                        e.printStackTrace();
                        showOkAlert("JSONExceptions107 :"+e.getStackTrace());
                    }

                }

                @Override
                public void onError(VolleyError error) {
                    pDialog.hide();
                    if(error.getClass().toString().equals("com.android.volley.NoConnectionError"))
                    {
                        System.out.println("***********************error.getClass() UnknownHostException:"+error.getClass());
                    }
                    System.out.println(new StringBuilder().append("2. *******Error Result from MainActivity102 :").append(error.getStackTrace()));

                    showOkAlert("Please check your internet connection!!"+error.getMessage());
                }
            });
        }
        System.out.println("----------------onApply call---------------");
    }

    private void showOkAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Alert !!!");
        alertDialog.show();
    }
}

