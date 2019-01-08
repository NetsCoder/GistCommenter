package com.netscoder.gistreader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.netscoder.gistreader.mylib.GetJsonObject;
import com.netscoder.gistreader.mylib.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ScannedBarcodeActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //Button btnAction;
    String intentData = "";
    boolean isEmail = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        initViews();
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

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        //btnAction = findViewById(R.id.btnAction);


//        btnAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (intentData.length() > 0) {
//                    if(intentData.contains("https://api.github.com/gists/"))
//                    {
//                        getMyGistData(intentData);
//                    }
//                    else{
//                        String msg="This QR code wants to take you to "+intentData+". This is an external site and Gist Commenter has no control on its content.";
//                        showOkAlert(msg);
//                    }
//                }
//
//
//            }
//        });
    }

    private void getMyGistData(String gist_url)
    {
        {

            //String gist_url =String.format("https://api.github.com/gists/279de5bf320469b7c708afe13f4d8411");


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
                        showOkAlert("There is some issue :"+e.getStackTrace());
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

                    showOkAlert("Please check your internet connection!!");
                }
            });
        }
    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {

//                            if (barcodes.valueAt(0).email != null) {
//                                txtBarcodeValue.removeCallbacks(null);
//                                intentData = barcodes.valueAt(0).email.address;
//                                txtBarcodeValue.setText(intentData);
//                                isEmail = true;
//                                btnAction.setText("ADD CONTENT TO THE MAIL");
//                            } else {
//                                isEmail = false;
//                                btnAction.setText("LAUNCH URL");
//                                intentData = barcodes.valueAt(0).displayValue;
//                                txtBarcodeValue.setText(intentData);
//
//                            }
                            isEmail = false;
                            intentData = barcodes.valueAt(0).displayValue;
                            txtBarcodeValue.setText(intentData);
                            if (intentData.length() > 0) {
                                if(intentData.contains("https://api.github.com/gists/"))
                                {
                                    getMyGistData(intentData);
                                }
                                else{
                                    String msg="This QR code wants to take you to "+intentData+". This is an external site and Gist Commenter has no control on its content.";
                                    showOkAlert(msg);
                                }
                            }
                        }
                    });



                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}

