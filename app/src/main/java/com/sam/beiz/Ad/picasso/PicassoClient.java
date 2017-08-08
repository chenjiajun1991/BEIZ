package com.sam.beiz.Ad.picasso;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import baidumapsdk.demo.demoapplication.R;


/**
 * Created by zhejunliu on 2017/2/8.
 */

public class PicassoClient {
    public static void downloadImage(Context c, String imageurl, ImageView img){
        if(imageurl.length()>0&&imageurl!=null){
            Picasso.with(c).load(imageurl).placeholder(R.drawable.health1).into(img);

        }else{
            Picasso.with(c).load(R.drawable.health1).into(img);
        }

    }

}
