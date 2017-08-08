package com.sam.beiz.controller;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.beiz.Ad.cache.ACache;
import com.utils.BitmapUtil;
import com.utils.ScreenUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import baidumapsdk.demo.demoapplication.AddCustomerInfo;
import baidumapsdk.demo.demoapplication.DistributorInfoList;
import baidumapsdk.demo.demoapplication.Login_main;
import baidumapsdk.demo.demoapplication.PreActivity;
import baidumapsdk.demo.demoapplication.R;
import baidumapsdk.demo.demoapplication.TestNetworkAsyncTask;
import baidumapsdk.demo.demoapplication.UserBatList;
import baidumapsdk.demo.demoapplication.UserView;

import static android.app.Activity.RESULT_OK;


/**
 * Created by SAM-PC2 on 2017/4/5.
 */
public class FifthFragment extends Fragment{
    private static final String TAG = "FifthFragment";
    protected View mView;
    protected Context mContext;
    private static String mUserPhone;

    public static String mShareUrl = null;

    Button btnState, btnShare, btnSosSet, btnExit,btnAdd;
    ImageView userIv;
    View viewTitle;

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;
    private int PICTURE_BACKGROUND = 2;

    //Bitmap to get image from gallery
    private Bitmap bitmap;
    //Uri to store the image uri
    private Uri filePath;

    private ProgressDialog dialog = null;

    private BroadcastReceiver mGetDataReceiver = null;

    SharedPreferences mainPref = null;
    SharedPreferences.Editor editor = null;

    TextView tvName,tvAdress;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        mContext = getActivity();
        mView = inflater.inflate(R.layout.layout_fragment_user, container, false);

         mainPref =  mContext.getSharedPreferences(getString(R.string.shared_pref_pacakge),
                 Context.MODE_PRIVATE);
         editor = mainPref.edit();
        editor.putString("loginView", "UserView");
        editor.putBoolean("LoggedIn", true);
        editor.commit();

        mUserPhone = mainPref.getString("lastAccount", "");

        mShareUrl = Login_main.preUrl + getString(R.string.bty_followers);

        String tag = getArguments().getString("tag");
//        TextView tv_third = (TextView) mView.findViewById(R.id.tv_third);
//        tv_third.setText(tv_third.getText().toString()+"\n来源："+tag);
        viewTitle = mView.findViewById(R.id.me_title);


        tvName = (TextView) mView.findViewById(R.id.text_name);
        tvAdress = (TextView) mView.findViewById(R.id.adress);

        String name = mainPref.getString("name", "");
        String privence = mainPref.getString("privence", "");
        String city = mainPref.getString("city", "");
        String six= mainPref.getString("six", "");

        if(name!= null){
            tvName.setText(name);
        }
        if(privence != null && city != null){
            tvAdress.setText(privence+"*"+city);
        }


        btnState = (Button) mView.findViewById(R.id.text_user_params);
        btnShare = (Button) mView.findViewById(R.id.text_user_share);
        btnSosSet = (Button) mView.findViewById(R.id.text_user_sosPhone);
        btnExit = (Button) mView.findViewById(R.id.text_user_exit);
        btnAdd = (Button) mView.findViewById(R.id.text_user_add);
        userIv=(ImageView)mView.findViewById(R.id.head_portrait);
        checkImage();


        btnState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (UserView.mUserBatList != null) {
                    Intent intent2 = new Intent(mContext,
                            DistributorInfoList.class);
                    intent2.putExtra("type", 1);
                    intent2.putExtra("UserPhone", mUserPhone);
                    startActivity(intent2);
                } else {
                    Intent intent = new Intent(mContext, Login_main.class);
                    startActivity(intent);
                }


            }
        });


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, SelfInfoAcitivity.class);
                    startActivityForResult(intent, 1);

//                if(UserView.mUserBatList != null){
//                    Bundle bundle = new Bundle();
//                    bundle.putString("userPhone", mUserPhone);
//                    new TestNetworkAsyncTask(mContext,
//                            TestNetworkAsyncTask.TYPE_BAT_FOLLOWERS,
//                            bundle).execute(mShareUrl);
//
//                    editor.putString("operation", "share");
//                    editor.commit();
//
//
//                    ensureDialog();
//                }else{
//                    Intent intent = new Intent(mContext, Login_main.class);
//                    startActivity(intent);
//                }

            }
        });

        viewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(PICTURE_BACKGROUND);
            }
        });

        userIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(PICK_IMAGE_REQUEST);
            }
        });

        btnSosSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserView.mUserBatList != null){
                    Bundle bundle = new Bundle();
                    bundle.putString("userPhone", mUserPhone);
                    new TestNetworkAsyncTask(mContext,
                            TestNetworkAsyncTask.TYPE_BAT_FOLLOWERS,
                            bundle).execute(mShareUrl);

                    editor.putString("operation", "setSos");
                    editor.commit();


                    ensureDialog();
                }else{
                    Intent intent = new Intent(mContext, Login_main.class);
                    startActivity(intent);
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,AddCustomerInfo.class);
                startActivityForResult(intent,3);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences mainPref = mContext.getSharedPreferences(getString(R.string.shared_pref_pacakge),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mainPref.edit();
//                editor.putString("loginView", "UserView");
                editor.putBoolean("LoggedIn", false);
                editor.commit();



                /*

                注销记录
                 */
                ACache aCache=ACache.get(mContext);
                aCache.put("namefromServer","");//
                aCache.put("userPHONE","");


                Intent intent =new Intent(mContext, PreActivity.class);
                startActivity(intent);

            }
        });


        if (mGetDataReceiver == null) {
            mGetDataReceiver = new GetDataMsgReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Login_main.ACTION_GET_USER_GPS_DATA);
            filter.addAction(Login_main.ACTION_GET_BATTERY_LIST);
            filter.addAction(Login_main.ACTION_GET_FRIEND_BATTERY_LIST);
            filter.addAction(Login_main.ACTION_GET_BAT_FOLLOWERS);
            filter.addAction(Login_main.ACTION_ADD_GROUP_CHILD_ITEM);
            filter.addAction(Login_main.ACTION_DEL_GROUP_CHILD_ITEM);
            filter.addAction(Login_main.ACTION_GET_APP_VERSION);
            filter.addAction(Login_main.ACTION_GET_HISTORY_GPS);
            filter.addAction(Login_main.ACTION_SHARE_BATTERY);
            LocalBroadcastManager.getInstance(mContext)
                    .registerReceiver(mGetDataReceiver, filter);
        }


        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGetDataReceiver != null) {
            LocalBroadcastManager.getInstance(mContext)
                    .unregisterReceiver(mGetDataReceiver);
            mGetDataReceiver = null;
        }

    }

    private void checkImage() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userimageurl", 0);
        String url = sharedPreferences.getString("url", "");

        String titleBgUrl = sharedPreferences.getString("title_bg_url", "");

        //String url=Config.getLocalImageUrl();
        if(url.isEmpty()==false){

            Bitmap bitmap1 = BitmapFactory.decodeFile(url);

            Bitmap bitmap2 = toRoundBitmap(bitmap1);

            userIv.setImageBitmap(bitmap2);

        }

        if(titleBgUrl.isEmpty()==false){

            Bitmap bm = BitmapFactory.decodeFile(titleBgUrl);

            Bitmap bm2 = toBGBitmap(bm,ScreenUtil.dip2px(mContext, 180f));

            BitmapDrawable bd=new BitmapDrawable(bm2);
            viewTitle.setBackgroundDrawable(bd);

        }
    }

    //method to show file chooser
    private void showFileChooser(int actionCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), actionCode);
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //  Log.v("picurl",getPath(filePath).toString());
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

                Bitmap tempBitmap = toRoundBitmap(bitmap);

                userIv.setImageBitmap(tempBitmap);

                SharedPreferences sp = getActivity().getSharedPreferences("userimageurl", 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("url", getPath(filePath).toString());
                //提交数据
                editor.commit();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (requestCode == PICTURE_BACKGROUND && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //  Log.v("picurl",getPath(filePath).toString());
                Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);

                Bitmap bm2 = toBGBitmap(bm, ScreenUtil.dip2px(mContext, 180f));

                BitmapDrawable bd=new BitmapDrawable(bm2);
                viewTitle.setBackgroundDrawable(bd);


                SharedPreferences sp = getActivity().getSharedPreferences("userimageurl", 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("title_bg_url", getPath(filePath).toString());
                //提交数据
                editor.commit();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor =getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    /**
     * 把bitmap转成圆形
     * */
    public Bitmap toRoundBitmap(Bitmap bitmap){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        int r=0;
        int l=0;
        //取最短边做边长
        if(width<height){
            r=width;
            l=height;
        }else{
            r=height;
            l=width;
        }
        //构建一个bitmap
        Bitmap backgroundBm=Bitmap.createBitmap(l,l, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas=new Canvas(backgroundBm);
        Paint p=new Paint();
        //设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);
        RectF rect=new RectF(0, 0, l, l);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, l/2,l/2, p);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return backgroundBm;
    }

    /**
     * 把bitmap转成背景
     * */
    public Bitmap toBGBitmap(Bitmap bitmap,int height){

        int bmWidth=bitmap.getWidth();
        int bmHeight = bitmap.getHeight();
        WindowManager wm = this.getActivity().getWindowManager();
        int screenWidth = wm.getDefaultDisplay().getWidth();

        float b = (float)screenWidth/(float)bmWidth;
        //按照屏幕的宽度缩放图片
        Bitmap bm = BitmapUtil.scaleImage(bitmap,screenWidth,(int)(bmHeight*b));


        //按照一定比例裁剪
        int y = 0;
        Matrix matrix = new Matrix();
        matrix.postScale(screenWidth, height);

        Log.i("Tag", "bm" + bm.getHeight());
        Log.i("Tag","height"+height);

        if(bm.getHeight()>height){
            y = (bm.getHeight() - height)/2;
            Log.i("Tag","y"+y);
            Bitmap newbm = Bitmap.createBitmap(bm, 0, y, bm.getWidth(),height, null, true);
            return newbm;
        }else{
            return bm;
        }
    }




    private void ensureDialog() {
        if (dialog == null) {
            String title = getString(R.string.process_wait_title);
            String msg = getString(R.string.process_wait_msg);

            dialog = ProgressDialog.show(mContext, null, msg, true, true);
            dialog.setIcon(android.R.drawable.ic_dialog_info);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (dialog != null) {
            dialog.hide();
            dialog.dismiss();
            dialog = null;
        }
    }

    private void AlertDialogShow(String message) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(mContext);
        builder.setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(true);
        Dialog dialog = builder.create();
        dialog.show();
    }


    class GetDataMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

          if (intent.getAction().equals(Login_main.ACTION_GET_BAT_FOLLOWERS)) {
              dismissProgressDialog();
              BatFollowersList(intent);
            }
        }
    }

    private void BatFollowersList(Intent intent) {
        if (intent.getBooleanExtra("shareBatSuccess", false)) {
            Intent intent1 = new Intent(mContext, UserBatList.class);
            List<String> list = intent.getStringArrayListExtra("BatList");
            List<String> simList = intent.getStringArrayListExtra("BatSimList");
            List<String> temp;
            intent1.putStringArrayListExtra("BatList", (ArrayList<String>)list);
            intent1.putStringArrayListExtra("BatSimList", (ArrayList<String>) simList);
            for (String batSN : list) {
                temp = intent.getStringArrayListExtra(batSN);
                intent1.putStringArrayListExtra(batSN, (ArrayList<String>)temp);
            }
            intent1.putExtra("userPhone", mUserPhone);
            startActivityForResult(intent1,0);
            return;
        }

        String msg = intent.getStringExtra("result");
        AlertDialogShow(msg);
    }

}

