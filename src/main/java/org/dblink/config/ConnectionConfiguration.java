package org.dblink.config;

import lombok.Data;

@Data
public class ConnectionConfiguration {

    private String url;
    private String username;
    private String password;
    private String driver;
    private Integer initialSize;
    private Integer minIdle;
    private Integer maxActive;

}
