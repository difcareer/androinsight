package com.andr0day.andrinsight.common;

public class XposedConstant {

    public static final String TAG = "Xposed";

    public static final String LOG = "log";

    public static final String EVENT = "event";

    public static final String HOST_PKG = "com.andr0day.andrinsight";

    public static final String HOST_FULL_PATH = "/data/data" + "/" + HOST_PKG;

    public static final String XPOSED = "xposed";

    public static final String XPOSED_FULL_PATH = XposedConstant.HOST_FULL_PATH + "/" + XposedConstant.XPOSED;

    public static final String INJECT_PKG_CONFIG = "cfg.properties";

    public static final String XPOSED_BROADCAST_PREFIX = "xposed_broadcast_prefix_";

    public static final String SO_NAME = "libclassfetcher.so";

    public static final String SO_FULL_PATH = HOST_FULL_PATH + "/files/" + SO_NAME;

}
