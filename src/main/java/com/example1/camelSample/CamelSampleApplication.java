package com.example1.camelSample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example1.camelSample.entity.PollingNode;
import com.example1.camelSample.entity.ProxyServer;
import com.example1.camelSample.entity.SrcFileInfo;
import com.example1.camelSample.repository.PollingNodeDao;
import com.example1.camelSample.repository.ProxyDao;
import com.example1.camelSample.repository.SrcFileInfoDao;
import com.jcraft.jsch.ProxyHTTP;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CamelSampleApplication {

  @Autowired
  private SrcFileInfoDao srcFileInfoDao;
  @Autowired
  private PollingNodeDao pollingNodeDao;
  @Autowired
  private ProxyDao proxyDao;

  public static void main(String[] args) {
    SpringApplication.run(CamelSampleApplication.class, args);
  }

  @PostConstruct
  public void display() {
    log.info("*****  Hawtio URL is  ***************");
    log.info("http://localhost:10001/actuator/hawtio/index.html");
    log.info("*************************************");
  }

  @Bean("regexSrcFileInfoList")
  public List<SrcFileInfo> getsrcFileInfoList() {
    log.info(srcFileInfoDao.getRegexSrcFileInfoList().toString());
    return srcFileInfoDao.getRegexSrcFileInfoList();
  }

  @Bean("PollingNodes")
  public List<PollingNode> getPollingLocalNodes() {
    return pollingNodeDao.getPollingNodes();
  }

  @Bean("Proxies")
  public Map<String, ProxyHTTP> getProxies() {
    List<ProxyServer> list = proxyDao.fildAll();
    Map<String, ProxyHTTP> map = new HashMap<>();
    for (ProxyServer it : list) {
      ProxyHTTP p = new ProxyHTTP(it.getProxyhost(), it.getProxyport());
      p.setUserPasswd(it.getProxyuserid(), it.getProxypasswd());
      map.put(it.getProxyId(), p);
    }
    return map;
  }
}
