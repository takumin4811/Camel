package com.example1.camelSample.entity;

import lombok.Data;

@Data
public class RouteInfo {
    String triggerFileName;
    String routeId;
    String routeType;
    String dstNodeId;
    String dstPath;
}
