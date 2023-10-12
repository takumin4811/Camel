package com.example1.camelSample.entity;

import lombok.Data;

@Data

public class SrcFileInfo {
  String fileId;
  String routeId;
  String srcNodeId;
  String srcPath;
  String srcfileName;
  String srcFileNameExt;
  String srcFileNameTrgExt;
  String srcLinefeed;
  String srcCharset;
  int regexKbn;

  public String getFileNameWithExt() {
    return srcfileName + "." + srcFileNameExt;
  }

  public String getTRgFileNameWithExt() {
    return srcfileName + "." + srcFileNameTrgExt;
  }

  public String getFilePathAndNameWithExt() {
    return srcPath + "/" + srcfileName + "." + srcFileNameExt;
  }

  public String getTrgFilePathAndNameWithExt() {
    return srcPath + "/" + srcfileName + "." + srcFileNameTrgExt;
  }
  public String getFilePath() {
    return srcPath + "/" ;
  }

}
