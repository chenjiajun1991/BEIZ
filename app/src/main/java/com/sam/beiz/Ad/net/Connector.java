package com.sam.beiz.Ad.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by zhejunliu on 2017/2/8.
 */

public class Connector {
    public   static HttpURLConnection connect(String urlAddress){
        try{
            URL url=new URL(urlAddress);
            HttpURLConnection con=(HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(20000);
            con.setReadTimeout(20000);
            con.setDoInput(true);
            return con;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
