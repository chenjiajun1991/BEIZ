package com.sam.beiz.controller;

import android.app.ActionBar;
import android.content.Intent;
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
import com.sam.beiz.Ad.parse.DataParser;
import com.sam.beiz.Ad.view.RefreshableView;
import com.sam.beiz.common.ActionBarSupport;
import com.utils.NetUtil;

import baidumapsdk.demo.demoapplication.R;

import static com.sam.beiz.Config.ConfigURL.repairListurl;

public class SeeMoreActivity extends ActionBarActivity {
    ListView moreList;
    RefreshableView refreshmoreView;

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

        textView.setText("更多");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        setContentView(R.layout.activity_see_more);

        refreshmoreView = (RefreshableView) findViewById(R.id.seeMorelist);
        moreList=(ListView)findViewById(R.id.more_listview);


        ACache aCache=ACache.get(SeeMoreActivity.this);
        if(aCache.getAsString("repairListjson")!=null){
            String json=aCache.getAsString("repairListjson");
            DataParser parser=new DataParser(SeeMoreActivity.this,json,moreList,repairListurl);
            parser.execute();
        }else{

            if(NetUtil.isNetworkAvailable(SeeMoreActivity.this)){
                new Downloader(SeeMoreActivity.this,moreList, repairListurl).execute();
                Log.v("checkSeeMore","true");
            }else{
                Toast.makeText(SeeMoreActivity.this, "您正处于离线模式", Toast.LENGTH_SHORT).show();
            }
            // new Downloader(repairStateActivity.this,repairListView,newurl ).execute();
        }

        moreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id=(TextView)view.findViewById(R.id.textView2);
                Intent intent=new Intent(SeeMoreActivity.this,HealthButlerActivity.class);
                intent.putExtra("value",id.getText().toString());
                startActivity(intent);
            }
        });
        refreshmoreView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try{
                    Thread.sleep(3000);

                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                if(NetUtil.isNetworkAvailable(SeeMoreActivity.this)){
                    new Downloader(SeeMoreActivity.this,moreList, repairListurl).execute();
                    Log.v("checkSeeMore","true");
                }else{
                    Toast.makeText(SeeMoreActivity.this, "您正处于离线模式", Toast.LENGTH_SHORT).show();
                }
                refreshmoreView.finishRefreshing();

            }
        },0);


    }
}
