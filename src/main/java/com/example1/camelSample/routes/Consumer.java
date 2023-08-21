package com.example1.camelSample.routes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import com.example1.camelSample.entity.RouteInfo;
import com.example1.camelSample.service.DB;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;

@Slf4j
@Component
public class Consumer extends EndpointRouteBuilder {
    @Autowired
    DB db;

    @Override
    public void configure() throws Exception {
        onException(EmptyResultDataAccessException.class) // DBに該当レコードが見つからない例外をキャッチしたら
                .log(LoggingLevel.WARN, "DBにノーヒット〜〜") // ワーニングログをだして
                .handled(false)// スタックトレースは出さない（システムエラー扱いにはしない）場合はTRUE
                .end();

        onException(Exception.class) // その他の予期しない例外をキャッチしたら
                .maximumRedeliveries(5) // 最大５回のリトライ
                .delayPattern("0:1000;3:5000") // 1秒間隔でリトライし3回目以後は5秒に一回
                .retryAttemptedLogLevel(LoggingLevel.WARN) // リトライ時のログはワーニングで
                .handled(false)// システム例外にする
                .log(LoggingLevel.ERROR, "！！！！共通の例外。リトライしたけどだめでした！！！！")// リトライオーバ時のログはエラーで
                .end();

        from(file("{{my.camel.localroot}}") // application.properties の変数my.camel.localrootに書かれたディレクトリパスを監視
                .doneFileName("${file:name.noext}.trg")// トリガファイルがあるときのみ処理
                .recursive(true)// サブディレクトリを再帰的に監視
                .move(".done/${file:name}_${date:now:yyyyMMddHHmmss}")// 処理が正常終了したファイルを.doneに移動
                .moveFailed(".error/${file:name}_${date:now:yyyyMMddHHmmss}")// 処理が以上終了したファイルを.errorに移動
        )
                .id("LocalFileTriggerConsumer") // このルートの名前
                .process(exchange -> { // ラムダ関数（引数ー＞｛引数に対する関数定義｝。関数宣言と実行を同時に行う）
                    log.info(exchange.getIn().getHeaders().toString()); // exchange(配達物）の中にあるラベル（ヘッダ）をログ出力
                    String fileName = exchange.getIn().getHeader("CamelFilePath").toString(); // exchange(配達物）の中にあるラベル（ヘッダ）に書かれているファイル名を取得
                    String trgFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".trg";// ファイル名からトリガ名を作成
                    RouteInfo routeInfo = db.getRouteInfoByTriggerFileName(trgFileName);// トリガ名に対応する宛先ルート情報をDBから引き当てる（引き当てられないと例外発生）
                    exchange.getIn().setHeader("routeInfo", routeInfo);// 引き当てた宛先ルート情報をヘッダに追加
                })
                .to("direct:ConvertionCenter")// 中央郵便局に配送する
                .end();
 
        from("ftp://foo1@localhost/tmp/from/?password=bar1&passiveMode=true&doneFileName=${file:name.noext}.trg&recursive=true")
                .id("FTPFileTriggerConsumer（Node01)")
                .process(exchange -> {
                    String fileName = exchange.getIn().getHeader("CamelFileAbsolutePath").toString();
                    String trgFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".trg";
                    ;
                    exchange.getIn().setHeader("routeInfo", db.getRouteInfoByTriggerFileName(trgFileName));
                })
                .to("direct:ConvertionCenter")
                .end();

    }
}