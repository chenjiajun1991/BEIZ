package baidumapsdk.demo.demoapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.common.ActionBarSupport;
import com.sam.beiz.controller.DefaultActivity;
import com.sam.beiz.controller.LauncherAcitivity;
import com.sam.beiz.controller.PushFragment;
import com.utils.ScreenUtil;

import java.io.IOException;

public class Login_main extends Activity {
    public static final String ACTION_GET_USER_GPS_DATA = "com.sam.action.GET_USER_GPS_DATA";
    public static final String ACTION_GET_DIS_GPS_DATA = "com.sam.action.GET_DIS_GPS_DATA";
    public static final String ACTION_USER_SIGN_UP_RESULT = "com.sam.action.SIGN_UP_RESULT";
    public static final String ACTION_USER_SIGN_IN_RESULT = "com.sam.action.SIGN_IN_RESULT";
    public static final String ACTION_ADD_USER_RESULT = "com.sam.action.ADD_USER_RESULT";
    public static final String ACTION_ADD_DISTRIBUTOR_RESULT = "com.sam.action.ADD_DISTRIBUTOR_RESULT";
    public static final String ACTION_GET_DISTRIBUTOR_RESULT = "com.sam.action.GET_DISTRIBUTOR_RESULT";
    public static final String ACTION_GET_BATTERY_LIST = "com.sam.action.GET_BATTERY_LIST";
    public static final String ACTION_GET_FRIEND_BATTERY_LIST = "com.sam.action.GET_FRIEND_BATTERY_LIST";
    public static final String ACTION_SHARE_BATTERY = "com.sam.action.SHARE_BATTERY";
    public static final String ACTION_GET_BAT_FOLLOWERS = "com.sam.action.BAT_FOLLOWERS";
    public static final String ACTION_ADD_GROUP_CHILD_ITEM = "com.sam.action.ADD_GROUP_CHILD_ITEM";
    public static final String ACTION_DEL_GROUP_CHILD_ITEM = "com.sam.action.DEL_GROUP_CHILD_ITEM";
    public static final String ACTION_DIS_GET_USER_INFO = "com.sam.action.DIS_GET_USRE_INFO";
    public static final String ACTION_GET_CITY_BAT_COUNT = "com.sam.action.GET_CITY_BAT_COUNT";
    public static final String ACTION_GET_DIS_LOC = "com.sam.action.GET_DIS_LOC";
    public static final String ACTION_GET_BAT_LOC = "com.sam.action.GET_BAT_LOC";
    public static final String ACTION_GET_APP_VERSION = "com.sam.action.GET_APP_VERSION";
    public static final String ACTION_LOCK_BATTERY = "com.sam.action.LOCK_BATTERY";
    public static final String ACTION_GET_HISTORY_GPS = "com.sam.action.GET_HISTORY_GPS";
    public static final String ACTION_QUERY_SALE_INFO = "com.sam.action.QUERY_SALE_INFO";
    public static final String ACTION_FETCH_NEARBY_INFO = "com.sam.action.FETCH_NEARBY_INFO";


    //public static String preUrl = "http://sam.yahengcloud.com:8080/sam/";
    //public static String preUrl = "http://101.231.206.30:9002/sam/";
    public static String preUrl;

    //public static String updateEndpoint = "/open/ver.json";
    public static String updateEndpoint;

    private String signinUrl = null;
    private String mAccountName = null;
    private String mAccountType = null;

    private String mIndex = null;

    //Bitmap to get image from gallery
    private Bitmap bitmap;
    //Uri to store the image uri
    private Uri filePath;

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    private static String headUrl = null;

    private String[] mUserGroups = new String[]{
            "普通用户",
            "经销商",
            "生产厂家"
    };

    public static String[] mChinaValidPN = new String[]{
            "130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
            "150", "151", "152", "153", "154", "155", "156", "157", "158", "159",
            "180", "181", "182", "183", "184", "185", "186", "187", "188", "189"
    };

    private Spinner mDistributor;
    private ArrayAdapter<String> mDistributorAdapter = null;

    private EditText mAccount;
    private EditText mPassWord;
    private ImageView mAccountImage;
    private ImageView mPasswordImage;
    private ImageView mAccountDel;
    private ImageView mPasswordDel;
    private int mChoice = 0;
    private ProgressReceiver mProgressReceiver;

    private ImageView headImageView;

    private BroadcastReceiver mGetSigninResultReceiver = null;

    TextView forgetPsd,textSinup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getIntent();
        if(intent != null){
            mIndex = intent.getStringExtra("index");
        }


        setContentView(R.layout.layout_login_in);

        headImageView = (ImageView) findViewById(R.id.login_view2);


        SharedPreferences sharedPreferences = getSharedPreferences("userimageurl", 0);
        headUrl = sharedPreferences.getString("url", "");

        if(headUrl != null && headUrl.length() >0){

            Bitmap bitmap = BitmapFactory.decodeFile(headUrl);
            Bitmap bitmapTemp = toRoundBitmap(bitmap);
            headImageView.setImageBitmap(bitmapTemp);

        }

        forgetPsd = (TextView) findViewById(R.id.passwordFind2);

        textSinup =(TextView) findViewById(R.id.distributorSignUp2);



        forgetPsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_main.this, ResetPassword.class);
                startActivityForResult(intent, 2);
            }
        });

        textSinup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login_main.this, UserSignUp.class);
               startActivityForResult(intent, 1);

            }
        });


//        mAccountImage = (ImageView) findViewById(R.id.accountImage);
//        mAccountDel = (ImageView) findViewById(R.id.accountDelImage);
//        mAccountDel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAccount.setText("");
//            }
//        });
//        mPasswordDel = (ImageView) findViewById(R.id.passwordDelImage);
        mAccount = (EditText) findViewById(R.id.textAccount2);
//        mAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mAccountImage.setImageResource(R.drawable.user_new_select);
//                    mAccountDel.setVisibility(View.VISIBLE);
//                } else {
//                    mAccountImage.setImageResource(R.drawable.user_new);
//                    mAccountDel.setVisibility(View.INVISIBLE);
//                }
//            }
//        });

//        mPasswordImage = (ImageView) findViewById(R.id.passwordImage);
//        mPasswordDel = (ImageView) findViewById(R.id.passwordDelImage);
//        mPasswordDel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPassWord.setText("");
//            }
//        });
        mPassWord = (EditText) findViewById(R.id.textPassword2);
//        mPassWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mPasswordImage.setImageResource(R.drawable.pwd_new_select);
//                    mPasswordDel.setVisibility(View.VISIBLE);
//                } else {
//                    mPasswordImage.setImageResource(R.drawable.pwd_new);
//                    mPasswordDel.setVisibility(View.INVISIBLE);
//                }
//            }
//        });

        headImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(PICK_IMAGE_REQUEST);
            }
        });

        signinUrl = preUrl + getString(R.string.user_signin);
        updateEndpoint = getString(R.string.open_ver);

        mProgressReceiver = new ProgressReceiver(new Handler());

        SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
                Context.MODE_PRIVATE);
        String account = mainPref.getString("lastAccount", "");
        int accountType = mainPref.getInt("accountType", 0);
        mChoice = accountType;

//        mDistributor = (Spinner) findViewById(R.id.userGroup2);
//        mDistributor.setBackgroundResource(R.drawable.spinner_default_holo_light_am);
//        mDistributorAdapter = new ArrayAdapter<String>(Login_main.this,
//                R.layout.customize_dropdown_item_bl, mUserGroups);
//        mDistributor.setAdapter(mDistributorAdapter);
//        //mDistributor.setSelection(0, true);
//        mDistributor.requestFocus();
//
//        mDistributor.setSelection(accountType, true);
//        mAccount.setText(account);

//        mDistributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                /*if (position != 0) {
//                    menuItem.setVisible(false);
//                } else {
//                   menuItem.setVisible(true);
//                }*/
//                mChoice = position;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//
//            }
//        });
    }

    @Override
    protected void onResume() {
        if (mGetSigninResultReceiver == null) {
            mGetSigninResultReceiver = new GetSignInResultReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Login_main.ACTION_USER_SIGN_IN_RESULT);
            LocalBroadcastManager.getInstance(Login_main.this)
                    .registerReceiver(mGetSigninResultReceiver, filter);
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mGetSigninResultReceiver != null) {
            LocalBroadcastManager.getInstance(Login_main.this)
                    .unregisterReceiver(mGetSigninResultReceiver);
            mGetSigninResultReceiver = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    public void enterOverlayPanel1(View view) {
        EditText account = (EditText) findViewById(R.id.textAccount2);
        String strAccount = account.getText().toString();
        EditText password = (EditText) findViewById(R.id.textPassword2);
        String strPassword = password.getText().toString();

        if (!isNetworkAvailable()) {
            AlertDialogShow("网络未连接");
            return;
        }

        if (strAccount.isEmpty() || strPassword.isEmpty()) {
            String msg = "帐号或密码不能为空！";
            AlertDialogShow(msg);
        } else {

            if(mIndex!= null && mIndex.equals("PushFragment")){

                Intent intent = new Intent(Login_main.this, DefaultActivity.class);
                startActivityForResult(intent,4);
                finish();
            }else{

                Bundle bundle = new Bundle();
                bundle.putString("phoneNum", strAccount);
                bundle.putString("password", strPassword);

                mProgressReceiver.showDialog(Login_main.this);

                new TestNetworkAsyncTask(Login_main.this,
                        TestNetworkAsyncTask.TYPE_USER_SIGN_IN,
                        bundle).execute(signinUrl);
            }

        }
    }

    public void enterForgetPWPanel1(View view) {
        Intent intent = new Intent(Login_main.this, ResetPassword.class);
        this.startActivityForResult(intent, 2);
    }

    public void enterUserSignUp1(View view) {
        Intent intent = new Intent(Login_main.this, UserSignUp.class);
        this.startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
/*        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);

        menuItem = menu.findItem(R.id.action_sign_up);*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_up:
                Intent intent = new Intent(Login_main.this, UserSignUp.class);
                this.startActivity(intent);
                return true;
        }
        return false;
    }

    public void AlertDialogShow(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login_main.this);
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class GetSignInResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            mProgressReceiver.send(ProgressReceiver.STATUS_COMPLETE, null);

            if (intent.getBooleanExtra("signinSuccess", false)) {
                String userPhone = intent.getStringExtra("userPhone");
                String userType = intent.getStringExtra("userType");
                ACache aCache=ACache.get(Login_main.this);
                aCache.put("userPHONE",userPhone);
                Intent intent2 = null;
                SharedPreferences mainPref = getSharedPreferences(getString(R.string.shared_pref_pacakge),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mainPref.edit();
                editor.putString("lastAccount", userPhone);
                editor.putInt("accountType", mChoice);
                editor.commit();

                if ("0".equals(userType)) {
                    if (mChoice == 1 || mChoice == 2) {
                        String msg = "你无权登录经销商或生产厂家界面!";
                        AlertDialogShow(msg);
                        return;
                    }
                    
                        intent2 = new Intent(Login_main.this, UserView.class);

                    editor.putBoolean("lastUser", true);
                    editor.commit();

                    finish();
                } else if ("1".equals(userType)) {
                    if (mChoice == 2) {
                        String msg = "你无权登录生产厂家界面!";
                        AlertDialogShow(msg);
                        return;
                    }
                    if (mChoice == 0) {
                        String msg = "你无权登录用户界面!";
                        AlertDialogShow(msg);
                        return;

//                        intent2 = new Intent(Login_main.this, UserView.class);
//                        finish();
                    } else if (mChoice == 1) {
                        intent2 = new Intent(Login_main.this, DistributorView.class);
                        editor.putBoolean("lastUser", false);
                        editor.commit();
                        finish();
                    }
                }
                else {
                    if (mChoice == 0) {
                        String msg = "你无权登录用户界面!";
                        AlertDialogShow(msg);
                        return;

//                        intent2 = new Intent(Login_main.this, UserView.class);
//                        finish();
                    } else if (mChoice == 1) {
                        String msg = "你无权登录经销商界面!";
                        AlertDialogShow(msg);
                        return;

//                        intent2 = new Intent(Login_main.this, DistributorView.class);
//                        finish();
                    } else {
                        intent2 = new Intent(Login_main.this, OverlayDemo.class);
                        editor.putBoolean("lastUser", false);
                        editor.commit();
                        finish();
                    }
                }

                intent2.putExtra("userPhone", userPhone);
                startActivity(intent2);
                finish();
            } else {
                String msg = "登录失败: " + intent.getStringExtra("result");
                AlertDialogShow(msg);
            }
        }
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        /*int value = Login_main.mChinaValidPN.length;
        for (int i = 0; i < value; i++) {
            if (phoneNumber.startsWith(Login_main.mChinaValidPN[i])) {
                return true;
            }
        }*/
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent =new Intent(Login_main.this, LauncherAcitivity.class);
            startActivity(intent);
            this.finish();
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        finish();
//        super.onBackPressed();
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



    private void checkImage() {
        SharedPreferences sharedPreferences = getSharedPreferences("userimageurl", 0);
        String url = sharedPreferences.getString("url", "");

        //String url=Config.getLocalImageUrl();
        if(url.isEmpty()==false){

            Bitmap bitmap1 = BitmapFactory.decodeFile(url);

            Bitmap bitmap2 = toRoundBitmap(bitmap1);

            headImageView.setImageBitmap(bitmap2);

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
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                Bitmap tempBitmap = toRoundBitmap(bitmap);

                headImageView.setImageBitmap(tempBitmap);

                SharedPreferences sp = getSharedPreferences("userimageurl", 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("url", getPath(filePath).toString());
                //提交数据
                editor.commit();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor =getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor =getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


}
