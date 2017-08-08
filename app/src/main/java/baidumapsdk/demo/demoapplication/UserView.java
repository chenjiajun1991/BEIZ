package baidumapsdk.demo.demoapplication;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.sam.beiz.common.ActionBarSupport;
import com.sam.beiz.common.RemainingMileage;
import com.sam.beiz.controller.LauncherAcitivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserView extends ActionBarActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private BroadcastReceiver mGetGPSDataReceiver = null;
    private PendingIntent mPendingIntent;
    private AlarmManager mAlarmManager;
    private CoordinateConverter coorConverter;
    private Animation mAnimFadeIn;
    private Animation mAnimFadeOut;
    private TextView mAlertInfo;
    private ArrayList<String> mIMEIList;
    private int mShortPeriod = 3000;
    private int mLongPeriod = 60000;
    private static String mUserPhone;

    private RadioButton mapNormal,mapWeixing;

    public static String mUrl = null;
    public static String mGetBatListUrl = null;
    public static String mShareBatteryUrl = null;
    public static String mUnShareBatteryUrl = null;
    public static String mFollowerUrl = null;
    public static String mRealtimeLocUrl = null;
    public static String updateUrl=null;
    public static String mHistoryGps=null;
    private static String mNearbyUrl = null;

    public static String jsonStr = null;
    public static String jsonBatListStr = null;
    public static String jsonHistoryGps=null;
    public static String jsonNearbyStr = null;

    public LatLng mCurrLoc = null;
    public float mCurrZoom;
    public boolean mRecover = false;
    private boolean mFirstEnterIn = true;
    private boolean mShowWarnDialog = true;


    public static int viewIndex = 0;



    private LocationManager locationManager;
    private static double lat = 0;
    private static double lon = 0;


    private RadioGroup radioGroup,mRadioGroupLoc;

    public static List<UserBatInfo> mUserBatList = null;
    public static List<UserBatInfo> mFriendsBatList = null;
    public static List<NearbyInfo> mNearBatList = null;
    private DistributorInfoAdapter mBatInfoAdapter = null;
    private InfoWindow mInfoWindow = null;

    private ProgressDialog dialog = null;

    private int mAboutCode = 0x01;
    private int mUpdateCode = 0x02;

    private static String headUrl = null;
    BitmapDescriptor mHead = null;

    private ArrayList<BitmapDescriptor> giflist;

    public final String[] mSettingsList = new String[]{
            "我的轮椅状态",
            "关注人状态",
            "我的信息分享",
            "轨迹回放",
            "切换账户"
    };

    MarkerOptions startMarker=null;
    MarkerOptions endMarker=null;
    BitmapDescriptor bmStart=null;
    BitmapDescriptor bmEnd=null;
    PolylineOptions polyline=null;



    BitmapDescriptor mRedPin = BitmapDescriptorFactory
            .fromResource(R.drawable.red_pin);



//    BitmapDescriptor mRedPin = BitmapDescriptorFactory
//        .fromResource(R.drawable.test11);
//    BitmapDescriptor mRedPin1 = BitmapDescriptorFactory
//            .fromResource(R.drawable.test22);
//    BitmapDescriptor mRedPin2 = BitmapDescriptorFactory
//            .fromResource(R.drawable.test33);
//    BitmapDescriptor mRedPin3 = BitmapDescriptorFactory
//            .fromResource(R.drawable.test44);

    public static double relLat=0;
    public static double relLon=0;


    private Runnable mDismissAlert = new Runnable(){
        public void run(){
            if (mAlertInfo != null) {
                mAlertInfo.setVisibility(View.INVISIBLE);
                mAlertInfo.startAnimation(mAnimFadeOut);
            }
        }
    };

    private Runnable mDismissProgressDialogTsk = new Runnable(){
        public void run(){
            dismissProgressDialog();
        }
    };

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
                R.layout.layout_user_view_bar, null);
        actionBar.setCustomView(actionbarLayout);

        mRadioGroupLoc = (RadioGroup) actionbarLayout.findViewById(R.id.user_view_group);





//        ImageButton btnBack = (ImageButton) actionbarLayout.findViewById(R.id.left_back);
//        TextView textView = (TextView) actionbarLayout.findViewById(R.id.actionBar_title);
//
//        textView.setText("用户视图");
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UserView.this, LauncherAcitivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

//        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.user_view_action_bar_bg));

        setContentView(R.layout.oem_view);


        mIMEIList = new ArrayList<String>();
        mAlertInfo = (TextView) findViewById(R.id.toastInfo);
        mAnimFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        mAnimFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);

        mUrl = Login_main.preUrl + getString(R.string.btyinfo);
        mGetBatListUrl = Login_main.preUrl + getString(R.string.btys);
        mShareBatteryUrl = Login_main.preUrl + getString(R.string.bty_share);
        mUnShareBatteryUrl = Login_main.preUrl + getString(R.string.bty_unshare);
        mFollowerUrl = Login_main.preUrl + getString(R.string.bty_followers);
        mRealtimeLocUrl = Login_main.preUrl + getString(R.string.realtime_loction);
        mHistoryGps=Login_main.preUrl+"/bty/latlngs.json";
        updateUrl = Login_main.preUrl + Login_main.updateEndpoint;
        mNearbyUrl =  Login_main.preUrl + "/user/nearby.json";

        //mUserPhone = getIntent().getStringExtra("userPhone");

        mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //getSupportActionBar().setDisplayShowHomeEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mainPref.edit();
        editor.putString("loginView", "UserView");
        editor.putBoolean("LoggedIn", true);
        editor.commit();

        mUserPhone = mainPref.getString("lastAccount", "");


        SharedPreferences sharedPreferences = getSharedPreferences("userimageurl", 0);
        headUrl = sharedPreferences.getString("url", "");

        String name = mainPref.getString("name", "");
        String six= mainPref.getString("six", "");


        if(headUrl != null && headUrl.length() >0){

            Bitmap bitmap = BitmapFactory.decodeFile(headUrl);
            Bitmap bitmapTemp = toRoundBitmap(bitmap,name,six);

            mHead= BitmapDescriptorFactory.fromBitmap(bitmapTemp);
        }



        //通过getSystemService接口获取LocationManager实例
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        //判断是否有GPS
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //判断是否有网络定位
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(gps){
            // 注册监听器 locationListener
            //第 2 、 3个参数可以控制接收GPS消息的频度以节省电力。第 2个参数为毫秒， 表示调用 listener的周期，第 3个参数为米 ,表示位置移动指定距离后就调用 listener
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10*1000, 0, locationListener);
        }else{
            if(network){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10*1000, 0, locationListener);
            }else {
                Toast.makeText(UserView.this,"请开启网络!",Toast.LENGTH_SHORT).show();
            }
        }






        coorConverter = new CoordinateConverter();
        coorConverter.from(CoordinateConverter.CoordType.GPS);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(6.0f);
        mBaiduMap.setMapStatus(msu);


        mapNormal = (RadioButton) findViewById(R.id.radio_map1);

        mapWeixing = (RadioButton) findViewById(R.id.radio_weixin1);

        mapNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

            }
        });

        mapWeixing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //卫星地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            }
        });


//        giflist = new ArrayList<BitmapDescriptor>();
//
//        giflist.add(mRedPin);
//        giflist.add(mRedPin1);
//        giflist.add(mRedPin2);
//        giflist.add(mRedPin3);



        //mBaiduMap.getUiSettings().setZoomGesturesEnabled(false);
        if (mUserBatList == null) {
            mUserBatList = new ArrayList<UserBatInfo>();
        }

        if (mFriendsBatList == null) {
            mFriendsBatList = new ArrayList<UserBatInfo>();
        }

//        new TestNetworkAsyncTask(UserView.this,
//                TestNetworkAsyncTask.TYPE_GET_APP_VERSION,
//                null).execute(updateUrl);

        Bundle bundle = new Bundle();
        bundle.putString("phoneNum", mUserPhone);
        bundle.putString("userType", "user");
        new TestNetworkAsyncTask(UserView.this,
                TestNetworkAsyncTask.TYPE_TEST,
                bundle).execute(mUrl);

        Bundle bundle1 = new Bundle();
        bundle1.putString("mobilePhone", mUserPhone);
        new TestNetworkAsyncTask(UserView.this,
                TestNetworkAsyncTask.TYPE_FETCH_NEARbY,
                bundle1).execute(mNearbyUrl);


        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                String title = marker.getTitle();
                if(title == null || title.length() == 0 || title.equals("0000")){
                    return false;
                }

                for (UserBatInfo userInfo : mUserBatList) {
                    if (title.equals(userInfo.imei)) {
                        showMarkerInfoWindow(marker, userInfo);
                        return true;
                    }
                }

                for (UserBatInfo userInfo : mFriendsBatList) {
                    if (title.equals(userInfo.imei)) {
                        showMarkerInfoWindow(marker, userInfo);
                        return true;
                    }
                }
                for(NearbyInfo nearbyInfo : mNearBatList){
                    if(title.equals(nearbyInfo.nearName)){
                        showMarkerInfoWindow1(marker,nearbyInfo);
                        return true;
                    }
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

        //切换图标
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                MapStatus ms = mBaiduMap.getMapStatus();
                mCurrLoc = ms.target;
                mCurrZoom = ms.zoom;
                mBaiduMap.clear();
                for (UserBatInfo userBatInfo : mUserBatList) {
                    addBatToMap(userBatInfo);
                }

                for (UserBatInfo userBatInfo : mFriendsBatList) {
                    addBatToMap(userBatInfo);
                }

                if(viewIndex == 1){
                    if(mNearBatList != null){
                        for(NearbyInfo nearbyInfo : mNearBatList){
                            addNearBatToMap(nearbyInfo);
                        }
                    }
                }

                if(relLat ==0 && relLon == 0){
                    if(lat!=0 && lon != 0){
                        LatLng lltemp3 = new LatLng(lat, lon);
                        coorConverter.coord(lltemp3);
                        LatLng llBaidu2 = coorConverter.convert();
                        OverlayOptions ooD;

                        if(mHead != null){
                            ooD = new MarkerOptions().position(llBaidu2).icon(mHead)
                                    .zIndex(10);
                        }else{
                            ooD = new MarkerOptions().position(llBaidu2).icon(mRedPin)
                                    .zIndex(10);
                        }

                        ((Marker)mBaiduMap.addOverlay(ooD)).setTitle("0000");


                    }
                }
            }
        });

        mRadioGroupLoc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.my_location:
                        viewIndex = 0;

                        Bundle bundle = new Bundle();
                        bundle.putString("phoneNum", mUserPhone);
                        bundle.putString("userType", "user");
                        new TestNetworkAsyncTask(UserView.this,
                                TestNetworkAsyncTask.TYPE_TEST,
                                bundle).execute(mUrl);

                        break;
                    case R.id.around_friend:

                        viewIndex = 1;

                        Bundle bundle1 = new Bundle();
                        bundle1.putString("mobilePhone", mUserPhone);
                        new TestNetworkAsyncTask(UserView.this,
                                TestNetworkAsyncTask.TYPE_FETCH_NEARbY,
                                bundle1).execute(mNearbyUrl);

                        Bundle bundle2 = new Bundle();
                        bundle2.putString("phoneNum", mUserPhone);
                        bundle2.putString("userType", "user");
                        new TestNetworkAsyncTask(UserView.this,
                                TestNetworkAsyncTask.TYPE_TEST,
                                bundle2).execute(mUrl);

                        break;
                    default:
                        break;
                }
            }
        });



//        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
//            @Override
//            public void onTouch(MotionEvent motionEvent) {
//
//                if(motionEvent.getAction() == motionEvent.ACTION_MOVE){
//                    Toast.makeText(UserView.this, "move",Toast.LENGTH_SHORT).show();
//                }
//
////                Toast.makeText(UserView.this, motionEvent.getY()+"",Toast.LENGTH_SHORT).show();
//
//
//
//            }
//        });



        if (mGetGPSDataReceiver == null) {
            mGetGPSDataReceiver = new GetGPSDataMsgReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Login_main.ACTION_GET_USER_GPS_DATA);
            filter.addAction(Login_main.ACTION_GET_BATTERY_LIST);
            filter.addAction(Login_main.ACTION_GET_FRIEND_BATTERY_LIST);
            filter.addAction(Login_main.ACTION_GET_BAT_FOLLOWERS);
            filter.addAction(Login_main.ACTION_ADD_GROUP_CHILD_ITEM);
            filter.addAction(Login_main.ACTION_DEL_GROUP_CHILD_ITEM);
            filter.addAction(Login_main.ACTION_GET_APP_VERSION);
            filter.addAction(Login_main.ACTION_GET_HISTORY_GPS);
            LocalBroadcastManager.getInstance(UserView.this)
                    .registerReceiver(mGetGPSDataReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        viewIndex = 0;
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
//        //检查更新
//        new TestNetworkAsyncTask(UserView.this,
//                TestNetworkAsyncTask.TYPE_GET_APP_VERSION,
//                null).execute(updateUrl);

        mMapView.onResume();

        Intent getData = new Intent(UserView.this, AlarmGetDataReceiver.class);
        getData.putExtra("phoneNum", mUserPhone);
        getData.putExtra("url", mUrl);
        mPendingIntent = PendingIntent.getBroadcast(UserView.this,
                0,
                getData,
                PendingIntent.FLAG_CANCEL_CURRENT);

        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60 * 1000,
                30 * 1000,
                mPendingIntent);
        //scheduleAlarm();
        super.onResume();
        readIMEIList();
    }

    @Override
    protected void onDestroy() {
        if (mGetGPSDataReceiver != null) {
            LocalBroadcastManager.getInstance(UserView.this)
                    .unregisterReceiver(mGetGPSDataReceiver);
            mGetGPSDataReceiver = null;
        }
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mShowWarnDialog = true;
        mMapView.onDestroy();
        mAlarmManager.cancel(mPendingIntent);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void readIMEIList() {
        if (mIMEIList != null) {
            SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
                    Context.MODE_PRIVATE);
            try {
                JSONArray jsonArray =
                        new JSONArray(mainPref.getString(getString(R.string.imei_list), "[]"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (!mIMEIList.contains(jsonArray.getString(i))) {
                        mIMEIList.add(jsonArray.getString(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeIMEIList() {
        if (mIMEIList != null) {
            SharedPreferences mainPref =
                    getSharedPreferences(getString(R.string.shared_pref_pacakge),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mainPref.edit();
            JSONArray jsonArray = new JSONArray(mIMEIList);
            editor.putString(getString(R.string.imei_list), jsonArray.toString());
            editor.commit();
        }
    }

    public void showMarkerInfoWindow1(Marker marker,NearbyInfo nearbyInfo) {
        TextView textView = (TextView) View.inflate(UserView.this,
                R.layout.marker_info_window,
                null);

        String info = "姓名：" + nearbyInfo.nearName + "\n" +
                "号码：" + nearbyInfo.nearPhone ;

        textView.setText(info);
        LatLng ll = marker.getPosition();
        mInfoWindow =
                new InfoWindow(BitmapDescriptorFactory.fromView(textView),
                        ll, -80, null);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

//以图形的方式显示电压
    public void showMarkerInfoWindow(Marker marker, UserBatInfo userInfo) {
        View view=View.inflate(UserView.this,
                R.layout.marker_info_window2,
                null);
        TextView imeiInfo= (TextView) view.findViewById(R.id.imeiInfo);
        TextView temperatureInfo= (TextView) view.findViewById(R.id.temperatureInfo);
        TextView textPower= (TextView) view.findViewById(R.id.textPower);
        ImageView voltageInfo= (ImageView) view.findViewById(R.id.voltageInfo);
        TextView textDate = (TextView) view.findViewById(R.id.dateInfo);

        textPower.setVisibility(View.VISIBLE);
        voltageInfo.setVisibility(View.VISIBLE);
        temperatureInfo.setVisibility(View.VISIBLE);
        textDate.setVisibility(View.VISIBLE);

//        if(!validateVoltage(userInfo.voltage)){
//            textPower.setVisibility(View.GONE);
//            voltageInfo.setVisibility(View.GONE);
//        }
//        if(!validateTemperature(userInfo.temperature)){
//            temperatureInfo.setVisibility(View.GONE);
//        }

        imeiInfo.setText("IMEI：" + userInfo.imei);
        temperatureInfo.setText("温度：" + userInfo.temperature);

        int p = 1;
        if(userInfo.voltage<25){
            p = 0;
        }else if(userInfo.voltage >= 25 && userInfo.voltage< 26.2){
            p = 1;
        }else if(userInfo.voltage >= 26.2 && userInfo.voltage< 27.5){
            p = 2 ;
        }else if(userInfo.voltage >= 27.5 && userInfo.voltage< 28.2){
            p = 3;
        }else if( userInfo.voltage >= 28.2){
            p = 4;
        }

        switch (p){
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
                voltageInfo.setImageResource(R.drawable.power1);
                break;
        }
        if(userInfo.receiveDate!=null){
            textDate.setText(userInfo.receiveDate);
        }
        LatLng ll = marker.getPosition();
        mInfoWindow =
                new InfoWindow(BitmapDescriptorFactory.fromView(view),
                        ll, -80, null);
        mBaiduMap.showInfoWindow(mInfoWindow);
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


    //图标切换
    public void addBatToMap(UserBatInfo userBatInfo) {
        LatLng lltemp2 = new LatLng(userBatInfo.lat, userBatInfo.lon);
        coorConverter.coord(lltemp2);
        LatLng llBaidu2 = coorConverter.convert();
        OverlayOptions ooD;

         if(mHead != null){
             ooD = new MarkerOptions().position(llBaidu2).icon(mHead)
                     .zIndex(10);
         }else{
              ooD = new MarkerOptions().position(llBaidu2).icon(mRedPin)
                     .zIndex(10);
         }

//        OverlayOptions ooD = new MarkerOptions().position(llBaidu2).icons(giflist)
//                .zIndex(10).period(50);

        ((Marker)mBaiduMap.addOverlay(ooD)).setTitle(userBatInfo.imei);

        if(userBatInfo.voltage != 0){

            double distance = RemainingMileage.getDistance(userBatInfo.voltage);


            OverlayOptions circleOption = new CircleOptions()
                    .center(llBaidu2)
                    .radius((int) (distance * 1000/2))
                    .fillColor(Color.parseColor("#220099ff"));

            mBaiduMap.addOverlay(circleOption);
        }
    }


    //图标切换
    public void addNearBatToMap(NearbyInfo nearbyInfo) {
        LatLng lltemp3 = new LatLng(nearbyInfo.lat, nearbyInfo.lon);
        coorConverter.coord(lltemp3);
        LatLng llBaidu3 = coorConverter.convert();
        OverlayOptions ooD = new MarkerOptions().position(llBaidu3).icon(mRedPin)
                .zIndex(10);;

        ((Marker)mBaiduMap.addOverlay(ooD)).setTitle(nearbyInfo.nearName);

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
        AlertDialog.Builder builder =  new AlertDialog.Builder(UserView.this);
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
        AlertDialog.Builder builder =  new AlertDialog.Builder(UserView.this);
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
        AlertDialog.Builder builder =  new AlertDialog.Builder(UserView.this);
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

    private void ensureDialog() {
        if (dialog == null) {
            String title = getString(R.string.process_wait_title);
            String msg = getString(R.string.process_wait_msg);

            dialog = ProgressDialog.show(UserView.this, null, msg, true, true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_customer, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void resetStaticMembers() {
        jsonBatListStr = null;
        jsonStr = null;
        mUserBatList.clear();
        mFriendsBatList.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_personal_info:
//                View view = findViewById(R.id.action_personal_info);
//                PopupMenu popupMenu = new PopupMenu(UserView.this, view);
//                for (int i =0; i < mSettingsList.length; i++) {
//                    popupMenu.getMenu().add(0, i, i, mSettingsList[i]);
//                }
//
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        Bundle bundle = new Bundle();
//                        switch (menuItem.getItemId()) {
//                            case 0:
//                                Intent intent2 = new Intent(UserView.this,
//                                        DistributorInfoList.class);
//                                intent2.putExtra("type", 1);
//                                intent2.putExtra("UserPhone", mUserPhone);
//                                startActivity(intent2);
//
//                                break;
//                            case 1:
//                                Intent intent1 = new Intent(UserView.this,
//                                        DistributorInfoList.class);
//                                intent1.putExtra("type", 2);
//                                startActivity(intent1);
//                                break;
//                            case 2:
//                                bundle.putString("userPhone", mUserPhone);
//                                new TestNetworkAsyncTask(UserView.this,
//                                        TestNetworkAsyncTask.TYPE_BAT_FOLLOWERS,
//                                        bundle).execute(mFollowerUrl);
//                                ensureDialog();
//                                break;
//
//                            case 3:
//                              SharedPreferences mainPref1 = getSharedPreferences(getString(R.string.shared_pref_pacakge),
//                               Context.MODE_PRIVATE);
//                                String imei = mainPref1.getString("lastImei","0");
//
//                                Intent intent3 = new Intent(UserView.this, BaiduTranceActivity.class);
//                                if (imei.equals("0")) {
//                                   Toast.makeText(UserView.this,"还未添加设备",Toast.LENGTH_LONG).show();
//                                }else{
//                                    intent3.putExtra("imei", imei);
//                                    startActivity(intent3);
//                                }
//
////                                Intent intent3 = new Intent(UserView.this,
////                                        AboutThisApp.class);
////                                startActivityForResult(intent3, mAboutCode);
//                                break;
//                            case 4:
//                                MapStatus ms = mBaiduMap.getMapStatus();
//                                SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
//                                        Context.MODE_PRIVATE);
//                                SharedPreferences.Editor editor = mainPref.edit();
//                                editor.putBoolean("LoggedIn", true);
//                                editor.putFloat("user_zoom", ms.zoom);
//                                editor.putFloat("user_lat", (float)ms.target.latitude);
//                                editor.putFloat("user_lon", (float)ms.target.longitude);
//                                editor.commit();
//
//                                resetStaticMembers();
//                                Intent intent = new Intent(UserView.this, Login_main.class);
//                                startActivity(intent);
//                                finish();
//                                break;
//
//
//                            default:
//                                break;
//                        }
//                        return true;
//                    }
//                });
//                popupMenu.show();
//                return true;
            case R.id.action_loc_refresh:
                Bundle bundle = new Bundle();
                bundle.putString("phoneNum", mUserPhone);
                bundle.putString("userType", "user");
                new TestNetworkAsyncTask(UserView.this,
                        TestNetworkAsyncTask.TYPE_TEST,
                        bundle).execute(mRealtimeLocUrl);

                Bundle bundle1 = new Bundle();
                bundle1.putString("mobilePhone", mUserPhone);
                new TestNetworkAsyncTask(UserView.this,
                        TestNetworkAsyncTask.TYPE_FETCH_NEARbY,
                        bundle1).execute(mNearbyUrl);


                ensureDialog();

                mAlertInfo.postDelayed(mDismissProgressDialogTsk, mShortPeriod);
            default:
                break;
        }
        return false;
    }

    @Override
    public Intent getSupportParentActivityIntent () {
        Intent intent;
        if (mRecover) {
            mBaiduMap.clear();
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(mCurrLoc, mCurrZoom);
            mBaiduMap.animateMapStatus(msu, 300);
            mRecover = false;
            intent = null;
        } else {
            intent = new Intent(UserView.this, Login_main.class);
        }
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (mRecover) {
            mBaiduMap.clear();
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(mCurrLoc, mCurrZoom);
            mBaiduMap.animateMapStatus(msu, 300);
            mRecover = false;
        } else {
            super.onBackPressed();
        }
    }

    public boolean isZero(double value){
        return value >= -0.00001 && value <= 0.00001;
    }


    class GetGPSDataMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismissProgressDialog();
            if (intent.getAction().equals(Login_main.ACTION_GET_USER_GPS_DATA)) {
                GetBatteryLocation(intent);
            } else if (intent.getAction().equals(Login_main.ACTION_GET_BATTERY_LIST)) {
                GetBatteryList(intent);
            } else if (intent.getAction().equals(Login_main.ACTION_SHARE_BATTERY)) {
                shareBatteryToFriend(intent);
            } else if (intent.getAction().equals(Login_main.ACTION_GET_BAT_FOLLOWERS)) {
                BatFollowersList(intent);
            } else if (intent.getAction().equals(Login_main.ACTION_GET_FRIEND_BATTERY_LIST)) {
                GetFriendBatteryList(intent);
            } else if (intent.getAction().equals(Login_main.ACTION_GET_APP_VERSION)) {
                processAppUpdate(intent);
            }
        }

        public void processAppUpdate(Intent intent) {
            if (intent.getBooleanExtra("getAppVersion", false)) {
                String apkVersion = intent.getStringExtra("apkVersion");
                String downloadUrl = intent.getStringExtra("downloadUrl");
                String updateMsg = "最新版本：V" + apkVersion + " "
                        + "已经可用，为保证此应用程序以后能正常工作，请您进行下载更新。";
                Intent intent4 = new Intent(UserView.this, UpdateApp.class);
                intent4.putExtra("apkUrl", downloadUrl);
                AlertUpdateDialogShow(updateMsg, intent4);
            }
        }

        public void GetBatteryLocation(Intent intent) {
            if (intent.getBooleanExtra("getUserBatSuccess", false)) {
                try {
                    MapStatus ms = mBaiduMap.getMapStatus();
                    mCurrLoc = ms.target;
                    mCurrZoom = ms.zoom;

                    boolean shouldShowAlert = false;
                    boolean showLongPeriod = false;

                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONObject jObect1 = jsonObject.getJSONObject("data");
                    JSONArray jArray1 = jObect1.getJSONArray("selfBtyInfo");
                    JSONArray jArray2 = jObect1.getJSONArray("friendsBtyInfo");
                    LatLng lltemp = null, lltemp2 = null,lltemp3 = null;
                    LatLng llBaidu=null, llBaidu2=null, llBaidu3 = null;

                    if(jsonNearbyStr != null){
                        JSONObject nearObject =  new JSONObject(jsonNearbyStr);
                        JSONObject nearObject1 = nearObject.getJSONObject("data");
                        JSONArray jNearArray = nearObject1.getJSONArray("peopleNearby");
                    }


                    mBaiduMap.clear();
//                    // test2
//                    UserView.UserBatInfo tempUser3 = new UserView.UserBatInfo();
//                    tempUser3.lat = 31.020806;
//                    tempUser3.lon = 121.462451;
//                    tempUser3.temperature = 25.3f;
//                    tempUser3.voltage = 76.9f;
//                    tempUser3.imei = "12545641234521235";
//                    lltemp3 = new LatLng(tempUser3.lat, tempUser3.lon);
//                    coorConverter.coord(lltemp3);
//                    LatLng llBaidu3 = coorConverter.convert();
//                    OverlayOptions ooD2 = new MarkerOptions().position(llBaidu3).icon(mRedPin)
//                            .zIndex(10);
//                    ((Marker)mBaiduMap.addOverlay(ooD2)).setTitle(tempUser3.imei);
//                    //

                    List<UserBatInfo> tempList1 = new ArrayList<UserBatInfo>();
                    for (int i = 0; i < jArray1.length(); i++) {
                        JSONObject temp = (JSONObject) jArray1.get(i);
                        UserBatInfo tempUser = new UserBatInfo();
                        tempUser.lat = Double.parseDouble(temp.getString("latitude"));

                        tempUser.lon = Double.parseDouble(temp.getString("longitude"));

                        tempUser.temperature = Float.parseFloat(temp.getString("temperature"));

                        tempUser.voltage = Float.parseFloat(temp.getString("voltage"));

                        tempUser.imei = temp.getString("btyImei");

                        tempUser.power=Integer.parseInt(temp.getString("power"));

                        tempUser.receiveDate = temp.getString("receiveDate");

                        tempList1.add(tempUser);

                        relLat = tempUser.lat;
                        relLon = tempUser.lon;

                    if(temp.getString("btyImei")!= null){
                        SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mainPref.edit();
                        editor.putString("lastImei", tempList1.get(0).imei);
                        editor.commit();
                    }


                        if (isZero(tempUser.lat) && isZero(tempUser.lon)) {
                            shouldShowAlert = true;
                            if (!mIMEIList.contains(tempUser.imei)) {
                                showLongPeriod = true;
                            }
                        } else if (!mIMEIList.contains(tempUser.imei)) {
                            mIMEIList.add(tempUser.imei);
                            writeIMEIList();
                        }
                        lltemp = new LatLng(tempUser.lat, tempUser.lon);
                        coorConverter.coord(lltemp);
                        llBaidu = coorConverter.convert();
                        OverlayOptions ooD;

                        if(mHead != null){
                            ooD = new MarkerOptions().position(llBaidu).icon(mHead)
                                    .zIndex(1);;
                        }else{
                            ooD = new MarkerOptions().position(llBaidu).icon(mRedPin)
                                    .zIndex(1);
                        }



//                        OverlayOptions ooD = new MarkerOptions().position(llBaidu).icons(giflist)
//                                .zIndex(1).period(50);
                        ((Marker)mBaiduMap.addOverlay(ooD)).setTitle(tempUser.imei);


                      if(temp.getString("voltage")!= null){
                          double voltage = Double.parseDouble(temp.getString("voltage"));

                          double distance = RemainingMileage.getDistance(voltage);

                          Stroke stroke = new Stroke(20,20);
                          OverlayOptions circleOption = new CircleOptions()
                                  .center(llBaidu)
                                  .radius((int) (distance * 1000/2))
                                  .stroke(stroke)
                                  .fillColor(Color.parseColor("#220099ff"));

                          mBaiduMap.addOverlay(circleOption);

                      }
                    }




                    List<NearbyInfo> tempListNear = new ArrayList<NearbyInfo>();
                    if(viewIndex == 1){

                        if(jsonNearbyStr != null){
                            JSONObject nearObject =  new JSONObject(jsonNearbyStr);
                            JSONObject nearObject1 = nearObject.getJSONObject("data");
                            JSONArray jNearArray = nearObject1.getJSONArray("peopleNearby");

                            if(jNearArray != null){
                                for(int i = 0 ;i< jNearArray.length(); i++){
                                    JSONObject temp = (JSONObject) jNearArray.get(i);
                                    NearbyInfo nearbyInfo = new NearbyInfo();

                                    nearbyInfo.lat = Double.parseDouble(temp.getString("latitude"));
                                    nearbyInfo.lon = Double.parseDouble(temp.getString("longitude"));
                                    nearbyInfo.nearName = temp.getString("userName");
                                    nearbyInfo.nearPhone = temp.getString("userPhone");
                                    nearbyInfo.nearSex = temp.getString("sex");

                                    tempListNear.add(nearbyInfo);

                                    lltemp3 = new LatLng(nearbyInfo.lat, nearbyInfo.lon);
                                    coorConverter.coord(lltemp3);
                                    llBaidu3 = coorConverter.convert();
                                    OverlayOptions ooD = new MarkerOptions().position(llBaidu3).icon(mRedPin)
                                            .zIndex(1);;

                                    ((Marker)mBaiduMap.addOverlay(ooD)).setTitle(nearbyInfo.nearName );

                                }
                            }



                        }

                    }

                    if(mNearBatList == null || mNearBatList.size() != tempListNear.size()){
                        mNearBatList = tempListNear;
                    }

                    List<UserBatInfo> tempList2 = new ArrayList<UserBatInfo>();
                    for (int i = 0; i < jArray2.length(); i++) {
                        JSONObject temp = (JSONObject) jArray2.get(i);
                        UserBatInfo tempUser = new UserBatInfo();
                        tempUser.lat = Double.parseDouble(temp.getString("latitude"));
                        tempUser.lon = Double.parseDouble(temp.getString("longitude"));
                        tempUser.temperature = Float.parseFloat(temp.getString("temperature"));
                        tempUser.voltage = Float.parseFloat(temp.getString("voltage"));
                        tempUser.imei = temp.getString("btyImei");
                        tempUser.power=Integer.parseInt(temp.getString("power"));
                        tempUser.receiveDate = temp.getString("receiveDate");
                        tempList2.add(tempUser);

                        if (isZero(tempUser.lat) && isZero(tempUser.lon)) {
                            shouldShowAlert = true;
                            if (!mIMEIList.contains(tempUser.imei)) {
                                showLongPeriod = true;
                            }
                        } else if (!mIMEIList.contains(tempUser.imei)) {
                            mIMEIList.add(tempUser.imei);
                            writeIMEIList();
                        }

                        lltemp2 = new LatLng(tempUser.lat, tempUser.lon);
                        coorConverter.coord(lltemp2);
                         llBaidu2 = coorConverter.convert();

//                        OverlayOptions ooD = new MarkerOptions().position(llBaidu).icons(giflist)
//                                .zIndex(10).period(50);
                        OverlayOptions ooD = new MarkerOptions().position(llBaidu2).icon(mRedPin)
                                .zIndex(10);
                        ((Marker)mBaiduMap.addOverlay(ooD)).setTitle(tempUser.imei);




                        if(temp.getString("voltage")!= null){
                            double voltage = Double.parseDouble(temp.getString("voltage"));

                            double distance = RemainingMileage.getDistance(voltage);


                            Stroke stroke = new Stroke(20,20);

                            OverlayOptions circleOption = new CircleOptions()
                                    .center(llBaidu2)
                                    .radius((int)(distance * 1000/2))
                                    .stroke(stroke)
                                    .fillColor(Color.parseColor("#220099ff"));


                            mBaiduMap.addOverlay(circleOption);

                        }

                    }


                    if (shouldShowAlert) {
                        if (showLongPeriod) {
                            mAlertInfo.setText(getString(R.string.long_alert));
                        } else {
                            mAlertInfo.setText(getString(R.string.short_alert));
                        }
                        mAlertInfo.setVisibility(View.VISIBLE);
                        mAlertInfo.startAnimation(mAnimFadeIn);
                        mAlertInfo.removeCallbacks(mDismissAlert);
                        if (!showLongPeriod) {
                            mAlertInfo.postDelayed(mDismissAlert, mShortPeriod);
                        } else {
                            mAlertInfo.postDelayed(mDismissAlert, mLongPeriod);
                        }
                    } else {
                        if (mAlertInfo.getVisibility() == View.VISIBLE) {
                            mAlertInfo.removeCallbacks(mDismissAlert);
                            mAlertInfo.setVisibility(View.INVISIBLE);
                            mAlertInfo.startAnimation(mAnimFadeOut);
                        }
                    }

                    if (mUserBatList.size() != tempList1.size()) {
                        mUserBatList = tempList1;
                    } else {
                        for (int i = 0; i < mUserBatList.size(); i++) {
                            mUserBatList.get(i).lat = tempList1.get(i).lat;
                            mUserBatList.get(i).lon = tempList1.get(i).lon;
                            mUserBatList.get(i).temperature = tempList1.get(i).temperature;
                            mUserBatList.get(i).voltage = tempList1.get(i).voltage;
                            mUserBatList.get(i).imei = tempList1.get(i).imei;
                            mUserBatList.get(i).power=tempList1.get(i).power;
                            mUserBatList.get(i).receiveDate = tempList1.get(i).receiveDate;
                        }
                    }

                    if (mFriendsBatList.size() != tempList2.size()) {
                        mFriendsBatList = tempList2;
                    } else {
                        for (int i = 0; i < mFriendsBatList.size(); i++) {
                            mFriendsBatList.get(i).lat = tempList2.get(i).lat;
                            mFriendsBatList.get(i).lon = tempList2.get(i).lon;
                            mFriendsBatList.get(i).temperature = tempList2.get(i).temperature;
                            mFriendsBatList.get(i).voltage = tempList2.get(i).voltage;
                            mFriendsBatList.get(i).imei = tempList2.get(i).imei;
                            mFriendsBatList.get(i).power=tempList2.get(i).power;
                            mFriendsBatList.get(i).receiveDate = tempList2.get(i).receiveDate;
                        }
                    }

                    MapStatusUpdate msu;
                    if (mFirstEnterIn) {
                        mFirstEnterIn = false;
                        if (lltemp != null) {
                            msu = MapStatusUpdateFactory.newLatLngZoom(llBaidu, 13.0f);
                            mBaiduMap.setMapStatus(msu);
                        } else if (lltemp2 != null) {
                            msu = MapStatusUpdateFactory.newLatLngZoom(llBaidu2, 13.0f);
                            mBaiduMap.setMapStatus(msu);
                        } else {
                            mFirstEnterIn = true;
                            if (mShowWarnDialog) {
                                mShowWarnDialog = false;
                                String msg = "你还没有追踪信息";
                                AlertDialogShow(msg);
                            }
                        }
                    } else {
                        if(llBaidu != null){
                            msu = MapStatusUpdateFactory.newLatLngZoom(llBaidu, mCurrZoom);
                            mBaiduMap.setMapStatus(msu);
                        }else if(llBaidu2 != null){
                            msu = MapStatusUpdateFactory.newLatLngZoom(llBaidu2, mCurrZoom);
                            mBaiduMap.setMapStatus(msu);
                        }else{
                            msu = MapStatusUpdateFactory.newLatLngZoom(mCurrLoc, mCurrZoom);
                            mBaiduMap.setMapStatus(msu);
                        }
                    }
                } catch (JSONException e) {
                }
                return;
            }
        }

        public void GetBatteryList( Intent intent) {
            String msg = "获取云电池数据失败";
            if (!intent.getBooleanExtra("getBatteryListSuccess", false)) {
                msg = intent.getStringExtra("result");
                AlertDialogShow(msg);
            }
        }

        public void GetFriendBatteryList( Intent intent) {
            String msg = "获取云电池数据失败";
            if (intent.getBooleanExtra("getBatteryListSuccess", false)) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonBatListStr);
                    JSONObject jObect1 = jsonObject.getJSONObject("data");
                    JSONArray jArray2 = jObect1.getJSONArray("friendBtys");
                    int count = 0;

                    List<String> batList = new ArrayList<String>();
                    Bundle bundle = new Bundle();

                    for (int i = 0; i < jArray2.length(); i++) {
                        JSONObject temp1 = (JSONObject) jArray2.get(i);
                        String simNo = temp1.getString("btySimNo");
                        String sn = temp1.getString("btyPubSn");
                        //String Item = "序列号: " + sn + "\n" + "SIM卡号: " + simNo;
                        String Item = "SIM卡号: " + simNo;
                        batList.add(Item);
                        bundle.putString(simNo, sn);
                        count++;
                    }

                    if (count != 0) {
                        new ShowBatList(UserView.this, batList, 10024).showDialog();
                        return;
                    }
                    msg = "你尚未跟踪任何好友的云电池。";
                } catch (JSONException e) {
                    Log.d("test2", "get battery list json exception");
                    msg = "数据格式异常，不能正确解析数据。";
                }
            } else {
                msg = intent.getStringExtra("result");
            }
            AlertDialogShow(msg);
        }

        private void shareBatteryToFriend(Intent intent) {
            String msg = "共享成功";
            if (!intent.getBooleanExtra("shareBatSuccess", false)) {
                msg = intent.getStringExtra("result");
            }
            AlertDialogShow(msg);
        }

        private void BatFollowersList(Intent intent) {
            if (intent.getBooleanExtra("shareBatSuccess", false)) {
                Intent intent1 = new Intent(UserView.this, UserBatList.class);
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
                startActivity(intent1);
                return;
            }

            String msg = intent.getStringExtra("result");
            AlertDialogShow(msg);
        }

    }

    private class ShowBatList extends UtilityDialog {
        public ShowBatList(Context context, List<?> items, int friendPos) {
            super(context, items, friendPos);
        }

        @Override
        public String getTitle() {
            return getString(R.string.friend_bat_list);
        }

        @Override
        public String getPositiveButtonText(){
            return getString(R.string.positive_button);
        }

        @Override
        public void handleButtonClick() {

        }
    }

    public static class UserBatInfo {
        UserBatInfo() {

        }
        public double lat;
        public double lon;
        public float voltage;
        public float temperature;
        //public String sn;
        public String imei;
        public int power;

        public String receiveDate;


    }


    public static class NearbyInfo {
        NearbyInfo() {
        }
        public double lat;
        public double lon;
        public String nearName;
        public String nearPhone;
        public String nearSex;
    }


    //通过Imei查询经纬度
    public static LatLng getLatLng(String imei){
        if(mUserBatList!=null && mUserBatList.size()>0){
            for(int i = 0; i< mUserBatList.size();i++){
                if(mUserBatList.get(i).imei.equals(imei)){
                    LatLng latLng = new LatLng(mUserBatList.get(i).lat,mUserBatList.get(i).lon);
                    return latLng;
                }
            }
        }
        return null;
    }



/*
    public void scheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent getData = new Intent(DistributorView.this, AlarmGetDataReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DistributorView.this,
                0,
                getData,
                PendingIntent.FLAG_ONE_SHOT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 5000, pendingIntent);
    }*/



    //绘制轨迹
    public void testDrawTrack(){

        Intent intent=new Intent(UserView.this,BaiduTranceActivity.class);
        if(mUserBatList!=null){
            intent.putExtra("imei", mUserBatList.get(0).imei);
        }else if(mUserBatList==null&&mFriendsBatList!=null){
            intent.putExtra("imei",mFriendsBatList.get(0).imei);
        }else{
            return;
        }
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(UserView.this, LauncherAcitivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 把bitmap转成圆形
     * */
    public Bitmap toRoundBitmap(Bitmap bitmap,String name,String six){
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
//        Bitmap backgroundBm=Bitmap.createBitmap(600,600, Bitmap.Config.ARGB_8888);


        //构建一个bitmap
        Bitmap backgroundBm=Bitmap.createBitmap(400,280, Bitmap.Config.ARGB_8888);

        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas=new Canvas(backgroundBm);
        Paint p=new Paint();


        //设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);
        RectF rect=new RectF(120, 120,270, 270);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形


        Paint p1=new Paint();
        p1.setTextSize(80);
        p1.setColor(Color.BLUE);
        Path path = new Path(); //定义一条路径
        path.moveTo(10, 60); //移动到 坐标10,10

        path.lineTo(250, 60);

        Bitmap  gril = BitmapFactory.decodeResource(getResources(), R.drawable.gril);
        Bitmap  boy = BitmapFactory.decodeResource(getResources(), R.drawable.boy);



//          canvas.drawPath(path, paint);
        if(name == null || name.length() == 0){
            canvas.drawTextOnPath("Angel", path, 10, 10, p1);
        }else{
            canvas.drawTextOnPath(name, path, 10, 10, p1);
        }


        if(six.equals("man")){
            canvas.drawBitmap(boy,260,10,p);
        }else{
            canvas.drawBitmap(gril,260,10,p);
        }







        canvas.drawRoundRect(rect, 75, 75, p);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return backgroundBm;
    }


    //实现监听器 LocationListener
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();

                if(relLon == 0 && relLat == 0){
                    mBaiduMap.clear();
                    LatLng  lltemp3 = new LatLng(lat, lon);
                    coorConverter.coord(lltemp3);
                    LatLng llBaidu3 = coorConverter.convert();
                    OverlayOptions ooD;

                    if(mHead != null){
                        ooD = new MarkerOptions().position(llBaidu3).icon(mHead)
                                .zIndex(1);;
                    }else{
                        ooD = new MarkerOptions().position(llBaidu3).icon(mRedPin)
                                .zIndex(1);
                    }

                    ((Marker)mBaiduMap.addOverlay(ooD)).setTitle("Angle");
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }



}
