package com.example1.camelSample.entity;

import java.net.Proxy;

import lombok.Data;

@Data
public class SftpServer {
    String nodeId;
    String host;
    Integer port;
    String userid;
    String passwd;
    Proxy proxy;
}
