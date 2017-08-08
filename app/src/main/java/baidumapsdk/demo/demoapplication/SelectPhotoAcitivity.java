package baidumapsdk.demo.demoapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.utils.BitmapUtil;
import com.utils.ScreenUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectPhotoAcitivity extends ActionBarActivity {

    private static final int PICK_IMAGE_REQUEST = 10;

    //Uri to store the image uri
    private Uri filePath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent == null){
            finish();
        }
        int reqCode = intent.getIntExtra("reqCode", 0);

        showFileChooser(reqCode);

    }





    //method to show file chooser
    private void showFileChooser(int reqCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), reqCode);
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            SharedPreferences sp = getSharedPreferences("userimageurl", 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("url:" + requestCode, getPath(filePath).toString());

            //提交数据
            editor.commit();
            finish();
        }else{
            Toast.makeText(SelectPhotoAcitivity.this,"设置头像失败!",Toast.LENGTH_LONG);
            finish();
        }

    }
    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
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
