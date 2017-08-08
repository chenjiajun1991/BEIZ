package com.sam.beiz.controller;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sam.beiz.common.ActionBarSupport;

import baidumapsdk.demo.demoapplication.R;

public class SelfInfoAcitivity extends ActionBarActivity {

    EditText edName, edPrivence,edCity;
    RadioGroup radioGroup;
    Button btnSummit;
    RadioButton rdMan,rdWomen;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_info);


        ActionBarSupport actionBarSupport = new ActionBarSupport();
        actionBarSupport.initActionBar(this, R.drawable.actionbar_bg);

        ActionBar actionBar = getActionBar();

        // 返回箭头（默认不显示）
        actionBar.setDisplayHomeAsUpEnabled(false);
        // 左侧图标点击事件使能
        actionBar.setHomeButtonEnabled(false);
        // 使左上角图标(系统)是否显示
        actionBar.setDisplayShowHomeEnabled(false);
        // 显示标题
        actionBar.setDisplayShowTitleEnabled(false);

        actionBar.setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(this).inflate(
                R.layout.layout_common_action_bar, null);
        actionBar.setCustomView(actionbarLayout);

        final ImageButton btnBack = (ImageButton) actionbarLayout.findViewById(R.id.left_back);
        TextView textView = (TextView) actionbarLayout.findViewById(R.id.actionBar_title);

        textView.setText("信息编辑");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));



        edName = (EditText) findViewById(R.id.user_name);
        edPrivence = (EditText) findViewById(R.id.user_privence);
        edCity = (EditText) findViewById(R.id.user_city);
        radioGroup = (RadioGroup) findViewById(R.id.user_six);
        btnSummit = (Button) findViewById(R.id.btn_info_summit);
        rdMan = (RadioButton) findViewById(R.id.rdMan);
        rdWomen = (RadioButton) findViewById(R.id.rdGril);

        SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
                Context.MODE_PRIVATE);


       String name = mainPref.getString("name", "");
        String privence = mainPref.getString("privence", "");
        String city = mainPref.getString("city", "");
        String six= mainPref.getString("six", "");

        if(name!= null){
            edName.setText(name);
        }
        if(privence != null){
            edPrivence.setText(privence);
        }
        if(city != null){
            edCity.setText(city);
        }
        if(six.equals("women")){
            rdWomen.setChecked(true);
        }



        btnSummit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edName.getText().toString().trim();
                String privence = edPrivence.getText().toString().trim();
                String city = edCity.getText().toString().trim();

                String six = "woman";
                if(rdMan.isChecked()){
                    six = "man";
                }else{
                    six = "women";
                }

                SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mainPref.edit();
                editor.putString("name", name);
                editor.putString("privence", privence);
                editor.putString("city", city);
                editor.putString("six", six);
                editor.commit();

                String six1= mainPref.getString("six", "");

                Log.i("Test",six1);


                finish();

            }
        });


    }

    @Override
    protected void onResume() {
        SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
                Context.MODE_PRIVATE);
        String six= mainPref.getString("six", "");
        if(six.equals("women")){
            rdWomen.setChecked(true);
        }
//
        super.onResume();
    }
}
