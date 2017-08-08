package com.sam.beiz.Calendar.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.sam.beiz.Calendar.bean.ScheduleDAO;
import com.sam.beiz.Calendar.vo.ScheduleVO;

import java.util.ArrayList;

import baidumapsdk.demo.demoapplication.R;

public class ScheduleInfoDetailActivity extends Activity {


	private TextView scheduledetailsTextView;
	private TextView scheduledetailsType,scheduledetailsDate;
	
	
	private ScheduleDAO dao = null;
	private ArrayList<String> scheduleDate;
	private String[] scheduleIDs;
	private ScheduleVO scheduleVO;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scan_schedule_details);
		
		dao = new ScheduleDAO(this);
		
		
		initView();
		
		Intent intent = getIntent();
		//scheduleID = Integer.parseInt(intent.getStringExtra("scheduleID"));
		//一个日期可能对应多个标记日程(scheduleID)
		scheduleDate=intent.getStringArrayListExtra("scheduleDate");
		
		Log.i("scheduinfodetails", "获得日程详细日期="+scheduleDate);
		scheduledetailsDate.setText(""+scheduleDate);
		scheduleVO=(ScheduleVO) intent.getExtras().getSerializable("scheduleVO");
		//显示日程详细信息
		//handlerInfo(scheduleVO);
		//setContentView(layout);
		
				
	}
	
	private void initView() {
		scheduledetailsTextView=(TextView)this.findViewById(R.id.scheduledetailsTextView);
		scheduledetailsType=(TextView)this.findViewById(R.id.scheduledetailsType);
		scheduledetailsDate=(TextView)this.findViewById(R.id.scheduledetailsDate);
	}

	/**
	 * 显示日程所有信息
	 */
	public void handlerInfo(ScheduleVO scheduleVO){
		//scheduledetailsType.setText(CalendarConstant.sch_type[scheduleVO.getScheduleTypeID()]);
		//scheduledetailsTextView.setText(scheduleVO.getScheduleContent());
		scheduledetailsDate.setText(scheduleVO.getScheduleDate());
		
		
		//长时间按住日程类型textview就提示是否删除日程信息
//		scheduledetailsTextView.setOnLongClickListener(new OnLongClickListener() {
//			
//			
//			public boolean onLongClick(View v) {
//
//				scheduleVO = (ScheduleVO) v.getTag();
//				
//				new AlertDialog.Builder(ScheduleInfoDetailActivity.this).setTitle("删除日程").setMessage("确认删除").setPositiveButton("确认", new OnClickListener() {
//					
//					
//					public void onClick(DialogInterface dialog, int which) {
//						dao.delete(scheduleVO.getScheduleID());
//						Toast.makeText(ScheduleInfoDetailActivity.this, "日程已删除", 0).show();
//						ScheduleViewAddActivity.setAlart(ScheduleInfoDetailActivity.this);
//						finish();
//					}
//				}).setNegativeButton("取消", null).show();
//				
//				return true;
//			}
//		});
		
		
	}
}
