package com.example1.camelSample.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example1.camelSample.entity.PollingNode;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PollingNodeDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;


  public List<PollingNode> getPollingNodes() {
    String sql = " SELECT N.nodeId,nodetype,host,port,userid,passwd,isPassive,pollingDirectory,PollingRecursive,PollingTrgName FROM PollingNodes as P inner join Nodes as N on N.nodeId=P.nodeId";
    log.info(sql);
    List<PollingNode> pollingNode = jdbcTemplate.query(sql, DataClassRowMapper.newInstance(PollingNode.class));
    return pollingNode;
  }

}
