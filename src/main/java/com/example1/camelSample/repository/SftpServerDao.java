package com.example1.camelSample.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example1.camelSample.entity.SftpServer;

import lombok.extern.slf4j.Slf4j;
import java.net.Proxy;

@Repository
@Slf4j
public class SftpServerDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public SftpServer getSFTPServerInfo(String nodeId) {
    String sql = "SELECT * from Nodes where nodetype='sftp' and nodeId= ? ";
    String sql2 = "SELECT proxyhost,proxyport,proxyuserid,proxypasswd from Nodes where nodetype='sftp' and nodeId= ? ";
    RowMapper<SftpServer> rowMapper = new BeanPropertyRowMapper<>(SftpServer.class);
    RowMapper<Proxy> rowMapper2 = new BeanPropertyRowMapper<>(Proxy.class);
    try {
      SftpServer sftpServer = jdbcTemplate.queryForObject(sql, rowMapper, nodeId);
      Proxy p = jdbcTemplate.queryForObject(sql2, rowMapper2, nodeId);
      sftpServer.setProxy(p);
      return sftpServer;
    } catch (EmptyResultDataAccessException e) {
      log.warn(sql + "  Args :  " + nodeId);
      throw e;
    }
  }

}
