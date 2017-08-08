package com.sam.beiz.Config;

/**
 * Created by zhejunliu on 2017/7/27.
 */

public class MyTag {

    public static String getCurrentClassName() {
        int level = 1;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String className = stacks[level].getClassName();
        return className;
    }
}
