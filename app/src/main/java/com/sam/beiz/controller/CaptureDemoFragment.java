package com.sam.beiz.controller;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.zhejunliu.service.up.MultipartUploadRequest;
import com.example.zhejunliu.service.up.UploadNotificationConfig;
import com.sam.beiz.Ad.cache.ACache;
import com.sam.beiz.Ad.landscapevideocapture.VideoCaptureActivity;
import com.sam.beiz.Ad.landscapevideocapture.configuration.CaptureConfiguration;
import com.sam.beiz.Ad.landscapevideocapture.configuration.PredefinedCaptureConfigurations;
import com.sam.beiz.Config.ConfigURL;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import baidumapsdk.demo.demoapplication.R;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class CaptureDemoFragment extends Fragment implements OnClickListener {

    static String username="";

    private final String KEY_STATUSMESSAGE = "com.sam.beiz.statusmessage";
    private final String KEY_ADVANCEDSETTINGS = "com.sam.beiz.advancedsettings";
    private final String KEY_FILENAME = "com.sam.beiz.outputfilename";

    private final String[] RESOLUTION_NAMES = new String[]{"1080p", "720p", "480p"};
    private final String[] QUALITY_NAMES = new String[]{"high", "medium", "low"};

    private String statusMessage = null;
    private String filename = null;
    private EditText filenameEt,messageET;
    private ImageView thumbnailIv;
    private TextView statusTv;
    private Spinner resolutionSp;
    private Spinner qualitySp;

    private RelativeLayout advancedRl;
    private EditText maxDurationEt;
    private CheckBox showTimerCb;
    private CheckBox allowFrontCameraCb;
    private ImageButton uploadIB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     //   setHasOptionsMenu(true);
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final Button captureBtn = (Button) rootView.findViewById(R.id.btn_capturevideo);
        captureBtn.setOnClickListener(this);

        thumbnailIv = (ImageView) rootView.findViewById(R.id.iv_thumbnail);
        thumbnailIv.setOnClickListener(this);
        statusTv = (TextView) rootView.findViewById(R.id.tv_status);
        advancedRl = (RelativeLayout) rootView.findViewById(R.id.rl_advanced);
      //  filenameEt = (EditText) rootView.findViewById(R.id.et_filename);
      //  fpsEt = (EditText) rootView.findViewById(R.id.et_fps);
        maxDurationEt = (EditText) rootView.findViewById(R.id.et_duration);
     //   maxFilesizeEt = (EditText) rootView.findViewById(R.id.et_filesize);
        showTimerCb = (CheckBox) rootView.findViewById(R.id.cb_showtimer);
      //  allowFrontCameraCb = (CheckBox) rootView.findViewById(R.id.cb_show_camera_switch);
        messageET=(EditText)rootView.findViewById(R.id.editText3);


     //   final View rootView2 = inflater.inflate(R.layout.activity_video, container, false);

        uploadIB=(ImageButton) getActivity().findViewById(R.id.right_imbt);
        uploadIB.setOnClickListener(this);

        ACache aCache=ACache.get(getActivity());
        if(aCache.getAsString("namefromServer")==null||aCache.getAsString("namefromServer").equals("")){
            Toast.makeText(getActivity(),"当前用户名不存在",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(getActivity(),LauncherAcitivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }else{
            username=aCache.getAsString("namefromServer");
        }


        if (savedInstanceState != null) {
            statusMessage = savedInstanceState.getString(KEY_STATUSMESSAGE);
            filename = savedInstanceState.getString(KEY_FILENAME);
   //         advancedRl.setVisibility(savedInstanceState.getInt(KEY_ADVANCEDSETTINGS));
        }

        updateStatusAndThumbnail();
        initializeSpinners(rootView);
        return rootView;
    }

    private void initializeSpinners(final View rootView) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, RESOLUTION_NAMES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resolutionSp = (Spinner) rootView.findViewById(R.id.sp_resolution);
        resolutionSp.setAdapter(adapter);

        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, QUALITY_NAMES);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        qualitySp = (Spinner) rootView.findViewById(R.id.sp_quality);
        qualitySp.setAdapter(adapter2);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_STATUSMESSAGE, statusMessage);
        outState.putString(KEY_FILENAME, filename);
        outState.putInt(KEY_ADVANCEDSETTINGS, advancedRl.getVisibility());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_capturevideo) {
            startVideoCaptureActivity();
        } else if (v.getId() == R.id.iv_thumbnail) {
            playVideo();
        }
        else if (v.getId() == R.id.right_imbt) {
           // playVideo();
            uploadMultipart();
        }
    }




    private boolean canHandleIntent(Intent intent) {
        final PackageManager mgr = getActivity().getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void startVideoCaptureActivity() {
        final CaptureConfiguration config = createCaptureConfiguration();
      //  final String filename = filenameEt.getEditableText().toString();

        final Intent intent = new Intent(getActivity(), VideoCaptureActivity.class);
        intent.putExtra(VideoCaptureActivity.EXTRA_CAPTURE_CONFIGURATION, config);
    //    intent.putExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME, filename);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            filename = data.getStringExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME);
            statusMessage = String.format(getString(R.string.status_capturesuccess), filename);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            filename = null;
            statusMessage = getString(R.string.status_capturecancelled);
        } else if (resultCode == VideoCaptureActivity.RESULT_ERROR) {
            filename = null;
            statusMessage = getString(R.string.status_capturefailed);
        }
        updateStatusAndThumbnail();

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateStatusAndThumbnail() {
        if (statusMessage == null) {
            statusMessage = getString(R.string.status_nocapture);
        }
        statusTv.setText(statusMessage);

        final Bitmap thumbnail = getThumbnail();

        if (thumbnail != null) {
            thumbnailIv.setImageBitmap(thumbnail);
        } else {
            thumbnailIv.setImageResource(R.drawable.thumbnail_placeholder);
        }
    }

    private Bitmap getThumbnail() {
        if (filename == null) return null;
        return ThumbnailUtils.createVideoThumbnail(filename, Thumbnails.FULL_SCREEN_KIND);
    }

    private CaptureConfiguration createCaptureConfiguration() {
        final PredefinedCaptureConfigurations.CaptureResolution resolution = getResolution(resolutionSp.getSelectedItemPosition());
        final PredefinedCaptureConfigurations.CaptureQuality quality = getQuality(qualitySp.getSelectedItemPosition());

        CaptureConfiguration.Builder builder = new CaptureConfiguration.Builder(resolution, quality);

        try {
            int maxDuration = Integer.valueOf(maxDurationEt.getEditableText().toString());
            builder.maxDuration(maxDuration);
        } catch (final Exception e) {
            //NOP
        }
        try {
            int maxFileSize = Integer.valueOf(30);
            builder.maxFileSize(maxFileSize);
        } catch (final Exception e) {
            //NOP
        }
        try {
            int fps = Integer.valueOf(24);
            builder.frameRate(fps);
        } catch (final Exception e) {
            //NOP
        }

            builder.showRecordingTime();


        
        return builder.build();
    }

    private PredefinedCaptureConfigurations.CaptureQuality getQuality(int position) {
        final PredefinedCaptureConfigurations.CaptureQuality[] quality = new PredefinedCaptureConfigurations.CaptureQuality[]{PredefinedCaptureConfigurations.CaptureQuality.HIGH, PredefinedCaptureConfigurations.CaptureQuality.MEDIUM,
                PredefinedCaptureConfigurations.CaptureQuality.LOW};
        return quality[position];
    }

    private PredefinedCaptureConfigurations.CaptureResolution getResolution(int position) {
        final PredefinedCaptureConfigurations.CaptureResolution[] resolution = new PredefinedCaptureConfigurations.CaptureResolution[]{PredefinedCaptureConfigurations.CaptureResolution.RES_1080P,
                PredefinedCaptureConfigurations.CaptureResolution.RES_720P, PredefinedCaptureConfigurations.CaptureResolution.RES_480P};
        return resolution[position];
    }

    public void playVideo() {
        if (filename == null) return;

        final Intent videoIntent = new Intent(Intent.ACTION_VIEW);
        videoIntent.setDataAndType(Uri.parse(filename), "video/*");
        try {
            startActivity(videoIntent);
        } catch (ActivityNotFoundException e) {
            // NOP
        }
    }

//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.sendmenuimage, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.uploadmenu:
//                uploadMultipart();
//                break;
//
//        }
//        return true;
//    }

    public void uploadMultipart() {

        String message=null;
        String Eusername=null;
//加urlencode
        try {
            message = URLEncoder.encode(messageET.getText().toString(),"UTF-8");

            Eusername = URLEncoder.encode(username,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.v("message",message);
        Log.v("Eusername",Eusername);
        Log.v("filepath",filename);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();


            //Creating a multi part request
            new MultipartUploadRequest(getActivity(), uploadId, ConfigURL.sendvideo)
                    .setUtf8Charset()
                    .addFileToUpload(filename, "image") //Adding file
                    .addParameter("name", Eusername) //Adding text parameter to the request
                    .addParameter("message", message) //用户描述
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .startUpload(); //Starting the upload
        } catch (Exception exc) {
            Toast.makeText(getActivity(), exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
        filename=null;

    }
}