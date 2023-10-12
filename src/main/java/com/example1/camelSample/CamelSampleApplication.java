package com.example1.camelSample;

import java.util.List;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example1.camelSample.entity.PollingNode;
import com.example1.camelSample.entity.SrcFileInfo;
import com.example1.camelSample.repository.PollingNodeDao;
import com.example1.camelSample.repository.SrcFileInfoDao;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CamelSampleApplication {

  @Autowired
  private SrcFileInfoDao srcFileInfoDao;
  @Autowired
  private PollingNodeDao pollingNodeDao;

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

}
