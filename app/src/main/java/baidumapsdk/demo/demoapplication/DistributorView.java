package baidumapsdk.demo.demoapplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
//import android.support.v4.view.MenuItemCompat;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.ShareActionProvider;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//import android.app.ActionBar;
import android.support.v7.app.ActionBar;

import org.json.*;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.sam.beiz.common.ActionBarSupport;
import com.sam.beiz.controller.LauncherAcitivity;

/**
 * 演示覆盖物的用法
 */
public class DistributorView extends ActionBarActivity {

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;
    private CoordinateConverter coorConverter;
    private BroadcastReceiver mGetGPSDataReceiver = null;

    private static String mUserPhone;

    public static String mUrl = null;
    private String mDisLocUrl = null;
    public static String updateUrl=null;

    private final String Tag = "Total";


    private Spinner mProvince;
    private Spinner mCity;
    private ArrayAdapter<String> provinceAdapter = null;
    private ArrayAdapter<String> cityAdapter = null;

    public static LatLng mCurrLoc = null;
    public static float mCurrZoom = 6.0f;
    private int mDotColor = 0xFFFF0000;

    public static List<UserLocInfo> mUserInfoList = null;
    public static boolean mGetUserInfoDone = false;
    public static int mUserTotalCount = 0;
    public static int mCurUserCount = 0;
    public static int mPageOffset = 1;
    private static int mPreUserOffset = 0;
    public final int mPageSize = 5;
    private static boolean mFirstGetUser = true;
    private static boolean mGetTotalSuccess= false;
    private final int mDisZIndex = 1024;
    private int mItemOffset = 0;
    public static LatLng mNewBatLoc = null;
    public static ResellerInfo mResellerInfo=null;

    private static int mUserPosionCount = 0;


    private int mAboutCode = 0x01;
    private int mUpdateCode = 0x02;

    private TextView mCountText;
    private Button mSearchButton;
    private EditText mEdSearch;

    BitmapDescriptor bdDis = BitmapDescriptorFactory
            .fromResource(R.drawable.download_2);
//    BitmapDescriptor mRedPin3 = BitmapDescriptorFactory
//            .fromResource(R.drawable.test44);
    BitmapDescriptor mRedPin3 = BitmapDescriptorFactory
            .fromResource(R.drawable.download_5);

    BitmapDescriptor mPinErr = BitmapDescriptorFactory
            .fromResource(R.drawable.download_error);

    public final String[] mSettingsList = new String[]{
            "添加新用户",
//            "关于",
            "切换账户"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActionBarSupport actionBarSupport = new ActionBarSupport();
        actionBarSupport.initActionBar(this, R.drawable.actionbar_bg);

        android.app.ActionBar actionBar = getActionBar();

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
                R.layout.layout_distributor_view_bar, null);
        actionBar.setCustomView(actionbarLayout);

        ImageButton btnBack = (ImageButton) actionbarLayout.findViewById(R.id.left_back);
        TextView textView = (TextView) actionbarLayout.findViewById(R.id.actionBar_title);

        textView.setText("经销商视图");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(DistributorView.this,LauncherAcitivity.class);
                startActivity(intent);
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));













        setContentView(R.layout.oem_view3);

        mUrl = Login_main.preUrl + getString(R.string.reseller_btyinfo);
        mDisLocUrl = Login_main.preUrl + getString(R.string.reseller_site);

        //mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //getSupportActionBar().setDisplayShowHomeEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mainPref.edit();
        editor.putString("loginView", "DistributorView");
        editor.putBoolean("LoggedIn", true);
        editor.commit();
        mUserPhone = mainPref.getString("lastAccount", "");

         updateUrl = Login_main.preUrl + Login_main.updateEndpoint;
//        new TestNetworkAsyncTask(DistributorView.this,
//                TestNetworkAsyncTask.TYPE_GET_APP_VERSION,
//                null).execute(updateUrl);

        mMapView = (MapView) findViewById(R.id.bmapView);

        mCountText = (TextView) findViewById(R.id.text_count);

        mSearchButton = (Button) findViewById(R.id.button_search);

        mEdSearch = (EditText) findViewById(R.id.text_search);

        mBaiduMap = mMapView.getMap();

        if (mCurrLoc == null) {
            MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(6.0f);
            mBaiduMap.setMapStatus(msu);

            Bundle bundle = new Bundle();
            bundle.putString("resellerPhone", mUserPhone);
            new TestNetworkAsyncTask(DistributorView.this,
                    TestNetworkAsyncTask.TYPE_GET_DIS_LOC,
                    bundle).execute(mDisLocUrl);
        } else {
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(mCurrLoc, mCurrZoom);
            mBaiduMap.setMapStatus(msu);
        }
        //mBaiduMap.getUiSettings().setZoomGesturesEnabled(false);

        coorConverter = new CoordinateConverter();
        coorConverter.from(CoordinateConverter.CoordType.GPS);

        if (mUserInfoList == null) {
            mUserInfoList = new ArrayList<UserLocInfo>();
        }
        if (mUserInfoList != null && mUserInfoList.size() > 0) {
            new Thread() {
                @Override
                public void run() {
                    addAllUsers();
                }
            }.start();
        }

        if (mGetGPSDataReceiver == null) {
            mGetGPSDataReceiver = new GetGPSDataMsgReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Login_main.ACTION_DIS_GET_USER_INFO);
            filter.addAction(Login_main.ACTION_GET_DIS_LOC);
            filter.addAction(Login_main.ACTION_GET_APP_VERSION);
            LocalBroadcastManager.getInstance(DistributorView.this)
                    .registerReceiver(mGetGPSDataReceiver, filter);
        }

        //增加每个用户图标点击显示
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                String title = marker.getTitle();
                for (UserLocInfo userLocInfo : mUserInfoList) {
                    if (title.equals(userLocInfo.imei)) {
                        showMarkerInfoWindow(marker, userLocInfo);
                        return true;
                    }
                }
                if(title.equals(mResellerInfo.resellerPhone)){
                    showResellerInfo(marker,mResellerInfo);
                    return true;
                }
                return false;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mInfoWindow != null) {
                    mBaiduMap.hideInfoWindow();
                    mInfoWindow = null;
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });


        mSearchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mEdSearch.getText().toString();
                if(msg == null || msg.length() == 0){
                    Toast.makeText(DistributorView.this,"用户名或手机号不能为空!",Toast.LENGTH_LONG).show();
                }else{

                    if(mUserInfoList != null){

                        List<String> tempUserName = new ArrayList<String>();
                        List<String> tempUserPhone = new ArrayList<String>();

                        for(int i = 0; i<mUserInfoList.size();i++){
                            tempUserName.add(mUserInfoList.get(i).userName);
                            tempUserPhone.add(mUserInfoList.get(i).userPhone);

                            if(msg.equals(mUserInfoList.get(i).userName)|| msg.equals(mUserInfoList.get(i).userPhone)){

//                                Toast.makeText(DistributorView.this,mUserInfoList.get(i).lat+"|"+mUserInfoList.get(i).lon,Toast.LENGTH_LONG).show();

                                LatLng ll = new LatLng(mUserInfoList.get(i).lat, mUserInfoList.get(i).lon);
                                coorConverter.coord(ll);
                                LatLng llBaidu = coorConverter.convert();
                                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(llBaidu, 19.0f);
                                mBaiduMap.animateMapStatus(msu, 300);
                            }
                        }

                        if(tempUserName != null && tempUserPhone != null){
                            if(!tempUserName.contains(msg) && !tempUserPhone.contains(msg)){
                                Toast.makeText(DistributorView.this,"没有找到你所查询的用户的位置信息,请检查是否输入有误!",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(DistributorView.this,"没有找到你所查询的用户的位置信息",Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(DistributorView.this,"你名下还没有用户，请去添加吧!",Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        //检查更新
        new TestNetworkAsyncTask(DistributorView.this,
                TestNetworkAsyncTask.TYPE_GET_APP_VERSION,
                null).execute(updateUrl);

        mMapView.onResume();
        super.onResume();
        if (!isNetworkAvailable()) {
            AlertDialogShow(getString(R.string.network_disconnect));
            return;
        }

        //解决重新回到经销商界面不加载经销商图标的问题
        if (mCurrLoc == null) {
            MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(6.0f);
            mBaiduMap.setMapStatus(msu);

            Bundle bundle = new Bundle();
            bundle.putString("resellerPhone", mUserPhone);
            new TestNetworkAsyncTask(DistributorView.this,
                    TestNetworkAsyncTask.TYPE_GET_DIS_LOC,
                    bundle).execute(mDisLocUrl);
        } else {
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(mCurrLoc, mCurrZoom);
            mBaiduMap.setMapStatus(msu);
        }


        if ((mUserInfoList != null && mUserInfoList.size() < mUserTotalCount)
                || mFirstGetUser) {
            mFirstGetUser = false;
            Bundle bundle = new Bundle();
            bundle.putString("resellerPhone", mUserPhone);
            bundle.putInt("pageNo", mPageOffset++);
            bundle.putInt("size", mPageSize);
            new TestNetworkAsyncTask(DistributorView.this,
                    TestNetworkAsyncTask.TYPE_DIS_GET_USER_INFO,
                    bundle).execute(mUrl);
        }

        addNewBatToMap();
    }

    @Override
    protected void onDestroy() {
        if (mGetGPSDataReceiver != null) {
            LocalBroadcastManager.getInstance(DistributorView.this)
                    .unregisterReceiver(mGetGPSDataReceiver);
            mGetGPSDataReceiver = null;
        }
        mMapView.onDestroy();
        bdDis.recycle();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addAllUsers() {
        synchronized (mUserInfoList) {
            for (int i = 0; i < mUserInfoList.size(); i++) {
                OverlayOptions testP;
                UserLocInfo temp = mUserInfoList.get(i);
                LatLng lltemp = new LatLng(temp.lat, temp.lon);
                coorConverter.coord(lltemp);
                LatLng llBaidu = coorConverter.convert();
//                OverlayOptions ooD = null;
//
//                if(temp.temperature > 60 || temp.power< 1){
//                     ooD = new MarkerOptions().position(llBaidu).icon(mPinErr)
//                            .zIndex(10);
//                }else{
//                     ooD = new MarkerOptions().position(llBaidu).icon(bdDis)
//                            .zIndex(10);
//                }
                OverlayOptions  ooD = new MarkerOptions().position(llBaidu).icon(bdDis)
                        .zIndex(10);

                ((Marker)mBaiduMap.addOverlay(ooD)).setTitle(temp.imei);


//                OverlayOptions ooDot = new DotOptions().center(llBaidu).radius(6)
//                        .color(mDotColor);
//                mBaiduMap.addOverlay(ooDot);
            }
        }
    }



    private void addNewBatToMap() {
        //处理经销商登录重新进入界面不显示点的问题
        if(mCurrLoc != null){
            coorConverter.coord(mCurrLoc);
            LatLng llBaidu = coorConverter.convert();
            OverlayOptions ooD = new MarkerOptions().position(llBaidu).icon( mRedPin3)
                    .zIndex(10);
            mBaiduMap.addOverlay(ooD);
        }

        if (mNewBatLoc != null) {
            coorConverter.coord(mNewBatLoc);
            LatLng llBaidu = coorConverter.convert();
            OverlayOptions ooD = new MarkerOptions().position(llBaidu).icon(bdDis)
                    .zIndex(10);

//            OverlayOptions ooDot = new DotOptions().center(llBaidu).radius(6)
//                    .color(mDotColor);
            mBaiduMap.addOverlay(ooD);
            synchronized (mUserInfoList) {
                UserLocInfo userLocInfo = new UserLocInfo();
                userLocInfo.lat = mNewBatLoc.latitude;
                userLocInfo.lon = mNewBatLoc.longitude;
                mUserInfoList.add(userLocInfo);
            }
            mNewBatLoc = null;
        }

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
        AlertDialog.Builder builder =  new AlertDialog.Builder(DistributorView.this);
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

    public void AlertUpdateDialogShow(String message, final Intent intent) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(DistributorView.this);
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
        AlertDialog.Builder builder =  new AlertDialog.Builder(DistributorView.this);
        builder.setTitle("提示")
                .setMessage(message)
                .setPositiveButton(getString(R.string.positive_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivityForResult(intent, mUpdateCode);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_distributor, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        return intent;
    }

    private void resetStaticMembers() {
        mUserInfoList.clear();
        mUserTotalCount = 0;
        mCurUserCount = 0;
        mPageOffset = 1;
        mPreUserOffset = 0;
        mFirstGetUser = true;
        mItemOffset = 0;
        mCurrLoc = null;
        mCurrZoom = 6.0f;
    }

    //以图形的方式显示电压
    public void showMarkerInfoWindow(Marker marker, UserLocInfo userLocInfo) {
        View view=View.inflate(DistributorView.this,
                R.layout.marker_info_window2,
                null);
        TextView imeiInfo= (TextView) view.findViewById(R.id.imeiInfo);
        TextView temperatureInfo= (TextView) view.findViewById(R.id.temperatureInfo);
        ImageView voltageInfo= (ImageView) view.findViewById(R.id.voltageInfo);

        imeiInfo.setText("IMEI号：" + userLocInfo.imei+"\n"
        +userLocInfo.userName+" : "+userLocInfo.userPhone);
        temperatureInfo.setText("温度：" + userLocInfo.temperature);
        switch (userLocInfo.power){
            case 0:
                voltageInfo.setImageResource(R.drawable.power0);
                break;
            case 1:
                voltageInfo.setImageResource(R.drawable.power1);
                break;
            case 2:
                voltageInfo.setImageResource(R.drawable.power2);
                break;
            case 3:
                voltageInfo.setImageResource(R.drawable.power3);
                break;
            case 4:
                voltageInfo.setImageResource(R.drawable.power4);
                break;
            default:
                voltageInfo.setImageResource(R.drawable.power4);
                break;
        }
        LatLng ll = marker.getPosition();
        mInfoWindow =
                new InfoWindow(BitmapDescriptorFactory.fromView(view),
                        ll, -80, null);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    public void showResellerInfo(Marker marker, ResellerInfo reseller){
        View view=View.inflate(DistributorView.this,
                R.layout.marker_info_window3,
                null);
        TextView tvResellerInfo= (TextView) view.findViewById(R.id.resellerInfo);
        if(mResellerInfo!=null){
            tvResellerInfo.setText("经销商:" + "   " + reseller.resellerName + "\n"
                    + "地址:" + "   " + reseller.cityName + reseller.officeAddress + "\n"
                    + "手机:" + "   " + reseller.resellerPhone);
        }
        LatLng ll = marker.getPosition();
        mInfoWindow =
                new InfoWindow(BitmapDescriptorFactory.fromView(view),
                        ll, -80, null);
        mBaiduMap.showInfoWindow(mInfoWindow);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_dis_info:
                View view = findViewById(R.id.action_dis_info);
                PopupMenu popupMenu = new PopupMenu(DistributorView.this, view);
                for (int i =0; i < mSettingsList.length; i++) {
                    popupMenu.getMenu().add(0, i, i, mSettingsList[i]);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Bundle bundle = new Bundle();
                        switch (menuItem.getItemId()) {
                            case 0:
                                Intent intent = new Intent(DistributorView.this, AddCustomerInfo.class);
                                startActivityForResult(intent,0);
                                break;
//                            case 1:
//                                Intent intent3 = new Intent(DistributorView.this,
//                                        AboutThisApp.class);
//                                startActivityForResult(intent3, mAboutCode);
//                                break;
                            case 1:
                                MapStatus ms = mBaiduMap.getMapStatus();
                                SharedPreferences mainPref =
                                        getSharedPreferences(getString(R.string.shared_pref_pacakge),
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mainPref.edit();
                                editor.putBoolean("LoggedIn", false);
                                editor.putFloat("dis_zoom", ms.zoom);
                                editor.putFloat("dis_lat", (float)ms.target.latitude);
                                editor.putFloat("dis_lon", (float) ms.target.longitude);
                                editor.commit();

                                resetStaticMembers();
                                Intent intent2 = new Intent(DistributorView.this, Login_main.class);
                                startActivity(intent2);
                                finish();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public Intent getSupportParentActivityIntent () {
        Intent intent;
        intent = new Intent(DistributorView.this, Login_main.class);
        MapStatus ms = mBaiduMap.getMapStatus();
        mCurrLoc = ms.target;
        mCurrZoom = ms.zoom;
        return intent;
    }

    @Override
    public void onBackPressed() {
        MapStatus ms = mBaiduMap.getMapStatus();
        mCurrLoc = ms.target;
        mCurrZoom = ms.zoom;
        super.onBackPressed();
    }

    class GetGPSDataMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Login_main.ACTION_DIS_GET_USER_INFO)) {
                if (intent.getBooleanExtra("getUserInfoSuccess", false)) {
                    mGetTotalSuccess = true;
					for (int i = mPreUserOffset; i < mUserInfoList.size(); i++) {
						OverlayOptions testP;
						UserLocInfo temp = mUserInfoList.get(i);
						LatLng lltemp = new LatLng(temp.lat,temp.lon);

                        coorConverter.coord(lltemp);
                        LatLng llBaidu = coorConverter.convert();
//                        OverlayOptions ooDot = new DotOptions().center(llBaidu).radius(6)
//                                .color(mDotColor);

//                        OverlayOptions ooD = null;
//
//                        if(temp.temperature > 60 || temp.power< 1){
//                            ooD = new MarkerOptions().position(llBaidu).icon(mPinErr)
//                                    .zIndex(10);
//                        }else{
//                            ooD = new MarkerOptions().position(llBaidu).icon(bdDis)
//                                    .zIndex(10);
//                        }

                        OverlayOptions ooD = new MarkerOptions().position(llBaidu).icon(bdDis)
                                .zIndex(10);

//                        mBaiduMap.addOverlay(ooD);
                    ((Marker)mBaiduMap.addOverlay(ooD)).setTitle(temp.imei);
					}
                    mPreUserOffset = mUserInfoList.size();

                    if (intent.getIntExtra("userCount", 0) == mPageSize) {
                        Bundle bundle = new Bundle();
                        bundle.putString("resellerPhone", mUserPhone);
                        bundle.putInt("pageNo", mPageOffset++);
                        bundle.putInt("size", mPageSize);
                        new TestNetworkAsyncTask(DistributorView.this,
                                TestNetworkAsyncTask.TYPE_DIS_GET_USER_INFO, bundle)
                                .execute(mUrl);
                        mCountText.setText("总计:" + mUserTotalCount);
                        mCountText.invalidate();
                    } else {
                        mUserTotalCount = mCurUserCount;
                        mPageOffset = mUserTotalCount / mPageSize + 1;
                        mCountText.setText("总计:"+mUserTotalCount);
                        mCountText.invalidate();
                    }
                } else {
                    String msg = intent.getStringExtra("result");
                    AlertDialogShow(msg);
                }
            } else if (intent.getAction().equals(Login_main.ACTION_GET_DIS_LOC)) {
                if (intent.getBooleanExtra("getDisLoc", false)) {
                    Double lat = Double.parseDouble(intent.getStringExtra("latitude"));
                    Double lon = Double.parseDouble(intent.getStringExtra("longitude"));
                      mResellerInfo=new ResellerInfo();
                      mResellerInfo.latitude=Double.parseDouble(intent.getStringExtra("latitude"));
                      mResellerInfo.longitude=Double.parseDouble(intent.getStringExtra("longitude"));
                      mResellerInfo.resellerName=intent.getStringExtra("resellerName");
                      mResellerInfo.resellerPhone=intent.getStringExtra("resellerPhone");
                      mResellerInfo.cityName=intent.getStringExtra("cityName");
                      mResellerInfo.officeAddress=intent.getStringExtra("officeAddress");

                    LatLng ll = new LatLng(lat, lon);
//                    coorConverter.coord(ll);
//                    LatLng llBaidu = coorConverter.convert();
                    //经销商得到的位置信息为百度坐标信息，不需要经过转换
                    OverlayOptions ooD = new MarkerOptions().position(ll).icon(mRedPin3)
                            .zIndex(10);
                    ((Marker)mBaiduMap.addOverlay(ooD)).setTitle(mResellerInfo.resellerPhone);

                    MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(ll, 13.0f);
                    mBaiduMap.animateMapStatus(msu, 300);
                } else {
                    String msg = intent.getStringExtra("result");
                    AlertDialogShow(msg);
                }
            }  else if (intent.getAction().equals(Login_main.ACTION_GET_APP_VERSION)) {
                processAppUpdate(intent);
            }
        }

        public void processAppUpdate(Intent intent) {
            if (intent.getBooleanExtra("getAppVersion", false)) {
                String apkVersion = intent.getStringExtra("apkVersion");
                String downloadUrl = intent.getStringExtra("downloadUrl");
                String updateMsg = "最新版本：V" + apkVersion + " "
                        + "已经可用，为保证此应用程序以后能正常工作，请您进行下载更新。";
                Intent intent4 = new Intent(DistributorView.this, UpdateApp.class);
                intent4.putExtra("apkUrl", downloadUrl);
                AlertUpdateDialogShow(updateMsg, intent4);
            }
        }
    }

    public static class UserLocInfo {
        UserLocInfo() {}
        double lat;
        double lon;
        public float temperature;
        public String imei;
        public int power;
        public String userName;
        public String userPhone;
    }
    public static class ResellerInfo  {
        double latitude;
        double longitude;
        String officeAddress;
        String resellerName;
        String resellerPhone;
        String cityName;

    }

}
