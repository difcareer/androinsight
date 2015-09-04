package com.andr0day.andrinsight.xposed.config;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.andr0day.andrinsight.clazz.Fetcher;
import com.andr0day.andrinsight.common.ConvertUtil;
import com.andr0day.andrinsight.common.HookUtil;
import com.andr0day.andrinsight.common.SerializeUtil;
import com.andr0day.andrinsight.common.XposedConstant;
import com.andr0day.andrinsight.hook.HookMethod;
import com.andr0day.andrinsight.xposed.XposedMain;
import de.robv.android.xposed.XC_MethodHook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Loader {

    private static Loader instance = new Loader();

    private static final long LOAD_INTERVAL = 1000;

    private Loader() {
    }

    public static Loader getInstance() {
        return instance;
    }


    public void startLoad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    doLoad();
                    try {
                        Thread.sleep(LOAD_INTERVAL);
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    public void doLoad() {
        File pkgDir = new File(XposedConstant.XPOSED_FULL_PATH + "/" + XposedMain.pkgName);
        if (pkgDir.exists()) {
            File[] files = pkgDir.listFiles();
            if (files != null) {
                for (File it : files) {
                    if (it.isFile()) {
                        String fName = it.getName();
                        if (!TextUtils.isEmpty(fName)) {
                            String[] parts = fName.split("_");
                            if (parts.length == 3) {
                                String clazz = parts[0];
                                String method = parts[1];
                                String type = parts[2];
                                startHook(clazz, method, type);
                            }

                        }

                    }
                }
            }

        }
    }

    private void startHook(final String clazz, final String method, final String type) {
        Class cl = Fetcher.getInstance().findClass(clazz);
        if (cl != null) {
            Method target = null;
            Method[] methods = cl.getDeclaredMethods();
            for (Method it : methods) {
                if (method.equals(ConvertUtil.methodToStr(it))) {
                    target = it;
                    break;
                }
            }
            if (target != null) {
                Class<?>[] paramTypes = target.getParameterTypes();
                Object[] paramsAndHook = new Object[paramTypes.length + 1];
                System.arraycopy(paramTypes, 0, paramsAndHook, 0, paramTypes.length);
                HookMethod hookMethod = HookMethod.valueOf(type);
                final String resFileName = XposedConstant.XPOSED_FULL_PATH + "/" + XposedMain.pkgName + clazz + "_" + method + "_" + type + "_res";
                switch (hookMethod) {
                    case stack:
                        paramsAndHook[paramsAndHook.length - 1] = new XC_MethodHook() {
                            @SuppressWarnings("unchecked")
                            protected void afterHookedMethod(MethodHookParam param) {
                                try {
                                    File res = new File(resFileName);
                                    if (!res.exists()) {
                                        res.createNewFile();
                                    }
                                    new Throwable().printStackTrace(new PrintWriter(new BufferedWriter(new FileWriter(res, true))));
                                } catch (Exception e) {

                                }
                            }
                        };
                        break;
                    case beforeWatch:
                        paramsAndHook[paramsAndHook.length - 1] = new XC_MethodHook() {
                            @SuppressWarnings("unchecked")
                            protected void beforeHookedMethod(MethodHookParam param) {
                                try {
                                    File res = new File(resFileName);
                                    if (!res.exists()) {
                                        res.createNewFile();
                                    }
                                    Map map = new HashMap();
                                    map.put("this", SerializeUtil.toMap(param.thisObject));
                                    map.put("args", SerializeUtil.toMap(param.args));
                                    if (param.getResult() != null) {
                                        map.put("result", SerializeUtil.toMap(param.getResult()));
                                    }
                                    if (param.getThrowable() != null) {
                                        map.put("throw", SerializeUtil.toMap(param.getThrowable()));
                                    }
                                    new PrintWriter(resFileName).append(JSON.toJSONString(map)).append("\n");
                                } catch (Exception e) {

                                }
                            }
                        };
                        break;
                    case afterWatch:
                        paramsAndHook[paramsAndHook.length - 1] = new XC_MethodHook() {
                            @SuppressWarnings("unchecked")
                            protected void afterHookedMethod(MethodHookParam param) {
                                try {
                                    File res = new File(resFileName);
                                    if (!res.exists()) {
                                        res.createNewFile();
                                    }
                                    Map map = new HashMap();
                                    map.put("this", SerializeUtil.toMap(param.thisObject));
                                    map.put("args", SerializeUtil.toMap(param.args));
                                    if (param.getResult() != null) {
                                        map.put("result", SerializeUtil.toMap(param.getResult()));
                                    }
                                    if (param.getThrowable() != null) {
                                        map.put("throw", SerializeUtil.toMap(param.getThrowable()));
                                    }
                                    new PrintWriter(resFileName).append(JSON.toJSONString(map)).append("\n");
                                } catch (Exception e) {

                                }
                            }
                        };
                        break;
                    case beforeSetTemp:
                    case beforeSetPerm:
                    case beforeSetCond:
                        paramsAndHook[paramsAndHook.length - 1] = new XC_MethodHook() {
                            @SuppressWarnings("unchecked")
                            protected void beforeHookedMethod(MethodHookParam param) {
                                try {
                                    File res = new File(resFileName);
                                    if (!res.exists()) {
                                        res.createNewFile();
                                    }
                                    Map map = new HashMap();
                                    map.put("this", SerializeUtil.toMap(param.thisObject));
                                    map.put("args", SerializeUtil.toMap(param.args));
                                    if (param.getResult() != null) {
                                        map.put("result", SerializeUtil.toMap(param.getResult()));
                                    }
                                    if (param.getThrowable() != null) {
                                        map.put("throw", SerializeUtil.toMap(param.getThrowable()));
                                    }
                                    new PrintWriter(resFileName).append(JSON.toJSONString(map)).append("\n");
                                } catch (Exception e) {

                                }
                            }
                        };
                        break;
                    case afterSetTemp:
                    case afterSetPerm:
                    case afterSetCond:
                        paramsAndHook[paramsAndHook.length - 1] = new XC_MethodHook() {
                            @SuppressWarnings("unchecked")
                            protected void afterHookedMethod(MethodHookParam param) {
                                try {
                                    File res = new File(resFileName);
                                    if (!res.exists()) {
                                        res.createNewFile();
                                    }
                                    Map map = new HashMap();
                                    map.put("this", SerializeUtil.toMap(param.thisObject));
                                    map.put("args", SerializeUtil.toMap(param.args));
                                    if (param.getResult() != null) {
                                        map.put("result", SerializeUtil.toMap(param.getResult()));
                                    }
                                    if (param.getThrowable() != null) {
                                        map.put("throw", SerializeUtil.toMap(param.getThrowable()));
                                    }
                                    new PrintWriter(resFileName).append(JSON.toJSONString(map)).append("\n");
                                } catch (Exception e) {

                                }
                            }
                        };
                        break;
                    default:
                        break;
                }

                paramsAndHook[paramsAndHook.length - 1] = new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) {

                    }
                };
                HookUtil.findAndHookMethod(cl, target.getName(), paramsAndHook);
            }
        }

    }
}
