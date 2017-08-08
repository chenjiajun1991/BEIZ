package com.sam.beiz.Ad.parse;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.sam.beiz.Ad.adapter.StateAdapter;
import com.sam.beiz.Ad.domain.Repairtrack;
import com.sam.beiz.Config.ConfigURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/7/20.
 */

public class RepairStateParse extends AsyncTask<Void,Void,Integer> {
    Context c;
    String jsonData;
    ListView lv;
    ArrayList<Repairtrack> repairtracks=new ArrayList<>();


    public RepairStateParse(Context c, String jsonData, ListView lv) {
        this.c = c;
        this.jsonData = jsonData;
        this.lv = lv;
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
                StateAdapter adapter=new StateAdapter(c,repairtracks);
                lv.setAdapter(adapter);

        }

    }
    private int parserData(){
        try{
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;

                repairtracks.clear();
                Repairtrack repairtrack;
                for(int i=0;i<ja.length();i++){
                    jo=ja.getJSONObject(i);
                    int id=jo.getInt("id");
                    String Tname= URLDecoder.decode(jo.getString("name"));
                    String express =URLDecoder.decode(jo.getString("express"));
                    String expressnum= jo.getString("expressnum");
                    String imageurl=jo.getString("imageurl");
                    String createtime=jo.getString("createtime");
                    String Tdiscribe=URLDecoder.decode(jo.getString("discribe"));
                    String state=URLDecoder.decode(jo.getString("state"));
                    String Tbeiz_describe=URLDecoder.decode(jo.getString("beiz_describe"));
                    String Tback_express=URLDecoder.decode(jo.getString("back_express"));
                    String Tback_expressnum=URLDecoder.decode(jo.getString("back_expressnum"));
                    String name=(Tname!="null")?Tname:"";
                    String discribe=(Tdiscribe!="null")?Tdiscribe:"";
                    String beiz_describe=(Tbeiz_describe!="null")?Tbeiz_describe:"";
                    String back_express=(Tback_express!="null")?Tback_express:"";
                    String back_expressnum=(Tback_expressnum!="null")?Tback_expressnum:"";
                    Log.v("id",id+"");
                    Log.v("express",express+"");
                    Log.v("expressnum",expressnum+"");
                    Log.v("imageurl",imageurl+"");
                    Log.v("createtime",createtime+"");
                    Log.v("discribe",discribe+"");
                    Log.v("state",state+"");
                    Log.v("beiz_describe",beiz_describe+"");
                    Log.v("back_express",back_express+"");
                    Log.v("back_expressnum",back_expressnum+"");
                    repairtrack=new Repairtrack();
                    repairtrack.setId(id);
                    repairtrack.setName(name);
                    repairtrack.setExpress(express);
                    repairtrack.setExpressnum(expressnum);
                    repairtrack.setImageurl(imageurl);
                    repairtrack.setCreatetime(createtime);
                    repairtrack.setDiscribe(discribe);
                    repairtrack.setState(state);
                    repairtrack.setBeiz_describe(beiz_describe);
                    repairtrack.setBack_express(back_express);
                    repairtrack.setBack_expressnum(back_expressnum);
                    repairtracks.add(repairtrack);
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
