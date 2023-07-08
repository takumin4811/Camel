package com.example1.camelSample.routes;
import org.springframework.stereotype.Component;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;

@Component
public class Hub extends EndpointRouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:CenterHub")//Consumerからのリンク先
        .id("CenterHub")//中央郵便局
        .log("RouteInfomation  ${in.headers.routeInfo}")//荷物（Exchange)の中のヘッダにある宛先ルート情報を取得・ログ出力
        .choice()//宛先ルート情報にあるタイプを判定し、ルートを振り分け
            .when(simple("${in.headers.routeInfo.routeType}").isEqualTo("LOCAL")) //ルートタイプがローカルならローカルファイルプロデュース（MOVE)のコースへ      
                .log("Local Route")
                .to("direct:LocalFileProducer")
            .when(simple("${in.headers.routeInfo.routeType}").isEqualTo("FTP"))//ルートタイプがFTPならFTPプロデュース（FTP-PUT)のコースへ
                .log("FTP Route")
                .to("direct:FtpProducer")
            .when(simple("${in.headers.routeInfo.routeType}").isEqualTo("EXEC"))//ルートタイプがEXECならEXECプロデュース（外部コマンド実行)のコースへ
                .log("EXEC Route")
                .to("direct:ExecProducer")
            .otherwise()//いずれにも合致しない場合は例外をスローの奈落コースへ
                .log("Others")
                .to("mock:error")
        .end()
        ;    
    
    }
}
