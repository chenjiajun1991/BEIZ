package com.sam.beiz.controller;

import android.app.ActionBar;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sam.beiz.Config.ConfigURL;
import com.sam.beiz.common.ActionBarSupport;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import baidumapsdk.demo.demoapplication.R;

import static com.sam.beiz.Config.ConfigURL.replaceHost;

public class OnlineServiceActivity extends ActionBarActivity {


    WebView webView;

    private static String onLineServiceUrl = "file:///android_asset/OnlineRepair.html";
    String data=null;

//    private static String weatherUrl = "https://baidu.com";

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

        ImageButton btnBack = (ImageButton) actionbarLayout.findViewById(R.id.left_back);
        TextView textView = (TextView) actionbarLayout.findViewById(R.id.actionBar_title);

        textView.setText("在线报修");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));


        setContentView(R.layout.activity_online_service);
        webView = (WebView) findViewById(R.id.onlinewebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build().LAX);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog()
                .build().LAX);
        JSONObject json = null;
        String str = "";
        cz.msebera.android.httpclient.HttpResponse response;
        cz.msebera.android.httpclient.client.HttpClient myClient = new cz.msebera.android.httpclient.impl.client.DefaultHttpClient();
        cz.msebera.android.httpclient.client.methods.HttpPost myConnection = new cz.msebera.android.httpclient.client.methods.HttpPost(ConfigURL.onlinerepair);

        try {
            response = myClient.execute(myConnection);
            str = cz.msebera.android.httpclient.util.EntityUtils.toString(response.getEntity(), "UTF-8");

        } catch (cz.msebera.android.httpclient.client.ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            JSONArray jArray = new JSONArray(str);
            json = jArray.getJSONObject(0);
            data=json.getString("message");
            Log.v("1",data);

        } catch ( JSONException e) {
            e.printStackTrace();
        }
        if(data!=null){
            webView.loadDataWithBaseURL(null, makeHTML(data), "text/html","UTF-8", null);
        }else{
            /**
             * 暂时不写
             */
        }



        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

    private String makeHTML(String data) {
        String f1="<html><head><meta  content=\"width=device-width, initial-scale=1\" charset=\"gbk2312\">  <link rel=\"stylesheet\" href=\"file:///android_asset/jquery.mobile-1.4.5.min.css\">    <script src=\"file:///android_asset/jquery-1.10.2.min\"></script>    <script src=\"file:///android_asset/jquery.mobile-1.4.5.min.js\"></script> <style type=\"text/css\">body{  } h3{  text-align: center;  } img{  height: auto;  width:100%; text-align: center;  }    </style></head><body><div data-role=\"page\" id=\"pageone\"><div data-role=\"content\">";
        String f2="</div></div></body></html> ";
        String str0="<html>"+"<meta charset=gbk2312>";
        String str = data.replaceAll("localhost", replaceHost);
        String str1="<Body>"+str+"</Body>";
        //   Log.v("tem",str1);
        String str2="</html>";

        //return str0+str1+str2;
        String show=f1+str+f2;
        //   String show=str0+str1+str2;
        Log.v("http",show);
        return show;

    }
}
