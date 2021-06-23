package org.dblink.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class TypeUtil {

    public static <T> HashMap<String,Object> readBean(T bean){
        Class<?> beanClass = bean.getClass();

        Field[] fields = beanClass.getDeclaredFields();

        HashMap<String, Object> map = new HashMap<>();

        for (Field field : fields) {
            field.setAccessible(true);

            String name = field.getName();
            Object value = getFieldValue(field, bean);
            if (null != value) {
                map.put(name, value);
            }
        }
        return map;
    }

    public static <T> T getFieldValue(Field field, T o) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String getter = "get" + firstLetter + fieldName.substring(1);
        Method method;
        T value = null;
        try {
            method = o.getClass().getMethod(getter);
            value = (T) method.invoke(o);

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return value;
    }
}
