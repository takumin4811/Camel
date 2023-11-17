package com.example1.camelSample.entity;

import lombok.Data;

@Data
public class ProxyServer {
    String proxyId;
    String proxyhost;
    Integer proxyport;
    String proxyuserid;
    String proxypasswd;
}
