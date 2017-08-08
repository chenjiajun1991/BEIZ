package com.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by SAM-PC2 on 2016/12/9.
 */
public class ScreenUtil {

    /**
     * dp(dip): device independent pixels(设备独立像素).不同设备有不同的显示效果,与屏幕尺寸有关，不依赖像素
     * px:像素，不同设备显示效果相同
     * sp:主要用于字体显示
     * @param context
     * @param dpValue
     * @return
     */

    //dp转化为px
    public static int dip2px(Context context,float dpValue){
        //获取屏幕的像素密度
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale + 0.5f);
    }
    //px转化为dp
    public static int px2dip(Context context,float pxValue){
        //获取屏幕的像素密度
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/scale + 0.5f);
    }

    //获取屏幕的宽 API 13以上
    public static int getScreenWidth(Context context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
    //获取屏幕的高 API 13以上
    public static int getScreenHeight(Context context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static Point getSize(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        Point size = new Point();
        size.x = dm.widthPixels;
        size.y = dm.heightPixels;
        return size;
    }
}

