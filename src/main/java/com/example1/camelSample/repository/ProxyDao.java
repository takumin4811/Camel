package com.example1.camelSample.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example1.camelSample.entity.ProxyServer;

@Repository

public class ProxyDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<ProxyServer> fildAll() {
    String sql = "SELECT * FROM Proxies";
    List<ProxyServer> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ProxyServer.class));
    return list;
  }
}
