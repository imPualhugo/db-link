package org.dblink.utils;


import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @author hyz
 */
public class DBUtils {

    /**
     * 将结果集中的第一条数据转化成JavaBean
     *
     * @param rs    结果集
     * @param clazz JavaBean类对象
     * @param <T>   Bean的类型
     * @return JavaBean
     */
    public static <T> T resultToBean(ResultSet rs, Class<T> clazz) {

        T t = null;
        try {
            //创建javaBean对象

            //获取结果集中的表头
            ResultSetMetaData metaData = rs.getMetaData();
            //拿到表头中的每个字段
            //获取结果集中的字段的总数
            int count = metaData.getColumnCount();
            //判断有没有下一条数据
            if (rs.next()) {
                t = clazz.newInstance();
                for (int i = 1; i <= count; i++) {
                    //获取表头的名称,就是字段名
                    String columnName = metaData.getColumnLabel(i);
                    //在根据字段名获取这个字段在这一行对应的值
                    Object obj = rs.getObject(columnName);
                    //获取JavaBean类中的成员变量
                    Field field = clazz.getDeclaredField(columnName);
                    //设置允许
                    field.setAccessible(true);
                    //将获取的值赋值给javaBean的这个名称的成员变量上
                    field.set(t, obj);
                }
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return t;
    }

    /**
     * 将结果集中的多条数据转化的多个JavaBean对象存入到List集合中
     *
     * @param rs    数据集
     * @param clazz Bean类对象
     * @param <T>   JavaBean类型
     * @return JavaBean
     */
    public static <T> ArrayList<T> resultToBeanList(ResultSet rs, Class<T> clazz) {

        ArrayList<T> list = new ArrayList<>();
        try {

            //获取结果集中的表头
            ResultSetMetaData metaData = rs.getMetaData();
            //拿到表头中的每个字段
            //获取结果集中的字段的总数
            int count = metaData.getColumnCount();

            while (rs.next()) {
                //创建javaBean对象
                T t = clazz.newInstance();
                for (int i = 1; i <= count; i++) {
                    //获取表头的名称,就是字段名
                    String columnName = metaData.getColumnLabel(i);
                    //在根据字段名获取这个字段在这一行对应的值
                    Object obj = rs.getObject(columnName);
                    Field field = null;
                    //获取Javabean对象中的成员变量
                    try {
                        field = clazz.getDeclaredField(columnName);
                    } catch (NoSuchFieldException e) {
                        try {
                            //获取父类字段
                            field = clazz.getField(columnName);
                        } catch (Exception e1) {
                            e.printStackTrace();
                        }
                    }
                    //设置允许
                    field.setAccessible(true);
                    //将获取的值赋值给javaBean的这个名称的成员变量上
                    field.set(t, obj);
                }
                //将对象放到list集合中
                list.add(t);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public static void free(Statement statement,ResultSet rs){
        if (rs != null){
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (statement != null ){
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
