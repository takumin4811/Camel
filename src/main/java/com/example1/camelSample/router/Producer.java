package com.example1.camelSample.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import com.example1.camelSample.exception.UnexpectedDataFoundException;
import com.example1.camelSample.service.GetTargetURL;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;

@Component
public class Producer extends EndpointRouteBuilder {
  @Autowired
  GetTargetURL getTargetURL;

  @Override
  public void configure() throws Exception {
    onException(EmptyResultDataAccessException.class) // DBに該当レコードが見つからない例外をキャッチしたら
        .log(LoggingLevel.WARN, "DBにデータが見つかりませんでした").handled(false)// スタックトレースは出さない（システムエラー扱いにはしない）場合はTRUE
        .end();

    onException(UnexpectedDataFoundException.class) // DBに該当レコードが見つからない例外をキャッチしたら
        .handled(false)// システム例外にする
        .log(LoggingLevel.ERROR, "!!!!!予期せぬデータが見つかりました").end();

    onException(Exception.class) // その他の予期しない例外をキャッチしたら
        .maximumRedeliveries(5) // 最大５回のリトライ
        .delayPattern("0:1000;3:5000") // 1秒間隔でリトライし3回目以後は5秒に一回
        .retryAttemptedLogLevel(LoggingLevel.WARN) // リトライ時のログはワーニングで
        .handled(false)// システム例外にする
        .log(LoggingLevel.ERROR, "！！！！共通の例外。リトライしたけどだめでした！！！！")// リトライオーバ時のログはエラーで
        .end();

    from("direct:LocalFileProducer").id("LocalFileProducer") // 中央郵便局から振り分け先。ここはローカル上でファイルをMoveするルート
        .process(exchange -> {
          String targetStr = getTargetURL.byRouteInfoInExchangeHeader(exchange);
          exchange.getIn().setHeader("targetStr", targetStr);// 荷物のヘッダに埋める
        }).log("${in.headers.targetStr}").toD("${in.headers.targetStr}")// 宛先URLへ配送
        .log("${file:name} is moved to local directory ${in.headers.routeInfo.dstFileInfo.dstPath}")// ログ
        .end();

    from("direct:FtpProducer").id("FtpProducer") // 中央郵便局から振り分け先。ここは外部にFTP-PUTするルート
        .process(exchange -> {
          String targetStr = getTargetURL.byRouteInfoInExchangeHeader(exchange);
          exchange.getIn().setHeader("targetStr", targetStr);// 荷物のヘッダに埋める
        }).toD("${in.headers.targetStr}")// 宛先URLへ配送
        .log("${in.headers.targetStr}").log("${file:name} is FTP Transfered").end();
  }
}
