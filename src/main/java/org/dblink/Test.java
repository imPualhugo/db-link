package org.dblink;

import org.dblink.config.ConnectionConfiguration;
import org.dblink.table.Table;
import org.dblink.table.TableManager;
import org.dblink.test.Author;

import java.util.List;

public class Test {


    public static void main(String[] args) {
        ConnectionConfiguration config = new ConnectionConfiguration();
        config.setUrl("jdbc:mysql://localhost:3306/bqg");
        config.setUsername("root");
        config.setPassword("123456");
        TableManager manager = new TableManager(config);

        Table<Author> t = manager.getTable(Author.class,"author");
//
//        List<Author> list = t
//                .select("id,userName")
//                .where("id",5).query();
//
//        System.out.println(list);

        Author author = new Author();
        author.setId(1);
        author.setUserName("aaa");
        author.setPassword("123456");
        System.out.println(t.insert(author).getSql());


    }
}
