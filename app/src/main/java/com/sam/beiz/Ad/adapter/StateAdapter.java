package com.sam.beiz.Ad.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sam.beiz.Ad.domain.Repairtrack;
import com.sam.beiz.Ad.picasso.PicassoClient;

import java.util.ArrayList;

import baidumapsdk.demo.demoapplication.R;

/**
 * Created by Administrator on 2017/7/20.
 */

public class StateAdapter extends BaseAdapter {
    Context c;
    ArrayList<Repairtrack> repairtracks;
    LayoutInflater inflater;

    public StateAdapter(Context c, ArrayList<Repairtrack> repairtracks) {
        this.c = c;
        this.repairtracks = repairtracks;
        inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return repairtracks.size();
    }

    @Override
    public Object getItem(int i) {
        return repairtracks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(R.layout.repairitem,viewGroup,false);
        }

        TextView express=(TextView) view.findViewById(R.id.expresstv);
        TextView expressnum=(TextView) view.findViewById(R.id.expressnumtv);
        TextView createtime=(TextView) view.findViewById(R.id.createtimetv);
        TextView repairdes=(TextView) view.findViewById(R.id.repairdestv);
        TextView repairBdes=(TextView) view.findViewById(R.id.repairBdestv);
        TextView state=(TextView) view.findViewById(R.id.statetv);
        TextView name=(TextView) view.findViewById(R.id.nametv);
        ImageView imageView=(ImageView)view.findViewById(R.id.repairiv);
        TextView back_express=(TextView) view.findViewById(R.id.back_expresstv);
        TextView back_expressnum=(TextView) view.findViewById(R.id.back_expressnumtv);

        Repairtrack repairtrack=repairtracks.get(i);
        express.setText(String.valueOf(repairtrack.getExpress()));
        expressnum.setText(String.valueOf(repairtrack.getExpressnum()));
        createtime.setText(String.valueOf(repairtrack.getCreatetime()));
        repairdes.setText(String.valueOf(repairtrack.getDiscribe()));
        repairBdes.setText(String.valueOf(repairtrack.getBeiz_describe()));

        String typeColor=String.valueOf(repairtrack.getState());

        if(typeColor.equals("已提交")){
            state.setTextColor(Color.parseColor("#FF0000"));//红色
        }
        if(typeColor.equals("正在处理中")){
            state.setTextColor(Color.parseColor("#FFFF00"));//黄色
        }
        if(typeColor.equals("已处理完毕")){
            state.setTextColor(Color.parseColor("#00FF00"));//绿色
        }

        state.setText(typeColor);
        name.setText(String.valueOf(repairtrack.getName()));
        back_express.setText(String.valueOf(repairtrack.getBack_express()));
        back_expressnum.setText(String.valueOf(repairtrack.getBack_expressnum()));
        PicassoClient.downloadImage(c, String.valueOf(repairtrack.getImageurl()),imageView);

        return view;
    }
}
