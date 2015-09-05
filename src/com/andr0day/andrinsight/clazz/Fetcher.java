package com.andr0day.andrinsight.clazz;

import android.util.Log;
import com.andr0day.andrinsight.common.XposedConstant;

import java.util.*;

public class Fetcher {

    private static Fetcher instance = new Fetcher();

    public Set<Class> classes = new HashSet<Class>();

    public Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();

    public Map<ClassLoader, Set<Class>> loaderMap = new HashMap<ClassLoader, Set<Class>>();


    public static Fetcher getInstance() {
        return instance;
    }

    private Fetcher() {
        if (instance != null) {
            throw new IllegalArgumentException("already instanced");
        }
        System.load(XposedConstant.SO_FULL_PATH);
    }

    public void loadClasses() {
        int tableSize = getTableSize();
        Log.e(XposedConstant.TAG, "tableSize:" + tableSize);
        for (int i = 0; i < tableSize; i++) {
            Class clazz = getLoadedClass(i);
            if (clazz != null && clazz.getCanonicalName() != null) {
                Log.e(XposedConstant.TAG, "class:" + clazz.getCanonicalName());
                classes.add(clazz);
                classLoaders.add(clazz.getClassLoader());
                Set<Class> cls = loaderMap.get(clazz.getClassLoader());
                if (cls == null) {
                    cls = new HashSet<Class>();
                    loaderMap.put(clazz.getClassLoader(), cls);
                }
                cls.add(clazz);
            }
        }
        for (ClassLoader it : classLoaders) {
            Log.e(XposedConstant.TAG, "classloader:" + it.toString());
        }
    }

    public Class findClass(String name) {
        for (Class it : classes) {
            if (it.getCanonicalName().equals(name)) {
                return it;
            }
        }
        return null;
    }


    public native Class getLoadedClass(int index);

    public native int getTableSize();

}
