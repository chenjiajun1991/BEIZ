package com.sam.beiz.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sam.beiz.Ad.download.ADdownloader;
import com.sam.beiz.Config.ConfigURL;
import com.sam.beiz.OnLineService.OnLineServiceMainActivity;
import com.sam.beiz.adapter.BannerPager;
import com.sam.beiz.common.LinkAppSupport;
import com.sam.beiz.dao.BannerClickListener;
import com.utils.ScreenUtil;

import baidumapsdk.demo.demoapplication.BaiduTranceActivity;
import baidumapsdk.demo.demoapplication.Login_main;
import baidumapsdk.demo.demoapplication.PreActivity;
import baidumapsdk.demo.demoapplication.R;
import baidumapsdk.demo.demoapplication.TestNetworkAsyncTask;

import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment  implements BannerClickListener {
    private static final String TAG = "HomeFragment";
    protected View mView;
    protected Context mContext;

    private static String mUserPhone;

    private String packageWeixin = "com.tencent.mm";
    private String packageHuaJiao = "com.huajiao";

    private TextView tv_pager;
    private BannerPager mBanner;

    private BroadcastReceiver mBroadcastReceiver = null;

    private String photoUrl = "http://samyh.oss-cn-shenzhen.aliyuncs.com/test.jpg";

    public static String mSendSosUrl = null;

    private ProgressDialog dialog = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        SharedPreferences mainPref = mContext.getSharedPreferences(getString(R.string.shared_pref_pacakge),
                Context.MODE_PRIVATE);

        mUserPhone = mainPref.getString("lastAccount", "");

        mSendSosUrl =  Login_main.preUrl+"/user/sos.json";


        mView = inflater.inflate(R.layout.fragment_home, container, false);

//        tv_pager = (TextView)mView.findViewById(R.id.tv_pager);

        mBanner = (BannerPager) mView.findViewById(R.id.banner_pager1);

        LayoutParams bannerParams = (LayoutParams) mBanner.getLayoutParams();
        ArrayList<String> bannerArray = new ArrayList<>();
        ArrayList<Integer> sbannerArray = new ArrayList<>();




        if(isNetworkAvailable()){
            new ADdownloader(getActivity(), ConfigURL.cad,mBanner).execute();
        }else{
            ConfigURL.Netconnect=false;

            sbannerArray.add(Integer.valueOf(R.drawable.img1));
            sbannerArray.add(Integer.valueOf(R.drawable.img2));
            sbannerArray.add(Integer.valueOf(R.drawable.img3));
            mBanner.setImages(sbannerArray);
        }
        mBanner.setOnBannerListener(this);

        GridView gridView = (GridView) mView.findViewById(R.id.gride_view);

        gridView.setAdapter(new GrideAdapter(mContext));


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(mContext, "picture:" + position, Toast.LENGTH_LONG).show();

                if(position == 0){
                    Intent intent = new Intent(mContext,OnLineServiceMainActivity.class);
                    startActivityForResult(intent,0);
                }

                if(position == 1){
                    Intent intent = new Intent(mContext,WebViewActivity.class);
                    intent.putExtra("request","weather");
                    startActivityForResult(intent, 1);
                }

                if(position == 2){
                    SharedPreferences mainPref = mContext.getSharedPreferences(getString(R.string.shared_pref_pacakge),
                            Context.MODE_PRIVATE);
                    String imei = mainPref.getString("lastImei", "0");
                    boolean b =mainPref.getBoolean("LoggedIn", false);
                    boolean lastUser = mainPref.getBoolean("lastUser", false);


                    if(imei.equals("0") || b == false || lastUser == false){
                        Intent intent = new Intent(mContext, PreActivity.class);
                        startActivity(intent);
                    }else{

                        Intent intent = new Intent(mContext, BaiduTranceActivity.class);
                        if (imei != null) {
                            intent.putExtra("imei", imei);
                            startActivity(intent);
                        }
                    }
                }
                if(position == 3){

                    if(mUserPhone != null){
                        Bundle bundle = new Bundle();
                        bundle.putString("userPhone", mUserPhone);
                        new TestNetworkAsyncTask(mContext,
                                TestNetworkAsyncTask.TYPE_SEND_SOS_MSG,
                                bundle).execute(mSendSosUrl);
                        ensureDialog();

                    }

                }

                if(position == 4){

                    Intent intent =new Intent(mContext,CalenActivity.class);
                    intent.putExtra("parms","4");
                    startActivityForResult(intent, 4);
                }


                if(position == 5){
                    Intent intent =new Intent(mContext,HealthListActivity.class);
                    startActivityForResult(intent,5);
                }

                if(position == 6){
                    Intent intent = new Intent(mContext,WebViewActivity.class);
                    intent.putExtra("request","lifeParadise");
                    startActivityForResult(intent, 1);
                }

                if(position == 7){
                    LinkAppSupport linkAppSupport = new LinkAppSupport();
                    if(linkAppSupport.isInstallByread(packageHuaJiao)){
                        linkAppSupport.launchApp(mContext,packageHuaJiao);
                    }else{
                        AlertDialogShowIsInstall("你尚未安装花椒直播，是否安装?", linkAppSupport, packageHuaJiao);
                    }
                }
                if(position == 8){
                    Intent intent = new Intent(mContext,StoreChoiceActivity.class);
                    startActivityForResult(intent,8);
                }

            }
        });


        return mView;
    }


    @Override
    public void onResume() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new GetResuitReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(LauncherAcitivity.ACTION_POST_SOS_MSG);
            LocalBroadcastManager.getInstance(mContext)
                    .registerReceiver(mBroadcastReceiver, filter);
        }

        super.onResume();
    }

    @Override
    public void onDestroy() {

        if (mBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(mContext)
                    .unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
        super.onDestroy();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBannerClick(int position) {
//        String desc = String.format("您点击了第%d张图片", position + 1);
//        Toast.makeText(mContext,desc,Toast.LENGTH_LONG).show();
//        tv_pager.setText(desc);

        /**
         *
         * 在此调用跳转
         *
         */
        if(isNetworkAvailable()){
            SharedPreferences preferDataList = getActivity().getSharedPreferences("EnvironDataList", MODE_PRIVATE);
            String environItem = preferDataList.getString("item_"+position, null);
            Log.v("滚动广告跳转链接",environItem);
            Intent i=new Intent(getActivity(),AdWebActivity.class);
            i.putExtra("link",environItem);
            startActivity(i);
        }else
        {
            Toast.makeText(getActivity(),"您正处于离线模式",Toast.LENGTH_SHORT).show();
        }
    }

    public void AlertDialogShowIsInstall(String message, final LinkAppSupport linkAppSupport, final String packageName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        linkAppSupport.loadApp(mContext,packageName);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);
        Dialog dialog = builder.create();
        dialog.show();
    }


    class GrideAdapter extends BaseAdapter {

        private Context context;

        private Integer imgs[] = {
                R.drawable.gride1_online_service, R.drawable.gride2_weather_broadcast, R.drawable.gride3_driving_record,
                R.drawable.gride4_sos, R.drawable.gride5_life_remind, R.drawable.gride6_healther_butler,
                R.drawable.gride7_life_paradise, R.drawable.gride8_broadcast_room, R.drawable.gride9_beiz_store
        };

        public GrideAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return imgs.length;
        }

        @Override
        public Object getItem(int position) {
            return imgs[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyImageView imageView;
            if (convertView == null) {
                imageView = new MyImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(ScreenUtil.getScreenWidth(context) / 3, ScreenUtil.getScreenWidth(context) / 3));//设置ImageView对象布局
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型
                imageView.setPadding(0, 0, 0, 0);//设置间距

            } else {
                imageView = (MyImageView) convertView;
            }
            imageView.setImageResource(imgs[position]);
            int color = Color.parseColor("#28808080");
            imageView.setBorderColor(color);

            return imageView;
        }
    }

    class MyImageView extends ImageView {

        private String namespace = "http://shadow.com";
        private int color;

        public MyImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
            color = Color.parseColor(attrs.getAttributeValue(namespace, "BorderColor"));
        }

        public MyImageView(Context context) {
            super(context);
        }

        public  void setBorderColor(int color){
            this.color =color;
        }


        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub

            super.onDraw(canvas);
            //画边框
            Rect rec = canvas.getClipBounds();
            rec.bottom--;
            rec.right--;
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(rec, paint);
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

    public void AlertDialogShow(String message) {
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




    class GetResuitReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LauncherAcitivity.ACTION_POST_SOS_MSG)) {
                dismissProgressDialog();
                getSosResult(intent);
            }
        }
    }


    private void getSosResult( Intent intent){

        if(intent.getBooleanExtra("sendSosSuccess",false)){
            Toast.makeText(mContext,"发送成功！",Toast.LENGTH_LONG).show();
        }else{
            String msg = intent.getStringExtra("result");
            AlertDialogShow(msg);

        }

    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}
