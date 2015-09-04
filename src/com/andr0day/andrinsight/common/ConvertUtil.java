package com.andr0day.andrinsight.common;

import java.lang.reflect.Method;

public class ConvertUtil {

    public static String clazzToStr(Class clazz) {
        return clazz.getCanonicalName();
    }

    public static String methodToStr(Method method) {
        return method.toGenericString();
    }
}
