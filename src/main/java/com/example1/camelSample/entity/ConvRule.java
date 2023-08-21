package com.example1.camelSample.entity;

import lombok.Data;

@Data
public class ConvRule {
    String routeId;
    String srcCharset;
    String srcLinefeed;
    String dstCharset;
    String dstLinefeed;
    Integer footerdel;
}
