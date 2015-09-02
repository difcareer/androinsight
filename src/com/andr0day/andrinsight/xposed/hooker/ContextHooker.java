package com.andr0day.andrinsight.xposed.hooker;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import com.andr0day.andrinsight.clazz.Fetcher;
import com.andr0day.andrinsight.common.HookUtil;
import com.andr0day.andrinsight.common.XposedConstant;
import com.andr0day.andrinsight.communication.CommunicationUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.lang.reflect.Method;


public class ContextHooker extends HookerAdaptor {

    private static final String ContextImpl_CLASS_NAME = "android.app.ContextImpl";

    private static ContextHooker instance = new ContextHooker();

    private ContextHookedListener listener;

    private ContextHooker() {

    }

    public static ContextHooker getInstance() {
        return instance;
    }

    public ContextHooker setListener(ContextHookedListener listener) {
        this.listener = listener;
        return this;
    }

    public volatile Context applicationContext;

    public volatile boolean hooked = false;

    @Override
    public void startHook() {
        Class contextImplClass;
        try {
            contextImplClass = Class.forName(ContextImpl_CLASS_NAME);
            Log.e(XposedConstant.TAG, "class.forName");
        } catch (ClassNotFoundException e) {
            contextImplClass = Fetcher.getInstance().findClass(ContextImpl_CLASS_NAME);
            Log.e(XposedConstant.TAG, "Fetcher.findClass");
        }
        if (contextImplClass != null) {
            Log.e(XposedConstant.TAG, "got ContextImpl");
            Method[] methods = contextImplClass.getDeclaredMethods();
            for (final Method it : methods) {
                Class<?>[] paramTypes = it.getParameterTypes();
                Object[] paramsAndHook = new Object[paramTypes.length + 1];
                System.arraycopy(paramTypes, 0, paramsAndHook, 0, paramTypes.length);
                paramsAndHook[paramsAndHook.length - 1] = new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) {
                        if (applicationContext == null) {
                            Context context = (Context) param.thisObject;
                            applicationContext = context.getApplicationContext();
                            if (listener != null) {
                                listener.hooked(applicationContext);
                            }
                            Toast.makeText(applicationContext, "application got", Toast.LENGTH_SHORT).show();
                        } else {
                            XposedBridge.unhookMethod(it, this);
                        }
                    }
                };
                HookUtil.findAndHookMethod(contextImplClass, it.getName(), paramsAndHook);
            }
            hooked = true;
        }
    }

    @Override
    public boolean needHook() {
        return !hooked;
    }

}
