package com.sam.beiz.Calendar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sam.beiz.Calendar.ui.ScheduleViewAddActivity;


public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
        	ScheduleViewAddActivity.setAlart(context);
        }
    }
}

