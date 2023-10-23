package com.example1.camelSample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example1.camelSample.entity.FtpServer;
import com.example1.camelSample.entity.NodeType;
import com.example1.camelSample.entity.RouteInfo;
import com.example1.camelSample.exception.UnexpectedDataFoundException;
import com.example1.camelSample.repository.FtpServerDao;

@Service
public class GetConsumeURL {
  @Autowired
  FtpServerDao ftpServerDao;

  public String byRouteInfo(RouteInfo r) throws UnexpectedDataFoundException {
    NodeType nodeType = r.getSrcNodeType();
    String endpointURL;
    if (nodeType.equals(NodeType.LOCAL)) {
      String fileName = r.getSrcFileInfo().getFileNameWithExt();
      String filePath = r.getSrcFileInfo().getFilePath();
      if (r.getSrcFileInfo().getRegexKbn() == 0) {
        endpointURL = "file://" + filePath + "?filename=" + fileName
            + "&move=.done/${file:name}_${date:now:yyyyMMddHHmmss}"
            + "&moveFailed=.error/${file:name}_${date:now:yyyyMMddHHmmss}";
      } else {
        String antfileName = fileName.replace("(.*)", "*");
        endpointURL = "file://" + filePath + "?antInclude=" + antfileName
            + "&move=.done/${file:name}_${date:now:yyyyMMddHHmmss}"
            + "&moveFailed=.error/${file:name}_${date:now:yyyyMMddHHmmss}";
      }
      return endpointURL;
    }
    if (nodeType.equals(NodeType.FTP)) {
      FtpServer f = ftpServerDao.getFTPServerInfo(r.getSrcFileInfo().getSrcNodeId());
      String fileName = r.getSrcFileInfo().getFileNameWithExt();
      String filePath = r.getSrcFileInfo().getFilePath();
      if (r.getSrcFileInfo().getRegexKbn() == 0) {
        endpointURL = "ftp://" + f.getUserid() + "@" + f.getHost() + ":" + f.getPort() + "/" + filePath + "?"
            + "password=" + f.getPasswd() + "&passiveMode=" + f.getIsPassive() + "&filename=" + fileName
            + "&noop=true&localworkdirectory=/tmp/";
      } else {
        String antfileName = fileName.replace("(.*)", "*");
        endpointURL = "ftp://" + f.getUserid() + "@" + f.getHost() + ":" + f.getPort() + "/" + filePath + "?"
            + "password=" + f.getPasswd() + "&passiveMode=" + f.getIsPassive() + "&antInclude=" + antfileName
            + "&noop=true&localworkdirectory=/tmp/";
          }
          return endpointURL;
    } else {
      throw new UnexpectedDataFoundException("unknown NodeType Error:" + nodeType);
    }
  }

}
