package com.andr0day.andrinsight.hook;

import com.andr0day.andrinsight.common.RootUtil;
import com.andr0day.andrinsight.common.XposedUtil;

import java.io.File;
import java.io.IOException;

public class SetHookUtil {

    public static void stack(String pkgName, String clazz, String method) {
        createFile(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName, clazz, method, HookMethod.stack.name());
    }

    public static void beforeWatch(String pkgName, String clazz, String method) {
        createFile(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName, clazz, method, HookMethod.beforeWatch.name());
    }

    public static void afterWatch(String pkgName, String clazz, String method) {
        createFile(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName, clazz, method, HookMethod.afterWatch.name());
    }

    public static void beforeSetTemp(String pkgName, String clazz, String method) {
        createFile(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName, clazz, method, HookMethod.beforeSetTemp.name());
    }

    public static void beforeSetPerm(String pkgName, String clazz, String method) {
        createFile(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName, clazz, method, HookMethod.beforeSetPerm.name());
    }

    public static void beforeSetCond(String pkgName, String clazz, String method) {
        createFile(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName, clazz, method, HookMethod.beforeSetCond.name());
    }

    public static void afterSetTemp(String pkgName, String clazz, String method) {
        createFile(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName, clazz, method, HookMethod.afterSetTemp.name());
    }

    public static void afterSetPerm(String pkgName, String clazz, String method) {
        createFile(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName, clazz, method, HookMethod.afterSetPerm.name());
    }

    public static void afterSetCond(String pkgName, String clazz, String method) {
        createFile(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName, clazz, method, HookMethod.afterSetCond.name());
    }

    private static void createFile(String base, String clazz, String method, String type) {
        File dir = new File(base);
        if (!dir.exists()) {
            dir.mkdir();
            RootUtil.safeExecStr("chmod 777 " + dir.getAbsolutePath());
        }
        File file = new File(dir, clazz + "_" + method + "_" + type);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            RootUtil.safeExecStr("chmod 777 " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
