package com.sam.beiz.controller;

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
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.beiz.Ad.download.ADdownloader;
import com.sam.beiz.Config.ConfigURL;
import com.sam.beiz.common.ActionBarSupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import baidumapsdk.demo.demoapplication.R;

import static com.sam.beiz.Config.ConfigURL.onlinerepair;
import static com.sam.beiz.Config.ConfigURL.replaceHost;

public class StoreChoiceActivity extends ActionBarActivity {

    ImageView displayImage;
    RadioButton onlineDirectly,physicalStore,onlineConsulting,suggestions;
    RelativeLayout relativeLayout;
    Context mContext;
    View itemView;
    LayoutInflater flater;
    RadioGroup radioGroup;
    private ProgressDialog dialog = null;

    private BroadcastReceiver mStoreReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
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

        final ImageButton btnBack = (ImageButton) actionbarLayout.findViewById(R.id.left_back);
        TextView textView = (TextView) actionbarLayout.findViewById(R.id.actionBar_title);

        textView.setText("贝珍商城");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));

        setContentView(R.layout.activity_store_choice);

        displayImage = (ImageView) findViewById(R.id.image_display);
        if(isNetworkAvailable()){
            new ADdownloader(ConfigURL.cad2,getApplicationContext(), displayImage).execute();
        }
        displayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("singleAdlink", 0);
                String link = sharedPreferences.getString("singlejumpurl", "");


                //   String link=AdstoreDB.getJumpurl();
                if(link!=""){
                    Intent i=new Intent(StoreChoiceActivity.this,AdWebActivity.class);
                    Log.v("link",link);
                    i.putExtra("link","http://"+link);
                    startActivity(i);
                }
            }
        });




        onlineDirectly = (RadioButton) findViewById(R.id.online_directly);
        radioGroup = (RadioGroup) findViewById(R.id.store_radio_group);
        physicalStore = (RadioButton) findViewById(R.id.physical_store);
        onlineConsulting = (RadioButton) findViewById(R.id.online_consulting);
        suggestions = (RadioButton) findViewById(R.id.suggestions);

        relativeLayout = (RelativeLayout) findViewById(R.id.item_layout);


        flater = LayoutInflater.from(this);
        itemView = flater.inflate(R.layout.online_directly_layout, null);
        relativeLayout.addView(itemView);
        ImageView ivTaoBao = (ImageView) itemView.findViewById(R.id.taobao_img);
        ivTaoBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreChoiceActivity.this, BeiZStoreActivity.class);
                intent.putExtra("store", "taobao");
                startActivityForResult(intent, 2);
            }
        });




        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.online_directly:
                        relativeLayout.removeView(itemView);
                        itemView = null;
                        flater = null;
                        flater = LayoutInflater.from(mContext);
                        itemView = flater.inflate(R.layout.online_directly_layout, null);
                        relativeLayout.addView(itemView);
                        relativeLayout.invalidate();

                        ImageView ivTaoBao = (ImageView) itemView.findViewById(R.id.taobao_img);
                        ivTaoBao.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(StoreChoiceActivity.this,BeiZStoreActivity.class);
                                intent.putExtra("store","taobao");
                                startActivityForResult(intent,2);
                            }
                        });


                        break;
                    case R.id.physical_store:
                        relativeLayout.removeView(itemView);
                        itemView = null;
                        flater = null;
                        flater = LayoutInflater.from(mContext);
                        itemView = flater.inflate(R.layout.physical_store_layout1, null);
                        relativeLayout.addView(itemView);
                        relativeLayout.invalidate();
                        String data=null;

                        WebView webView=(WebView)itemView.findViewById(R.id.physical_storewebview);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setBlockNetworkImage(false);

                        webView.getSettings().setSupportZoom(true);
                        webView.getSettings().setBuiltInZoomControls(true);
                        webView.getSettings().setLoadsImagesAutomatically(true);



                        try {
                            data=new storeTask().execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        if(data!=null){
                            webView.loadDataWithBaseURL(null, makeHTML(data), "text/html","UTF-8", null);
                        }else{
                            /**
                             * 暂时不写
                             */
                        }



                        webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                // TODO Auto-generated method stub
                                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                                view.loadUrl(url);
                                return true;
                            }
                        });




                        break;
                    case R.id.online_consulting:
                        relativeLayout.removeView(itemView);
                        itemView = null;
                        flater = null;
                        flater = LayoutInflater.from(mContext);
                        itemView = flater.inflate(R.layout.online_consulting_layout, null);
                        relativeLayout.addView(itemView);
                        relativeLayout.invalidate();
                        break;
                    case R.id.suggestions:
                        relativeLayout.removeView(itemView);
                        itemView = null;
                        flater = null;
                        flater = LayoutInflater.from(mContext);
                        itemView = flater.inflate(R.layout.suggestions_layout, null);
                        relativeLayout.addView(itemView);
                        relativeLayout.invalidate();

                        final EditText editText = (EditText) itemView.findViewById(R.id.info_suggetion);
                        editText.setFocusable(true);
                        editText.requestFocus();
                        Button summit = (Button) itemView.findViewById(R.id.btn_suggest_summit);

                        summit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(editText.getText() == null || editText.getText().length()<=0){
                                    Toast.makeText(mContext,"提交内容不能为空!",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ensureDialog();
                                TimerTask task = new TimerTask()
                                {
                                    public void run()
                                    {
                                        Intent intent = new Intent(LauncherAcitivity.ACTION_SUMMIT_SUGGESTION_MSG);
                                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                                    }
                                };
                                Timer timer = new Timer();
                                timer.schedule(task, 3000);
                            }
                        });

                        break;
                    default:
                        break;

                }
            }
        });
    }

    @Override
    protected void onResume() {
        if(mStoreReceiver == null){
            mStoreReceiver = new StoreReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(LauncherAcitivity.ACTION_SUMMIT_SUGGESTION_MSG);
            LocalBroadcastManager.getInstance(mContext).registerReceiver(mStoreReceiver,filter);

        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(mStoreReceiver != null){
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mStoreReceiver);
            mStoreReceiver = null;
        }
        super.onDestroy();
    }

    public void AlertDialogShow(String message) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(StoreChoiceActivity.this);
        builder.setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(true);
        Dialog dialog1 = builder.create();
        dialog1.show();
    }

    private void ensureDialog() {
        if (dialog == null) {
            String title = getString(R.string.process_wait_title);
            String msg = getString(R.string.process_wait_msg);

            dialog = ProgressDialog.show(StoreChoiceActivity.this, title, msg, true, true);
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


    class StoreReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(LauncherAcitivity.ACTION_SUMMIT_SUGGESTION_MSG)){
                dismissProgressDialog();
                AlertDialogShow("提交成功！感谢您的宝贵建议，我们将尽快处理");
            }
        }
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private String makeHTML(String data) {
        String f1="<html><head><meta  content=\"width=device-width, initial-scale=1\" charset=\"gbk2312\">  <link rel=\"stylesheet\" href=\"file:///android_asset/jquery.mobile-1.4.5.min.css\">    <script src=\"file:///android_asset/jquery-1.10.2.min\"></script>    <script src=\"file:///android_asset/jquery.mobile-1.4.5.min.js\"></script> <style type=\"text/css\">body{  } h3{  text-align: center;  } img{  height: auto;  width:100%; text-align: center;  }    </style></head><body><div data-role=\"page\" id=\"pageone\"><div data-role=\"content\">";
        String f2="</div></div></body></html> ";
        String str0="<html>"+"<meta charset=gbk2312>";
        String str = data.replaceAll("localhost", replaceHost);
        String str1="<Body>"+str+"</Body>";
        //   Log.v("tem",str1);
        String str2="</html>";

        //return str0+str1+str2;
        String show=f1+str+f2;
        //   String show=str0+str1+str2;
        Log.v("http",show);
        return show;

    }

    public class storeTask extends AsyncTask<Void,Void,String> {
        String data=null;

        @Override
        protected String doInBackground(Void... voids) {
            JSONObject json = null;
            String str = "";
            cz.msebera.android.httpclient.HttpResponse response;
            cz.msebera.android.httpclient.client.HttpClient myClient = new cz.msebera.android.httpclient.impl.client.DefaultHttpClient();
            cz.msebera.android.httpclient.client.methods.HttpPost myConnection = new cz.msebera.android.httpclient.client.methods.HttpPost(ConfigURL.phystoreurl);

            try {
                response = myClient.execute(myConnection);
                str = cz.msebera.android.httpclient.util.EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (cz.msebera.android.httpclient.client.ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                JSONArray jArray = new JSONArray(str);
                json = jArray.getJSONObject(0);
                data=json.getString("message");
                Log.v("1",data);

            } catch ( JSONException e) {
                e.printStackTrace();
            }
            return data;
        }
    }


}
