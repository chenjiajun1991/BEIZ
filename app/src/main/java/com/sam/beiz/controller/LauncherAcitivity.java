package com.sam.beiz.controller;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.sam.beiz.Ad.MyDialog;
import com.sam.beiz.common.ActionBarSupport;
import com.sam.beiz.common.LinkAppSupport;

import baidumapsdk.demo.demoapplication.DistributorView;
import baidumapsdk.demo.demoapplication.Login_main;
import baidumapsdk.demo.demoapplication.OverlayDemo;
import baidumapsdk.demo.demoapplication.PreActivity;
import baidumapsdk.demo.demoapplication.ProgressReceiver;
import baidumapsdk.demo.demoapplication.R;
import baidumapsdk.demo.demoapplication.TestNetworkAsyncTask;
import baidumapsdk.demo.demoapplication.UpdateApp;
import baidumapsdk.demo.demoapplication.UserView;

public class LauncherAcitivity extends ActionBarActivity {

    public static String mVersionName = "0.0.1";

    public static String preUrl = null;
    public static String updateEndpoint = null;

    public static final String ACTION_POST_SOS_MSG = "com.sam.action.POST_SOS_MSG";
    public static final String ACTION_SUMMIT_SUGGESTION_MSG = "com.sam.action.SUMMIT_SUGGESTION_MSG";

    private String packageWeixin = "com.tencent.mm";

    private static final String TAG = "TestFragmentActivity";
    private Bundle mBundle = new Bundle();
    private FragmentTabHost mTabHost;
    private Bundle mHomeBundle = new Bundle();
    public static String updateUrl=null;

    private int mUpdateCode = 0x02;
    private BroadcastReceiver mReceiver = null;

    private String downloadUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.sam.beiz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(
                    this.getPackageName(), 0);
            mVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

       preUrl = getString(R.string.pre_url);
        updateEndpoint = getString(R.string.open_ver);

        Login_main.preUrl = getString(R.string.pre_url);


   //     startService(new Intent(this, Myservice.class));
//        initWindow();
        ActionBarSupport actionBarSupport = new ActionBarSupport();
        actionBarSupport.initActionBar(this,R.drawable.actionbar_bg);

        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            actionBar.hide();  //隐藏ActionBar
////            actionBar.show();  //显示ActionBar
//        }

        // 返回箭头（默认不显示）
        actionBar.setDisplayHomeAsUpEnabled(false);
        // 左侧图标点击事件使能
        actionBar.setHomeButtonEnabled(true);
        // 使左上角图标(系统)是否显示
        actionBar.setDisplayShowHomeEnabled(false);
        // 显示标题
        actionBar.setDisplayShowTitleEnabled(false);


        actionBar.setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(this).inflate(
                R.layout.actionbar_layout, null);
        actionBar.setCustomView(actionbarLayout);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));

        ImageButton btnAdd = ( ImageButton) actionbarLayout.findViewById(R.id.right_imbt);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copy(downloadUrl, LauncherAcitivity.this);

                String msg = "已为你复制下载链" + downloadUrl + "到剪切板,前往微信粘贴发送给好友";

                AlertDialogShowShareWeixin(msg);
            }
        });



        setContentView(R.layout.activity_launcher_fragment);

        updateUrl = preUrl + updateEndpoint;

        mBundle.putString("tag", TAG);
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);


        //addTab(标题，跳转的Fragment，传递参数的Bundle)
        mTabHost.addTab(getTabView(R.string.menu_home, R.drawable.tabbar_home_selector), HomeFragment.class, mHomeBundle);
        mTabHost.addTab(getTabView(R.string.menu_loc, R.drawable.tabbar_loc_selector), SecondFragment.class, mBundle);
      //  mTabHost.addTab(getTabView(R.string.menu_publish, R.drawable.tabbar_publish_selector), Dialogfragment.class, mBundle);
        mTabHost.addTab(getTabView(R.string.menu_publish, R.drawable.tabbar_publish_selector), MyDialog.class, mBundle);
        mTabHost.addTab(getTabView(R.string.menu_friends, R.drawable.tabbar_friends_selector), FriendsCircleFragment.class, mBundle);
        mTabHost.addTab(getTabView(R.string.menu_me, R.drawable.tabbar_me_selector), FifthFragment.class, mBundle);
        //设置tabs之间的分隔线不显示
        mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);

/*
使用DialogFragment使用下述代码   对应于Dialogfragment.javca
 */

//mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//    @Override
//    public void onTabChanged(String s) {
//        if(mTabHost.getCurrentTab()==2){
//            Dialogfragment bottomDialogFragment = (Dialogfragment) Fragment.instantiate(LauncherAcitivity.this, MyDialog.class.getName());
//            getSupportFragmentManager().beginTransaction().add(bottomDialogFragment, "bottomDialogFragment").commitAllowingStateLoss();
//        }
//    }
//});




        if (mReceiver == null) {
            mReceiver = new GetDataMsgReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Login_main.ACTION_GET_APP_VERSION);
            LocalBroadcastManager.getInstance(LauncherAcitivity.this)
                    .registerReceiver(mReceiver, filter);
        }

        String versionName = mVersionName;

        Log.i("version",mVersionName);


            //检查更新
            new TestNetworkAsyncTask(LauncherAcitivity.this,
                    TestNetworkAsyncTask.TYPE_GET_APP_VERSION,
                    null).execute(updateUrl);

    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onDestroy() {

        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(LauncherAcitivity.this)
                    .unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
    }

    private TabHost.TabSpec getTabView(int textId, int imgId) {
        String text = getResources().getString(textId);
        Drawable drawable = getResources().getDrawable(imgId);
        //必须设置图片大小，否则不显示
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        View tabbar_item = getLayoutInflater().inflate(R.layout.tabbar_item, null);
        TextView tv_item = (TextView) tabbar_item.findViewById(R.id.tv_item);
        tv_item.setText(text);
        tv_item.setCompoundDrawables(null, drawable, null, null);
        TabHost.TabSpec spec = mTabHost.newTabSpec(text).setIndicator(tabbar_item);
        return spec;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            this.finish();
        }
        return true;
    }


    class GetDataMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Login_main.ACTION_GET_APP_VERSION)) {
                processAppUpdate(intent);
            }
        }

    }

    public void processAppUpdate(Intent intent) {
        if (intent.getBooleanExtra("getAppVersion", false)) {
            String apkVersion = intent.getStringExtra("apkVersion");
            String downloadUrl = intent.getStringExtra("downloadUrl");
            String updateMsg = "最新版本：V" + apkVersion + " "
                    + "已经可用，为保证此应用程序以后能正常工作，请您进行下载更新。";
            Intent intent4 = new Intent(LauncherAcitivity.this, UpdateApp.class);
            intent4.putExtra("apkUrl", downloadUrl);
            AlertUpdateDialogShow(updateMsg, intent4);
        }
    }


    public void AlertUpdateDialogShow(String message, final Intent intent) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(LauncherAcitivity.this);
        builder.setTitle("提示")
                .setMessage(message)
                .setPositiveButton(getString(R.string.update_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                AlertDialogShow(getString(R.string.update_confirm), intent);
                            }
                        })
                .setNegativeButton(getString(R.string.non_update_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .setCancelable(true);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void AlertDialogShow(String message, final Intent intent) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(LauncherAcitivity.this);
        builder.setTitle("提示")
                .setMessage(message)
                .setPositiveButton(getString(R.string.positive_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivityForResult(intent, mUpdateCode);
                                finish();
                                dialogInterface.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.negative_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .setCancelable(true);
        Dialog dialog = builder.create();
        dialog.show();
    }



    /**
     * 实现文本复制功能
     * add by wangqianzhou
     * @param content
     */
    public static void copy(String content, Context context)
    {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }
    /**
     * 实现粘贴功能
     * add by wangqianzhou
     * @param context
     * @return
     */
    public static String paste(Context context)
    {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

        public void AlertDialogShowIsInstall(String message, final LinkAppSupport linkAppSupport, final String packageName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LauncherAcitivity.this);
        builder.setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        linkAppSupport.loadApp(LauncherAcitivity.this,packageName);
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


    public void AlertDialogShowShareWeixin(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LauncherAcitivity.this);
        builder.setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        LinkAppSupport linkAppSupport = new LinkAppSupport();
                        if(linkAppSupport.isInstallByread(packageWeixin)){
                            linkAppSupport.launchApp(LauncherAcitivity.this, packageWeixin);
                        }else{
                            AlertDialogShowIsInstall("你尚未安装微信，是否安装?", linkAppSupport, packageWeixin);
                        }
                        dialogInterface.dismiss();
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


}
