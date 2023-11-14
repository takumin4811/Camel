package com.example1.camelSample.entity;

import java.net.Proxy;

import lombok.Data;

@Data
public class FtpServer {
    String nodeId;
    String host;
    Integer port;
    String userid;
    String passwd;
    Boolean isPassive;
    Proxy proxy;
}
