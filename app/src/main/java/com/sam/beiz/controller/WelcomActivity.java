package com.sam.beiz.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.widget.LinearLayout;
import com.sam.beiz.adapter.BannerPager;
import com.sam.beiz.dao.BannerClickListener;
import com.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import baidumapsdk.demo.demoapplication.R;

public class WelcomActivity extends Activity implements BannerClickListener{


    private BannerPager mBanner;
    private Context mContext;
    private final  Timer timer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);

        mContext = this;

        mBanner = (BannerPager)findViewById(R.id.banner_pager2);


        LinearLayout.LayoutParams bannerParams = (LinearLayout.LayoutParams) mBanner.getLayoutParams();

        bannerParams.weight = ScreenUtil.getScreenWidth(mContext);
        bannerParams.height = ScreenUtil.getScreenHeight(mContext);
        mBanner.setLayoutParams(bannerParams);

        ArrayList<Integer> bannerArray = new ArrayList<Integer>();
        bannerArray.add(Integer.valueOf(R.drawable.index1));
        bannerArray.add(Integer.valueOf(R.drawable.index2));
        bannerArray.add(Integer.valueOf(R.drawable.index3));
        bannerArray.add(Integer.valueOf(R.drawable.index4));
        mBanner.setImages(bannerArray);
        mBanner.setRadioGroupVisible(false);
        mBanner.setOnBannerListener(this);


        TimerTask task = new TimerTask()
        {
            public void run()
            {
            if(mBanner.getPosion() == 3){
                timer.cancel();
            Intent intent = new Intent(WelcomActivity.this,LauncherAcitivity.class);
            startActivity(intent);
            finish();
           }
            }
        };

        timer.schedule(task, 200, 1000);

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBannerClick(int position) {
            timer.cancel();
            Intent intent = new Intent(WelcomActivity.this,LauncherAcitivity.class);
            startActivity(intent);
            finish();
    }


}
