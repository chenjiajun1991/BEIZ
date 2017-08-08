package com.sam.beiz.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sam.beiz.common.LinkAppSupport;

import baidumapsdk.demo.demoapplication.Login_main;
import baidumapsdk.demo.demoapplication.R;

/**
 * Created by SAM-PC2 on 2017/4/5.
 */
public class FriendsCircleFragment extends Fragment {
    private static final String TAG = "FriendsCircleFragment";
    protected View mView;
    protected Context mContext;
    private String packageWeixin = "com.tencent.mm";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        Intent intent = new Intent(mContext, Login_main.class);
        intent.putExtra("index","PushFragment");
        startActivityForResult(intent, 4);
        this.getActivity().finish();

    }
//
//    public void AlertDialogShowIsInstall(String message, final LinkAppSupport linkAppSupport, final String packageName) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle("提示")
//                .setMessage(message)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                        linkAppSupport.loadApp(mContext,packageName);
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(true);
//        Dialog dialog = builder.create();
//        dialog.show();
//    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mContext = getActivity();
//        mView = inflater.inflate(R.layout.activity_third, container, false);
//
//        String tag = getArguments().getString("tag");
////        TextView tv_third = (TextView) mView.findViewById(R.id.tv_third);
////        tv_third.setText(tv_third.getText().toString()+"\n来源："+tag);
//
//        return mView;
//    }
}
