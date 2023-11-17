package com.example1.camelSample.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Responce {
  String status;
  String message;
  List<TranferedFile> tranferedFileList;

  public Responce(String status, String message) {
    this.status = status;
    this.message = message;
    this.tranferedFileList = new ArrayList<>();
    switch (status) {
    case "Error":
      log.info(message);
      break;
    case "Warn":
      log.warn(message);
      break;
    default:
      break;
    }
  }

  public void addtranferedFilesList(String consumedFileName, String producedFileName, String result) {
    tranferedFileList.add(new TranferedFile(consumedFileName, producedFileName, result));
  }

  @Data
  @AllArgsConstructor
  private class TranferedFile {
    String consumedFileName;
    String producedFileName;
    String result;
  }
}
