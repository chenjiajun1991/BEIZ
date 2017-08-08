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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sam.beiz.common.ActionBarSupport;

import baidumapsdk.demo.demoapplication.R;

public class WebViewActivity extends ActionBarActivity {

    WebView webView;

//    private static String weatherUrl = "http://baidu.weather.com.cn/mweather/*.shtml";
    private static String weatherUrl = "http://weather.html5.qq.com/";
    private static String lifeParadiseUrl = "http://yule.360.cn/";

//    private static String weatherUrl = "https://baidu.com";

    private String mTitle = "";

    private String mUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent != null){
            String request = intent.getStringExtra("request");
            if(request.equals("weather")){
                mTitle = "天气预报";
                mUrl = weatherUrl;
            }else{
                mTitle = "生活乐园";
                mUrl = lifeParadiseUrl;
            }
        }else{
            mTitle = "生活乐园";
            mUrl = lifeParadiseUrl;
        }

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

        textView.setText(mTitle);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));



        setContentView(R.layout.activity_web_view);

        webView = (WebView) findViewById(R.id.webView);




        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        webView.getSettings().setDomStorageEnabled(true);//DOM Storage
//        webView.getSettings().setUserAgentString("User-Agent:Android");//设置用户代理，一般不用


        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webView.getSettings().supportMultipleWindows();  //多窗口
        webView.getSettings().setAllowFileAccess(true);  //设置可以访问文件
        webView.getSettings().setNeedInitialFocus(true); //当webview调用requestFocus


        // 时为webview设置节点
        webView.getSettings().setLoadsImagesAutomatically(true);  //支持自动加载图片





        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
         webView.requestFocus();
        webView.loadUrl(mUrl);


        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // 接受所有网站的证书
//                super.onReceivedSslError(view, handler, error);
            }
        });


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
