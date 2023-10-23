
package com.example1.camelSample.web.controller;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example1.camelSample.entity.Responce;
import com.example1.camelSample.entity.RouteInfo;
import com.example1.camelSample.exception.UnexpectedDataFoundException;
import com.example1.camelSample.service.ConvertAndRename;
import com.example1.camelSample.service.GetConsumeURL;
import com.example1.camelSample.service.GetRouteInfo;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HelloRestController {
  @Autowired
  private ConvertAndRename convertAndRename;
  @Autowired
  private GetRouteInfo getRouteInfo;
  @Autowired
  private GetConsumeURL getConsumeURL;
  @Autowired
  private ProducerTemplate producerTemplate;
  @Autowired
  private ConsumerTemplate consumerTemplate;

  @GetMapping(value = "/api/") // HTTP（REST）リクエスト。本来はPOSTが望ましいが簡略化のためGETで
  public Responce apiRequest(@RequestParam("fileId") String fileId) {
    RouteInfo routeInfo;
    try {
      routeInfo = getRouteInfo.byFileId(fileId);
    } catch (EmptyResultDataAccessException e) {
      log.warn(e.getMessage());
      return new Responce("Warn", "Cannot GetRouteInfo because DataIsNotFound");
    }
    return commonLogic(routeInfo);
  }

  @GetMapping(value = "/api/{nodeId}") // HTTP（REST）リクエスト。本来はPOSTが望ましいが簡略化のためGETで
  public Responce apiRequestByFileName(@PathVariable("nodeId") String nodeId, @RequestParam("srcPath") String srcPath,
      @RequestParam("srcFileNameWithExt") String srcFileNameWithExt) {
    RouteInfo routeInfo;
    try {
      routeInfo = getRouteInfo.bySrcFileName(nodeId, srcPath, srcFileNameWithExt);
    } catch (EmptyResultDataAccessException e) {
      log.warn(e.getMessage());
      return new Responce("Warn", "Cannot GetRouteInfo because DataIsNotFound");
    }
    return commonLogic(routeInfo);
  }

  private Responce commonLogic(RouteInfo routeInfo) {
    String endpointURL = "";
    Exchange exchange;
    Integer numberOfFiles = 0;
    Responce okResponce = new Responce("OK", "");
    try {
      endpointURL = getConsumeURL.byRouteInfo(routeInfo);
    } catch (UnexpectedDataFoundException e) {
      log.error(e.getMessage());
      return new Responce("Error", "Cannot GetConsumerURL because UnexpectedDataFound");
    }
    log.info("<ConsumeEndpointURL>" + endpointURL);

    do{
      exchange = consumerTemplate.receive(endpointURL, 10);
      if (exchange == null) {
        break;
      }
      exchange.getIn().setHeader("routeInfo", routeInfo);
      convertAndRename.call(exchange);
      producerTemplate.send("direct:DistributionCenter", exchange);
      if (exchange.getException() != null) {
          return new Responce("Error", exchange.getException().getMessage());
      }
      String camelFileNameConsumed = (String) exchange.getIn().getHeader("CamelFileNameConsumed");
      String camelFileNameProduced = (String) exchange.getIn().getHeader("CamelFileNameProduced");
      okResponce.addtranferedFilesList(camelFileNameConsumed, camelFileNameProduced, "OK");
      numberOfFiles++;
    } while(exchange!=null);
    
    if (numberOfFiles == 0) {
      return new Responce("Warn", routeInfo.getSrcFileSimpleInfo() + " is not found");
    } else {
      okResponce.setMessage("Request is Completed. "+numberOfFiles+" File Transfered");
      log.info(okResponce.toString());
      return okResponce;
    }
  }

}
