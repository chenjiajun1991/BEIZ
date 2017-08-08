package com.sam.beiz.Ad.parse;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.sam.beiz.Ad.adapter.CustomAdapter;
import com.sam.beiz.Ad.domain.ImgWordAd;
import com.sam.beiz.Config.ConfigURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sam.beiz.Ad.string.StrCode.TimeTrans;
import static com.sam.beiz.Config.ConfigURL.healthUrl;
import static com.sam.beiz.Config.ConfigURL.repairListurl;
import static com.sam.beiz.Config.ConfigURL.repairListurl3;

/**
 * Created by zhejunliu on 2017/2/8.
 */

public class DataParser extends AsyncTask<Void,Void,Integer>{
    Context c;
    String jsonData;
    ListView lv;
    ArrayList<ImgWordAd> imgWordAds=new ArrayList<>();
    String url;
    LayoutInflater mLayoutInflater;
    View view;


    public DataParser(Context c, String jsonData, ListView lv, String url) {
        this.c = c;
        this.jsonData = jsonData;
        this.lv = lv;
        this.url=url;
        mLayoutInflater = LayoutInflater.from(c);
    }

    public DataParser(Context c, String jsonData, String url) {
        this.c = c;
        this.jsonData = jsonData;
        this.url=url;
    }
    @Override
    protected Integer doInBackground(Void... voids) {
            return this.parserData();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if(result==0){
        }else{
            if(lv!=null){
                if(url.equals(healthUrl)){
                    CustomAdapter adapter=new CustomAdapter(c,imgWordAds,healthUrl);
                    Log.v("进入健康管家adapter",healthUrl);
                    lv.setAdapter(adapter);
                  //  imgWordAds.clear();
                }
                if(url.equals(repairListurl3)){
                    CustomAdapter adapter=new CustomAdapter(c,imgWordAds,repairListurl3);
                    lv.setAdapter(adapter);
              //      imgWordAds.clear();
                }
                if(url.equals(ConfigURL.repairListurl)){
                    CustomAdapter adapter=new CustomAdapter(c,imgWordAds,url);
                    lv.setAdapter(adapter);
                //    imgWordAds.clear();
                }

            }


        }

    }
    private int parserData(){
        try{
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
                imgWordAds.clear();
                ImgWordAd imgWordAd;
                for(int i=0;i<ja.length();i++){
                    jo=ja.getJSONObject(i);
                    int id=jo.getInt("id");
                    String title=jo.getString("subject");
                    String text=jo.getString("message");
                    String time=jo.getString("lastmodifytime");
                    imgWordAd=new ImgWordAd();
                    imgWordAd.setId(id);
                    imgWordAd.setTitle(title);
                    imgWordAd.setUrl(text);
                    imgWordAd.setTime(TimeTrans(time));
                //    imgWordAd.setTime(time);
                    //  StrCode strCode=new StrCode();
                    imgWordAd.setPic(getImageUrl(text));
               //     Log.v("2",title);
                    // healthAd.setUrl(image);
                    imgWordAds.add(imgWordAd);
                }
                return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public String getImageUrl(String s){
    Pattern ATTR_PATTERN = Pattern.compile("<img[^<>]*?\\ssrc=['\"]?(.*?)['\"]?\\s.*?>", Pattern.CASE_INSENSITIVE);
    if(StringUtils.hasText(s)){
        Matcher matcher = ATTR_PATTERN.matcher(s);
        String str = "";
        while (matcher.find()) {
            str=matcher.group(1).replaceAll("localhost", ConfigURL.replaceHost) ;
            //str= matcher.group(1);
        }
        return str;
    }
    return null;
}

}
