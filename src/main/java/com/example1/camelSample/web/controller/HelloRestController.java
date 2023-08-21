
package com.example1.camelSample.web.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example1.camelSample.service.DB;
import com.example1.camelSample.service.HelloService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HelloRestController {
	@Autowired
	private CamelContext ctx;
    @Autowired
    private DB db;

    @Autowired
    private HelloService helloService;
    @Autowired
    private ProducerTemplate producerTemplate;
    @Autowired
    private ConsumerTemplate consumerTemplate;

    @Value("${my.camel.localroot}")
    public String localroot;

    //リクエストベースでローカルから外部へ指定ファイルを配送。イメージとしては中央郵便局に荷物を置いておいて、コールセンター（Webサービス）に電話（リクエスト）があって初めて荷物を送り届けるイメージ
    //普通は中央郵便局に届いた処理は、次々に処理されるが、ここは依頼がない限り配送しない。ファイルの発生基準（イベントドリブン）ではなく、JOB起動やリクエスト起動をイメージ

    @RequestMapping(method = RequestMethod.GET, value = "/fromLocal") //HTTP（REST）リクエスト。本来はPOSTが望ましいが簡略化のためGETで
    //GETでパラメータを受け取る。パラメータは３種類。取りに行く家の名前（ノードID）、取りに行った荷物を配送するためのルートID,取りに行く荷物の名前（ファイル名）
    public String fromLocal(@RequestParam("routeId") String routeId,@RequestParam("fileName") String fileName) {

        // ConsumerTemplate consumerTemplate = ctx.createConsumerTemplate();    //荷物の集荷人を召喚
        //集荷するためのURL
        String endpointURL = "file://"+localroot+"?filename="+fileName+"&move=.done/${file:name}_${date:now:yyyyMMddHHmmss}&charset=utf8&moveFailed=.error/${file:name}_${date:now:yyyyMMddHHmmss}" ; 
        log.info(endpointURL);
        Exchange exchange = consumerTemplate.receive(endpointURL,10);//集荷人に荷物を取りに行ってもらう。１０秒以内に！
        log.info(exchange.getIn().getHeaders().toString());

        helloService.hello(exchange,routeId);
        // producerTemplate = ctx.createProducerTemplate();//今度は荷物の配達人を召喚
        // producerTemplate.send("direct:DistributionCenter", exchange);//中央郵便局まで運んでもらう。宛先ルート情報はヘッダに入ってるし、あとは任せた！
        producerTemplate.send("direct:DistributionCenter", exchange);//中央郵便局まで運んでもらう。宛先ルート情報はヘッダに入ってるし、あとは任せた！
        //任せっぱなしにはしないで、結果はちゃんと見る
        if (exchange.getException() != null) {
            return "NG";
        }
        log.info(exchange.getIn().getHeader("result",String.class));
        return "OK";        //HTTPリクエスト元（ブラウザやCurl発行者）に結果を返す
    }

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
