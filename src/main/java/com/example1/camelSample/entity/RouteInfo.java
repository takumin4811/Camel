package com.example1.camelSample.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteInfo {
  SrcFileInfo srcFileInfo;
  DstFileInfo dstFileInfo;
  NodeType srcNodeType;
  NodeType dstNodeType;

  public String getSrcFileSimpleInfo() {
    return "[" + srcNodeType + "]" + srcFileInfo.getSrcNodeId() + "://" + srcFileInfo.getFilePathAndNameWithExt() ;
  }


}
