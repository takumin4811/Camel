package com.example1.camelSample.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import com.example1.camelSample.exception.UnexpectedDataFoundException;
import com.example1.camelSample.service.ConvertAndRename;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;

@Component
public class Hub extends EndpointRouteBuilder {
    @Autowired
    ConvertAndRename convertAndRename;

    @Override
    public void configure() throws Exception {
        onException(EmptyResultDataAccessException.class) // DBに該当レコードが見つからない例外をキャッチしたら
                .log(LoggingLevel.WARN, "DBにデータが見つかりませんでした").handled(false)// スタックトレースは出さない（システムエラー扱いにはしない）場合はTRUE
                .end();

        onException(UnexpectedDataFoundException.class) // DBに該当レコードが見つからない例外をキャッチしたら
                .handled(false)// システム例外にする
                .log(LoggingLevel.ERROR, "!!!!!予期せぬデータが見つかりました").end();

        from("direct:ConvertionCenter")// Consumerからのリンク先
                .id("ConvertionCenter")// 中央郵便局
                .process(exchange -> convertAndRename.call(exchange)).to("direct:DistributionCenter").end();

        from("direct:DistributionCenter")// Consumerからのリンク先
                .id("DistributionCenter")// 中央郵便局
                // .log("Header ${in.headers}")//荷物（Exchange)の中のヘッダ情報を取得・ログ出力
                .choice()// 宛先ルート情報にあるタイプを判定し、ルートを振り分け
                .when(simple("${in.headers.routeInfo.dstNodeType}").isEqualTo("local")) // ルートタイプがローカルならローカルファイルプロデュース（MOVE)のコースへ
                .log("Local Route").to("direct:LocalFileProducer")
                .when(simple("${in.headers.routeInfo.dstNodeType}").isEqualTo("ftp"))// ルートタイプがFTPならFTPプロデュース（FTP-PUT)のコースへ
                .log("FTP Route").to("direct:FtpProducer")
                .when(simple("${in.headers.routeInfo.dstNodeType}").isEqualTo("sftp"))// ルートタイプがSFTPならSFTPプロデュース（FTP-PUT)のコースへ
                .log("SFTP Route").to("direct:SftpProducer")                
                .otherwise()// いずれにも合致しない場合は例外をスローの奈落コースへ
                .log("Others").to("mock:error").end();
    }
}
