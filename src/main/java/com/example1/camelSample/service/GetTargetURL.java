package com.example1.camelSample.service;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example1.camelSample.entity.FtpServer;
import com.example1.camelSample.entity.NodeType;
import com.example1.camelSample.entity.RouteInfo;
import com.example1.camelSample.entity.SftpServer;
import com.example1.camelSample.exception.UnexpectedDataFoundException;
import com.example1.camelSample.repository.FtpServerDao;
import com.example1.camelSample.repository.SftpServerDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GetTargetURL {
  @Autowired
  FtpServerDao ftpServerDao;
  @Autowired
  SftpServerDao sftpServerDao;

  public String byRouteInfoInExchangeHeader(Exchange exchange) throws Exception {
    RouteInfo r = (RouteInfo) exchange.getIn().getHeader("routeInfo");
    String targetStr = "";

    if (r.getDstNodeType().equals(NodeType.LOCAL)) {
      r.getDstFileInfo().getDstFileNameExt();
      targetStr = "file:" + r.getDstFileInfo().getDstPath() + "?flatten=true" + getDoneFileName(r);
      return targetStr;
    }
    if (r.getDstNodeType().equals(NodeType.FTP)) {
      FtpServer f = ftpServerDao.getFTPServerInfo(r.getDstFileInfo().getDstNodeId());
      targetStr = "ftp://" + f.getUserid() + "@" + f.getHost() + ":" + f.getPort() + "/"
          + r.getDstFileInfo().getDstPath() + "?" + "password=" + f.getPasswd() + "&passiveMode=" + f.getIsPassive()
          + "&flatten=true" + getDoneFileName(r);
      return targetStr;
    }
    if (r.getDstNodeType().equals(NodeType.SFTP)) {
      SftpServer f = sftpServerDao.getSFTPServerInfo(r.getDstFileInfo().getDstNodeId());
      targetStr = "sftp://" + f.getUserid() + "@" + f.getHost() + ":" + f.getPort() + "/"
          + r.getDstFileInfo().getDstPath() + "?" + "password=" + f.getPasswd() + "&flatten=true" + getDoneFileName(r)
          + "&useUserKnownHostsFile=false";
      if (f.getProxyId() != null) {
        targetStr += "&proxy=#" + f.getProxyId();
      }
      return targetStr;
    } else {
      log.error("Error:" + r.getDstFileInfo());
      throw new UnexpectedDataFoundException("unknown NodeType Error:" + r.getDstNodeType());
    }
  }

  private String getDoneFileName(RouteInfo r) {
    if (r.getDstFileInfo().getDstFileNameTrgExt().equals("")) {
      return "";
    } else
      return "&doneFileName=${file:name.noext}." + r.getDstFileInfo().getDstFileNameTrgExt();
  }
}
