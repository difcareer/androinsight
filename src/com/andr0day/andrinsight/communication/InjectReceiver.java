package com.andr0day.andrinsight.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.andr0day.andrinsight.clazz.Fetcher;
import com.andr0day.andrinsight.xposed.XposedMain;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InjectReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String act = intent.getStringExtra(CommunicationUtil.ACT);
        if (TextUtils.isEmpty(act)) {
            return;
        }
        Act act1 = Act.valueOf(act);
        switch (act1) {
            case classloader:
                sendClassLoaders();
                break;
            case clazz:
                sendClasses(intent);
                break;
            case method:
                sendMethods(intent);
                break;
            default:
                break;
        }
    }

    private void sendClassLoaders() {
        List<String> res = new ArrayList<String>();
        for (ClassLoader cl : Fetcher.getInstance().classLoaders) {
            res.add(cl.getClass().getCanonicalName());
        }
        CommunicationUtil.sendClassLoaderResBroadCast(XposedMain.context, XposedMain.pkgName, JSON.toJSONString(res));
    }

    private void sendClasses(Intent intent) {
        List<String> res = new ArrayList<String>();
        String cl = intent.getStringExtra(CommunicationUtil.EXT);
        if (!TextUtils.isEmpty(cl)) {
            for (ClassLoader classLoader : Fetcher.getInstance().classLoaders) {
                if (cl.equals(classLoader.getClass().getCanonicalName())) {
                    for (Class it : Fetcher.getInstance().loaderMap.get(classLoader)) {
                        res.add(it.getCanonicalName());
                    }
                    break;
                }
            }
        }
        CommunicationUtil.sendClassResBroadCast(XposedMain.context, XposedMain.pkgName, cl, JSON.toJSONString(res));
    }

    private void sendMethods(Intent intent) {
        List<String> res = new ArrayList<String>();
        String cl = intent.getStringExtra(CommunicationUtil.EXT);
        Class clazz = Fetcher.getInstance().findClass(cl);
        if (clazz != null) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method it : methods) {
                res.add(it.toGenericString());
            }
        }
        CommunicationUtil.sendMethodResBroadCast(XposedMain.context, XposedMain.pkgName, cl, JSON.toJSONString(res));
    }
}
