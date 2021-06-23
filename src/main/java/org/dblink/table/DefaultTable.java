package org.dblink.table;

import org.dblink.utils.DBUtils;
import org.dblink.utils.TypeUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.dblink.utils.DBUtils.free;

public class DefaultTable<T> implements Table<T> {

    private final Class<T> clazz;

    private final Connection conn;

    private final String tableName;

    private String sql = "";

    private final StringBuilder sb;

    private boolean hasSubQuery = false;

    private StringBuilder clearSb() {
        this.sb.delete(0, sb.length());
        return sb;
    }

    DefaultTable(Class<T> clazz, Connection connection, String tableName) {
        this.clazz = clazz;
        this.conn = connection;
        this.tableName = tableName;
        sb = new StringBuilder(sql);
    }

    @Override
    public Table<T> select(String arg) {
        clearSb().append("SELECT ").append(arg);
        return this;
    }

    @Override
    public Table<T> select(T bean) {
        return this;
    }

    @Override
    public Table<T> select(T bean, String... arg) {


        return this;
    }

    @Override
    public Table<T> insert(T bean) {
        clearSb().append("INSERT INTO ").append(tableName);


        HashMap<String, Object> map = TypeUtil.readBean(bean);

        if (map.size() > 0) {
            sb.append("(");
            map.keySet().forEach(k -> {
                sb.append(k).append(", ");
            });
            //去掉最后多余的逗号
            sb.deleteCharAt(sb.length() - 2);

            sb.append(") VALUES (");

            map.values().forEach(k -> {
                if (k instanceof Number) {
                    sb.append(k);
                } else {
                    sb.append("'").append(k).append("'");
                }
                sb.append(", ");
            });
            //去掉最后多余的逗号
            sb.deleteCharAt(sb.length() - 2);

            sb.append(")");
        }


        return this;
    }


    @Override
    public Table<T> update(T bean, String key) {
        clearSb().append("UPDATE ").append(tableName).append(" SET ");
        HashMap<String, Object> map = TypeUtil.readBean(bean);

        map.forEach((k, v) -> {
            //非key才进行更新

            if (!k.equals(key)) {
                sb.append(k).append("=");
                if (v instanceof Number) {
                    sb.append(v);
                } else {
                    sb.append("'");
                    sb.append(v);
                    sb.append("'");
                }

                sb.append(", ");
            }
        });
        sb.deleteCharAt(sb.length() - 2);

        if (null != key) {
            sb.append(" WHERE ")
                    .append(key)
                    .append(" = ");
            if (map.get(key) instanceof Number) {
                sb.append(map.get(key));
            } else {
                sb.append("'");
                sb.append(map.get(key));
                sb.append("'");
            }
        }

        return this;
    }

    @Override
    public Table<T> delete(T bean) {


        return this;

    }

    @Override
    public String getName() {
        return this.tableName;
    }

    @Override
    public Table<T> from(Table<T> t) {
        sb.append(" FROM ").append(t.getName()).append(" ");
        return this;
    }


    @Override
    public Table<T> delete() {
        clearSb().append("DELETE ");
//                .append(" FROM ").append(tableName).append(" ");
//
//        if (map.size() > 0) {
//            map.forEach((k, v) -> {
//                sb.append(k).append(" = ");
//                if (v instanceof Number) {
//                    sb.append(v);
//                } else {
//                    sb.append("'");
//                    sb.append(v);
//                    sb.append("'");
//                }
//                sb.append(" AND ");
//            });
//            System.out.println("delete : " + sb);
//            sb.deleteCharAt(sb.length() - 2);
//            sb.deleteCharAt(sb.length() - 2);
//            sb.deleteCharAt(sb.length() - 2);
//            System.out.println("delete : " + sb);
//        }

        return this;

    }

    @Override
    public Table<T> delete(Map<String, Object> map) {
        return null;
    }

    @Override
    public Table<T> where(Map<String, Object> map) {
        sb.append(" WHERE ");
        map.forEach((x, y) -> {
            sb.append(" ")
                    .append(x)
                    .append("=")
                    .append(y)
                    .append(" ");
        });
        return this;
    }

    @Override
    public Table<T> where(String args) {
        sb.append(" WHERE ").append(args);
        return this;
    }


    @Override
    public Table<T> where(String args, Object obj) {
        sb.append(" WHERE ")
                .append(args)
                .append("=")
                .append(obj.toString());
        return this;
    }

    @Override
    public Table<T> set(Map<String, Object> map) {
        return this;
    }

    @Override
    public Table<T> If(T bean, Predicate<T> predicate) {

        if (predicate.test(bean)) {

        }

        return this;
    }

    @Override
    public Table<T> and(String arg) {
        return this;
    }

    @Override
    public Table<T> or(String arg) {
        return this;
    }

    @Override
    public <V> Table<T> innerJoin(Table<V> table) {
        sb.append(" INNER JOIN ").append(table);
        return this;
    }

    @Override
    public <V> Table<T> leftJoin(Table<V> table) {
        sb.append(" LEFT JOIN ").append(table);
        return this;
    }

    @Override
    public <V> Table<T> rightJoin(Table<V> table) {
        sb.append(" RIGHT JOIN ").append(table);
        return this;
    }

    @Override
    public Table<T> on(Map<Object, Object> map) {
        sb.append(" ON ");
        map.forEach((x, y) -> {
            sb.append(" ")
                    .append(x)
                    .append("=")
                    .append(y)
                    .append(" ");
        });

        return this;
    }

    @Override
    public Table<T> on(String args) {
        return this;
    }

    @Override
    public Table<T> as(String alias) {
        return this;
    }

    @Override
    public Table<T> subQuery() {
        hasSubQuery = true;
        return this;
    }

    @Override
    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public List<T> query() {

        completeSql();

        List<T> list = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            list = DBUtils.resultToBeanList(rs, clazz);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            free(statement, rs);
        }

        return list;
    }

    @Override
    public Integer commit() {
        return null;
    }

    @Override
    public void rollback() {

    }

    @Override
    public Table<T> addSql(String sql) {
        sb.append(" ");
        sb.append(sql);
        sb.append(" ");
        return this;
    }

    @Override
    public String getSql() {
        completeSql();
        return sql;
    }


    private void completeSql() {
        if (hasSubQuery) {
            sb.append(")");
        }
        sb.append(";");


        String[] strs = sb.toString().split("WHERE");

        if (strs.length < 1 || (strs.length > 2 && !hasSubQuery)) {
            sql = "";
            return;
        }
        StringBuilder sb0 = new StringBuilder(strs[0]);

        String almostSql = sb.toString();

        //通过正则表达式匹配非update与insert语句
        Pattern pattern = Pattern.compile("UPDATE|INSERT[\\s\\S]*", Pattern.CASE_INSENSITIVE);

        boolean isUpdate = pattern.matcher(almostSql).matches();


        if (!isUpdate) {
            sb0.append(" FROM ").append(tableName);
            if (strs.length >= 2) {
                sb0.append(" WHERE ").append(strs[1]);
            }
            sql = sb0.toString();
        } else {
            sql = sb.toString();
        }

        System.out.println(sql);
    }
}
