package org.dblink.table;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Predicate;

public interface Table<T> {

    Table<T> select(String arg);

    Table<T> select(T bean);

    Table<T> select(T bean,String...arg);

    Table<T> insert(T bean);

    Table<T> update(T bean);

    Table<T> delete(T bean);

    Table<T> where(Map<String, Object> map);

    Table<T> where(String args);

    Table<T> where(String args, Object obj);

    Table<T> set(Map<String, Object> map);

    Table<T> If(T bean,Predicate<T> predicate);

    Table<T> and(String arg);

    Table<T> or(String arg);

    <V> Table<T> innerJoin(Table<V> table);

    <V> Table<T> leftJoin(Table<V> table);

    <V> Table<T> rightJoin(Table<V> table);

    Table<T> on(Map<Object, Object> map);

    Table<T> on(String args);

    Table<T> as(String alias);

    Table<T> subQuery();

    void close();

    List<T> query();

    Integer commit();

    Table<T> addSql(String sql);

    String getSql();

    void rollback();
}
