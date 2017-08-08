package com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;


import java.io.InputStream;

/**
 * Created by SAM-PC2 on 2017/6/6.
 */
public class BitmapUtil {
    /**
     * 1、通过BitmapFactory取得Bitmap
     * Bitmap bm=BitmapFactory.decodeStream(InputStream is );
     */

    /**
     * 2、Bimap的createBitmap()方法
     * Bitmap newbm = Bitmap.createBitmap( Bitmap s, int x, int y, int w, int h, Matrix m, boolean f);
     * 该方法可实现位图的缩放、裁剪、旋转操作
     * Bitmap s：要处理的原始位图
     * int x ,y：起始位置坐标
     * int w：要截的图的宽度
     * int h：要截的图的宽度
     * Matrix m 矩阵，主要是用于平面的缩放、平移、旋转
     * boolean f：是否保证等比
     * 返回值：返回处理后的Bitmap
     */

    /**
     * 通过资源id转化成Bitmap
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitmapById(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 缩放图片
     * @param bm 要缩放图片
     * @param newWidth 宽度
     * @param newHeight 高度
     * @return处理后的图片
     */
    public static  Bitmap  scaleImage(Bitmap bm, int newWidth, int newHeight){
        if (bm == null){
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        if (bm != null & !bm.isRecycled()){
            bm.recycle();//销毁原图片
            bm = null;
        }
        return newbm;
    }

    /**
     * 按照一定的宽高比例裁剪图片
     * @param bitmap 要裁剪的图片
     * @param num1 长边的比例
     * @param num2 短边的比例
     *  isRecycled是否回收原图片
     * @return 裁剪后的图片
     */
    public static Bitmap  imageCrop(Bitmap bitmap, int num1, int num2, boolean isRecycled){
        if (bitmap == null){
            return null;
        }
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int retX, retY;
        int nw, nh;
        if (w > h){
            if (h > w * num2 / num1){
                nw = w;
                nh = w * num2 / num1;
                retX = 0;
                retY = (h - nh) / 2;
            } else{
                nw = h * num1 / num2;
                nh = h;
                retX = (w - nw) / 2;
                retY = 0;
            }
        } else{
            if (w > h * num2 / num1){
                nh = h;
                nw = h * num2 / num1;
                retY = 0;
                retX = (w - nw) / 2;
            } else{
                nh = w * num1 / num2;
                nw = w;
                retY = (h - nh) / 2;
                retX = 0;}
        }
        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,false);
        if (isRecycled && bitmap != null && !bitmap.equals(bmp)&& !bitmap.isRecycled()){
            bitmap.recycle();//回收原图片
            bitmap = null;
        }
        return bmp;
    }

    /**
     *图片转圆角
     *  bitmap需要转的bitmap
     * pixels转圆角的弧度
     * @return 转圆角的bitmap
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels)    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        if (bitmap != null && !bitmap.isRecycled())
        {
            bitmap.recycle();
        }
        return output;
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap){
        if (bitmap == null){
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height){
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else{
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        if (bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        return output;
    }

    /**
     * 旋转图片
     * @param angle 旋转角度
     * @param bitmap 要处理的Bitmap
     * @return 处理后的Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap)
    {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        return resizedBitmap;
    }



}
