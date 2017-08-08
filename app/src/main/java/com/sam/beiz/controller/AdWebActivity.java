package com.sam.beiz.controller;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sam.beiz.common.ActionBarSupport;

import baidumapsdk.demo.demoapplication.R;

public class AdWebActivity extends Activity {
    WebView webView;

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

        textView.setText("内部浏览器");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));

        setContentView(R.layout.activity_ad_web);
        webView=(WebView)findViewById(R.id.AdWebView) ;


        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setLoadsImagesAutomatically(true);
        //  webview.getSettings().setUseWideViewPort(true);
        //   webview.getSettings().setLoadWithOverviewMode(true);

        String mylink=getIntent().getStringExtra("link");
        Log.v("链接",mylink);
        webView.loadUrl(mylink);

        webView.setWebViewClient(new WebViewClient());


    }


}
