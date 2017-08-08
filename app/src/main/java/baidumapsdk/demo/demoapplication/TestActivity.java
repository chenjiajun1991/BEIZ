package baidumapsdk.demo.demoapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TestActivity extends ActionBarActivity {
   private double lat;
    private double lon;
    Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mButton = (Button) findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lat = UserView.relLat;
                lon = UserView.relLon;

                Log.i("Test1",lat+"  "+lon);

            }
        });








    }
}
