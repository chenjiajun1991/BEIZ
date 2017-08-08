package com.sam.beiz.controller;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhejunliu.service.up.MultipartUploadRequest;
import com.example.zhejunliu.service.up.UploadNotificationConfig;
import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.Config.ConfigURL;
import com.sam.beiz.OnLineService.OnLineServiceMainActivity;
import com.sam.beiz.common.ActionBarSupport;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import baidumapsdk.demo.demoapplication.R;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.sam.beiz.Config.ConfigURL.checkJson;


public class AppliRepairActivity extends ActionBarActivity implements View.OnClickListener{
    Spinner expressSpinner;
    ImageView imageView;
    EditText discribet,expresset;
    Bitmap myBitmap;
    private int PICK_IMAGE_REQUEST = 1;  //相册请求
    static final int REQUEST_TAKE_PHOTO = 2; //相机请求
    //Uri to store the image uri
    private Uri filePath;
    static String username="";
    static String IMEI="";
    ProgressDialog loadingDialog;
    String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarSupport actionBarSupport = new ActionBarSupport();
        actionBarSupport.initActionBar(this, R.drawable.actionbar_bg);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build().LAX);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog()
                .build().LAX);
        ACache mCache= ACache.get(this);
        if(  mCache.getAsString("userPHONE")!=null&&!mCache.getAsString("userPHONE").equals("")){
            if(mCache.getAsString("namefromServer")!=null&&!mCache.getAsString("namefromServer").equals("")){
                username=mCache.getAsString("namefromServer");
            }else{
                checkPassword();
            }
        }else{
            Intent i=new Intent(AppliRepairActivity.this,OnLineServiceMainActivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        ActionBar actionBar = getActionBar();

        // 返回箭头（默认不显示）
        actionBar.setDisplayHomeAsUpEnabled(false);
        // 左侧图标点击事件使能
        actionBar.setHomeButtonEnabled(false);
        // 使左上角图标(系统)是否显示
        actionBar.setDisplayShowHomeEnabled(false);
        // 显示标题
        actionBar.setDisplayShowTitleEnabled(false);

        actionBar.setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(this).inflate(
                R.layout.layout_common_action_bar, null);
        actionBar.setCustomView(actionbarLayout);

        final ImageButton btnBack = (ImageButton) actionbarLayout.findViewById(R.id.left_back);
        TextView textView = (TextView) actionbarLayout.findViewById(R.id.actionBar_title);
        ImageButton uploadIB=(ImageButton)actionbarLayout.findViewById(R.id.right_imbt);
        uploadIB.setBackgroundResource(R.drawable.upload_bar);


        textView.setText("在线报修申请");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*

        上传按钮放到了顶部bar内
         */
        uploadIB.setOnClickListener(this);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        setContentView(R.layout.activity_appli_repair);
/*

这里需要修改  需要插入其他模块的用户名，imei号上传
用户名会经过urlencode传入数据库，web会urldecode

 */
        mCache.put("username",username);
        mCache.put("IMEI",IMEI);
        expresset=(EditText)findViewById(R.id.expressEt);
        discribet=(EditText)findViewById(R.id.discribet);
        expressSpinner=(Spinner)findViewById(R.id.spinner);
        imageView=(ImageView)findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
    }

    private void checkPassword() {
        final AlertDialog.Builder mydialog=new AlertDialog.Builder(AppliRepairActivity.this);
        LayoutInflater mLayoutInflater=LayoutInflater.from(this);
        final View view=mLayoutInflater.inflate(R.layout.passworddialog, null);
        mydialog.setView(view);
        mydialog.setCancelable(false);
        mydialog.setPositiveButton("用户验证", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText password=(EditText) view.findViewById(R.id.editText4);
                String textPass=password.getText().toString();
                if(textPass.equals("")){
                    Toast.makeText(AppliRepairActivity.this,"您没有输入",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(AppliRepairActivity.this,OnLineServiceMainActivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    ACache mCache=ACache.get(AppliRepairActivity.this);
                    testGetVerificationCode(checkJson,mCache.getAsString("userPHONE"),textPass);
                }
            }
        });
        mydialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView: {
                showDialog(AppliRepairActivity.this);
                break;
            }
            case R.id.right_imbt: {
                if(mCurrentPhotoPath!=null&&!mCurrentPhotoPath.equals("")){
                    uploadMultipart();
                }
                if (mCurrentPhotoPath==null||mCurrentPhotoPath.equals("")) {
                    String s0 = username;
                    String s1 = IMEI;
                    String s2 = expressSpinner.getSelectedItem().toString();
                    String s3 = expresset.getText().toString();
                    String s4 = discribet.getText().toString();
                    uploadwithOutPic(s0, s1, s2, s3, s4);
                }
                break;
            }
        }
    }
    private void uploadwithOutPic(String s0, String s1, String s2, String s3, String s4) {
        class uploadwithOutPic extends AsyncTask<String, Void, String> {
            Context mcontext;
            public uploadwithOutPic(Context c) {
                mcontext = c;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(mcontext, "Please wait", "Loading...");
            }
            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }
            @Override
            protected String doInBackground(String... strings) {
                String name = URLEncoder.encode(strings[0]);
                String imei = strings[1];
                String express = URLEncoder.encode(strings[2]);
                String expressnum = strings[3];
                String discribe = URLEncoder.encode(strings[4]);
                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("imei", imei));
                nameValuePairs.add(new BasicNameValuePair("express", express));
                nameValuePairs.add(new BasicNameValuePair("expressnum", expressnum));
                nameValuePairs.add(new BasicNameValuePair("discribe", discribe));
                String result = null;
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(ConfigURL.UPLOADNoPic_URL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                String s = result.trim();
                loadingDialog.dismiss();
                if (s.equalsIgnoreCase("success")) {
                    Toast.makeText(getApplicationContext(), "成功上传数据,您未添加图片", Toast.LENGTH_LONG).show();

                }
                if (s.equalsIgnoreCase("failure")) {
                    Toast.makeText(getApplicationContext(), "存在其他错误", Toast.LENGTH_LONG).show();
                }
            }
        }
        uploadwithOutPic la = new uploadwithOutPic(AppliRepairActivity.this);
        la.execute(s0, s1, s2,s3,s4);

    }


    private void showDialog(Context c) {
        final Dialog pickdialog = new Dialog(c);
        pickdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater li = LayoutInflater.from(c);
        View myView = li.inflate(R.layout.choosedialog, null);
        pickdialog.setContentView(myView);
        pickdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView photobtn=(ImageView)myView.findViewById(R.id.btnPhoto);
        ImageView gallerybtn=(ImageView)myView.findViewById(R.id.btnGallery);


        photobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // TODO Auto-generated method stub
                dispatchTakePictureIntent();
                pickdialog.dismiss();

            }


        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
                pickdialog.dismiss();
            }
        });
        pickdialog.show();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
// TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK
                && null != data) {
            imageView.setImageURI(Uri.parse(mCurrentPhotoPath));
            imageView.setBackgroundDrawable(null);
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                myBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(myBitmap);
                imageView.setBackgroundDrawable(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCurrentPhotoPath=getPath(filePath);
        } else {
            Log.d("requestCode", "Not Need");
        }
    }
    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        Log.v("truePath",path);

        return path;
    }


    public void uploadMultipart() {
        String expressnum = expresset.getText().toString().trim();
        String Eusername = null;

        String expressType= null;
        String discribe= null;

//加urlencode
        try {
            expressType = URLEncoder.encode(expressSpinner.getSelectedItem().toString(),"UTF-8");
            discribe = URLEncoder.encode(discribet.getText().toString(),"UTF-8");
            Eusername=URLEncoder.encode(username,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.v("type",expressType);
        Log.v("discribe",discribe);
        Log.v("Eusername",Eusername);
        Log.v("filepath",mCurrentPhotoPath);
        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, ConfigURL.UPLOAD_URL)
                    .setUtf8Charset()
                    .addFileToUpload(mCurrentPhotoPath, "image") //Adding file
                    .addParameter("name", Eusername) //Adding text parameter to the request
                    .addParameter("imei", IMEI) //传入IMEI
                    .addParameter("express", expressType) //快递种类
                    .addParameter("expressnum", expressnum) //快递种类
                    .addParameter("discribe", discribe) //用户描述
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(6)
                    .startUpload(); //Starting the upload
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void testGetVerificationCode(String srcUrl,String phonenum,String password) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceInfo", "android");
            jsonObject.put("userPhone", phonenum);
            jsonObject.put("password", password);
            String message = "jsonReq=" + jsonObject.toString();
            Log.v("URL",srcUrl+message);
            URL url = new URL(srcUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            //connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setFixedLengthStreamingMode(message.getBytes().length);
            connection.connect();
            OutputStream os = new BufferedOutputStream(
                    connection.getOutputStream());
            os.write(message.getBytes());
            os.close();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                ByteArrayBuffer baf = new ByteArrayBuffer(512);
                byte[] buffer = new byte[512];
                InputStream is = new BufferedInputStream(connection.getInputStream());
                int read = 0, offset = 0;
                while ((read = is.read(buffer, 0, 512)) != -1) {
                    baf.append(buffer, 0, read);
                }
                String temp = new String(baf.toByteArray());
                Log.v("查看返回值",temp);
                /*
                服务器返回值解析
                 */
                String c=BackParse(temp);
                if(c==""){
                    Toast.makeText(AppliRepairActivity.this,"验证失败",Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(AppliRepairActivity.this,OnLineServiceMainActivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }else{
                    username=c;
                    ACache aCache=ACache.get(AppliRepairActivity.this);
                    /*
                    记录登陆状态 10分钟
                     */
                    aCache.put("namefromServer",c,600);
                }
                is.close();
                connection.disconnect();
            } else {
                Log.d("test2", "connection failed");
            }
        } catch (UnsupportedEncodingException e) {
            Log.d("test2", "UnsupportedEncodingException");
        } catch (MalformedURLException e) {
            Log.d("test2", "MalformedURLException");
        } catch (IOException e) {
            Log.d("test2", "IOException");
        } catch (JSONException e) {
            Log.d("test2", "JsonException");
        }
    }

    private String BackParse(String temp) {
        try {
            JSONObject jObject = new JSONObject(temp);
           // JSONArray jarray=new JSONArray(temp);
            String state = "";
            String backdata = "";
            if (jObject.getString("result").equals("")) {
                JSONObject jobject = jObject.getJSONObject("data");
                backdata=jobject.getString("userName");
//                for (int i = 0; i < 1; i++) {
//                    JSONObject oneObject = jobject.getJSONObject(i);
//                    backdata = oneObject.getString("userName");
//                }
                Log.v("servername",backdata);
                return backdata;
            } else {
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.sam.beiz",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

}
