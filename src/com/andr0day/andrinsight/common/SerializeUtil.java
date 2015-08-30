package com.andr0day.andrinsight.common;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializeUtil {

    private final static int MAX_DEPTH = 3;

    public static Map toMap(Object obj) {
        return new Serial().toMap(obj, 0);
    }

    public static Object toObj(Map map, Class clazz, Object preObj) {
        return new Serial().toObj(map, clazz, preObj);
    }


    static class Serial {

        @SuppressWarnings("unchecked")
        public Map toMap(Object obj, int depth) {
            Map outerMap = new HashMap();
            if (depth < MAX_DEPTH) {
                String clazzName = obj.getClass().getCanonicalName();
                outerMap.put("class", clazzName);
                Map fieldMap = new HashMap();
                Field[] fields = obj.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.getClass().isPrimitive()) {
                        try {
                            fieldMap.put(field.getName(), field.get(obj));
                        } catch (Exception e) {

                        }
                    } else if (field.getClass().isArray()) {
                        try {
                            List list = new ArrayList();
                            Object[] arrays = (Object[]) field.get(obj);
                            for (Object it : arrays) {
                                list.add(toMap(it, depth + 1));
                            }
                            fieldMap.put(field.getName(), list);
                        } catch (Exception e) {

                        }
                    } else {
                        try {
                            fieldMap.put(field.getName(), toMap(field.get(obj), depth + 1));
                        } catch (Exception e) {

                        }
                    }
                }
                outerMap.put("fields", fieldMap);
            }
            return outerMap;
        }

        public Object toObj(Map map, Class clss, Object instance) {
            String clazz = (String) map.get("class");
            if (TextUtils.isEmpty(clazz)) {
                return null;
            }
            Map fieldMap = (Map) map.get("fields");
            if (fieldMap == null || fieldMap.isEmpty()) {
                return null;
            }
            try {
                Class cls = Class.forName(clazz);
                if (!cls.equals(clss)) {
                    return null;
                }
                if (instance == null) {
                    instance = clss.newInstance();
                }
                for (Object key : fieldMap.keySet()) {
                    Object value = fieldMap.get(key);
                    Field field = clss.getDeclaredField((String) key);
                    field.setAccessible(true);
                    if (field.getClass().isPrimitive()) {
                        field.set(instance, value);
                    } else if (field.getClass().isArray()) {
                        Object[] array = (Object[]) value;
                        Object[] arrField = new Object[array.length];
                        for (int i = 0; i < array.length; i++) {
                            //todo maybe not null
                            arrField[i] = toObj((Map) array[i], field.getClass().getComponentType(), null);
                        }
                        field.set(instance, arrField);
                    } else {
                        //todo maybe not null
                        field.set(instance, toObj((Map) value, field.getClass(), null));
                    }
                }
                return instance;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
