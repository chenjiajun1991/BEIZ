package com.sam.beiz.controller;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhejunliu.service.up.MultipartUploadRequest;
import com.example.zhejunliu.service.up.UploadNotificationConfig;
import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.Config.ConfigURL;
import com.sam.beiz.Config.MyTag;
import com.sam.beiz.common.ActionBarSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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


public class ImageActivity extends ActionBarActivity implements View.OnClickListener{
    final String TAG="ImageActivity";
    static String username="";
    EditText messageET;
    ImageView imageView;
    private Uri filePath;
    Bitmap myBitmap;
    ProgressDialog loadingDialog;
    private static String imageURL="";
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBarSupport actionBarSupport = new ActionBarSupport();
        actionBarSupport.initActionBar(this, R.drawable.actionbar_bg);

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

        ACache aCache=ACache.get(this);
        if(aCache.getAsString("namefromServer")==null||aCache.getAsString("namefromServer").equals("")){
            Toast.makeText(this,"当前用户名不存在",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(ImageActivity.this,LauncherAcitivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }else{
            username=aCache.getAsString("namefromServer");
        }

        ImageButton btnBack = (ImageButton) actionbarLayout.findViewById(R.id.left_back);
        ImageButton  upload=(ImageButton)actionbarLayout.findViewById(R.id.right_imbt);
        upload.setImageResource(R.drawable.upload_bar);
        upload.setOnClickListener(this);
        TextView textView = (TextView) actionbarLayout.findViewById(R.id.actionBar_title);
        textView.setText("我的相册");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));

        setContentView(R.layout.activity_image);
        Log.v(MyTag.getCurrentClassName(), "启动了图片发布");
        messageET = (EditText) findViewById(R.id.editText);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.right_imbt:  {
                String isPicUrl=imageURL;
                String message=messageET.getText().toString();
                if (!isPicUrl.equals("")) {
                    uploadMultipart();
                    Log.v("isPic","pic is true");
                }
                if (isPicUrl.equals("")&&!message.equals("")) {
                    String s0 = username;
                    String s1=message;
                    uploadwithOutPic(username, message);
                    Log.v("isPic","pic no true");

                };
                break;

            }
            case R.id.imageView: {
                showFileChooser();
                break;
            }

        }
    }


    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
// TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                myBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(myBitmap);
                imageView.setBackgroundDrawable(null);
                imageURL=getPath(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("requestCode", "Not Need");
        }
    }
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

        return path;
    }



    /*
    无图片的传输
     */
    private void uploadwithOutPic(String s0, String s1) {
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
                String imei =URLEncoder.encode( strings[1]);

                Log.v(MyTag.getCurrentClassName(),name);
                Log.v(MyTag.getCurrentClassName(),imei);
                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("message", imei));
                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(ConfigURL.sendNopic);
                    Log.v("url",ConfigURL.sendNopic);
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
                    Toast.makeText(mcontext, "成功上传数据,您未添加图片", Toast.LENGTH_LONG).show();

                }
                if (s.equalsIgnoreCase("failure")) {
                    Toast.makeText(mcontext, "存在其他错误", Toast.LENGTH_LONG).show();
                }
            }
        }

        uploadwithOutPic la = new uploadwithOutPic(ImageActivity.this);
        la.execute(s0, s1);

    }




    public void uploadMultipart() {

        String message=null;
        String Eusername=null;
        //getting the actual path of the image

        String path =null;
        path =getPath(filePath);



        String expressType= null;
        String discribe= null;

//加urlencode
        try {
            message = URLEncoder.encode(messageET.getText().toString(),"UTF-8");
            Eusername = URLEncoder.encode(username,"UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        expressType =expressSpinner.getSelectedItem().toString();
//        discribe = discribet.getText().toString();
//        Eusername=username;


        Log.v("message",message);
        Log.v("Eusername",Eusername);




        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();


            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, ConfigURL.sendpic)
                    .setUtf8Charset()
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", Eusername) //Adding text parameter to the request
                    .addParameter("message", message) //用户描述
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .startUpload(); //Starting the upload
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }




}