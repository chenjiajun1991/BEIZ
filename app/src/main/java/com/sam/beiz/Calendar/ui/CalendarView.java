package com.sam.beiz.Calendar.ui;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sam.beiz.Calendar.bean.ScheduleDAO;
import com.sam.beiz.Calendar.bean.ScheduleDateTag;
import com.sam.beiz.Calendar.calender.SpecialCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import baidumapsdk.demo.demoapplication.R;

/**
 * 日历gridview中的每一个item显示的textview
 * @author jack_peng
 *
 */
public class CalendarView extends BaseAdapter {
	
	//private static final String Tag="CalendarView";

	private ScheduleDAO dao = null;
	private boolean isLeapyear = false;  //是否为闰年
	private int daysOfMonth = 0;      //某月的天数
	private int firstDayOfMonth = 0;        //具体某一天是星期几
	private int lastDaysOfMonth = 0;  //上一个月的总天数
	private Context context;
	private String[] dayNumber = new String[49];  //一个gridview中的日期存入此数组中
	private static String week[] = {"日","一","二","三","四","五","六"};
	//private static String week[] = {"SUN","MON","TUE","WED","THU","FRI","SAT"};
	private SpecialCalendar specialCalendar = null;
	private Resources res = null;
	private Drawable drawable = null;
	
	private String currentYear = "";
	private String currentMonth = "";
	private String currentDay = "";
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	private int currentFlag = -1;     //用于标记当天
	private int[] schDateTagFlag = null;  //存储当月所有的日程安排的日期
	
	private String showYear = "";   //用于在头部显示的年份
	private String showMonth = "";  //用于在头部显示的月份
	private String animalsYear = "";
	private String leapMonth = "";   //闰哪一个月
	//系统当前时间
	private String sysDate = "";
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";

	//日程时间(需要标记的日程日期)
	private String sch_year = "";
	private String sch_month = "";
	private String sch_day = "";
	
	public CalendarView(){
		Date date = new Date();
		sysDate = sdf.format(date);  //当期日期
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];
		
	}
	
	public CalendarView(Context context, Resources rs, int jumpMonth, int jumpYear, int year_c, int month_c, int day_c){
		this();
		this.context= context;
		specialCalendar = new SpecialCalendar();
		this.res = rs;
		
		int stepYear = year_c+jumpYear;
		int stepMonth = month_c+jumpMonth ;
		if(stepMonth > 0){
			//往下一个月滑动
			if(stepMonth%12 == 0){
				stepYear = year_c + stepMonth/12 -1;
				stepMonth = 12;
			}else{
				stepYear = year_c + stepMonth/12;
				stepMonth = stepMonth%12;
			}
		}else{
			//往上一个月滑动
			stepYear = year_c - 1 + stepMonth/12;
			stepMonth = stepMonth%12 + 12;
			if(stepMonth%12 == 0){
				
			}
		}
	
		currentYear = String.valueOf(stepYear);;  //得到当前的年份
		currentMonth = String.valueOf(stepMonth);  //得到本月 （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
		currentDay = String.valueOf(day_c);  //得到当前日期是哪天
		
		getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
		
	}
	
	public CalendarView(Context context, Resources rs, int year, int month, int day){
		this();
		this.context= context;
		specialCalendar = new SpecialCalendar();
		this.res = rs;
		currentYear = String.valueOf(year); //得到跳转到的年份
		currentMonth = String.valueOf(month);  //得到跳转到的月份
		currentDay = String.valueOf(day);  //得到跳转到的天
		
		getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return dayNumber.length;
	}

	
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	//给Gridview添加值
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.calendar, null);
		 }
		TextView textView = (TextView) convertView.findViewById(R.id.tvtext);
		String d = dayNumber[position].split("\\.")[0];
	//	String dv = dayNumber[position].split("\\.")[1];
	//	Log.i("calendarview", d+","+dv);
		//Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica.ttf");
		//textView.setTypeface(typeface);
		//SpannableString sp = new SpannableString(d+"\n"+dv);
		SpannableString sp = new SpannableString(d+"\n"+"\n");
		
	//	Log.i(Tag, "SpannableString---"+sp);

		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.2f) , 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new TypefaceSpan("monospace"), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//sp.setSpan(new BackgroundColorSpan(Color.RED), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		if(dv != null || dv != ""){
//			//农历显示的样式
//            sp.setSpan(new RelativeSizeSpan(0.75f), d.length()+1, dayNumber[position].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            //sp.setSpan(new BackgroundColorSpan(Color.RED), d.length()+1, dayNumber[position].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		}
		
		textView.setText(sp);
		//textView.setTextColor(R.color.little_grey);
		textView.setTextColor(Color.RED);
		
		// 当前月字体属性，设字体和背景
		if (position < daysOfMonth + firstDayOfMonth+7 && position >= firstDayOfMonth+7) {
			
			
			
			textView.setTextColor(Color.BLACK);// 当月字体设黑
			drawable = res.getDrawable(R.drawable.item);
			textView.setBackgroundDrawable(drawable);
			textView.setBackgroundColor(Color.WHITE);
			//星期一和星期六加红
			if(position%7==0||position%7==6){
				textView.setTextColor(Color.rgb(255,120,20));
			}
			
		}else {

			//设置周的字体属性,如果position为0-6
			if(position<7){
				
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(14.0f);
//				textView.setGravity(45);
		//		drawable = res.getDrawable(R.drawable.week_top);
				textView.setBackgroundDrawable(drawable);
			}
			//设置当月其他不在月内显示的字体为浅灰色
			else{
		
				textView.setTextColor(Color.rgb(200, 195, 200));
			}
		}
		if(schDateTagFlag != null && schDateTagFlag.length >0){
			for(int i = 0; i < schDateTagFlag.length; i++){
				if(schDateTagFlag[i] == position){
					//设置有日程安排的标记背景
					
					textView.setBackgroundResource(R.drawable.back2);
				}
			}
		}
		//设置当天的背景
		if(currentFlag == position){ 
			
			drawable = res.getDrawable(R.drawable.back);
			textView.setBackgroundDrawable(drawable);
			textView.setTextColor(Color.WHITE);
		}
		//获得每个月的周末
		Calendar calendar= Calendar.getInstance();
		if(calendar.get(Calendar.DAY_OF_WEEK)== Calendar.SATURDAY||calendar.get(Calendar.DAY_OF_WEEK)== Calendar.SUNDAY){
			textView.setTextColor(Color.rgb(255, 145, 90));
		}
		
		return convertView;
	}
	
	//得到某年的某月的天数且这月的第一天是星期几
	public void getCalendar(int year, int month){
		isLeapyear = specialCalendar.isLeapYear(year);              //是否为闰年
		daysOfMonth = specialCalendar.getDaysOfMonth(isLeapyear, month);  //某月的总天数
		firstDayOfMonth = specialCalendar.getWeekdayOfMonth(year, month);      //某月第一天为星期几
		lastDaysOfMonth = specialCalendar.getDaysOfMonth(isLeapyear, month-1);  //上一个月的总天数
		
		Log.d("DAY", isLeapyear+" ======  "+daysOfMonth+"  ============  "+firstDayOfMonth+"  =========   "+lastDaysOfMonth);
		getweek(year,month);
	}
	
	//将一个月中的每一天的值添加入数组dayNuMber中
	private void getweek(int year, int month) {
		int j = 1;
		int flag = 0;
		String lunarDay = "";
		
		//得到当前月的所有日程日期(这些日期需要标记并计数)
		dao = new ScheduleDAO(context);
		ArrayList<ScheduleDateTag> dateTagList = dao.getTagDate(year,month);
		if(dateTagList != null && dateTagList.size() > 0){
			schDateTagFlag = new int[dateTagList.size()];
		}
		
		for (int i = 0; i < dayNumber.length; i++) {
			// 周一
			if(i<7){
				dayNumber[i]=week[i]+"."+" ";
			}
			else if(i < firstDayOfMonth+7){  //前一个月
				int temp = lastDaysOfMonth - firstDayOfMonth+1-7;
				//获得阳历对应的农历
				dayNumber[i] = (temp + i)+"."+lunarDay;
			}else if(i < daysOfMonth + firstDayOfMonth+7){   //本月
				String day = String.valueOf(i-firstDayOfMonth+1-7);
				//得到的日期
				dayNumber[i] = i-firstDayOfMonth+1-7+"."+lunarDay;
				//对于当前月才去标记当前日期
				if(sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)){
					//标记当前日期
					currentFlag = i;
				}
				
				//标记日程日期
				if(dateTagList != null && dateTagList.size() > 0){
					for(int m = 0; m < dateTagList.size(); m++){
						ScheduleDateTag dateTag = dateTagList.get(m);
						int matchYear = dateTag.getYear();
						int matchMonth = dateTag.getMonth();
						int matchDay = dateTag.getDay();
						if(matchYear == year && matchMonth == month && matchDay == Integer.parseInt(day)){
							schDateTagFlag[flag] = i;
							flag++;
						}
					}
				}
				
				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
			}else{   //下一个月
				dayNumber[i] = j+"."+lunarDay;
				j++; 
			}
		}
        
        String dayStr = "";
        for(int i = 0; i < dayNumber.length; i++){
        	dayStr = dayStr+dayNumber[i]+":";
        }
   //     Log.d("calendarview",dayStr);


	}
	
	
	public void matchScheduleDate(int year, int month, int day){
		
	}
	
	/**
	 * 点击每一个item时返回item中的日期
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position){
		return dayNumber[position];
	}
	
	/**
	 * 在点击gridView时，得到这个月中第一天的位置
	 * @return
	 */
	public int getStartPositon(){
		return firstDayOfMonth+7;
	}
	
	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 * @return
	 */
	public int getEndPosition(){
		return  (firstDayOfMonth+daysOfMonth+7)-1;
	}
	
	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}
	
	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}
	
	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}
}
