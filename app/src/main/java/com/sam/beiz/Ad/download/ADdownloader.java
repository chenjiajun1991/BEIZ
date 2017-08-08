package com.sam.beiz.Ad.download;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.Ad.net.Connector;
import com.sam.beiz.Ad.parse.AdParse;
import com.sam.beiz.Config.ConfigURL;
import com.sam.beiz.adapter.BannerPager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by Administrator on 2017/6/15.
 */

public class ADdownloader extends AsyncTask<Void,Void,String> {
    String urlAddress;
    LayoutInflater mLayoutInflater;
    Context c;
    ImageView imageView;
    BannerPager mBanner;



    public ADdownloader(String urlAddress, Context c, ImageView imageView) {
        this.urlAddress = urlAddress;
        this.c = c;
        this.imageView = imageView;
    }

    public ADdownloader(Context c, String urlAddress, BannerPager bannerPager) {
        this.c = c;
        this.urlAddress = urlAddress;
        mLayoutInflater = LayoutInflater.from(c);
        this.mBanner=bannerPager;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return downloadData();
    }

    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        if(jsonData==null){
        }else{
            if(urlAddress== ConfigURL.cad){
                   /*
                加入缓存主滚动广告
                 */
                ACache aCache= ACache.get(c);
                aCache.put("mainScrollImages",jsonData,1*60);//1分钟缓存
                AdParse parser=new AdParse(c,jsonData,mBanner,urlAddress);
                parser.execute();
            }
            if(urlAddress== ConfigURL.cad2){
                   /*
                加入缓存
                 */
                ACache aCache= ACache.get(c);
                aCache.put("shopAboveImage",jsonData,1*60);//1分钟缓存
                AdParse parser=new AdParse(c,jsonData,imageView,urlAddress);
                parser.execute();
            }

            if(urlAddress== ConfigURL.onlineRepairAboveurl){
                   /*
                加入缓存在线报修滚动广告
                 */
                ACache aCache= ACache.get(c);
                aCache.put("repairScrollImages",jsonData,1*60);//1分钟缓存
                AdParse parser=new AdParse(c,jsonData,mBanner,urlAddress);
                parser.execute();
            }


        }
    }
    private  String downloadData(){

        HttpURLConnection con= Connector.connect(urlAddress);
        if(con==null){
            return null;
        }
        try{InputStream is=new BufferedInputStream(con.getInputStream());
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer jsonData=new StringBuffer();
            while((line=br.readLine())!=null){
                jsonData.append(line+"\n");

            }
            br.close();
            is.close();
            return  jsonData.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

}