package com.sam.beiz.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import baidumapsdk.demo.demoapplication.R;

/**
 * Created by zhejunliu on 2017/8/2.
 */

public class Dialogfragment extends android.support.v4.app.DialogFragment {
    ImageView cameraIV,galleryIV,captureIV;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
//        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
//        dialog.setContentView(R.layout.choosedialog2);
//        dialog.setCanceledOnTouchOutside(true); // 外部点击取消
//
//        // 设置宽度为屏宽, 靠近屏幕底部。
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.gravity = Gravity.CENTER; // 紧贴底部
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
//        window.setAttributes(lp);
//
//      //  ButterKnife.bind(this, dialog); // Dialog即View
//        dialog.show();
//
//        return dialog;
       // return super.onCreateDialog(savedInstanceState);


        final Dialog pickdialog = new Dialog(getActivity());
        pickdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater li = LayoutInflater.from(getActivity());
        View myView = li.inflate(R.layout.choosedialog2, null);
        pickdialog.setContentView(myView);
        Window window = pickdialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_style);
        pickdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        /*
        该位置已经可以引起其他tab无法切换，注意
         */
        // final AlertDialog dialog = pickdialog.create();
        ImageView photobtn=(ImageView)myView.findViewById(R.id.btnPhoto);
        ImageView gallerybtn=(ImageView)myView.findViewById(R.id.btnGallery);
        ImageView capturebtn=(ImageView)myView.findViewById(R.id.btnCapture);


        photobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // TODO Auto-generated method stub
                //  takephoto();
                       Intent i=new Intent(getActivity(),CameraActivity.class);
                       startActivity(i);
                pickdialog.dismiss();

            }


        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                      Intent i=new Intent(getActivity(),ImageActivity.class);
                      startActivity(i);
                pickdialog.dismiss();
            }
        });
        capturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   showFileChooser();
                      Intent i=new Intent(getActivity(),VideoActivity.class);
                      startActivity(i);
                pickdialog.dismiss();
            }
        });
        pickdialog.show();
        return  pickdialog;

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v=inflater.inflate(R.layout.activity_third,container,false);
//      //  return super.onCreateView(inflater, container, savedInstanceState);
//        return v;
//    }





}
