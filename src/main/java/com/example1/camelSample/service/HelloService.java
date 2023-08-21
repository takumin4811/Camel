
package com.example1.camelSample.service;

import java.io.File;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example1.camelSample.entity.ConvRule;
import com.example1.camelSample.entity.RouteInfo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HelloService {
    @Autowired
    private DB db;
    @Autowired
    Converter conv;

    public void hello(Exchange exchange,String routeId) {
            RouteInfo r = db.getRouteInfoByRouteId(routeId);
            hello2(exchange,r);           
    }
    
    public void hello2(Exchange exchange,RouteInfo r) {
            ConvRule c = db.getConvRuleByRouteId(r.getRouteId());
            exchange.getIn().setHeader("routeInfo",r );
            exchange.getIn().setHeader("convRule", c);

            File f = exchange.getIn().getBody(File.class);
            File f2 = conv.convert(f, c);
            exchange.getIn().setBody(f2);

            String srcfilename = exchange.getIn().getHeader("CamelFileName").toString();
            String dstfilename = db.getDstFilename(r.getRouteId(), srcfilename);
            exchange.getIn().setHeader(Exchange.FILE_NAME, dstfilename);
    }

}