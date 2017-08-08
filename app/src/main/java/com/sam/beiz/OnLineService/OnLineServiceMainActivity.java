package com.sam.beiz.OnLineService;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.Ad.download.ADdownloader;
import com.sam.beiz.Ad.parse.AdParse;
import com.sam.beiz.Config.ConfigURL;
import com.sam.beiz.adapter.BannerPager;
import com.sam.beiz.common.ActionBarSupport;
import com.sam.beiz.controller.AdWebActivity;
import com.sam.beiz.controller.AppliRepairActivity;
import com.sam.beiz.controller.OnlineServiceActivity;
import com.sam.beiz.controller.RepairStateActivity;
import com.sam.beiz.controller.SeeMoreActivity;
import com.sam.beiz.controller.WebView2Activity;
import com.sam.beiz.dao.BannerClickListener;
import com.utils.NetUtil;

import java.util.ArrayList;

import baidumapsdk.demo.demoapplication.R;

import static com.sam.beiz.Config.ConfigURL.onlineRepairAboveurl;

public class OnLineServiceMainActivity extends ActionBarActivity implements BannerClickListener ,View.OnClickListener {
    private ImageView Conlinerepairiv, Crepairstateiv, Crepairfeeiv, Cpolicyiv, Crepairlocationiv, Cquestioniv;
    TextView seeMore;

    private BannerPager mBanner;
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

        final ImageButton btnBack = (ImageButton) actionbarLayout.findViewById(R.id.left_back);
        TextView textView = (TextView) actionbarLayout.findViewById(R.id.actionBar_title);

        textView.setText("在线报修");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));



        setContentView(R.layout.activity_on_line_service_main);
        Conlinerepairiv = (ImageView) findViewById(R.id.onlinerepairiv);
        Crepairstateiv = (ImageView) findViewById(R.id.repairstateiv);
        Crepairfeeiv = (ImageView) findViewById(R.id.repairfeeiv);
        Cpolicyiv = (ImageView) findViewById(R.id.policyiv);
        Crepairlocationiv = (ImageView) findViewById(R.id.repairlocationiv);
        Cquestioniv = (ImageView) findViewById(R.id.questioniv);

        Conlinerepairiv.setOnClickListener(this);
        Crepairstateiv.setOnClickListener(this);
        Crepairfeeiv.setOnClickListener(this);
        Cpolicyiv.setOnClickListener(this);
        Crepairlocationiv.setOnClickListener(this);
        Cquestioniv.setOnClickListener(this);
        seeMore = (TextView) findViewById(R.id.seemoretv);
        seeMore.setOnClickListener(this);


        mBanner = (BannerPager) findViewById(R.id.banner_pager2);


        ArrayList<Integer> sbannerArray = new ArrayList<>();

        /*
        取缓存操作
         */
        ACache aCache=ACache.get(OnLineServiceMainActivity.this);
        if(aCache.getAsString("repairScrollImages")!=null){
            String checkjson=aCache.getAsString("repairScrollImages");
            AdParse parser=new AdParse(this,checkjson,mBanner, onlineRepairAboveurl);
            parser.execute();
            Log.v("滚动图片走缓存",checkjson);
            mBanner.setOnBannerListener(this);
        }else {

            if (NetUtil.isNetworkAvailable(this)) {
                new ADdownloader(this, onlineRepairAboveurl, mBanner).execute();
                Log.v("滚图走downloader 显示url", onlineRepairAboveurl);
                mBanner.setOnBannerListener(this);
            } else {
                ConfigURL.Netconnect = false;

                sbannerArray.add(Integer.valueOf(R.drawable.img1));
                sbannerArray.add(Integer.valueOf(R.drawable.img2));
                sbannerArray.add(Integer.valueOf(R.drawable.img3));
                mBanner.setImages(sbannerArray);
            }



        }
    }

    @Override
    public void onBannerClick(int position) {

        if(NetUtil.isNetworkAvailable(this)){
            SharedPreferences preferDataList = getSharedPreferences("EnvironDataRepairList", MODE_PRIVATE);
            String environItem = preferDataList.getString("item_"+position, null);
            Log.v("滚动广告跳转链接", environItem);
            Intent i=new Intent(this,AdWebActivity.class);
            i.putExtra("link",environItem);
            startActivity(i);
        }else
        {
            Toast.makeText(OnLineServiceMainActivity.this, "您正处于离线模式", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){


            case R.id.onlinerepairiv: {
                Intent intent=new Intent(OnLineServiceMainActivity.this,AppliRepairActivity.class);
                //   Log.v("onlinerepairiv","onlinerepairiv");
                startActivity(intent);
                break;
            }
            case R.id.repairstateiv: {
                Intent intent=new Intent(OnLineServiceMainActivity.this,RepairStateActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.repairfeeiv: {
                Intent intent=new Intent(OnLineServiceMainActivity.this,WebView2Activity.class);

                intent.putExtra("type","repairfee");
                startActivity(intent);
                break;
            }
            case R.id.policyiv: {
                Intent intent=new Intent(OnLineServiceMainActivity.this,WebView2Activity.class);

                intent.putExtra("type","policy");
                startActivity(intent);
                break;
            }


            case R.id.repairlocationiv: {
              /*


               */
                break;
            }
            case R.id.questioniv: {
                Intent intent=new Intent(OnLineServiceMainActivity.this,WebView2Activity.class);

                intent.putExtra("type","question");
                startActivity(intent);
                break;
            }
            case R.id.seemoretv: {
                Intent intent=new Intent(OnLineServiceMainActivity.this,SeeMoreActivity.class);
                startActivity(intent);
                break;
            }

        }



    }
}
