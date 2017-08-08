package com.sam.beiz.controller;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.Ad.download.Downloader;
import com.sam.beiz.Ad.view.RefreshableView;
import com.sam.beiz.common.ActionBarSupport;


import baidumapsdk.demo.demoapplication.R;

import static com.sam.beiz.Config.ConfigURL.healthUrl;

public class HealthListActivity extends ActionBarActivity {
    ListView helthList;
    RefreshableView refreshableView;

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

        textView.setText("健康管家");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        setContentView(R.layout.activity_health_list);
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        helthList=(ListView)findViewById(R.id.health_listview);


        //  helthList.setEmptyView(findViewById(R.id.emptyIv));

        ACache aCache= ACache.get(HealthListActivity.this);

        /*

        缓存  json字符串
         */
//        if(aCache.getAsString("healthlistjsondata")!=null){
//            String checkjson=aCache.getAsString("healthlistjsondata");
//            DataParser parser=new DataParser(HealthListActivity.this,checkjson,helthList,healthUrl);
//            parser.execute();
//        }else{
        if(isNetworkAvailable()){
            new Downloader(HealthListActivity.this,helthList, healthUrl).execute();
        }else{
            Toast.makeText(getApplicationContext(),"您正处于离线模式",Toast.LENGTH_SHORT).show();
        }
//        }




        helthList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id=(TextView)view.findViewById(R.id.textView2);
                Intent intent=new Intent(HealthListActivity.this,HealthButlerActivity.class);
                intent.putExtra("value",id.getText().toString());
                startActivity(intent);
            }
        });
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try{
                    Thread.sleep(3000);

                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                //   refreshableView.setPersonal(helthList,healthUrl);
                if(isNetworkAvailable()){
                    new Downloader(HealthListActivity.this,helthList, healthUrl).execute();
                }else{
                    Toast.makeText(getApplicationContext(),"您正处于离线模式",Toast.LENGTH_SHORT).show();
                }
                refreshableView.finishRefreshing();

            }
        },0);


    }
/*
    @Override
    protected void onResume() {
        super.onResume();
        new Downloader(HealthListActivity.this,helthList, healthUrl).execute();
    }
    */


    public boolean isNetworkAvailable() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
