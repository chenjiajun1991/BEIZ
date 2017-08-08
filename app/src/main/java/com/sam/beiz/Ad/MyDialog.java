package com.sam.beiz.Ad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.OnLineService.OnLineServiceMainActivity;
import com.sam.beiz.controller.CameraActivity;
import com.sam.beiz.controller.ImageActivity;
import com.sam.beiz.controller.LauncherAcitivity;
import com.sam.beiz.controller.VideoActivity;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import baidumapsdk.demo.demoapplication.Login_main;
import baidumapsdk.demo.demoapplication.R;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.sam.beiz.Config.ConfigURL.checkJson;

/**
 * Created by zhejunliu on 2017/8/6.
 */

public class MyDialog extends android.support.v4.app.Fragment {
    ImageView imageView;
    static String username="";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.circlebackgroung, null);


        check();
        imageView=(ImageView)myView.findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!username.equals("")){
                    buildDialog();
                }

            }
        });

        return myView;
       // return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void check() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build().LAX);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog()
                .build().LAX);
        ACache mCache= ACache.get(getActivity());
        if(  mCache.getAsString("userPHONE")!=null&&!mCache.getAsString("userPHONE").equals("")){
            if(mCache.getAsString("namefromServer")!=null&&!mCache.getAsString("namefromServer").equals("")){
                username=mCache.getAsString("namefromServer");
            }else{
                checkPassword();
            }
        }else{
            Intent i=new Intent(getActivity(),Login_main.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!username.equals("")){
            buildDialog();
        }
     //   setCancelable(false);

    }

    private void buildDialog(){
        final Dialog pickdialog = new Dialog(getActivity());
        pickdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater li = LayoutInflater.from(getActivity());
        View myView = li.inflate(R.layout.choosedialog2, null);
        pickdialog.setContentView(myView);
        Window window = pickdialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_style);
        pickdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView photobtn=(ImageView)myView.findViewById(R.id.btnPhoto);
        ImageView gallerybtn=(ImageView)myView.findViewById(R.id.btnGallery);
        ImageView capturebtn=(ImageView)myView.findViewById(R.id.btnCapture);


        photobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // TODO Auto-generated method stub
                //  takephoto();
                Intent i=new Intent(getActivity(),CameraActivity.class);
                startActivity(i);
                pickdialog.dismiss();

            }


        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getActivity(),ImageActivity.class);
                startActivity(i);
                pickdialog.dismiss();
            }
        });
        capturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   showFileChooser();
                Intent i=new Intent(getActivity(),VideoActivity.class);
                startActivity(i);
                pickdialog.dismiss();
            }
        });
        pickdialog.show();
    }

    private void checkPassword() {
        final AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater mLayoutInflater=LayoutInflater.from(getActivity());
        final View view=mLayoutInflater.inflate(R.layout.passworddialog, null);
        mydialog.setView(view);
        mydialog.setCancelable(false);
        mydialog.setPositiveButton("用户验证", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText password=(EditText) view.findViewById(R.id.editText4);
                String textPass=password.getText().toString();
                if(textPass.equals("")){
                    Toast.makeText(getActivity(),"您没有输入",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity(),LauncherAcitivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    ACache mCache=ACache.get(getActivity());
                    testGetVerificationCode(checkJson,mCache.getAsString("userPHONE"),textPass);
                }
            }
        });
        mydialog.show();
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
                if(c.equals("")){
                    Toast.makeText(getActivity(),"验证失败",Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(getActivity(),LauncherAcitivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }else{
                    username=c;
                    ACache aCache=ACache.get(getActivity());
                    /*
                    记录登陆状态 10分钟
                     */
                    aCache.put("namefromServer",c,600);
                    Log.v("namefromServer",c);
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

}
