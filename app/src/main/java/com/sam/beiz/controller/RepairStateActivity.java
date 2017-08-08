package com.sam.beiz.controller;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.Ad.download.RepairStateDownloader;
import com.sam.beiz.Ad.parse.RepairStateParse;
import com.sam.beiz.Ad.view.RefreshableView;
import com.sam.beiz.Config.ConfigURL;
import com.sam.beiz.OnLineService.OnLineServiceMainActivity;
import com.sam.beiz.common.ActionBarSupport;
import com.utils.NetUtil;

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

import baidumapsdk.demo.demoapplication.R;

import static com.sam.beiz.Config.ConfigURL.checkJson;


public class RepairStateActivity extends ActionBarActivity {
    static String userName="";
    static String imei="";
    ListView repairListView;
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
        textView.setText("维修进度查询");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));


        setContentView(R.layout.activity_repair_state);
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        repairListView=(ListView)findViewById(R.id.repairList);
/*
这里需要获取用户名，imei号
使用字段imei  userName

此时暂只支持imei查询



 */StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build().LAX);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog()
                .build().LAX);

        //      username=account;
        ACache mCache= ACache.get(this);
        if(mCache.getAsString("namefromServer")!=null&&!mCache.getAsString("namefromServer").equals("")){
                userName=mCache.getAsString("namefromServer");
        }else{
            Intent i=new Intent(RepairStateActivity.this,OnLineServiceMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }


        final String newurl= ConfigURL.getNRepair_URL+"?name=%22"+userName+"%22";
        Log.v("首次获取imei 存入cache",newurl);
        ACache aCache=ACache.get(this);
        aCache.put("repairstateurl",newurl);

        if(aCache.getAsString("repairstatejson")!=null){
            String json=aCache.getAsString("repairstatejson");
            RepairStateParse parser=new RepairStateParse(this,json,repairListView);
            parser.execute();
        }else{
            if(NetUtil.isNetworkAvailable(this)) {
                new RepairStateDownloader(RepairStateActivity.this, repairListView, newurl).execute();
            }else{
                Toast.makeText(this, "您正处于离线模式", Toast.LENGTH_SHORT).show();
            }
        }


        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(NetUtil.isNetworkAvailable(RepairStateActivity.this)) {
                    new RepairStateDownloader(RepairStateActivity.this, repairListView, newurl).execute();
                }else{
                    Toast.makeText(RepairStateActivity.this, "您正处于离线模式", Toast.LENGTH_SHORT).show();
                }

                refreshableView.finishRefreshing();
                // new RepairStateDownloader(repairStateActivity.this,repairListView,newurl ).execute();
            }
        }, 0);

        repairListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView discribe=(TextView)view.findViewById(R.id.repairdestv);
                TextView beiz_describe=(TextView)view.findViewById(R.id.repairBdestv);
                String Tdiscribe=(discribe.getText().toString()!=null)?discribe.getText().toString():"您没有填写该字段";
                String Tbeiz_describe=(beiz_describe.getText().toString()!=null)?beiz_describe.getText().toString():"您没有填写该字段";
                AlertDialog.Builder dialog=new AlertDialog.Builder(RepairStateActivity.this);
                dialog.setTitle("详情");
                dialog.setMessage("用户描述:"+Tdiscribe+"\n"+"\n"+"贝珍描述:"+Tbeiz_describe);
                dialog.create();
                dialog.setCancelable(true);

                dialog.show();


                return true;
            }
        });


    }










}
