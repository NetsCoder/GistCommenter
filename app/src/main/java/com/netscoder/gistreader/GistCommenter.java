package com.netscoder.gistreader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.netscoder.gistreader.mylib.GetJsonArray;
import com.netscoder.gistreader.mylib.GetJsonArrayHeader;
import com.netscoder.gistreader.mylib.GetJsonObject;
import com.netscoder.gistreader.mylib.GetJsonObjectHeader;
import com.netscoder.gistreader.mylib.VolleyCallback;
import com.netscoder.gistreader.mylib.VolleyJSONArrayCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GistCommenter extends AppCompatActivity {
    private  String avatar_url,login,description,filename,content,comments_url,last_active;
    private TextView tv_login,tv_desc,tv_filename,tv_content,tv_leave,tv_last_active;
    private ImageView img_avatar,img_btn_send;
    private RecyclerView list_comm;
    private EditText et_comment;
    private String comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gist_commenter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getIntent() != null){
            Intent i = getIntent();
            avatar_url=i.getStringExtra("avatar_url");
            login=i.getStringExtra("login");
            description=i.getStringExtra("description");
            filename=i.getStringExtra("filename");
            content=i.getStringExtra("content");
            comments_url=i.getStringExtra("comments_url");
            last_active = i.getStringExtra("last_active");
            tv_login = (TextView)findViewById(R.id.tv_login);
            tv_login.setText(login);
            tv_desc = (TextView)findViewById(R.id.tv_desc);
            tv_desc.setText(description);
            tv_filename = (TextView)findViewById(R.id.tv_filename);
            tv_filename.setText(filename);
            tv_content = (TextView)findViewById(R.id.tv_content);
            tv_content.setText(content);
            tv_last_active = (TextView)findViewById(R.id.tv_last_active);
            tv_last_active.setText(last_active);
            img_avatar = (ImageView) findViewById(R.id.img_avatar);
            new ImageLoadTask(avatar_url, img_avatar).execute();
            list_comm = (RecyclerView)findViewById(R.id.list_comm);
            setRecyclerView();
        }
        tv_leave = (TextView)findViewById(R.id.tv_leave);
        et_comment = (EditText)findViewById(R.id.et_comment);
        img_btn_send = (ImageView) findViewById(R.id.img_btn_send);
   }

   public void setRecyclerView()
   {
      System.out.println("---------------setRecyclerView------------\n1. URLPassed comments_url:"+comments_url);

       //Show progress dialog
       final ProgressDialog pDialog = new ProgressDialog(GistCommenter.this);
       pDialog.setMessage("Loading...");
       pDialog.setCancelable(false);
       pDialog.show();

       GetJsonArray report=new GetJsonArray(this.getApplicationContext(),comments_url,null);

       report.getResponse(new VolleyJSONArrayCallback() {

           @Override
           public void onSuccess(JSONArray jsonArray) {
               pDialog.hide();
               try {
                   if (jsonArray.length() > 0) {
                       try {
                           List<Data> data = fill_with_data( jsonArray );

                           Recycler_View_Adapter adapter = new Recycler_View_Adapter(data, getApplication());
                           list_comm.setAdapter(adapter);
                           list_comm.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                       } catch (Exception e) {
                           System.out.println("Exception GistCommenter110::"+e.getStackTrace());
                       }

                   } else {
                       showOkAlert("There is no comments!!!");
                   }

               } catch (Exception e) {
                   System.out.println("error.getClass() :"+e.getStackTrace().toString());
               }

           }

           @Override
           public void onError(VolleyError error) {
               pDialog.hide();
               if(error.getClass().toString().equals("com.android.volley.NoConnectionError"))
               {
                   System.out.println("***********************error.getClass() UnknownHostException:"+error.getClass());
               }
               System.out.println(new StringBuilder().append("2. *******Error Result from GistCommenter:").append(error.getStackTrace()));
               showOkAlert("Please check your internet connection!!"+error.getMessage());
           }
       });

        System.out.println("----------------onApply call---------------");
   }



    public void submitCommentOnGist(String username, String password,String requestBodystr) throws JSONException  {
        comments_url = comments_url.replace("+","%2B");
        System.out.println("-----------submitCommentOnGist------------1. URLPassed gist_url:"+comments_url);
        final String requestBody;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("body", requestBodystr);
            requestBody = jsonBody.toString();
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password",password);
        params.put("body", requestBodystr);
System.out.println("Request body::"+requestBody);



        //Show progress dialog 042418
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        String credentials = username+":"+password;
        GetJsonObjectHeader report=new GetJsonObjectHeader(this.getApplicationContext(),comments_url,null,credentials,requestBody);
        System.out.println(report.toString());
        report.getResponse(new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject myGist) {
                pDialog.hide();
                try {
                    System.out.println(new StringBuilder().append("2. ************ Result from submitCommentOnGist :   ******** \n\n").append(myGist));
                    if (myGist != null) {
//                        JSONObject owner = myGist.getJSONObject("owner");
//                        String login = owner.getString("login");
//                        String avatar_url = owner.getString("avatar_url");
//
//
//                        String description = myGist.getString("description");
//
//                        JSONObject files1 = myGist.getJSONObject("files").getJSONObject("gistfile1.txt");//file name hardcoded need to change
//                        String filename = files1.getString("filename");
//                        String content = files1.getString("content");
//                        String last_active = myGist.getString("updated_at");
//                        String comments_url = myGist.getString("comments_url");
//                        System.out.println(new StringBuilder().append("---------------- description:\t").append(description)
//                                .append("  filename:\t").append(filename)
//                                .append("  content:\t").append(content)
//                                .append("  comments_url:\t").append(comments_url)
//                                .append("----------------------\n\n"));
//                        Intent intent = new Intent(getApplicationContext(), GistCommenter.class);
//                        intent.putExtra("avatar_url", avatar_url);
//                        intent.putExtra("login", login);
//                        intent.putExtra("description", description);
//                        intent.putExtra("filename", filename);
//                        intent.putExtra("content", content);
//                        intent.putExtra("comments_url", comments_url);
//                        intent.putExtra("last_active", last_active);
//                        startActivity(intent);
                        //showOkAlert("Done :"+myGist);
                        setRecyclerView();
                    }else{
                        showOkAlert("Not Done :"+myGist);
                    }
//

                } catch (Exception e) {
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

    private void showCredentialAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        dialog.setContentView(R.layout.signin);
        dialog.setTitle("GitHub Signin");

        // set the custom dialog components - text, image and button
        final EditText et_username = (EditText)dialog.findViewById(R.id.et_username);
        final EditText et_password = (EditText)dialog.findViewById(R.id.et_password);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_comm);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=et_username.getText().toString();
                String pass=et_password.getText().toString();
                dialog.dismiss();
                /*try {
                    submitCommentOnGist(username, pass, comment);
                }catch (JSONException e){
                    showOkAlert("Can not send this comment!!!");
                }*/
                showOkAlert("Under Progress!!");
            }
        });

        dialog.show();





    }

   public void onTextClick(View v)
   {
        tv_leave.setVisibility(View.GONE);
        et_comment.setVisibility(View.VISIBLE);
        img_btn_send.setClickable(true);
   }

   public void onCommentSend(View v)
   {
        comment = et_comment.getText().toString();
        if(comment != null)
        {
            try {
                tv_leave.setVisibility(View.VISIBLE);
                et_comment.setVisibility(View.GONE);
                img_btn_send.setClickable(false);


                final Dialog dialog = new Dialog(GistCommenter.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.signin);
                dialog.setTitle("GitHub Signin");

                // set the custom dialog components - text, image and button
                final EditText et_username = (EditText) dialog.findViewById(R.id.et_username);
                final EditText et_password = (EditText) dialog.findViewById(R.id.et_password);

                Button dialogButton = (Button) dialog.findViewById(R.id.btn_comm);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = et_username.getText().toString();
                        String pass = et_password.getText().toString();
                        dialog.dismiss();
                        try {
                            submitCommentOnGist(username, pass, comment);
                        }catch (JSONException e){
                            showOkAlert("Can not send this comment!!!");
                        }
                    }
                });

                dialog.show();
            }catch (Exception e)
            {
                System.out.println("SENDBUTTON 314:"+e.getStackTrace());
                e.printStackTrace();
            }

        }else{
            showOkAlert("Please add comment before submitting!!");
        }
   }


     public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }


    public List<Data> fill_with_data(JSONArray jsonArray) {

        List<Data> data = new ArrayList<>();
        try {

            //JSONArray jsonArray = response.getJSONArray("employees");
            System.out.println(new StringBuilder().append("****** Result from GistCommenter:   ********/n/n"));
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject myGist = jsonArray.getJSONObject(i);

                String comment = myGist.getString("body");
                String updated_at = myGist.getString("updated_at");
                JSONObject user = myGist.getJSONObject("user");
                String login = user.getString("login");

                System.out.println(new StringBuilder().append("login:").append(login)
                        .append("comment:").append(comment)
                        .append("updated_at:").append(updated_at)
                        .append("/n/n"));
                data.add(new Data(login, updated_at, comment));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class Data {
        public String login;
        public String updated_at;
        public String comment;

        Data(String login, String updated_at,String comment) {
            this.login = login;
            this.updated_at = updated_at;
            this.comment = comment;
        }

    }

    public class View_Holder extends RecyclerView.ViewHolder {

        TextView tv_user_login,tv_updated_at,tv_comm;

        View_Holder(View itemView) {
            super(itemView);
            tv_user_login = (TextView) itemView.findViewById(R.id.tv_user_login);
            tv_updated_at = (TextView) itemView.findViewById(R.id.tv_updated_at);
            tv_comm = (TextView)itemView.findViewById(R.id.tv_comm);
        }
    }

    public class Recycler_View_Adapter extends RecyclerView.Adapter<View_Holder> {

        List<Data> list = Collections.emptyList();
        Context context;

        public Recycler_View_Adapter(List<Data> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Inflate the layout, initialize the View Holder
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_gist_list, parent, false);
            View_Holder holder = new View_Holder(v);
            return holder;

        }

        @Override
        public void onBindViewHolder(View_Holder holder, int position) {

            //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
            holder.tv_user_login.setText(list.get(position).login);
            holder.tv_updated_at.setText(list.get(position).updated_at);
            holder.tv_comm.setText(list.get(position).comment);

            //animate(holder);

        }

        @Override
        public int getItemCount() {
            //returns the number of elements the RecyclerView will display
            return list.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        // Insert a new item to the RecyclerView on a predefined position
        public void insert(int position, Data data) {
            list.add(position, data);
            notifyItemInserted(position);
        }

        // Remove a RecyclerView item containing a specified Data object
        public void remove(Data data) {
            int position = list.indexOf(data);
            list.remove(position);
            notifyItemRemoved(position);
        }

    }

}
