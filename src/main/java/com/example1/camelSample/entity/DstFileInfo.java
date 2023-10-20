package com.example1.camelSample.entity;

import lombok.Data;

@Data
public class DstFileInfo {
  String fileId;
  String routeId;
  String dstNodeId;
  String dstPath;
  String dstfileName;
  String dstFileNameExt;
  String dstFileNameTrgExt;
  String dstLinefeed;
  String dstCharset;
  Integer footerdel;
  int regexKbn;

  public String getFileNameWithExt() {
    return dstfileName + "." + dstFileNameExt;
  }

  public String getFilePathAndName() {
    return dstPath + "/" + dstfileName + "." + dstFileNameExt;
  }

}
