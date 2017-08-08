package com.sam.beiz.adapter;

import java.util.ArrayList;
import java.util.List;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.sam.beiz.Ad.picasso.PicassoClient;
import com.sam.beiz.dao.BannerClickListener;
import com.utils.ScreenUtil;

import baidumapsdk.demo.demoapplication.R;

@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
public class BannerPager extends RelativeLayout implements View.OnClickListener{
    private static final String TAG = "BannerPager";

    private Context mContext;
    private BannerClickListener mListener;
    private ViewPager mPager;
    private List<ImageView> mViewList = new ArrayList<ImageView>();
    private RadioGroup mGroup;
    private int mCount;
    private LayoutInflater mInflater;
    private BannerHandler mHandler;
    private int dip_13;
    private static int mInterval = 3000;

    private static int currentItem = 0;



    public BannerPager(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public BannerPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setOnBannerListener(BannerClickListener listener) {
        mListener = listener;
    }

    public void start() {
        mHandler.sendEmptyMessageDelayed(0, mInterval);
    }




    public void setImage(ArrayList<String> imageList) {
        for (int i = 0; i < imageList.size(); i++) {
            String imageID = ((String) imageList.get(i));
            ImageView ivNew = new ImageView(mContext);
            ivNew.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            ivNew.setScaleType(ImageView.ScaleType.FIT_XY);
            PicassoClient.downloadImage(getContext(), imageID, ivNew);
            //  ivNew.setImageResource(imageID);
            ivNew.setOnClickListener(this);
            mViewList.add(ivNew);

        }
        mPager.setAdapter(new ImageAdapater());
        mPager.setOnPageChangeListener(new BannerChangeListener(this));

        mCount = imageList.size();
        for (int i = 0; i < mCount; i++) {
            RadioButton radio = new RadioButton(mContext);
            radio.setLayoutParams(new RadioGroup.LayoutParams(dip_13, dip_13));
            radio.setGravity(Gravity.CENTER);
            radio.setButtonDrawable(R.drawable.radio_btn_select);
            mGroup.addView(radio);
        }

        mPager.setCurrentItem(0);
        setButton(0);
        mHandler = new BannerHandler();
        start();
    }



    public void setImages(ArrayList<Integer> imageList) {
        for (int i = 0; i < imageList.size(); i++) {
            Integer imageID = ((Integer) imageList.get(i)).intValue();
            ImageView ivNew = new ImageView(mContext);
            ivNew.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            ivNew.setScaleType(ImageView.ScaleType.FIT_XY);
            ivNew.setImageResource(imageID);
            ivNew.setOnClickListener(this);
            mViewList.add(ivNew);
            Log.d(TAG, "i=" + i + ",image_id=" + imageID);
        }
        mPager.setAdapter(new ImageAdapater());
        mPager.setOnPageChangeListener(new BannerChangeListener(this));

        mCount = imageList.size();
        for (int i = 0; i < mCount; i++) {
            RadioButton radio = new RadioButton(mContext);
            radio.setLayoutParams(new RadioGroup.LayoutParams(dip_13, dip_13));
            radio.setGravity(Gravity.CENTER);
            radio.setButtonDrawable(R.drawable.radio_btn_select);
            mGroup.addView(radio);
        }

        mPager.setCurrentItem(0);
        setButton(0);
        mHandler = new BannerHandler();
        start();
    }

    private void setButton(int position) {
        ((RadioButton) mGroup.getChildAt(position)).setChecked(true);
    }

    public void setRadioGroupVisible(boolean b){
        if(b){
            mGroup.setVisibility(VISIBLE);
        }else{
            mGroup.setVisibility(GONE);
        }
    }

    private void init() {
        mInflater = ((Activity) mContext).getLayoutInflater();
        View view = mInflater.inflate(R.layout.banner_pager, null);
        mPager = (ViewPager) view.findViewById(R.id.banner_pager);
        mGroup = (RadioGroup) view.findViewById(R.id.banner_pager_group);
        addView(view);
        dip_13 = ScreenUtil.dip2px(mContext, 15);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("HandlerLeak")
    private class BannerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            scrollToNext();
            sendEmptyMessageDelayed(0, mInterval);
        }
    }

    public void scrollToNext() {
        int index = mPager.getCurrentItem() + 1;
        if (mViewList.size() <= index) {
            index = 0;
        }
        mPager.setCurrentItem(index);
    }

    private class ImageAdapater extends PagerAdapter {

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

    }

    private class BannerChangeListener implements ViewPager.OnPageChangeListener {
        private BannerChangeListener(BannerPager bannerPager) {


        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            currentItem = mPager.getCurrentItem();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {


        }

        @Override
        public void onPageSelected(int arg0) {
            setButton(arg0);
        }
    }

    @Override
    public void onClick(View v) {
        int position = mPager.getCurrentItem();
        mListener.onBannerClick(position);
    }

    public int getPosion(){
        return currentItem;
    }



}