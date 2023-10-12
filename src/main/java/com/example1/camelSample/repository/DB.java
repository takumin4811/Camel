package com.example1.camelSample.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example1.camelSample.entity.NodeType;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DB {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public  NodeType getNodeTypeByNodeId(String nodeId) {
    String sql = "SELECT nodetype from Nodes where nodeId=? ";
    try {
      String s = jdbcTemplate.queryForObject(sql, String.class, nodeId);
      return NodeType.valueOf(s.toUpperCase());
    } catch (Exception e) {
      log.warn(sql + "  Args :  " + nodeId);
      throw e;
    }
  }

  public Integer countFileRoutes(String srcNodeId, String srcPath, String srcFileName, String srcFileNameExt) {
    String sql = "SELECT count(*) from FilesRoutes where srcNodeId=? and srcPath=? and srcfilename=? and srcFileNameExt=?";
    Integer i = jdbcTemplate.queryForObject(sql, Integer.class, srcNodeId, srcPath, srcFileName, srcFileNameExt);
    return i;
  }

}
