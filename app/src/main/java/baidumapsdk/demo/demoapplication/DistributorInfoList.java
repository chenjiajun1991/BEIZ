package baidumapsdk.demo.demoapplication;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.beiz.common.ActionBarSupport;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class DistributorInfoList extends ActionBarActivity
        implements CompoundButton.OnCheckedChangeListener {

    private ProgressDialog dialog = null;
    private List<?> mDisList = null;
    private ListView mDisListView = null;
    private DistributorInfoAdapter mDisInfoAdapter = null;
    private DisInfoDatabaseHandler mDatabaseHandler = null;
    private int mType;
    private String mUserPhone = null;
    private static int confirmFlag=1;
    private Dialog dialog1 = null;


    private BroadcastReceiver mSetBatteryLockState = null;

    private final static String mLockUrl = Login_main.preUrl + "/user/bty/lock.json";
    private final static String mUnlockUrl = Login_main.preUrl + "/user/bty/unlock.json";

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

//        textView.setText("查看经销商");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));



        setContentView(R.layout.picker_list);

//        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabaseHandler = new DisInfoDatabaseHandler(DistributorInfoList.this);
        mType = getIntent().getIntExtra("type", 0);

        if (mType == 0) {
            List<OverlayDemo.SimpleDisInfo> tempList = OverlayDemo.mSimpleDisInfoList;
            if(tempList !=null){
                Collections.sort(tempList, new Comparents());
                mDisList = tempList;
            }else{
                mDisList = OverlayDemo.mSimpleDisInfoList;
            }
            setTitle(getString(R.string.dis_list));
        } else if (mType == 1) {
            setTitle(getString(R.string.my_bat_info));
            textView.setText(getString(R.string.my_bat_info));
            mUserPhone = getIntent().getStringExtra("UserPhone");
            mDisList = UserView.mUserBatList;
        } else if (mType == 3){
            setTitle(getString(R.string.sale_info));
            textView.setText(getString(R.string.sale_info));
            mDisList=OverlayDemo.mBtySaleInfoList;
        } else {
            setTitle(getString(R.string.friends_bat_info));
            textView.setText(getString(R.string.friends_bat_info));
            mDisList = UserView.mFriendsBatList;
        }
        mDisListView = (ListView) findViewById(android.R.id.list);
        mDisInfoAdapter = new DistributorInfoAdapter(DistributorInfoList.this,
                mDisList, mType);
        mDisListView.setAdapter(mDisInfoAdapter);

        mDisListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView itemSummary = (TextView) view.findViewById(R.id.item_summary);
                TextView textPower = (TextView) view.findViewById(R.id.text_power);
                ImageView voltageInfoList = (ImageView) view.findViewById(R.id.voltageInfoList);

                Switch switch_lock = (Switch) view.findViewById(R.id.battery_lock);

                TextView track = (TextView) view.findViewById(R.id.text_track);

//                TextView guide = (TextView) view.findViewById(R.id.text_guide);

                LinearLayout summaryItems =
                        (LinearLayout) view.findViewById(R.id.summary_items);
                TextView itemHeader = (TextView) view.findViewById(R.id.item_header);
                if (summaryItems.getVisibility() == View.GONE) {
                    itemHeader.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.dx_expander_maximized, 0, 0, 0);
                    String summary = "";
                    if (mType == 0) {
                        List<OverlayDemo.SimpleDisInfo> tempList =
                                (List<OverlayDemo.SimpleDisInfo>) mDisList;
                        DistributorInfo disInfo =
                                mDatabaseHandler.getDisInfoByPhone(tempList.get(i).phoneNumber);
                        summary = "省市：" + disInfo.resellerProvince
                                + " " + disInfo.resellerCity + "\n"
                                + "地址：" + disInfo.resellerAddress
                                + "\n" + "号码：" + disInfo.resellerPhone;
                        voltageInfoList.setVisibility(View.GONE);
                        textPower.setVisibility(View.GONE);
                        track.setVisibility(View.GONE);
//                        guide.setVisibility(View.GONE);
                    } else if (mType == 3) {
                        final List<OverlayDemo.BtySaleInfo> tempList =
                                (List<OverlayDemo.BtySaleInfo>) mDisList;
                        summary = "sim卡号：" + tempList.get(i).btySimNo + "\n"
                                + "设备序列号：" + tempList.get(i).btySn + "\n"
                                + "用户姓名：" + tempList.get(i).userName + "\n"
                                + "用户手机号码：" + tempList.get(i).userphone + "\n"
                                + "经销商姓名：" + tempList.get(i).resellerName + "\n"
                                + "经销商手机号码：" + tempList.get(i).resellerPhone + "\n"
                                + "销售时间：" + tempList.get(i).saleDate;

                        voltageInfoList.setVisibility(View.GONE);
                        textPower.setVisibility(View.GONE);
                        track.setVisibility(View.GONE);
//                        guide.setVisibility(View.GONE);

                    } else {
                        textPower.setVisibility(View.VISIBLE);
                        voltageInfoList.setVisibility(View.VISIBLE);

                        final List<UserView.UserBatInfo> tempList =
                                (List<UserView.UserBatInfo>) mDisList;

                        summary = "温度：" + "     " + tempList.get(i).temperature + " 摄氏度";

//                        if (!validateVoltage(tempList.get(i).voltage)) {
//                            textPower.setVisibility(View.GONE);
//                            voltageInfoList.setVisibility(View.GONE);
//                        }
//                        if (!validateTemperature(tempList.get(i).temperature)) {
//                            summary = "";
//                        }

                        final String imei = tempList.get(i).imei;


                       double v =  tempList.get(i).voltage;
                        int p = 1;
                        if(v<25){
                            p = 0;
                        }else if(v >= 25 && v< 26.2){
                            p = 1;
                        }else if(v >= 26.2 && v< 27.5){
                            p = 2 ;
                        }else if(v >= 27.5 && v< 28.2){
                            p = 3;
                        }else if( v >= 28.2){
                            p = 4;
                        }



                        switch (p) {
                            case 0:
                                voltageInfoList.setImageResource(R.drawable.power0);
                                break;
                            case 1:
                                voltageInfoList.setImageResource(R.drawable.power1);
                                break;
                            case 2:
                                voltageInfoList.setImageResource(R.drawable.power2);
                                break;
                            case 3:
                                voltageInfoList.setImageResource(R.drawable.power3);
                                break;
                            case 4:
                                voltageInfoList.setImageResource(R.drawable.power4);
                                break;
                            default:
                                voltageInfoList.setImageResource(R.drawable.power3);
                                break;
                        }
                        track.setVisibility(View.VISIBLE);
//                        guide.setVisibility(View.VISIBLE);
                        track.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(DistributorInfoList.this, BaiduTranceActivity.class);
                                if (imei != null) {
                                    intent.putExtra("imei", imei);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(DistributorInfoList.this, "你还没有电池", Toast.LENGTH_LONG).show();
                                    return;
                                }

                            }
                        });

//                        guide.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent(DistributorInfoList.this, GuideActivity.class);
//                                if (imei != null) {
//                                    intent.putExtra("imei", imei);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                    Toast.makeText(DistributorInfoList.this, "你还没有电池", Toast.LENGTH_LONG).show();
//                                    return;
//                                }
//
//                            }
//                        });

                    }

                    if (mType == 1) {
                        switch_lock.setVisibility(View.VISIBLE);
//                        String str = "布防" + ":";
//                        switch_lock.setText(str);
                        SharedPreferences mainPref =
                                getSharedPreferences(getString(R.string.shared_pref_pacakge),
                                        Context.MODE_PRIVATE);
                        String imei = ((UserView.UserBatInfo) mDisList.get(i)).imei;

                        boolean lockState = mainPref.getBoolean(imei, false);
                        switch_lock.setChecked(lockState);
                        switch_lock.setOnCheckedChangeListener(DistributorInfoList.this);
                    } else {
                        switch_lock.setVisibility(View.GONE);
                    }

                    itemSummary.setText(summary);
                    //itemSummary.setVisibility(View.VISIBLE);
                    summaryItems.setVisibility(View.VISIBLE);
                } else {
                    itemHeader.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.dx_expander_minimized, 0, 0, 0);
                    //itemSummary.setVisibility(View.GONE);
                    summaryItems.setVisibility(View.GONE);
                }
            }
        });

        if (mType == 1) {
            if (mSetBatteryLockState == null) {
                mSetBatteryLockState = new SetBatteryLockState();
                IntentFilter filter = new IntentFilter();
                filter.addAction(Login_main.ACTION_LOCK_BATTERY);
                LocalBroadcastManager.getInstance(DistributorInfoList.this)
                        .registerReceiver(mSetBatteryLockState, filter);
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mDisList == null || mDisList.size() == 0) {
            String msg = "未能查询到任何信息";
            AlertDialogShow(msg);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mSetBatteryLockState != null) {
            LocalBroadcastManager.getInstance(DistributorInfoList.this)
                    .unregisterReceiver(mSetBatteryLockState);
            mSetBatteryLockState = null;
        }
        super.onDestroy();
    }

    @Override
    public Intent getSupportParentActivityIntent () {
        Intent intent;
        intent = new Intent(this, OverlayDemo.class);
        return intent;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void AlertDialogShow(String message) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(DistributorInfoList.this);
        builder.setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .setCancelable(true);
        Dialog dialog = builder.create();
        dialog.show();
    }

    private boolean validateVoltage(float voltage){
        if(voltage>=41.5f&&voltage<=75f){
            return true;
        }
        return false;
    }
    private boolean validateTemperature(float temperature){
        if(temperature>=0f&&temperature<=50f){
            return true;
        }
        return false;
    }


    private void ensureDialog() {
        if (dialog == null) {
            String title = getString(R.string.process_wait_title);
            String msg = getString(R.string.process_wait_msg);

            dialog = ProgressDialog.show(DistributorInfoList.this, title, msg, true, true);
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        String imei = " ";
        ViewGroup vg = (ViewGroup) compoundButton.getParent().getParent();
        if (vg != null) {
            TextView tv = (TextView) vg.findViewById(R.id.item_header);
            if (tv != null) {
                String str2 = tv.getText().toString().trim();
                int index2 = str2.indexOf('：');
                imei = str2.substring(index2 + 1);
            }
        }


        Bundle bundle = new Bundle();
        bundle.putString("userPhone", mUserPhone);
        bundle.putString("btyImei", imei);
        if (checked) {
            showDialog("开启布防","当电池异常移动时，您将收到报警信息，不用时，需手动关闭该功能！");
//            LockDialogShow("开启布防?", "布防将开启，移动会有短信报警提示，若不用此功能，请手动关闭它");
                bundle.putString("operation", "lock");
                new TestNetworkAsyncTask(DistributorInfoList.this,
                        TestNetworkAsyncTask.TYPE_LOCK_BATTERY,
                        bundle).execute(mLockUrl);

        } else {
            showDialog("关闭布防","当电池异常移动时，您将不会收到报警信息，若还需要报警功能，请保持开启！");
//            LockDialogShow("关闭布防?", "布防将关闭，你的电池移动中将不会收到报警信息，若你还需要报警功能，请保持开启");
                bundle.putString("operation", "unlock");
                new TestNetworkAsyncTask(DistributorInfoList.this,
                        TestNetworkAsyncTask.TYPE_LOCK_BATTERY,
                        bundle).execute(mUnlockUrl);
        }
    }

    public  void LockDialogShow(final String title,String message) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(DistributorInfoList.this);
        builder.setTitle(title)
                .setMessage(message)

                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
//                        confirmFlag=3;
                    }
                })
                .setCancelable(true);
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void showDialog(String title,String content){

        dialog1 = new Dialog(this, R.style.TANCStyle);
        dialog1.setContentView(R.layout.dialog);
        TextView dialogTitle= (TextView) dialog1.findViewById(R.id.dialog_title);
        TextView dialogContent= (TextView) dialog1.findViewById(R.id.dialog_content);
        dialogTitle.setText(title);
        dialogContent.setText(content);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(true);
        dialog1.show();
        Button btn = (Button) dialog1.findViewById(R.id.dialog_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });
    }

    class SetBatteryLockState extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Login_main.ACTION_LOCK_BATTERY)) {
                if (intent.getBooleanExtra("setLockState", false)) {
                    SharedPreferences mainPref =
                            getSharedPreferences(getString(R.string.shared_pref_pacakge),
                                    Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mainPref.edit();
                    if (intent.getStringExtra("resCode").equals("10002")) {
                        editor.putBoolean(intent.getStringExtra("imei"), true);
                    } else {
                        if (intent.getStringExtra("operation").equals("lock")) {
                            editor.putBoolean(intent.getStringExtra("imei"), true);
                        } else {
                            editor.putBoolean(intent.getStringExtra("imei"), false);
                        }
                    }
                    editor.commit();
                } else {
                    String message = "IMEI: " + intent.getStringExtra("imei") + "\n\n"
                            + intent.getStringExtra("result");
                    AlertDialogShow(message);
                }
            }
        }
    }


    public class Comparents implements Comparator<OverlayDemo.SimpleDisInfo> {
        @Override
        public int compare(OverlayDemo.SimpleDisInfo arg0, OverlayDemo.SimpleDisInfo arg1) {
            String one = arg0.name;
            String two = arg1.name;
            Collator ca = Collator.getInstance(Locale.CHINA);
            int flags = 0;
            if (ca.compare(one,two) < 0) {
                flags = -1;
            }
            else if(ca.compare(one,two) > 0) {
                flags = 1;
            }
            else {
                flags = 0;
            }
            return flags;
        }
    }
}
