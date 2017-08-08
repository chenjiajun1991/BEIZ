package com.sam.beiz.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import baidumapsdk.demo.demoapplication.PreActivity;


/**
 * Created by SAM-PC2 on 2017/4/1.
 */
public class SecondFragment extends Fragment {

    private static final String TAG = "SecondFragment";
    protected View mView;
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

//        SharedPreferences mainPref = mContext.getSharedPreferences(getString(R.string.shared_pref_pacakge),
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = mainPref.edit();
//        editor.putString("lastAccount", "18297966410");
//        editor.putInt("accountType", 0);
//        editor.commit();

        Intent intent = new Intent(mContext, PreActivity.class);
        this.getActivity().startActivity(intent);
        this.getActivity().finish();



    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mContext = getActivity();
//        mView = inflater.inflate(R.layout.activity_third, container, false);
//
////        String tag = getArguments().getString("tag");
////        TextView tv_second = (TextView) mView.findViewById(R.id.tv_second);
////        tv_second.setText(tv_second.getText().toString()+"\n来源："+tag);
//
//        return mView;
//    }
}
