package baidumapsdk.demo.demoapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sam.beiz.controller.LauncherAcitivity;

public class UpdateAppWebAcitivity extends ActionBarActivity {

    WebView webView;
//    private String reqView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String url = getIntent().getStringExtra("apkUrl");

//        SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
//                Context.MODE_PRIVATE);
//        reqView = mainPref.getString("updateView","UserView");



        setContentView(R.layout.activity_update_app_web_acitivity);
        webView = (WebView) findViewById(R.id.web_view_update);
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.loadUrl(url);
    }


    @Override
    public void onBackPressed() {
//        if (reqView.equals("UserView")){
//            Intent intent = new Intent(UpdateAppWebAcitivity.this,UserView.class);
//            startActivity(intent);
//            finish();
//        }else if(reqView.equals("OverlayDemo")){
//            Intent intent = new Intent(UpdateAppWebAcitivity.this,OverlayDemo.class);
//            startActivity(intent);
//            finish();
//        }else if(reqView.equals("DistributorView")){
//            Intent intent = new Intent(UpdateAppWebAcitivity.this,DistributorView.class);
//            startActivity(intent);
//            finish();
//        }else{
            Intent intent = new Intent(UpdateAppWebAcitivity.this,LauncherAcitivity.class);
            startActivity(intent);
            finish();
//        }
//        super.onBackPressed();
    }
}
