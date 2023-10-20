package com.example1.camelSample.entity;

import com.example1.camelSample.exception.UnexpectedDataFoundException;

import lombok.Data;

@Data
public class PollingNode {
  String nodeId;
  String nodetype;
  String host;
  String port;
  String userid;
  String passwd;
  Boolean isPassive;
  String pollingDirectory;
  Boolean pollingRecursive;
  String pollingTrgName;

  public String getPolingString() throws UnexpectedDataFoundException {
    if (this.nodetype.equals("local")) {
      return getLocalPolingString();
    }
    if (this.nodetype.equals("ftp")) {
      return getFtpPolingString();
    } else {
      throw new UnexpectedDataFoundException("unknown NodeType Error:" + this.nodetype);
    }
  }

  private String getLocalPolingString() {
    return "file://" + pollingDirectory + "?doneFileName=" + "${file:name.noext}." + pollingTrgName + "&recursive="
        + pollingRecursive + "&move=.done/${file:name}_${date:now:yyyyMMddHHmmss}"
        + "&moveFailed=.error/${file:name}_${date:now:yyyyMMddHHmmss}";
  }

  private String getFtpPolingString() {
    return "ftp://" + userid + "@" + host + ":" + port + "/" + pollingDirectory + "?password=" + passwd
        + "&passiveMode=" + isPassive + "&doneFileName=" + "${file:name.noext}." + pollingTrgName + "&recursive="
        + pollingRecursive + "&move=.done/${file:name}_${date:now:yyyyMMddHHmmss}"
        + "&moveFailed=.error/${file:name}_${date:now:yyyyMMddHHmmss}" + "&localworkdirectory=/tmp/";
  }

}
