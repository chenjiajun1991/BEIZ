package com.sam.beiz.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import baidumapsdk.demo.demoapplication.R;

/**
 * Created by zhejunliu on 2017/8/2.
 */

public class MyFragment extends Fragment {
    FragmentManager fragmentManager;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v=inflater.inflate(R.layout.myfragment, container, false);
        if (savedInstanceState == null) {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().add(R.id.container, new CaptureDemoFragment()).commit();
        }

        return v;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
