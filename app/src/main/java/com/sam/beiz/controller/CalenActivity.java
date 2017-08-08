package com.sam.beiz.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.sam.beiz.Calendar.bean.ScheduleDAO;
import com.sam.beiz.Calendar.ui.CalendarView;
import com.sam.beiz.Calendar.ui.ScheduleViewAddActivity;
import com.sam.beiz.Calendar.vo.ScheduleVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import baidumapsdk.demo.demoapplication.MainActivity;
import baidumapsdk.demo.demoapplication.R;

public class CalenActivity extends Activity implements GestureDetector.OnGestureListener{

    private static final String Tag="CalActivity";
    //   private LunarCalendar lcCalendar = null;
    private ViewFlipper flipper = null;
    private GestureDetector gestureDetector = null;
    private CalendarView calV = null;
    private GridView gridView = null;
    private TextView topText = null;
    private Drawable draw = null;
    private static int jumpMonth = 0;      //每次滑动，增加或减去一个月,默认为0（即显示当前月）
    private static int jumpYear = 0;       //滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private String currentDate = "";
    private ScheduleDAO dao = null;
    private ScheduleVO scheduleVO;
    private String[] scheduleIDs;
    private ArrayList<String> scheduleDate;
    private ArrayList<ScheduleVO> mschedulevo;
    private Dialog builder;
    private ScheduleVO scheduleVO_del;
    private TextView schdule_tip;
    private Button add;
    private Button quit;
    private TextView day_tv;
    private ListView listView;
    private TextView weekday;
    private ListView list;
    private String dateInfo;//点击gridview的日期信息
    private LayoutInflater inflater;
    private ListView timeList;


    public CalenActivity() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date);  //当期日期
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
        dao = new ScheduleDAO(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //     setContentView(R.layout.calender_main);
        setContentView(R.layout.activity_calen);
        gestureDetector = new GestureDetector(this);
        flipper = (ViewFlipper) findViewById(R.id.flipper);
        flipper.removeAllViews();
        calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);

        addGridView();
        gridView.setAdapter(calV);
        //flipper.addView(gridView);
        flipper.addView(gridView,0);

        topText = (TextView) findViewById(R.id.schedule_toptext);
        addTextToTopTextView(topText);




        timeList=(ListView)findViewById(R.id.timeline_lv);



        final View inflate=getLayoutInflater().inflate(R.layout.timeitem, null);
        String  from2[]={"data","content"};
        int to2[]={R.id.show_time,R.id.show_title};
        SimpleAdapter adapter2=new SimpleAdapter(CalenActivity.this,initList() ,R.layout.timeitem, from2, to2);
        timeList.setAdapter(adapter2);


    }

    private List<HashMap<String,String>> initList() {
        List<HashMap<String,String>> listData=new ArrayList<HashMap<String,String>>();

        mschedulevo=new ArrayList<>();
        mschedulevo=dao.getAllSchedulesort();

        if(mschedulevo==null){
            Toast.makeText(CalenActivity.this,"您此时没有计划",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mschedulevo.size()>0){
                for(int i=0;i<mschedulevo.size();i++){
                    HashMap<String, String> map=new HashMap<String, String>();
                    Long dv=Long.valueOf(mschedulevo.get(i).getAlartime());
                    Date df=new java.util.Date(dv);
                    String newdata=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(df);
                    String newcontent=mschedulevo.get(i).getScheduleContent();
                    map.put("data",newdata);
                    map.put("content",newcontent);

                    Log.v("data",newdata);
                    Log.v("content",newcontent);
                    listData.add(map);
                }
            }

        }
        return listData;


    }


    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        int gvFlag = 0;         //每次添加gridview到viewflipper中时给的标记
        if (e1.getX() - e2.getX() > 50) {
            //像左滑动
            addGridView();   //添加一个gridView
            jumpMonth++;     //下一个月

            calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
            gridView.setAdapter(calV);
            //flipper.addView(gridView);
            addTextToTopTextView(topText);
            gvFlag++;
            flipper.addView(gridView, gvFlag);
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
            this.flipper.showNext();
            flipper.removeViewAt(0);
            return true;
        } else if (e1.getX() - e2.getX() < -50) {
            //向右滑动
            addGridView();   //添加一个gridView
            jumpMonth--;     //上一个月

            calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
            gridView.setAdapter(calV);
            gvFlag++;
            addTextToTopTextView(topText);
            //flipper.addView(gridView);
            flipper.addView(gridView,gvFlag);

            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_out));
            this.flipper.showPrevious();
            flipper.removeViewAt(0);
            return true;
        }
        return false;
    }


//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        menu.add(0, menu.FIRST, menu.FIRST, "今天");
//        menu.add(0, menu.FIRST+1, menu.FIRST+1, "跳转");
//        menu.add(0, menu.FIRST+2, menu.FIRST+2, "日期转换");
//        return super.onCreateOptionsMenu(menu);
//    }





    public boolean onTouchEvent(MotionEvent event) {

        return this.gestureDetector.onTouchEvent(event);
    }


    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }


    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }


    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }


    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }


    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 添加画板头部的年份 闰哪月等信息
     * */
    public void addTextToTopTextView(TextView view){
        StringBuffer textDate = new StringBuffer();
        draw = getResources().getDrawable(R.drawable.schedule_title_bg);
        view.setBackgroundDrawable(draw);
        textDate.append(calV.getShowYear()).append("年").append(
                calV.getShowMonth()).append("月").append("\t");
        view.setText(textDate);
        view.setTextColor(Color.WHITE);
        view.setTextSize(15.0f);
        view.setTypeface(Typeface.DEFAULT_BOLD);
    }



    //添加gridview,显示具体的日期
    private void addGridView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.FILL_PARENT);
        //取得屏幕的宽度和高度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int Width = display.getWidth();
        int Height = display.getHeight();

        Log.d(Tag, "屏幕分辨率=="+"height*weight"+Height+Width);

        gridView = new GridView(this);
        gridView.setNumColumns(7);
        gridView.setColumnWidth(46);
        //	gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        if(Width == 480 && Height == 800){
            gridView.setColumnWidth(69);
        }else if(Width==800&&Height==1280){
            gridView.setColumnWidth(69);
        }


        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // 去除gridView边框
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        // gridView.setBackgroundResource(R.drawable.gridview_bk);
        gridView.setOnTouchListener(new View.OnTouchListener() {
            //将gridview中的触摸事件回传给gestureDetector

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return CalenActivity.this.gestureDetector
                        .onTouchEvent(event);
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //gridView中的每一个item的点击事件

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                //点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
                int startPosition = calV.getStartPositon();
                int endPosition = calV.getEndPosition();
                if(startPosition <= position  && position <= endPosition){
                    String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];  //这一天的阳历
                    //String scheduleLunarDay = calV.getDateByClickItem(position).split("\\.")[1];  //这一天的阴历
                    String scheduleYear = calV.getShowYear();
                    String scheduleMonth = calV.getShowMonth();
                    String week = "";

                    //        Log.i("日程历史浏览", scheduleDay);

                    //通过日期查询这一天是否被标记，如果标记了日程就查询出这天的所有日程信息
                    scheduleIDs = dao.getScheduleByTagDate(Integer.parseInt(scheduleYear), Integer.parseInt(scheduleMonth), Integer.parseInt(scheduleDay));

                    //得到这一天是星期几
                    switch(position%7){
                        case 0:
                            week = "星期日";
                            break;
                        case 1:
                            week = "星期一";
                            break;
                        case 2:
                            week = "星期二";
                            break;
                        case 3:
                            week = "星期三";
                            break;
                        case 4:
                            week = "星期四";
                            break;
                        case 5:
                            week = "星期五";
                            break;
                        case 6:
                            week = "星期六";
                            break;
                    }

                    scheduleDate = new ArrayList<String>();
                    scheduleDate.add(scheduleYear);
                    scheduleDate.add(scheduleMonth);
                    scheduleDate.add(scheduleDay);
                    scheduleDate.add(week);

                    /**
                     *
                     * 通过scheduleIDs是否被标记，标记在通过listview显示出来
                     */


//	            	  Intent mIntent=new Intent(CalendarActivity.this, ScheduleDetailsActivity.class);
////                	  startActivity(mIntent);
//
                    LayoutInflater inflater=getLayoutInflater();
                    View linearlayout= inflater.inflate(R.layout.schedule_details, null);
                    add=(Button)linearlayout.findViewById(R.id.btn_add);
                    quit=(Button) linearlayout.findViewById(R.id.btn_back);
                    day_tv=(TextView) linearlayout.findViewById(R.id.todayDate);
                    schdule_tip=(TextView) linearlayout.findViewById(R.id.schdule_tip);
                    listView=(ListView)linearlayout.findViewById(R.id.schedulelist);
                    list=(ListView)linearlayout.findViewById(R.id.schedulelist);

                    dateInfo=scheduleYear+"年"+scheduleMonth+"月"+scheduleDay+"日";
                    day_tv.setText(dateInfo);

//                    Log.i("scheduleDate",""+scheduleDate);
                    //添加日程按钮
                    //TableLayout dialog_tab=(TableLayout) linearlayout.findViewById(R.id.dialog_tab);
                    add.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if(builder!=null&&builder.isShowing()){
                                builder.dismiss();
                                Intent intent = new Intent();
                                intent.putStringArrayListExtra("scheduleDate", scheduleDate);
                                intent.setClass(CalenActivity.this, ScheduleViewAddActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                    //返回按钮
                    quit.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if(builder!=null&&builder.isShowing()){
                                builder.dismiss();
                            }
                        }
                    });

                    //如果被标记，则加载相应的日程信息列表
                    if(scheduleIDs != null && scheduleIDs.length > 0){


                        //list.setAdapter(new MyAdapter());
                        final View inflate=getLayoutInflater().inflate(R.layout.schedule_detail_item, null);
                        //通过arraylist绑定数据导listview中去
                        //     ArrayList<HashMap<String,String>> Data = new ArrayList<HashMap<String, String>>();
                        List<HashMap<String,String>> Data = new ArrayList<HashMap<String,String>>();
                        final ScheduleDAO dao=new ScheduleDAO(CalenActivity.this);
                        String time="";
                        String content="";
                        String type="";
                        for(int i=0;i<scheduleIDs.length;i++){
                            scheduleVO=dao.getScheduleByID(CalenActivity.this,Integer.parseInt(scheduleIDs[i]));
                            time="";
                            content="";
                            type="";

                            time=dateInfo+" "+scheduleVO.getTime();
                            content=scheduleVO.getScheduleContent();
                            type=scheduleVO.getType();



                            HashMap<String, String> map=new HashMap<String, String>();
                            map.put("date", time);
                            map.put("content", content);
                            map.put("type",type);
//                            Log.v("firstcontent",content);
//                            Log.v("firsdata",time);
//                            Log.v("firstype",type);

                            Data.add(map);

                        }
                        String  from[]={"date","content","type"};
                        int to[]={R.id.itemTime,R.id.itemContent,R.id.text_item};

                        SimpleAdapter adapter=new SimpleAdapter(CalenActivity.this, Data, R.layout.schedule_detail_item, from, to);

                        list.setAdapter(adapter);

                        //点击list的item相应事件
//	                	  list.setOnClickListener(MainActivity.this);
////	                	  list.setOnLongClickListener(MainActivity.this);
                        final String finalContent = content;
                        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                scheduleVO_del=  (ScheduleVO) view.getTag();
                                //         scheduleVO_del=(ScheduleVO)view.getTag(i);
                                final TextView contect=(TextView)view.findViewById(R.id.itemContent);
                                TextView text=(TextView)view.findViewById(R.id.text_item);
                                TextView data=(TextView)view.findViewById(R.id.itemTime);;


                                Dialog alertDialog = new AlertDialog.Builder(CalenActivity.this).
                                        setMessage("删除日程信息？").
                                        setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                dao.deleteBytextName( contect.getText().toString());

                                                //       ScheduleViewAddActivity.setAlart(MainActivity.this);

                                                Intent intent=new Intent(CalenActivity.this,CalenActivity.class);
                                                finish();
                                                startActivity(intent);
//            	if(builder!=null&&builder.isShowing()){
//            		builder.dismiss();
//            	}

                                            }

                                        }).
                                        setNegativeButton("取消",null). create();
                                alertDialog.show();





                                return true;
                            }
                        });


                    }else{ //如果没有标记位直接则跟换为“暂无安排”


                        schdule_tip.setText("暂无安排");
                        listView.setVisibility(View.INVISIBLE);

//		                  Intent intent = new Intent();
//		                  intent.putExtra("top_Time", dateInfo);
//		                  Log.i("calendar", "calendar ifo-->"+dateInfo);
//		                  intent.putStringArrayListExtra("scheduleDate", scheduleDate);
//		                  intent.setClass(CalendarActivity.this,ScheduleDetailsNoDataActivity.class);
//		                  startActivity(intent);
                    }

                    //以dialog的形式显示到windows上
                    builder =	new Dialog(CalenActivity.this,R.style.FullScreenDialog);
                    builder.setContentView(linearlayout);
                    WindowManager windowManager = getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = builder.getWindow().getAttributes();
                    lp.width = (int)(display.getWidth()); //设置宽度
                    lp.height=display.getHeight();
                    builder.getWindow().setAttributes(lp);
                    builder.setCanceledOnTouchOutside(true);
                    builder.show();


                }
            }
        });
        gridView.setLayoutParams(params);
    }





    @Override
    protected void onRestart() {
        int xMonth = jumpMonth;
        int xYear = jumpYear;
        int gvFlag =0;
        jumpMonth = 0;
        jumpYear = 0;
        addGridView();   //添加一个gridView
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
        calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
        gridView.setAdapter(calV);
        addTextToTopTextView(topText);
        gvFlag++;
        flipper.addView(gridView,gvFlag);
        flipper.removeViewAt(0);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}