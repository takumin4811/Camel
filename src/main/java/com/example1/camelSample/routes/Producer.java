package com.example1.camelSample.routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import com.example1.camelSample.entity.ExecCmd;
import com.example1.camelSample.entity.FtpServer;
import com.example1.camelSample.entity.RouteInfo;
import com.example1.camelSample.service.DB;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;

@Component
public class Producer extends EndpointRouteBuilder {
    @Autowired
    DB db;
    @Override
    public void configure() throws Exception {
        onException(EmptyResultDataAccessException.class) // DBに該当レコードが見つからない例外をキャッチしたら
        .log(LoggingLevel.WARN,"DBにノーヒット〜〜") //ワーニングログをだして
        .handled(false)// スタックトレースは出さない（システムエラー扱いにはしない）場合はTRUE
        .end();

        // onException(Exception.class) // その他の予期しない例外をキャッチしたら
        // .maximumRedeliveries(5) //最大５回のリトライ
        // .delayPattern("0:1000;3:5000") // 1秒間隔でリトライし3回目以後は5秒に一回
        // .retryAttemptedLogLevel(LoggingLevel.WARN) //リトライ時のログはワーニングで
        // .handled(false)//システム例外にする
        // .log(LoggingLevel.ERROR,"！！！！共通の例外。リトライしたけどだめでした！！！！")//リトライオーバ時のログはエラーで
        // .end();

        from("direct:LocalFileProducer").id("LocalFileProducer")  //中央郵便局から振り分け先。ここはローカル上でファイルをMoveするルート
        // .log("HEADER=${in.headers}")
        // .log("${file:name.noext} is will be moved to ${in.headers.routeInfo.dstPath}")//ログ
        // .to("mock:result")//ヘッダの情報に従いファイルを指定ディレクトリに移動する。移動後にトリガファイルを生成
        .toD("file:${in.headers.routeInfo.dstPath}?doneFileName=${file:name.noext}.trg")//ヘッダの情報に従いファイルを指定ディレクトリに移動する。移動後にトリガファイルを生成
        .log("${file:name} is moved to ${in.headers.routeInfo.dstPath}")//ログ
        ;

        from("direct:FtpProducer").id("FtpProducer") //中央郵便局から振り分け先。ここは外部にFTP-PUTするルート
        .process(exchange ->{
            RouteInfo r=(RouteInfo)exchange.getIn().getHeader("routeInfo");//荷物からルート情報を取り出して
            FtpServer f=db.getNodeInfo(r.getDstNodeId());//ルート情報に書かれた宛先ノード情報を引数にDBから宛先ノードのFTPサーバの情報を取得（ホスト名やPasswordなど）
            String targetStr="ftp://"+f.getUserid()+"@"+f.getHost()+
            "/"+r.getDstPath()+ "?"+"password="+f.getPasswd()+"&passiveMode="+f.getIsPassive()
            +"&flatten=true"
            +"&doneFileName=${file:name.noext}.trg"
            ;//Camelが理解できる宛先情報（URL)に組み立て直して
            exchange.getIn().setHeader("targetStr",targetStr);//荷物のヘッダに埋める
        })
        // .log("${in.headers.routeInfo}")//荷物のヘッダにあるルート情報のログ出力
        // .log("${in.headers.targetStr}")//荷物のヘッダにある宛先URL情報のログ出力
        .toD("${in.headers.targetStr}")//宛先URLへ配送
        .log("${file:name.noext} is Transfered")
        ;

        from("direct:ExecProducer").id("ExecProducer") //中央郵便局から振り分け先。ここは外部コマンド実行ルート
        // .log("routeInfo=${in.headers.routeInfo}")
        .process(exchange ->{
            RouteInfo r=(RouteInfo)exchange.getIn().getHeader("routeInfo");//荷物からルート情報を取り出して
            ExecCmd e=db.getExecCmd(r.getRouteId());
            String execStr = "exec:"+e.getCmd()+"?args="+e.getArg1();
            String fileName = exchange.getIn().getHeader("CamelFilePath").toString();
            execStr=execStr.replace("${FILENAME}", fileName);
            exchange.getIn().setHeader("ExecStr",execStr);//荷物のヘッダに埋める
        })
        .to("exec:pwd")
        .log("Body=${in.body}")
        .log("${in.headers.execStr}")
        .toD("${in.headers.execStr}") //コマンドの実行
        .log("Header=${in.headers}")
        .log("Body=${in.body}")
        .end()
        ;

    }
}

