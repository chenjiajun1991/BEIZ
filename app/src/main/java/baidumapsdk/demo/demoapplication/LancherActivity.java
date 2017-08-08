package baidumapsdk.demo.demoapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LancherActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //去掉Activity上面的状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_lancher2);

        new Handler().postDelayed(new Runnable() {
            public void run() {
            Intent intent = new Intent(LancherActivity.this,PreActivity.class);
            startActivity(intent);
                finish();
            }
        }, 3000);


//        Intent intent = new Intent(LancherActivity.this,PreActivity.class);
//        startActivity(intent);
    }


    @Override
    protected void onResume() {

        super.onResume();

    }
//
//    @Override
//    protected void onStart() {
//        try {
//            Thread.sleep(3000);
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        super.onStart();
//    }
}
