package com.andr0day.andrinsight.communication;

import android.content.Context;
import android.content.Intent;

public class CommunicationUtil {

    private final static String HOST_PREFIX = "communication_host_";

    private final static String INJECT_PREFIX = "communication_inject_";

    public final static String ACT = "act";

    public final static String EXT = "ext";

    public final static String EXT2 = "ext2";

    public static String getHostAction(String pkgName) {
        return HOST_PREFIX + pkgName;
    }

    public static String getInjectAction(String pkgName) {
        return INJECT_PREFIX + pkgName;
    }

    public static void sendClassloaderBroadcast(Context context, String pkgName) {
        Intent intent = new Intent();
        intent.setAction(getInjectAction(pkgName));
        intent.putExtra(ACT, Act.classloader.name());
        context.sendBroadcast(intent);
    }

    public static void sendClassBroadcast(Context context, String pkgName, String classloader) {
        Intent intent = new Intent();
        intent.setAction(getInjectAction(pkgName));
        intent.putExtra(ACT, Act.clazz.name());
        intent.putExtra(EXT, classloader);
        context.sendBroadcast(intent);
    }

    public static void sendMethodBroadcast(Context context, String pkgName, String clazz) {
        Intent intent = new Intent();
        intent.setAction(getInjectAction(pkgName));
        intent.putExtra(ACT, Act.method.name());
        intent.putExtra(EXT, clazz);
        context.sendBroadcast(intent);
    }

    public static void sendClassloaderResBroadCast(Context context, String pkgName, String json) {
        Intent intent = new Intent();
        intent.setAction(getHostAction(pkgName));
        intent.putExtra(ACT, Act.classloader.name());
        intent.putExtra(EXT, json);
        context.sendBroadcast(intent);
    }

    public static void sendClassResBroadCast(Context context, String pkgName, String classloader, String json) {
        Intent intent = new Intent();
        intent.setAction(getHostAction(pkgName));
        intent.putExtra(ACT, Act.clazz.name());
        intent.putExtra(EXT, classloader);
        intent.putExtra(EXT2, json);
        context.sendBroadcast(intent);
    }

    public static void sendMethodResBroadCast(Context context, String pkgName, String clazz, String json) {
        Intent intent = new Intent();
        intent.setAction(getHostAction(pkgName));
        intent.putExtra(ACT, Act.method.name());
        intent.putExtra(EXT, clazz);
        intent.putExtra(EXT2, json);
        context.sendBroadcast(intent);
    }

}
