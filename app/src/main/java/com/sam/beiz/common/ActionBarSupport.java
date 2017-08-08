package com.sam.beiz.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.support.lib.SystemBarTintManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import baidumapsdk.demo.demoapplication.R;

/**
 * Created by SAM-PC2 on 2017/4/11.
 */
public class ActionBarSupport {

    //状态栏透明
    public void initActionBar(Activity activity,int draeableId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {            //系统版本大于19
            setTranslucentStatus(true,activity);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(draeableId);
        Class clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if(true){
                extraFlagField.invoke(activity.getWindow(),darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
            }else{
                extraFlagField.invoke(activity.getWindow(), 0, darkModeFlag);//清除黑色字体
            }
        }catch (Exception e){
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on,Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;        // a|=b的意思就是把a和b按位或然后赋值给a   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
        } else {
            winParams.flags &= ~bits;        //&是位运算里面，与运算  a&=b相当于 a = a&b  ~非运算符
        }
        win.setAttributes(winParams);
    }


}
