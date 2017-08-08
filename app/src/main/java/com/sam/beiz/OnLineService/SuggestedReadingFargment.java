package com.sam.beiz.OnLineService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.Ad.download.Downloader;
import com.sam.beiz.Ad.parse.DataParser;
import com.sam.beiz.Ad.view.RefreshableView;
import com.sam.beiz.controller.HealthButlerActivity;
import com.utils.NetUtil;

import baidumapsdk.demo.demoapplication.R;

import static com.sam.beiz.Config.ConfigURL.healthUrl;
import static com.sam.beiz.Config.ConfigURL.repairListurl3;

public class SuggestedReadingFargment extends Fragment {
    ListView suggestReadList;
    RefreshableView refreshableView;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_suggest_read, container, true);

        suggestReadList=(ListView) view.findViewById(R.id.suggect_read_listview);
        refreshableView=(RefreshableView)view.findViewById(R.id.suggestRefresh);

        ACache aCache=ACache.get(getActivity());
        if(aCache.getAsString("repairListjson3")!=null){
            String json=aCache.getAsString("repairListjson3");
            Log.v("建议阅读缓存",json);
            DataParser parser=new DataParser(getActivity(),json,suggestReadList,repairListurl3);
            parser.execute();
        }else {

            if (NetUtil.isNetworkAvailable(mContext)) {
                new Downloader(mContext, suggestReadList, repairListurl3).execute();
            } else {
                Toast.makeText(mContext, "您正处于离线模式", Toast.LENGTH_SHORT).show();
            }
        }


        // new Downloader(repairStateActivity.this,repairListView,newurl ).execute();
//        }

//
//        if(NetUtil.isNetworkAvailable(mContext)){
//            new Downloader(mContext,suggestReadList, repairListurl).execute();
//        }else{
//            Toast.makeText(mContext, "您正处于离线模式", Toast.LENGTH_SHORT).show();
//        }






        suggestReadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id=(TextView)view.findViewById(R.id.textView2);
                Intent intent=new Intent(mContext,HealthButlerActivity.class);
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
                if (NetUtil.isNetworkAvailable(mContext)) {
                    new Downloader(mContext, suggestReadList, repairListurl3).execute();
                } else {
                    Toast.makeText(mContext, "您正处于离线模式", Toast.LENGTH_SHORT).show();
                }
                refreshableView.finishRefreshing();

            }
        },0);


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
