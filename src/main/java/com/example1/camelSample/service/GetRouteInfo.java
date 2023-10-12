
package com.example1.camelSample.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example1.camelSample.entity.DstFileInfo;
import com.example1.camelSample.entity.NodeType;
import com.example1.camelSample.entity.RouteInfo;
import com.example1.camelSample.entity.SrcFileInfo;
import com.example1.camelSample.repository.DB;
import com.example1.camelSample.repository.DstFileInfoDao;
import com.example1.camelSample.repository.SrcFileInfoDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GetRouteInfo {
  @Value("${my.camel.tmpdir}")
  private String tmpdirpath;

  @Autowired
  DstFileInfoDao dstFileInfoDao;
  @Autowired
  SrcFileInfoDao srcFileInfoDao;
  @Autowired
  DB db;

  @Autowired
  @Qualifier("regexSrcFileInfoList")
  List<SrcFileInfo> regexSrcFileInfoList;

  public RouteInfo byFileId(String fileId) {
    SrcFileInfo srcFileInfo = srcFileInfoDao.getSrcFileInfoBySrcFileId(fileId);
    DstFileInfo dstFileInfo = dstFileInfoDao.getDstFileInfoBySrcFileId(fileId);
    NodeType srcNodeType = db.getNodeTypeByNodeId(srcFileInfo.getSrcNodeId());
    NodeType dstNodeType = db.getNodeTypeByNodeId(dstFileInfo.getDstNodeId());
    renameDstRedexFileName(srcFileInfo,dstFileInfo);
    return new RouteInfo(srcFileInfo, dstFileInfo, srcNodeType, dstNodeType);
  }

  
  public RouteInfo bySrcFileName(String srcNodeId, String srcPath, String srcFileNameWithExt) {
    String srcFileName = srcFileNameWithExt.substring(0, srcFileNameWithExt.lastIndexOf('.'));
    String srcFileNameExt = srcFileNameWithExt.substring(srcFileNameWithExt.lastIndexOf(".") + 1);
    RouteInfo r=bySrcFileName(srcNodeId, srcPath, srcFileName, srcFileNameExt);
    return r;
  }
  
  public RouteInfo bySrcFileName(String srcNodeId, String srcPath, String srcFileName, String srcFileNameExt) {
    Integer i = db.countFileRoutes(srcNodeId, srcPath, srcFileName, srcFileNameExt);
    if (i.equals(0)) {
      log.info("FileName may be defined by regex-expression");
      String pathSeparator = File.separator;
      String filePathAndNameWithExt = srcPath + pathSeparator + srcFileName + "." + srcFileNameExt;
      for (SrcFileInfo regexSrcFileInfo : regexSrcFileInfoList) {
        if (filePathAndNameWithExt.matches(regexSrcFileInfo.getFilePathAndNameWithExt())) {
          return byFileId(regexSrcFileInfo.getFileId());
        }
      }
    }
    SrcFileInfo srcFileInfo = srcFileInfoDao.getSrcFileInfoBySrcFileName(srcNodeId, srcPath, srcFileName, srcFileNameExt);
    DstFileInfo dstFileInfo = dstFileInfoDao.getDstFileInfoBySrcFileName(srcNodeId, srcPath, srcFileName, srcFileNameExt);
    NodeType srcNodeType = db.getNodeTypeByNodeId(srcFileInfo.getSrcNodeId());
    NodeType dstNodeType = db.getNodeTypeByNodeId(dstFileInfo.getDstNodeId());
    return new RouteInfo(srcFileInfo, dstFileInfo, srcNodeType, dstNodeType);
  }

  private void renameDstRedexFileName(SrcFileInfo srcFileInfo, DstFileInfo dstFileInfo) {
    log.info(srcFileInfo.getFileNameWithExt());
    log.info(dstFileInfo.getFileNameWithExt());

  }
  
  
  
}