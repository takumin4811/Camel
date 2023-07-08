
package com.example1.camelSample.web.controller;

import java.io.File;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example1.camelSample.util.DB;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HelloRestController {
	@Autowired
	private CamelContext ctx;
    @Autowired
    private DB db;

    //リクエストベースでローカルから外部へ指定ファイルを配送。イメージとしては中央郵便局に荷物を置いておいて、コールセンター（Webサービス）に電話（リクエスト）があって初めて荷物を送り届けるイメージ
    //普通は中央郵便局に届いた処理は、次々に処理されるが、ここは依頼がない限り配送しない。ファイルの発生基準（イベントドリブン）ではなく、JOB起動やリクエスト起動をイメージ

    @RequestMapping(method = RequestMethod.GET, value = "/fromLocal") //HTTP（REST）リクエスト。本来はPOSTが望ましいが簡略化のためGETで
    public String fromLocal(@RequestParam("routeId") String routeId,@RequestParam("fileName") String fileName) {//リクエストパラメータはルートIDとファイル名（フルパス）
 
        Exchange exchange = new DefaultExchange(ctx);//空の荷物（Exchange)を作成
        ProducerTemplate producerTemplate = ctx.createProducerTemplate();//荷物の配達人を作成
        log.info(exchange.getIn().getHeaders().toString());
        exchange.getIn().setHeader(Exchange.FILE_NAME, fileName);//空の荷物（Exchange)のヘッダにファイル名を設定（このファイル名が宛先ファイル名となる）
        exchange.getIn().setBody(new File(fileName));//空の荷物（Exchange)にボディを設定（送るファイルを設定）
        log.info(exchange.getIn().getHeaders().toString());
        exchange.getIn().setHeader("routeInfo",db.getRouteInfoByRouteId(routeId));//ルートIDを引数にDBから宛先ルート情報を引当（見つからない場合はノーヒット例外がスローされる）ヘッダにセット
        producerTemplate.send("direct:CenterHub", exchange);//配達人に荷物を送ってもらう。といっても自分で送る必要はないので、来た荷物を次々に処理する中央郵便局（HUB)へ渡せば仕事終了。
        //上の処理は同期的に行われる（directは同期）。つまり最終宛先まで配信して初めて次のステップ（この行）に進む。以下で配送結果判定をしている。
        if (exchange.getException() != null) {return "NG";} 
        log.info("===================");
        return "OK";//リターン先は呼び出し元（ブラウザやCurlコマンド発行元）
    }

    //リクエストベースでローカルから外部にファイルを取りに行く。イメージとしてはコールセンターに電話があって、中央郵便局の集荷業者が送り主の家まで配送品を取りに行くイメージ
    //普通は送り主は送りたい荷物をポストに投げ込むことで荷物が配送されるが、ここは送り主の意思とは関係なく、中央郵便局の集荷業者が荷物を取りに行く。
    //電話をする（リクエストをする）人は、送り主とは限らない。第三者かもしれない。

    @RequestMapping(method = RequestMethod.GET, value = "/fromRemote")
    //GETでパラメータを受け取る。パラメータは３種類。取りに行く家の名前（ノードID）、取りに行った荷物を配送するためのルートID,取りに行く荷物の名前（ファイル名）
    public String fromRemote(@RequestParam("nodeId") String nodeId,@RequestParam("routeId") String routeId,@RequestParam("fileName") String fileName) {

        ConsumerTemplate consumerTemplate = ctx.createConsumerTemplate();    //荷物の集荷人を召喚
        //集荷するためのURL（ここでは手抜きで固定。本来はノードIDをベースにDBから組み立てる）
        String endpointURL ="ftp://test@localhost/from/?password=hoge&passiveMode=true&recursive=true&filename="+fileName ; 
        log.info(endpointURL);
        Exchange exchange = consumerTemplate.receive(endpointURL,10);//集荷人に荷物を取りに行ってもらう。１０秒以内に！
        log.info(exchange.getIn().getHeaders().toString());
        exchange.getIn().setHeader("routeInfo",db.getRouteInfoByRouteId(routeId));//取ってきた荷物のヘッダに宛先ルート情報をセット
        
        ProducerTemplate producerTemplate = ctx.createProducerTemplate();//今度は荷物の配達人を召喚
        producerTemplate.send("direct:CenterHub", exchange);//中央郵便局まで運んでもらう。宛先ルート情報はヘッダに入ってるし、あとは任せた！
        //任せっぱなしにはしないで、結果はちゃんと見る
        if (exchange.getException() != null) {
            return "NG";
        }
        log.info(exchange.getIn().getHeader("result",String.class));
        return "OK";        //HTTPリクエスト元（ブラウザやCurl発行者）に結果を返す
    }

}
