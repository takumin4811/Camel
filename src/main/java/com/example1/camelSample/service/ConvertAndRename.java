
package com.example1.camelSample.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.example1.camelSample.entity.RouteInfo;
import com.example1.camelSample.repository.DstFileInfoDao;
import com.example1.camelSample.repository.FtpServerDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConvertAndRename {
  @Value("${my.camel.tmpdir}")
  private String tmpdirpath;

  @Autowired
  DstFileInfoDao dstFileInfoDao;
  @Autowired
  FtpServerDao ftpServerDao;

  @Autowired
  private Environment env;

 
  public void call(Exchange exchange) {
    File f2 = this.convert(exchange);
    exchange.getIn().setBody(f2);
    String dstfilename = this.getDstFileName(exchange);
    exchange.getIn().setHeader(Exchange.FILE_NAME, dstfilename);
    
  }
  
  private String getDstFileName(Exchange exchange) {
    RouteInfo r = (RouteInfo) exchange.getIn().getHeader("routeInfo");    
    String srcfilename = r.getSrcFileInfo().getFileNameWithExt();
    String dstfilename = r.getDstFileInfo().getFileNameWithExt();
    
    if (dstfilename.equals("")) {
      dstfilename = srcfilename;
    }
    
    if (r.getSrcFileInfo().getRegexKbn()==1 || r.getDstFileInfo().getRegexKbn()==1){      
        String srcfileNameActual = exchange.getIn().getHeader("CamelFileNameOnly").toString();
        dstfilename=srcfileNameActual.replaceAll(srcfilename,dstfilename);
        srcfilename=srcfileNameActual;
    }    


    String today = env.getProperty("TODAY");
    if (today == null) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      today = sdf.format(new Date());
    }
    dstfilename = dstfilename.replace("${DATE}", today);
    
    log.info("srcfilename="+srcfilename+" => dstfilename="+dstfilename);
    return dstfilename;
  }

  
  private File convert(Exchange exchange) {   
    RouteInfo r = (RouteInfo) exchange.getIn().getHeader("routeInfo");    
    String lineCd = "\n";
    if (r.getDstFileInfo().getDstLinefeed().equals("CRLF")) {
      lineCd = "\r\n";
    }
    
    log.info("srcCharset=" + r.getSrcFileInfo().getSrcCharset() +"=>dstCharset="+r.getDstFileInfo().getDstCharset());
    log.info("srcLinefeed=" + r.getSrcFileInfo().getSrcLinefeed() +"=>dstLinefeed="+r.getDstFileInfo().getDstLinefeed());
    log.info("footerdel=" + r.getDstFileInfo().getFooterdel());
    
    File f = exchange.getIn().getBody(File.class);
    List<String> lines = new ArrayList<>();
    Path tmpFile = Paths.get(tmpdirpath);
    try {
      tmpFile = Files.createTempFile(Paths.get(tmpdirpath), f.getName() + ".", ".tmp");
      lines = Files.readAllLines(f.toPath(), Charset.forName(r.getSrcFileInfo().getSrcCharset()));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    try (BufferedWriter out = Files.newBufferedWriter(tmpFile, Charset.forName(r.getDstFileInfo().getDstCharset()));) {
      for (int i = 0; i < lines.size() - r.getDstFileInfo().getFooterdel(); i++) {
        out.write(lines.get(i) + lineCd);
      }
      return tmpFile.toFile();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}