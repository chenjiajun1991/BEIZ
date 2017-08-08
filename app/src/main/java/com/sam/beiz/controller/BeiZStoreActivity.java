package com.sam.beiz.controller;

import android.app.ActionBar;
import android.content.Intent;
import android.net.http.SslError;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sam.beiz.common.ActionBarSupport;

import baidumapsdk.demo.demoapplication.R;

public class BeiZStoreActivity extends ActionBarActivity {
    WebView webView;
    private static String storeUrl = "";

    private static String storeJdUrl = "https://mall.jd.com/qr/v.html?type=js&Id=173516&src=qr&resourceType=jdapp_share&resourceValue=CopyURL&utm_source=iosapp&utm_medium=appshare&utm_campaign=t_335139774&utm_term=CopyURL";
    private static String storeTaoBaoUrl = "http://c.b0yp.com/h.T4YBma?cv=6Gg0ZGWr1Fy&sm=1897ff";



//    private static String weatherUrl = "http://baidu.weather.com.cn/mweather/*.shtml";

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

        textView.setText("贝珍商城");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));


//        String store = getIntent().getStringExtra("store");
//
//        if(store != null){
//            if(store.equals("jd")){
//                storeUrl = storeJdUrl;
//            }else {
//                storeUrl = storeTaoBaoUrl;
//            }
//        }else{
//            storeUrl = storeJdUrl;
//        }


        setContentView(R.layout.activity_web_view);

        init();

//        webView = (WebView) findViewById(R.id.webView);


//
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
//        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
//        webView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
//        webView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
//        webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
//        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
//        webView.getSettings().setAppCacheEnabled(true);//是否使用缓存
//        webView.getSettings().setDomStorageEnabled(true);//DOM Storage
//// displayWebview.getSettings().setUserAgentString("User-Agent:Android");//设置用户代理，一般不用
//
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//
//        webView.loadUrl(storeUrl);
//
//
//        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                // TODO Auto-generated method stub
//                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//                view.loadUrl(url);
//                return true;
//            }
//
//        });

    }

    private void init(){
        webView = (WebView) findViewById(R.id.webView);
        //WebView加载web资源
        webView.loadUrl(storeTaoBaoUrl);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
