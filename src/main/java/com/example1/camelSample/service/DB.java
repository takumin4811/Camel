package com.example1.camelSample.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example1.camelSample.entity.ConvRule;
import com.example1.camelSample.entity.ExecCmd;
import com.example1.camelSample.entity.FtpServer;
import com.example1.camelSample.entity.RouteInfo;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DB {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private Environment env;

    public RouteInfo getRouteInfoByTriggerFileName(String triggerFileName) {
        String sql = "SELECT * from Routes T1 inner join Triggers T2 on T1.routeId=T2.routeId where T2.triggerFileName= ? ";
        RowMapper<RouteInfo> rowMapper = new BeanPropertyRowMapper<RouteInfo>(RouteInfo.class);
        RouteInfo routeInfo = jdbcTemplate.queryForObject(sql, rowMapper, triggerFileName);
        return routeInfo;
    }

    public FtpServer getNodeInfo(String nodeId) {
        String sql = "SELECT * from FTPServers where nodeId= ? ";
        RowMapper<FtpServer> rowMapper = new BeanPropertyRowMapper<FtpServer>(FtpServer.class);
        FtpServer ftpServer = jdbcTemplate.queryForObject(sql, rowMapper, nodeId);
        return ftpServer;
    }

    public RouteInfo getRouteInfoByRouteId(String routeId) {
        String sql = "SELECT * from Routes where routeId= ? ";
        RowMapper<RouteInfo> rowMapper = new BeanPropertyRowMapper<RouteInfo>(RouteInfo.class);
        RouteInfo routeInfo = jdbcTemplate.queryForObject(sql, rowMapper, routeId);
        return routeInfo;
    }

    public ExecCmd getExecCmd(String routeId) {
        String sql = "SELECT * from PostExec where routeId= ? ";
        RowMapper<ExecCmd> rowMapper = new BeanPropertyRowMapper<ExecCmd>(ExecCmd.class);
        ExecCmd execCmd = jdbcTemplate.queryForObject(sql, rowMapper, routeId);
        return execCmd;
    }

    public ConvRule getConvRuleByRouteId(String routeId) {
        String sql = "SELECT * from ConvertRules where routeId= ? ";
        RowMapper<ConvRule> rowMapper = new BeanPropertyRowMapper<ConvRule>(ConvRule.class);
        ConvRule convRule = jdbcTemplate.queryForObject(sql, rowMapper, routeId);
        return convRule;
    }

    public String getDstFilename(String routeId, String srcfileName) {
        String sql = "SELECT dstfilename from FileNameConvertRules where routeId= ? and srcfileName= ? ";
        String dstfilename="";
        try{
            dstfilename = jdbcTemplate.queryForObject(sql, String.class, routeId, srcfileName);
        } catch (EmptyResultDataAccessException e){
            log.warn("no hit by " + sql);
            dstfilename="";
        }
        String today = env.getProperty("TODAY");
        if (today == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
            today = sdf.format(new Date());
        }
        log.info(today);

        log.info(dstfilename);
        dstfilename = dstfilename.replace("${DATE}", today);
        log.info(dstfilename);
        if (dstfilename.equals("")){
            dstfilename=srcfileName;
        }
        return dstfilename;
    }

}
