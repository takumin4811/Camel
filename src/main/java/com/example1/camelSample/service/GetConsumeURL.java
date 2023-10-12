package com.example1.camelSample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example1.camelSample.entity.FtpServer;
import com.example1.camelSample.entity.NodeType;
import com.example1.camelSample.entity.RouteInfo;
import com.example1.camelSample.exception.UnexpectedDataFoundException;
import com.example1.camelSample.repository.FtpServerDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GetConsumeURL {
  @Autowired
  FtpServerDao ftpServerDao;

  public String byRouteInfo(RouteInfo r) throws UnexpectedDataFoundException {
    NodeType nodeType = r.getSrcNodeType();
    if (nodeType.equals(NodeType.LOCAL)){
      String fileName = r.getSrcFileInfo().getFileNameWithExt();
      String filePath = r.getSrcFileInfo().getFilePath();
      String endpointURL = "file://"+filePath
          + "?filename=" + fileName
          + "&move=.done/${file:name}_${date:now:yyyyMMddHHmmss}"
          + "&moveFailed=.error/${file:name}_${date:now:yyyyMMddHHmmss}";
      log.info("endpointURL:"+endpointURL);
      return endpointURL;
    }
    if (nodeType.equals(NodeType.LOCAL)){
      FtpServer f = ftpServerDao.getFTPServerInfo(r.getSrcFileInfo().getSrcNodeId());
      String fileName = r.getSrcFileInfo().getFileNameWithExt();
      String filePath = r.getSrcFileInfo().getFilePath();
      String endpointURL = "ftp://" + f.getUserid() + "@" + f.getHost() + "/"  + filePath  
          + "?"
          + "&password=" + f.getPasswd() + "&passiveMode=" + f.getIsPassive() 
          + "?filename=" + fileName
          + "&move=.done/${file:name}_${date:now:yyyyMMddHHmmss}"
          + "&moveFailed=.error/${file:name}_${date:now:yyyyMMddHHmmss";
      log.info("endpointURL:"+endpointURL);
      return endpointURL;     
    }
    else{
      throw new UnexpectedDataFoundException("unknown NodeType Error:"+nodeType);
    }
  }
}
