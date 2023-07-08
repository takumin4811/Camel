package com.example1.camelSample.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example1.camelSample.entity.FtpServer;
import com.example1.camelSample.entity.RouteInfo;

@Repository
public class DB {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public RouteInfo getRouteInfoByTriggerFileName(String triggerFileName) {
    String sql = "SELECT * from Routes T1 inner join Triggers T2 on T1.routeId=T2.routeId where T2.triggerFileName= ? " ;
    RowMapper<RouteInfo> rowMapper = new BeanPropertyRowMapper<RouteInfo>(RouteInfo.class);
    RouteInfo routeInfo = jdbcTemplate.queryForObject(sql, rowMapper, triggerFileName);
    return routeInfo;
	}

    public FtpServer getNodeInfo(String nodeId) {
        String sql = "SELECT * from FTPServers where nodeId= ? " ;
        RowMapper<FtpServer> rowMapper = new BeanPropertyRowMapper<FtpServer>(FtpServer.class);
        FtpServer ftpServer = jdbcTemplate.queryForObject(sql, rowMapper, nodeId);
        return ftpServer;
    }

    public RouteInfo getRouteInfoByRouteId(String routeId) {
        String sql = "SELECT * from Routes where routeId= ? " ;
        RowMapper<RouteInfo> rowMapper = new BeanPropertyRowMapper<RouteInfo>(RouteInfo.class);
        RouteInfo routeInfo = jdbcTemplate.queryForObject(sql, rowMapper, routeId);
        return routeInfo;
	}

}
