package com.sam.beiz.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;

/**
 * Created by SAM-PC2 on 2017/4/12.
 */
public class LinkAppSupport {

    /**
     * 判断是否安装目标应用
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    public boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    /**
     * 启动目标应用
     * @param packageName 目标应用安装后的包名
     */
    public void launchApp(Context context,String packageName) {
        PackageManager packageManager=context.getPackageManager();
        Intent intent=packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }
    /**
     * 下载目标应用
     * @param packageName 目标应用安装后的包名
     */
    public void loadApp(Context context,String packageName) {
        Uri uri = Uri.parse("market://details?id=" +packageName);//id后面接包名
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}
