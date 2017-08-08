package baidumapsdk.demo.demoapplication;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import org.json.JSONException;
import org.json.JSONObject;


public class GuideActivity extends ActionBarActivity {

    private WebView webView;
    private static final String TAG = "WebDemo";
    private LocationManager locationManager;
    private Context mContext;
    private static double lat = 0;
    private static double lon = 0;

    private static double btyLat = 0;
    private static double btyLon = 0;

    private  Handler mHandler;
    private Runnable mRunnable;

    private static String  jsonStr =null;
    private static String  test =null;

    private static String imei="12345";

    private CoordinateConverter coorConverter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = GuideActivity.this;

        Intent intent=getIntent();
        if(intent!=null){
            imei=intent.getStringExtra("imei");
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
                Toast.makeText(GuideActivity.this,"请开启网络!",Toast.LENGTH_LONG).show();
            }
        }

        coorConverter = new CoordinateConverter();
        coorConverter.from(CoordinateConverter.CoordType.GPS);

        setContentView(R.layout.activity_guide);
        webView = (WebView) findViewById(R.id.myweb);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setAllowContentAccess(true);
//        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        webSettings.setSupportZoom(false);
        webView.loadUrl("file:///android_asset/guide.html");

        webView.setWebChromeClient(new HarlanWebChromeClient());
        webView.setWebViewClient(new HarlanWebViewClient());

       mHandler=new Handler();
       mRunnable=new Runnable() {
            @Override
            public void run() {

                  LatLng latLng = UserView.getLatLng(imei);
                if(latLng!=null){
                    btyLat = latLng.latitude;
                    btyLon = latLng.longitude;
                }else {
                    btyLat = UserView.relLat;
                    btyLon = UserView.relLon;
                }


                LatLng lltemp1 = new LatLng(lat, lon);
                coorConverter.coord(lltemp1);
                LatLng llBaidu1= coorConverter.convert();

                LatLng lltemp2 = new LatLng(btyLat, btyLon);
                coorConverter.coord(lltemp2);
                LatLng llBaidu2= coorConverter.convert();


                if(lat!=0 && lon!=0 && btyLat!=0 && btyLon!=0){
                    JSONObject jsonObject = new JSONObject();
                    try {
//                        jsonObject.put("startLat",lat);
//                        jsonObject.put("startLon",lon);
//                        jsonObject.put("endLat",btyLat);
//                        jsonObject.put("endLon",btyLon);
                        jsonObject.put("startLat",llBaidu1.latitude);
                        jsonObject.put("startLon",llBaidu1.longitude);
                        jsonObject.put("endLat",llBaidu2.latitude);
                        jsonObject.put("endLon",llBaidu2.longitude);
                        jsonStr = jsonObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    webView.loadUrl("javascript:rfInfo(" + jsonStr + ")");
//                    mHandler.postDelayed(mRunnable,60*1000);//设置延迟时间，此处是5秒
                }
            }
        };

        mHandler.postDelayed(mRunnable, 5 * 1000);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fresh_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_fresh:
                mHandler.post(mRunnable);
//                webView.loadUrl("javascript:rfInfo("+jsonStr+")");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //实现监听器 LocationListener
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.i(TAG, "Location changed : Lat: " +lat+ " Lng: " + lon);

                webView.loadUrl("javascript:test(" + jsonStr + ")");
            } else {
                Log.i(TAG, "Location changed : Lat: " + "NULL" + " Lng: " + "NULL");
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



    /***
     *webChromeClient主要是将javascript中相应的方法翻译成android本地方法
     *
     * 例如：我们重写了onJsAlert方法，那么当页面中需要弹出alert窗口时，便
     * 会执行我们的代码，按照我们的Toast的形式提示用户。
     */
    class HarlanWebChromeClient extends WebChromeClient {

        /*此处覆盖的是javascript中的alert方法。
         *当网页需要弹出alert窗口时，会执行onJsAlert中的方法
         * 网页自身的alert方法不会被调用。
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            Toast.makeText(getApplicationContext(), message,
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "弹出了提示框");
						/*此处代码非常重要，若没有，android就不能与js继续进行交互了，
						 * 且第一次交互后，webview不再展示出来。
						 * result：A JsResult to confirm that the user hit enter.
						 * 我的理解是，confirm代表着此次交互执行完毕。只有执行完毕了，才可以进行下一次交互。
						 */
            result.confirm();
            return true;
        }

        /*此处覆盖的是javascript中的confirm方法。
         *当网页需要弹出confirm窗口时，会执行onJsConfirm中的方法
         * 网页自身的confirm方法不会被调用。
         */
        @Override
        public boolean onJsConfirm(WebView view, String url,
                                   String message, JsResult result) {
            Log.d(TAG, "弹出了确认框");
            result.confirm();
            return true;
        }

        /*此处覆盖的是javascript中的confirm方法。
         *当网页需要弹出confirm窗口时，会执行onJsConfirm中的方法
         * 网页自身的confirm方法不会被调用。
         */
        @Override
        public boolean onJsPrompt(WebView view, String url,
                                  String message, String defaultValue,
                                  JsPromptResult result) {
            Log.d(TAG, "弹出了输入框");
            result.confirm();
            return true;
        }

        /*
         * 如果页面被强制关闭,弹窗提示：是否确定离开？
         * 点击确定 保存数据离开，点击取消，停留在当前页面
         */
        @Override
        public boolean onJsBeforeUnload(WebView view, String url,
                                        String message, JsResult result) {
            Log.d(TAG, "弹出了离开确认框");
            result.confirm();
            return true;
        }
    }


    class HarlanWebViewClient extends WebViewClient {
        /*点击页面的某条链接进行跳转的话，会启动系统的默认浏览器进行加载，调出了我们本身的应用
         * 因此，要在shouldOverrideUrlLoading方法中
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,
                                                String url) {
            //使用当前的WebView加载页面
            view.loadUrl(url);
            return true ;
        }

        /*
         * 网页加载完毕(仅指主页，不包括图片)
         */
        @Override
        public void onPageStarted(WebView view, String url,
                                  Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        /*
         * 网页加载完毕(仅指主页，不包括图片)
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
        }

        /*
         * 加载页面资源
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onLoadResource(view, url);
        }

        /*
         * 错误提示
         */
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    };


}
