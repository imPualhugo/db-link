package org.dblink.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TypeUtil {

    public static <T> T getFieldValue(Field field, T o) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String getter = "get" + firstLetter + fieldName.substring(1);
        Method method = null;
        T value = null;
        try {
            method = o.getClass().getMethod(getter);
            value = (T) method.invoke(o);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return value;
    }
}
