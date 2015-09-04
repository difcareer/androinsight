package com.andr0day.andrinsight.xposed;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import android.util.Log;
import com.andr0day.andrinsight.clazz.Fetcher;
import com.andr0day.andrinsight.common.ConfigUtil;
import com.andr0day.andrinsight.common.XposedConstant;
import com.andr0day.andrinsight.common.XposedLogUtil;
import com.andr0day.andrinsight.communication.CommunicationUtil;
import com.andr0day.andrinsight.communication.InjectReceiver;
import com.andr0day.andrinsight.xposed.config.Loader;
import com.andr0day.andrinsight.xposed.hooker.ContextHookedListener;
import com.andr0day.andrinsight.xposed.hooker.ContextHooker;
import com.andr0day.andrinsight.xposed.hooker.Hookers;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedMain implements IXposedHookLoadPackage, ContextHookedListener {

    public static ApplicationInfo applicationInfo;
    public static String pkgName;
    public static String applicationName;
    private static long lastLoadTime = System.currentTimeMillis();
    public static Context context;


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null) {
            return;
        }
        applicationInfo = lpparam.appInfo;
        if (applicationInfo == null) {
            return;
        }
        pkgName = applicationInfo.packageName;
        applicationName = applicationInfo.className;
        if (TextUtils.isEmpty(pkgName)) {
            return;
        }

        Loader.getInstance().startLoad();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean xposedEnabled = "true".equals(ConfigUtil.getConfigNormal(pkgName, "enable", "false"));
                if (!xposedEnabled) {
                    return;
                }
                Log.e(XposedConstant.TAG, "active pkgName:" + pkgName);
                Log.e(XposedConstant.TAG, "active applicationName:" + applicationName);
                if (lpparam.isFirstApplication) {
                    initEnv();
                    startLoopLoad();

//                    Hookers.instance.startHook();
                }
            }
        }).start();
    }

    private void initEnv() {
        ConfigUtil.init(pkgName);
        XposedLogUtil.init(pkgName);
    }

    private void startLoopLoad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long current = System.currentTimeMillis();
                while (true) {
                    if (current - lastLoadTime >= 10 * 1000) {
                        lastLoadTime = current;
                        Fetcher.getInstance().loadClasses();
                        ContextHooker contextHooker = ContextHooker.getInstance().setListener(XposedMain.this);
                        if (contextHooker.needHook()) {
                            contextHooker.startHook();
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void hooked(Context ctx) {
        context = ctx;
        registerBroadCast();
    }


    private void registerBroadCast() {
        IntentFilter intentFilter = new IntentFilter(CommunicationUtil.getInjectAction(pkgName));
        context.registerReceiver(new InjectReceiver(), intentFilter);
    }
}
