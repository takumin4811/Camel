package com.example1.camelSample.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example1.camelSample.entity.SrcFileInfo;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SrcFileInfoDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<SrcFileInfo> getRegexSrcFileInfoList() {
    String sql = "SELECT fileId,srcNodeId,srcPath,srcfileName,srcFileNameExt,srcFileNameTrgExt,routeId from FilesRoutes where RegexKbn=1 ";
    List<SrcFileInfo> regexSrcFileInfoList = jdbcTemplate.query(sql, DataClassRowMapper.newInstance(SrcFileInfo.class));
    return regexSrcFileInfoList;
  }

  public SrcFileInfo getSrcFileInfoBySrcFileName(String srcNodeId, String srcPath, String srcFileName,
      String srcFileNameExt) {
    String sql = "SELECT fileId,srcNodeId,srcPath,srcfileName,srcFileNameExt,srcFileNameTrgExt,srcLinefeed,srcCharset,routeId,RegexKbn "
        + " from FilesRoutes where srcNodeId=? and srcPath=? and srcfilename=? and srcFileNameExt=?";
    RowMapper<SrcFileInfo> rowMapper = new BeanPropertyRowMapper<>(SrcFileInfo.class);
    try {
      SrcFileInfo srcFileInfo = jdbcTemplate.queryForObject(sql, rowMapper, srcNodeId, srcPath, srcFileName,
          srcFileNameExt);
      return srcFileInfo;
    } catch (Exception e) {
      log.warn(sql + "  Args :  " + srcNodeId + "," + srcPath + "," + srcFileName + "," + srcFileNameExt);
      throw e;
    }
  }

  public SrcFileInfo getSrcFileInfoBySrcFileId(String fileId) {
    String sql = "SELECT fileId,srcNodeId,srcPath,srcfileName,srcFileNameExt,srcFileNameTrgExt,srcLinefeed,srcCharset,routeId "
        + " from FilesRoutes where fileId =?  ";
    RowMapper<SrcFileInfo> rowMapper = new BeanPropertyRowMapper<>(SrcFileInfo.class);
    try {
      SrcFileInfo srcFileInfo = jdbcTemplate.queryForObject(sql, rowMapper, fileId);
      return srcFileInfo;
    } catch (Exception e) {
      log.warn(sql + "  Args :  " + fileId);
      throw e;
    }
  }

}
