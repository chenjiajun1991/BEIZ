package com.sam.beiz.controller;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.sam.beiz.OnLineService.OnLineServiceMainActivity;
import com.sam.beiz.common.ActionBarSupport;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import baidumapsdk.demo.demoapplication.Login_main;
import baidumapsdk.demo.demoapplication.R;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.sam.beiz.Config.ConfigURL.checkJson;


public class CameraActivity extends ActionBarActivity implements View.OnClickListener{
    ImageView imageView;
    EditText messageET;
    static String username="";
    static Uri uriImageData;
    static final int REQUEST_TAKE_PHOTO = 2; //相机请求
    String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarSupport actionBarSupport = new ActionBarSupport();
        actionBarSupport.initActionBar(this, R.drawable.actionbar_bg);
        ACache aCache=ACache.get(this);
        if(aCache.getAsString("namefromServer")==null||aCache.getAsString("namefromServer").equals("")){
            Toast.makeText(this,"当前用户名不存在",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(CameraActivity.this,LauncherAcitivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }else{
            username=aCache.getAsString("namefromServer");
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
        ImageButton btnBack = (ImageButton) actionbarLayout.findViewById(R.id.left_back);
        ImageButton  upload=(ImageButton)actionbarLayout.findViewById(R.id.right_imbt);
        upload.setImageResource(R.drawable.upload_bar);
        upload.setOnClickListener(this);
        TextView textView = (TextView) actionbarLayout.findViewById(R.id.actionBar_title);
        textView.setText("我的相机");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        setContentView(R.layout.activity_camera);
        imageView=(ImageView)findViewById(R.id.imageView);
        messageET=(EditText)findViewById(R.id.editText);
        imageView.setOnClickListener(this);
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
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView:   dispatchTakePictureIntent(); break;
            case R.id.right_imbt:
//                if (saveImageUrl!=null&&!saveImageUrl.equals("")) {
//                uploadMultipart();
//            }  break;
            if(mCurrentPhotoPath!=null&&!mCurrentPhotoPath.equals("")){
                uploadMultipart();
            }  break;
        }


    }
    public void uploadMultipart() {

        String message=null;
        String Eusername=null;

//加urlencode
        try {
            message = URLEncoder.encode(messageET.getText().toString(),"UTF-8");
            Eusername = URLEncoder.encode(username,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.v("message",message+ "------"+URLDecoder.decode(message));


        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, ConfigURL.sendpic)
                    .setUtf8Charset()
                    .addFileToUpload(mCurrentPhotoPath, "image") //Adding file
                    .addParameter("name", Eusername) //Adding text parameter to the request
                    .addParameter("message", message) //用户描述
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .startUpload(); //Starting the upload
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
     //   saveImageUrl=null;
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
    }





}
