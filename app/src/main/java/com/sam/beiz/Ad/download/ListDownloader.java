package com.sam.beiz.Ad.download;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.Ad.net.Connector;
import com.sam.beiz.Ad.parse.DataParser;
import com.sam.beiz.Config.ConfigURL;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by zhejunliu on 2017/7/21.
 */
/*

两个相近list
 */
public class ListDownloader extends AsyncTask<Void,Void,String> { Context c;
    String urlAddress;
    ListView lv;

    public ListDownloader(Context c, ListView lv, String urlAddress) {
        this.c = c;
        this.lv = lv;
        this.urlAddress = urlAddress;
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



        if(jsonData==null){

        }else{
             if(ConfigURL.repairListurl3==urlAddress){
               /*

               存入缓存
                */
                aCache.put("repairListjson3",jsonData,3*60);//3分钟缓存

                DataParser parser=new DataParser(c,jsonData,lv,urlAddress);
                parser.execute();
            }else{

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
