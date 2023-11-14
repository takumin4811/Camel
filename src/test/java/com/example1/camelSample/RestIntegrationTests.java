package com.example1.camelSample;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.commons.io.FileUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.time.Duration;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@CamelSpringBootTest
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RestIntegrationTests {
        /** 接続テスト用のクライアント */
        private WebTestClient client = WebTestClient.bindToServer().responseTimeout(Duration.ofSeconds(30)).build();
        @LocalServerPort
        private int port;
        @Autowired
        private ProducerTemplate producer;
        @Autowired
        private ConsumerTemplate consumer;
        @Value("${my.camel.testdir}")
        private String testdir;

        @Test
        void F101単純コピーリクエスト_FILDID指定() throws Exception {
                String baseurl = "http://localhost:" + port;
                Exchange origin = consumer
                                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");
                producer.send("file://./test/from/101?fileName=f101-utf-lf.dat", origin);

                String url = baseurl + "/api/?fileId=F101";
                String expectedStr = """
                        {"status":"OK","message":"Request is Completed. 1 File Transfered","tranferedFileList":[{"consumedFileName":"f101-utf-lf.dat","producedFileName":"./test/to/101/F101-UTF-LF.DAT","result":"OK"}]}""";

                this.client.get().uri(url)
                                // .accept(MediaType.APPLICATION_JSON)
                                .exchange().expectBody(String.class).isEqualTo(expectedStr);

                File output = new File("./test/to/101/F101-UTF-LF.DAT");
                File expected = new File("./test/testfile-utf8.dat");
                assertEquals(true, output.exists());
                assertEquals(FileUtils.readFileToString(output, "UTF-8"),
                                (FileUtils.readFileToString(expected, "UTF-8")));
        }

        @Test
        void F101単純コピーリクエスト_FILDNAME指定() throws Exception {
                String baseurl = "http://localhost:" + port;
                Exchange origin = consumer
                                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");
                producer.send("file://./test/from/101?fileName=f101-utf-lf.dat", origin);

                String url = baseurl + "/api/nodeA?srcPath=./test/from/101&srcFileNameWithExt=f101-utf-lf.dat";
                String expectedStr = """
                        {"status":"OK","message":"Request is Completed. 1 File Transfered","tranferedFileList":[{"consumedFileName":"f101-utf-lf.dat","producedFileName":"./test/to/101/F101-UTF-LF.DAT","result":"OK"}]}""";

                this.client.get().uri(url).exchange().expectBody(String.class).isEqualTo(expectedStr);

                File output = new File("./test/to/101/F101-UTF-LF.DAT");
                File expected = new File("./test/testfile-utf8.dat");
                assertEquals(true, output.exists());
                assertEquals(FileUtils.readFileToString(output, "UTF-8"),
                                (FileUtils.readFileToString(expected, "UTF-8")));
        }

        @Test
        void F102リモートからの取得リクエスト() throws Exception {
                String baseurl = "http://localhost:" + port;
                Exchange origin = consumer
                                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");
                producer.send("ftp://foo1@ftpSrv1:21/./102?password=bar1&passiveMode=true&fileName=f102-utf-lf.dat",
                                origin);
                String url = baseurl + "/api/nodeB?srcPath=/102&srcFileNameWithExt=f102-utf-lf.dat";
                String expectedStr = """
                        {"status":"OK","message":"Request is Completed. 1 File Transfered","tranferedFileList":[{"consumedFileName":"f102-utf-lf.dat","producedFileName":"./test/to/102/F102-UTF-LF.DAT","result":"OK"}]}""";

                this.client.get().uri(url).exchange().expectBody(String.class).isEqualTo(expectedStr);

                File output = new File("./test/to/102/F102-UTF-LF.DAT");
                File expected = new File("./test/testfile-utf8.dat");
                assertEquals(true, output.exists());
                assertEquals(FileUtils.readFileToString(output, "UTF-8"),
                                (FileUtils.readFileToString(expected, "UTF-8")));
        }
        @Test
        void F112リモートからの取得リクエスト() throws Exception {
                String baseurl = "http://localhost:" + port;
                Exchange origin = consumer
                                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");
                producer.send("sftp://hoge1@sftpSrv1:22/data?password=fuga1&fileName=f112-utf-lf.dat",
                                origin);
                String url = baseurl + "/api/nodeD?srcPath=data&srcFileNameWithExt=f112-utf-lf.dat";
                String expectedStr = """
                        {"status":"OK","message":"Request is Completed. 1 File Transfered","tranferedFileList":[{"consumedFileName":"f112-utf-lf.dat","producedFileName":"./test/to/112/F112-UTF-LF.DAT","result":"OK"}]}""";

                this.client.get().uri(url).exchange().expectBody(String.class).isEqualTo(expectedStr);

                File output = new File("./test/to/112/F112-UTF-LF.DAT");
                File expected = new File("./test/testfile-utf8.dat");
                assertEquals(true, output.exists());
                assertEquals(FileUtils.readFileToString(output, "UTF-8"),
                                (FileUtils.readFileToString(expected, "UTF-8")));
        }

        @Test
        void F103リモートからの取得リクエスト正規表現による複数取得() throws Exception {
                String baseurl = "http://localhost:" + port;
                Exchange origin = consumer
                                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");
                producer.send("ftp://foo1@ftpSrv1:21/./test/from/103?password=bar1&passiveMode=true&fileName=f103-utf-lf1.dat&noop=true",
                                origin);
                producer.send("ftp://foo1@ftpSrv1:21/./test/from/103?password=bar1&passiveMode=true&fileName=f103-utf-lf2.dat&noop=true",
                                origin);
                producer.send("ftp://foo1@ftpSrv1:21/./test/from/103?password=bar1&passiveMode=true&fileName=f103-utf-lf3.dat&noop=true",
                                origin);
                producer.send("ftp://foo1@ftpSrv1:21/./test/from/103?password=bar1&passiveMode=true&fileName=f103-utf-lg1.dat&noop=true",
                                origin);
                String url = baseurl + "/api/nodeB?srcPath=/test/from/103&srcFileNameWithExt=f103-utf-lf(.*).dat";
                String expectedStr = """
                                {"status":"OK","message":"Request is Completed. 3 File Transfered","tranferedFileList":[{"consumedFileName":"f103-utf-lf1.dat","producedFileName":"./test/to/103/F103-UTF-LF1.DAT","result":"OK"},{"consumedFileName":"f103-utf-lf2.dat","producedFileName":"./test/to/103/F103-UTF-LF2.DAT","result":"OK"},{"consumedFileName":"f103-utf-lf3.dat","producedFileName":"./test/to/103/F103-UTF-LF3.DAT","result":"OK"}]}""";
                this.client.get().uri(url).exchange().expectBody(String.class).isEqualTo(expectedStr);

                File expected = new File("./test/testfile-utf8.dat");

                File output1 = new File("./test/to/103/F103-UTF-LF1.DAT");
                assertEquals(true, output1.exists());
                assertEquals(FileUtils.readFileToString(output1, "UTF-8"),
                                (FileUtils.readFileToString(expected, "UTF-8")));
                File output2 = new File("./test/to/103/F103-UTF-LF2.DAT");
                assertEquals(true, output2.exists());
                assertEquals(FileUtils.readFileToString(output2, "UTF-8"),
                                (FileUtils.readFileToString(expected, "UTF-8")));
                File output3 = new File("./test/to/103/F103-UTF-LF3.DAT");
                assertEquals(true, output3.exists());
                assertEquals(FileUtils.readFileToString(output3, "UTF-8"),
                                (FileUtils.readFileToString(expected, "UTF-8")));
        }

}
