package com.example1.camelSample.router;

import java.util.List;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import com.example1.camelSample.entity.PollingNode;
import com.example1.camelSample.entity.RouteInfo;
import com.example1.camelSample.entity.SrcFileInfo;
import com.example1.camelSample.exception.UnexpectedDataFoundException;
import com.example1.camelSample.service.GetRouteInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Consumer extends EndpointRouteBuilder {
  @Autowired
  GetRouteInfo getRouteInfo;

  @Autowired
  @Qualifier("regexSrcFileInfoList")
  List<SrcFileInfo> regexSrcFileInfoList;

  @Autowired
  @Qualifier("PollingNodes")
  List<PollingNode> pollingNodes;

  @Override
  public void configure() throws Exception {
    onException(EmptyResultDataAccessException.class) // DBに該当レコードが見つからない例外をキャッチしたら
        .log(LoggingLevel.WARN, "DBにデータが見つかりませんでした") 
        .handled(true)// スタックトレースは出さない（システムエラー扱いにはしない）場合はTRUE
        .end();
        
    onException(UnexpectedDataFoundException.class) // DBに該当レコードが見つからない例外をキャッチしたら
        .handled(false)// システム例外にする
        .log(LoggingLevel.ERROR, "!!!!!予期せぬデータが見つかりました") 
        .end();

    onException(Exception.class) // その他の予期しない例外をキャッチしたら
        .maximumRedeliveries(1) // 最大５回のリトライ
        .delayPattern("0:1000;3:5000") // 1秒間隔でリトライし3回目以後は5秒に一回
        .retryAttemptedLogLevel(LoggingLevel.WARN) // リトライ時のログはワーニングで
        .handled(false)// システム例外にする
        .log(LoggingLevel.ERROR, "!!!!!共通の例外。リトライオーバー")// リトライオーバ時のログはエラーで
        .end();

    for (PollingNode p : pollingNodes) {
      log.info(p.getPolingString());
      from(p.getPolingString()).id((p.getNodetype() + "-" + p.getNodeId())) // このルートの名前
          .process(exchange -> {
            String srcNodeId = p.getNodeId();
            String filePath = exchange.getIn().getHeader("CamelFileParent").toString();
            String fileNameWithExt = exchange.getIn().getHeader("CamelFileNameOnly").toString();
            String fileName = fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf('.'));
            String fileNameExt = fileNameWithExt.substring(fileNameWithExt.lastIndexOf(".") + 1);

            RouteInfo routeInfo = getRouteInfo.bySrcFileName(srcNodeId, filePath, fileName, fileNameExt);
            exchange.getIn().setHeader("routeInfo", routeInfo);
            log.info(exchange.getIn().getHeaders().toString());
            log.debug(exchange.getIn().getBody().toString());
          }).to("direct:ConvertionCenter")// 中央郵便局に配送する
          .end();
    }

  }
}