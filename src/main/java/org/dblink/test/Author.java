package org.dblink.test;

import lombok.Data;

@Data
public class Author {
    private Integer id;
    private String userName;
    private String password;
    private String nickName;
    private Long createTime;
    private Long updateTime;
}
