package com.example1.camelSample.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example1.camelSample.entity.DstFileInfo;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DstFileInfoDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public DstFileInfo getDstFileInfoBySrcFileName(String srcNodeId, String srcPath, String srcFileName,
      String srcFileNameExt) {
    String sql = "SELECT routeId ,fileId,dstNodeId,dstPath,dstfileName,dstFileNameExt,dstFileNameTrgExt,dstLinefeed,dstCharset,footerdel,RegexKbn"
        + " from FilesRoutes where srcNodeId=? and srcPath=? and srcfilename=? and srcFileNameExt=?";
    RowMapper<DstFileInfo> rowMapper = new BeanPropertyRowMapper<>(DstFileInfo.class);
    try {
      DstFileInfo dstFileInfo = jdbcTemplate.queryForObject(sql, rowMapper, srcNodeId, srcPath, srcFileName,
          srcFileNameExt);
      return dstFileInfo;
    } catch (Exception e) {
      log.warn(sql + "  Args :  " + srcNodeId + "," + srcPath + "," + srcFileName + "," + srcFileNameExt);
      throw e;
    }
  }

  public DstFileInfo getDstFileInfoBySrcFileId(String fileId) {
    String sql = "SELECT routeId ,fileId,dstNodeId,dstPath,dstfileName,dstFileNameExt,dstFileNameTrgExt,dstLinefeed,dstCharset,footerdel,RegexKbn"
        + " from FilesRoutes where fileId =? ";
    RowMapper<DstFileInfo> rowMapper = new BeanPropertyRowMapper<>(DstFileInfo.class);
    try {
      DstFileInfo dstFileInfo = jdbcTemplate.queryForObject(sql, rowMapper, fileId);
      return dstFileInfo;
    } catch (Exception e) {
      log.warn(sql + "  Args :  " + fileId);
      throw e;
    }
  }

}
