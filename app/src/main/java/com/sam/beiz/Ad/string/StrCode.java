package com.sam.beiz.Ad.string;





import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/6/12.
 */

public class StrCode {
    private static SimpleDateFormat simpleDateFormat;
    private static int day, hour, minute;
    private static long between;
    private static String strCurrentDate;
    private static String strLastDate;
    private static ParsePosition pos;
    private static Activity context;

    public StrCode(Context context) {
        this.context=(Activity)context;
    }

    public String getTmageUrl(String s){
        String rx = "http:/(?:/[^/]+)+\\.(?:jpg|gif|png)";
   //     String url = "http://www.medivision360.com/pharma/pages/articleImg/thumbnail/thumb3756d839adc5da3.jpg";
        Pattern pat = Pattern.compile(rx);
        Matcher matcher = pat.matcher(s);
        if (matcher.matches()) {
           // System.out.println(matcher.group());


            return matcher.group();
        }
        return null;
    }


    public static String timeTrans(String s){
        Date date=null;
        String newDate="";
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        try {
            date = formatter.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date curDate = new Date(System.currentTimeMillis());//获取系统时间
        long diff = curDate.getTime() - date.getTime();//这样得到的差值是毫秒级别
        Log.v("checkTime1",s+"");
        Log.v("checkTime2",curDate.getTime()+"");
        Log.v("checkTime",diff+"");
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes =  (diff % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (diff % (1000 * 60)) / 1000;
        if(seconds<60&&seconds>0){
            newDate="刚刚";
            return newDate;
        }
        if(minutes<60){
            newDate=minutes+"分钟前";
            return newDate;
        }
        if(hours<24){
            newDate=minutes+"分钟前";
            return newDate;
        }else{
            newDate=days+"天前";
            return newDate;
        }
    }


    public static String TimeTrans(String s){

        GetCurrentDate();
        pos = new ParsePosition(0);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        strCurrentDate = simpleDateFormat.format(GetCurrentDate());
        between = (GetCurrentDate().getTime() - simpleDateFormat.parse(
                s, pos).getTime()) / 1000;
        day = (int) between / (24 * 3600);
        hour = (int) between % (24 * 3600) / 3600;
        minute = (int) between % 3600 / 60;
        String lastDate="";

        if (day > 0) {
//            lastDate=day + "天" + hour + "小时" + minute
//                    + "分钟" + "前";
            lastDate=day + "天" + "前";
        }
        if (day == 0 && hour > 0) {
            lastDate=hour + "小时"  + "前";
        }
        if (hour == 0 && minute > 0) {
            lastDate=minute + "分钟" + "前";
        }
        return  lastDate;
    };


    public static Date GetCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date mDate = calendar.getTime();
        return mDate;
    }
/*
获取屏幕高宽
 */
public static  int ScreenHeight(){
    DisplayMetrics dm = new DisplayMetrics();
    context.getWindowManager().getDefaultDisplay().getMetrics(dm);
    return  dm.heightPixels;

};


}



