package com.sam.beiz.controller;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.beiz.common.ActionBarSupport;

import java.util.List;

import baidumapsdk.demo.demoapplication.R;

public class SetSosPhoneAcivity extends ActionBarActivity {

    EditText editSosName,editSosPhone;

    Button btnSave,btnEdit;

    SharedPreferences mainPref;

    SharedPreferences.Editor editor;

    protected List myShare;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        ImageButton btnBack = (ImageButton) actionbarLayout.findViewById(R.id.left_back);
        TextView textView = (TextView) actionbarLayout.findViewById(R.id.actionBar_title);

        textView.setText("设置紧急联系人");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));


        setContentView(R.layout.activity_set_sos_phone_acivity);



        editSosName = (EditText) findViewById(R.id.sos_name);

        editSosPhone = (EditText) findViewById(R.id.sos_phone);

        btnSave = (Button) findViewById(R.id.btn_save_phone);

        btnEdit = (Button) findViewById(R.id.btnEdit_phone);

         mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
                 Context.MODE_PRIVATE);
         editor = mainPref.edit();

        String userName = mainPref.getString("sosName","");
        String phoneNumber = mainPref.getString("sosPhone","");

        if(phoneNumber !=null && phoneNumber.length() > 0){
            editSosPhone.setText(phoneNumber);
            if(userName != null && userName.length() >0){
                editSosName.setText(userName);
            }

            editSosName.setEnabled(false);
            editSosPhone.setEnabled(false);
            btnSave.setEnabled(false);

        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sosPhone = editSosPhone.getText().toString().trim();
                String sosName = editSosName.getText().toString().trim();

                if(sosPhone == null){
                    Toast.makeText(SetSosPhoneAcivity.this,"号码不能为空!",Toast.LENGTH_LONG).show();
                    return;
                }else if(sosName == null){
                    Toast.makeText(SetSosPhoneAcivity.this,"姓名不能为空!",Toast.LENGTH_LONG).show();
                    return;
                }
                else {

//                    SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
//                            Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = mainPref.edit();
                    editor.putString("sosPhone", sosPhone);
                    editor.putString("sosName", sosName);
                    editor.commit();

                }

                editSosName.setEnabled(false);
                editSosPhone.setEnabled(false);
                btnSave.setEnabled(false);

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSosName.setEnabled(true);
                editSosPhone.setEnabled(true);
                btnSave.setEnabled(true);
            }
        });





    }

//    class MyAdapt extends BaseAdapter{
//
//        Context context;
//
//        public MyAdapt(Context context){
//            this.context = context;
//        }
//
//        @Override
//        public int getCount() {
//            return myShare.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return getItem(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder;
//            if(convertView == null){
//                 viewHolder = new ViewHolder();
//                convertView = LayoutInflater.from(context).inflate(R.layout.expand_child_item,null);
//
//                viewHolder.add = (ImageView) convertView.findViewById(R.id.list_item_add);
//
//                viewHolder.delete = (ImageView) convertView.findViewById(R.id.list_item_delete);
//
//                viewHolder.item = (TextView) convertView.findViewById(R.id.expand_battery_sim);
//            }else{
//                viewHolder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
//                 }
//
////            viewHolder.item.setText(getDate().get(position).get("ItemTitle").toString());
//
//            return null;
//        }
//
//        public final class ViewHolder{
//            public ImageView add;
//            public ImageView delete;
//            public TextView item;
//        }
//    }

}
