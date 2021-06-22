package org.dblink.table;

import org.dblink.utils.DBUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.dblink.utils.DBUtils.free;

public class DefaultTable<T> implements Table<T> {

    private Class<T> clazz;

    private Connection conn;

    private String table;

    private String sql = "";

    private StringBuilder sb;

    private boolean hasSubQuery = false;

    private StringBuilder clearSb() {
        this.sb.delete(0, sb.length());
        return sb;
    }

    DefaultTable(Class<T> clazz, Connection connection, String table) {
        this.clazz = clazz;
        this.conn = connection;
        this.table = table;
        sb = new StringBuilder(sql);
    }

    @Override
    public Table<T> select(String arg) {
        clearSb().append("SELECT ").append(arg);
        return this;
    }

    @Override
    public Table<T> select(T bean) {
        return null;
    }

    @Override
    public Table<T> select(T bean, String... arg) {
        return null;
    }

    @Override
    public Table<T> insert(T bean) {
//        clearSb().append("insert").append()

        return this;
    }

    @Override
    public Table<T> update(T bean) {
        return this;
    }

    @Override
    public Table<T> delete(T bean) {
        return this;

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


        if (!sb.toString().startsWith("INSERT")) {
            sb0.append(" FROM ").append(table);
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
