package com.example1.camelSample.routes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import com.example1.camelSample.entity.RouteInfo;
import com.example1.camelSample.service.DB;
import com.example1.camelSample.service.HelloService;

import lombok.extern.slf4j.Slf4j;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;

@Component
@Slf4j
public class Hub extends EndpointRouteBuilder {
    @Autowired
    HelloService helloService;
    @Autowired
    DB db;
    @Value("${my.camel.tmpdir}")
    public String tmpdirpath;

    @Override
    public void configure() throws Exception {
        onException(EmptyResultDataAccessException.class) // DBに該当レコードが見つからない例外をキャッチしたら
                .log(LoggingLevel.WARN, "DBにノーヒット〜〜") // ワーニングログをだして
                .handled(false)// スタックトレースは出さない（システムエラー扱いにはしない）場合はTRUE
                .end();

        from("direct:ConvertionCenter")// Consumerからのリンク先
                .id("ConvertionCenter")// 中央郵便局
                .process(exchange -> {
                    RouteInfo r = (RouteInfo) exchange.getIn().getHeader("routeInfo");// 荷物からルート情報を取り出して
                    helloService.hello2(exchange,r);
                })
                .to("direct:DistributionCenter")
                .end();

        from("direct:DistributionCenter")// Consumerからのリンク先
                .id("DistributionCenter")// 中央郵便局
                // .log("Header ${in.headers}")//荷物（Exchange)の中のヘッダ情報を取得・ログ出力
                .process(exchange -> {
                })
                .choice()// 宛先ルート情報にあるタイプを判定し、ルートを振り分け
                .when(simple("${in.headers.routeInfo.routeType}").isEqualTo("LOCAL")) // ルートタイプがローカルならローカルファイルプロデュース（MOVE)のコースへ
                .log("Local Route")
                .to("direct:LocalFileProducer")
                .when(simple("${in.headers.routeInfo.routeType}").isEqualTo("FTP"))// ルートタイプがFTPならFTPプロデュース（FTP-PUT)のコースへ
                .log("FTP Route")
                .to("direct:FtpProducer")
                .when(simple("${in.headers.routeInfo.routeType}").isEqualTo("EXEC"))// ルートタイプがEXECならEXECプロデュース（外部コマンド実行)のコースへ
                .log("EXEC Route")
                .to("direct:ExecProducer")
                .otherwise()// いずれにも合致しない場合は例外をスローの奈落コースへ
                .log("Others")
                .to("mock:error")
                .end();

    }
}
