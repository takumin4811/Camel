package com.example1.camelSample.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example1.camelSample.entity.FtpServer;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class FtpServerDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public FtpServer getFTPServerInfo(String nodeId) {
    String sql = "SELECT * from Nodes where nodetype='FTP' and nodeId= ? ";
    RowMapper<FtpServer> rowMapper = new BeanPropertyRowMapper<>(FtpServer.class);
    try {
      FtpServer ftpServer = jdbcTemplate.queryForObject(sql, rowMapper, nodeId);
      return ftpServer;
    } catch (EmptyResultDataAccessException e) {
      log.warn(sql + "  Args :  " + nodeId);
      throw e;
    }
  }

}
