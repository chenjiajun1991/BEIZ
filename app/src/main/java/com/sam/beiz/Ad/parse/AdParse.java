package com.sam.beiz.Ad.parse;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.sam.beiz.Ad.domain.Sad;
import com.sam.beiz.Ad.picasso.PicassoClient;
import com.sam.beiz.Config.ConfigURL;
import com.sam.beiz.adapter.BannerPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.sam.beiz.Config.ConfigURL.picstorepath;

/**
 * Created by Administrator on 2017/6/15.
 */

public class AdParse extends AsyncTask<Void,Void,ArrayList<Sad>> {
    Context c;
    String jsonData;
    ArrayList<Sad> sads=new ArrayList<>();
    BannerPager mBanner;
    ArrayList<String> adArray=new ArrayList<String>();
    List<String> environmentList = new ArrayList<String>();
    ImageView imageView;
    String url;

    public AdParse(Context c, String jsonData, BannerPager mBanner, String url) {
        this.c = c;
        this.jsonData = jsonData;
        this.mBanner=mBanner;
        this.url=url;
    }


    public AdParse(Context c, String jsonData, ImageView imageView, String url) {
        this.c = c;
        this.jsonData = jsonData;
        this.imageView = imageView;
        this.url=url;
    }

    @Override
    protected ArrayList<Sad> doInBackground(Void... voids) {
        return this.parserData();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<Sad> result) {
        super.onPostExecute(result);
        if(result==null){

        }else{
/*
滚动广告用到SharedPreferences
 */
            if(url== ConfigURL.cad){
                /*

                主页面滚动广告
                 */
                mBanner.setImage(adArray);
                SharedPreferences.Editor editor = c.getSharedPreferences("EnvironDataList", MODE_PRIVATE).edit();
                for (int i = 0; i < environmentList.size(); i++)
                {
                    editor.putString("item_"+i, environmentList.get(i));
                }
                editor.commit();
            }
            if(url== ConfigURL.onlineRepairAboveurl){
                /*
                在线报修滚动广告
                 */
                mBanner.setImage(adArray);
                SharedPreferences.Editor editor = c.getSharedPreferences("EnvironDataRepairList", MODE_PRIVATE).edit();
                for (int i = 0; i < environmentList.size(); i++)
                {
                    editor.putString("item_"+i, environmentList.get(i));
                }
                editor.commit();
            }
            if(url== ConfigURL.cad2){
                String url=result.get(0).getPic();
                String info=result.get(0).getUrl();
                Log.v("link",info);
//                ACache aCache=ACache.get(c);
//                aCache.put("SingleImgJump",info);
           //     AdstoreDB.setJumpurl(info);

               PicassoClient.downloadImage(c, url, imageView);
            }

        }

    }
    private ArrayList<Sad> parserData(){
        try{
            JSONArray ja=new JSONArray(jsonData);
            int count=0;
            JSONObject jo=null;
            sads.clear();
            adArray.clear();
            environmentList.clear();
            Sad cad;
            if(ja.length()>=3){
               count=3;
            }else{
                count=ja.length();
            }

            for(int i=0;i<count;i++){
                jo=ja.getJSONObject(i);
                String text=jo.getString("text");
                String pic=jo.getString("pic");

                adArray.add(picstorepath+pic);
                environmentList.add("http://"+text);
                String newpic=addpath(pic);

                cad=new Sad();
                cad.setUrl(text);
                cad.setPic(newpic);
                sads.add(cad);
            }
            return sads;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String addpath(String before) {
        String output= picstorepath+before;
        return output;
    }


}