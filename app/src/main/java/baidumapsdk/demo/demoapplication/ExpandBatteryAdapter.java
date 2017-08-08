package baidumapsdk.demo.demoapplication;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpandBatteryAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> laptopCollections;
    private List<String> laptops;
    private String mUserPhone;
    private List<String> mBatList;
    public static String emptyNote = "点击 + 按钮添加";
    private ProgressDialog dialog = null;

    public ExpandBatteryAdapter(Activity context,
                                List<String> laptops,
                                Map<String, List<String>> laptopCollections,
                                String userPhone,
                                List<String> list) {
        this.context = context;
        this.laptopCollections = laptopCollections;
        this.laptops = laptops;
        mUserPhone = userPhone;

        if (list != null) {
            mBatList = new ArrayList<String>();
            for (String bat : list) {
                mBatList.add(bat);
            }
        }
    }

    public Object getChild(int groupPosition, int childPosition) {
        return laptopCollections.get(laptops.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String laptop = (String) getChild(groupPosition, childPosition);
        final LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expand_child_item1, null);
        }

        List<String> child =
                laptopCollections.get(laptops.get(groupPosition));

        final ImageView headImg = (ImageView) convertView.findViewById(R.id.item_head_img);




        SharedPreferences sharedPreferences = context.getSharedPreferences("userimageurl", 0);
        String url = sharedPreferences.getString("url:"+(int)(groupPosition*10+childPosition), "");

        if(url.isEmpty()==false){

            Bitmap bitmap1 = BitmapFactory.decodeFile(url);

            Bitmap bitmap2 = toRoundBitmap(bitmap1);

            headImg.setImageBitmap(bitmap2);
            headImg.invalidate();

        }




        TextView name = (TextView) convertView.findViewById(R.id.item_name);
        TextView phone = (TextView) convertView.findViewById(R.id.item_phone);


//        TextView item = (TextView) convertView.findViewById(R.id.expand_battery_sim);
        final ImageView add = (ImageView) convertView.findViewById(R.id.list_item_add);


        final ImageView delete = (ImageView) convertView.findViewById(R.id.list_item_delete);
        if (childPosition == child.size() - 1) {
            delete.setVisibility(View.GONE);
            add.setVisibility(View.VISIBLE);
            phone.setTextColor(Color.BLUE);
            name.setTextColor(Color.BLUE);

            add.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    final ViewGroup contentView =
                            (ViewGroup)View.inflate(context, R.layout.add_follower, null);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("添加关注人");
                    builder.setCancelable(false);
                    builder.setPositiveButton(context.getString(R.string.positive_button),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    List<String> child =
                                            laptopCollections.get(laptops.get(groupPosition));
                                    EditText editText1 =
                                            (EditText)contentView.findViewById(R.id.textFollowerName);
                                    EditText editText2 =
                                            (EditText)contentView.findViewById(R.id.textFollowerPN);
                                    String followerName = editText1.getText().toString().trim();
                                    String friendPhone = editText2.getText().toString().trim();
                                    if (followerName == null
                                            || followerName.length() == 0
                                            || friendPhone == null
                                            || friendPhone.length() == 0
                                            ) {
                                        AlertDialogShow(context.getString(R.string.empty_follower_info));
                                        return;
                                    } else if (!Login_main.isPhoneNumberValid(friendPhone)) {
                                        AlertDialogShow(context.getString(R.string.invalid_phone_number));
                                        return;
                                    }

                                    Bundle bundle = new Bundle();
                                    bundle.putString("friendPhone",
                                            friendPhone);
                                    bundle.putString("userPhone", mUserPhone);
                                    bundle.putString("friendNickName", followerName);
                                    bundle.putString("batterySN", mBatList.get(groupPosition));
                                    bundle.putInt("groupPos", groupPosition);
                                    new TestNetworkAsyncTask(context,
                                            TestNetworkAsyncTask.TYPE_SHARE_BATTERY,
                                            bundle).execute(UserView.mShareBatteryUrl);
                                    ensureDialog();
                                }
                            });
                    builder.setNegativeButton(context.getString(R.string.negative_button),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    builder.setView(contentView);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        } else {
            delete.setVisibility(View.VISIBLE);
            add.setVisibility(View.GONE);
            phone.setTextColor(Color.BLACK);
            name.setTextColor(Color.BLACK);

            delete.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("你确定要删除此好友关注人？");
                    builder.setCancelable(false);
                    builder.setPositiveButton(context.getString(R.string.positive_button),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    List<String> child =
                                            laptopCollections.get(laptops.get(groupPosition));
                                    String fpStr = child.get(childPosition);
                                    int index = fpStr.indexOf('(');
                                    int index2 = fpStr.indexOf(')');
                                    String fpSubStr = fpStr.substring(index + 1, index2);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("friendPhone", fpSubStr);
                                    bundle.putString("userPhone", mUserPhone);
                                    bundle.putString("batSN", mBatList.get(groupPosition));
                                    bundle.putInt("groupPos", groupPosition);
                                    bundle.putInt("childPos", childPosition);
                                    new TestNetworkAsyncTask(context,
                                            TestNetworkAsyncTask.TYPE_DEL_GROUP_CHILD,
                                            bundle).execute(UserView.mUnShareBatteryUrl);
                                    ensureDialog();
                                }
                            });
                    builder.setNegativeButton(context.getString(R.string.negative_button),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });


            headImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(context,SelectPhotoAcitivity.class);
                    intent.putExtra("reqCode", groupPosition * 10 + childPosition);

                    context.startActivityForResult(intent, 2);


                    SharedPreferences sharedPreferences = context.getSharedPreferences("userimageurl", 0);
                    String url = sharedPreferences.getString("url:"+(int)(groupPosition*10+childPosition), "");


                    if(url.isEmpty()==false){

                        Bitmap bitmap1 = BitmapFactory.decodeFile(url);

                        Bitmap bitmap2 = toRoundBitmap(bitmap1);

                        headImg.setImageBitmap(bitmap2);
                        headImg.invalidate();

                    }

                }
            });


        }
        if(laptop != null){
            if(laptop.length()>13){
                String []s = laptop.split(":");
                if(s[1] != null){
                    String sName = s[1].substring(0,s[1].length()-13);
                    String sPhone = s[1].substring(s[1].length()-12,s[1].length()-1);

                    name.setText(sName.trim());
                    phone.setText(sPhone.trim());

                }

            }
        }else{
            name.setText("");
            phone.setText(laptop);
        }

//        phone.setText(laptop);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        List<String> list = laptopCollections.get(laptops.get(groupPosition));
        if (list == null)
            return 0;
        else
            return list.size();
    }

    public Object getGroup(int groupPosition) {
        return laptops.get(groupPosition);
    }

    public int getGroupCount() {
        return laptops.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String laptopName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.picker_item,
                    null);
        }
        TextView item = (TextView) convertView/*.findViewById(R.id.laptop)*/;
        item.setTypeface(null, Typeface.BOLD);
        item.setText(laptopName);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addGroupChildItem(int groupPos, Bundle bundle) {
        List<String> child =
                laptopCollections.get(laptops.get(groupPos));
        dismissProgressDialog();
        String friendPhone = UserBatList.mFollowerPre
                + bundle.getString("friendName")
                + " ("
                + bundle.getString("friendPhone")
                + ")";
        child.add(0, friendPhone);
        notifyDataSetChanged();
    }

    public void deleteGroupChildItem(int groupPos, int childPos) {
        List<String> child =
                laptopCollections.get(laptops.get(groupPos));
        dismissProgressDialog();
        child.remove(childPos);
        notifyDataSetChanged();
    }

    public void ensureDialog() {
        if (dialog == null) {
            String title = context.getString(R.string.process_wait_title);
            String msg = context.getString(R.string.process_wait_msg);

            dialog = ProgressDialog.show(context, title, msg, true, true);
            dialog.setIcon(android.R.drawable.ic_dialog_info);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (dialog != null) {
            dialog.hide();
            dialog.dismiss();
            dialog = null;
        }
    }

    public void AlertDialogShow(String message) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(true);
        Dialog dialog = builder.create();
        dialog.show();
    }


    /**
     * 把bitmap转成圆形
     * */
    public Bitmap toRoundBitmap(Bitmap bitmap){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        int r=0;
        int l=0;
        //取最短边做边长
        if(width<height){
            r=width;
            l=height;
        }else{
            r=height;
            l=width;
        }
        //构建一个bitmap
        Bitmap backgroundBm=Bitmap.createBitmap(l,l, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas=new Canvas(backgroundBm);
        Paint p=new Paint();
        //设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);
        RectF rect=new RectF(0, 0, l, l);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, l/2,l/2, p);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return backgroundBm;
    }


}


