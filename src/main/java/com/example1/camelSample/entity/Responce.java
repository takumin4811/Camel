package com.example1.camelSample.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Responce {
  String status;
  String message;

  public Responce(String status, String message) {
    this.status = status;
    this.message = message;

    switch (status) {
    case "Error":
      log.info(message);
      break;
    case "Warn":
      log.warn(message);
      break;
    default:
      log.info(message);
    }
  }
}
