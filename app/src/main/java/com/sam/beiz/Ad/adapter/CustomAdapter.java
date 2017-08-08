package com.sam.beiz.Ad.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sam.beiz.Ad.domain.ImgWordAd;
import com.sam.beiz.Ad.picasso.PicassoClient;

import java.util.ArrayList;

import baidumapsdk.demo.demoapplication.R;

/**
 * Created by zhejunliu on 2017/2/8.
 */

public class CustomAdapter extends BaseAdapter {

    Context c;
    ArrayList<ImgWordAd> imgWordAds;
    LayoutInflater inflater;
    String url;

    public CustomAdapter(Context c, ArrayList<ImgWordAd> imgWordAds, String url) {
        this.c = c;
        this.imgWordAds = imgWordAds;
        inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.v("url",url);
        this.url=url;
    }

    @Override
    public int getCount() {
        return imgWordAds.size();
    }

    @Override
    public Object getItem(int i) {
        return imgWordAds.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.healthitem, viewGroup, false);
        }
                TextView id = (TextView) view.findViewById(R.id.textView2);
                TextView name = (TextView) view.findViewById(R.id.textView);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView2);
                 TextView  time=(TextView)view.findViewById(R.id.textView3);
        /*
        textview3  时间
         */
                ImgWordAd healthAd = imgWordAds.get(i);
                id.setText(String.valueOf(healthAd.getId()));
                name.setText(String.valueOf(healthAd.getTitle()));
                PicassoClient.downloadImage(c, String.valueOf(healthAd.getPic()), imageView);
                time.setText(String.valueOf(healthAd.getTime()));
                Log.v("3", String.valueOf(healthAd.getPic()));
            return view;


    }

}
