package com.example1.camelSample.entity;

import lombok.Data;

@Data
public class FtpServer {
    String nodeId;
    String host;
    Integer port;
    String userid;
    String passwd;
    Boolean isPassive;

}
