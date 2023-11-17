package com.example1.camelSample.entity;

import lombok.Data;

@Data
public class SftpServer {
    String nodeId;
    String host;
    Integer port;
    String userid;
    String passwd;
    String proxyId;
}
