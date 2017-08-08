package com.sam.beiz.controller;

import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sam.beiz.common.ActionBarSupport;

import baidumapsdk.demo.demoapplication.R;

public class DefaultActivity extends ActionBarActivity {

    private static String title ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String parms = getIntent().getStringExtra("parms");

        if(parms != null){
            if(parms.equals("4")){
                title = "生活提醒";
            }else {
                title = "生活乐园";
            }
        }else{
            title = "暂未开放";
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

        textView.setText(title);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));





        setContentView(R.layout.activity_third);
    }
}
