package com.example1.camelSample.entity;

public enum NodeType {
  LOCAL("local"), FTP("Ftp"), SFTP("sftp");

  String nodeTypeName;

  private NodeType(String s) {
    this.nodeTypeName = s;
  }

  public String getNodeTypeName() {
    return this.nodeTypeName;
  }
}
