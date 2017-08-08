package com.sam.beiz.Ad.download;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.Ad.net.Connector;
import com.sam.beiz.Ad.parse.RepairStateParse;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by zhejunliu on 2017/7/20.
 */

public class RepairStateDownloader extends AsyncTask<Void,Void,String> {
    Context c;
    String jsonData;
    ListView lv;
    String url;


    public RepairStateDownloader(Context c, ListView lv, String url) {
        this.c = c;
        this.lv = lv;
        this.url = url;
    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... params) {
        return downloadData();
    }




    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);


        ACache aCache= ACache.get(c);
//        String url2 = null;
//        if(aCache.getAsString("repairstateurl")!=null){  //拼写的url
//            url2=aCache.getAsString("repairstateurl");
//            Log.v("用户报修拼写的cache url",url2);
//        }


        if(jsonData==null){

        }else{
               /*

               存入缓存
                */
            aCache.put("repairstatejson",jsonData,3*60);//3分钟缓存
            Log.v("保存statelistjson",jsonData);

            RepairStateParse parser=new RepairStateParse(c,jsonData,lv);
            parser.execute();


        }
    }
    private  String downloadData(){

        HttpURLConnection con= Connector.connect(url);
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
