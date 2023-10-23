package com.example1.camelSample;

import org.apache.camel.ProducerTemplate;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.commons.io.FileUtils;

@CamelSpringBootTest
@SpringBootTest
class IntegrationTests {
    @Autowired
    protected ProducerTemplate producer;
    @Autowired
    protected ConsumerTemplate consumer;
    @Value("${my.camel.testdir}")
    private String testdir;
    Exchange origin;
    private final int WAITTIME = 3000;

    @BeforeAll
    static void setup() throws IOException {
        FileUtils.cleanDirectory(new File("./test/to/"));
        FileUtils.cleanDirectory(new File("./test/from/"));
    }

    // 'RT01','単純コピー','nodeA','./test/from/01','utf8','LF','nodeA','./test/to/01','utf8','LF',0);
    // 'RT02','LFからCRLFに変換','nodeA','./test/from/02','utf8','LF','nodeA','./test/to/02','utf8','CRLF',0);
    // 'RT03','UTFからEUCに変換','nodeA','./test/from/03','utf8','LF','nodeA','./test/to/03','euc-jp','LF',0);
    // 'RT04','UTFからSJISに変換','nodeA','./test/from/04','utf8','LF','nodeA','./test/to/04','sjis','LF',0);
    // 'RT05','EUCーCRLFからSJIS-LF、フッタ削除','nodeA','./test/from/05','euc-jp','CRLF','nodeA','./test/to/05','utf8','LF',3);
    // 'RT06','nodeA','FTPPUT','./test/from/06','utf8','LF','nodeB','./06','utf8','LF',0);
    // 'RT07','nodeB','FTPGET','./07','utf8','LF','nodeB','to/07','utf8','LF',0);
    // 'RT08','nodeA','正規表現パターン','./08','utf8','LF','nodeA','./test/to/08','utf8','LF',0);

    // F01','単純コピー','f01-utf-lf','dat','trg','F01-UTF-LF','DAT','','RT01','Trigger',0);
    // F02','LFからCRLFに変換','f02-utf-lf','dat','trg','F02-UTF-CRLF','DAT','','RT02','Trigger',0);
    // F03','UTFからEUCに変換','f03-utf-lf','dat','trg','F03-EUC-LF','DAT','','RT03','Trigger',0);
    // F04','UTFからSJISに変換','f04-utf-lf','dat','trg','F03-SJIS-LF','DAT','TRG','RT04','Trigger',0);
    // F05','EUCーCRLFからSJIS-LF、フッタ削除、ファイル名に日付','f05-euc-crlf','dat','trg','f05-utf-lf-3_${DATE}','DAT','TRG','RT05','Trigger',0);
    // F06','FTPPUT','f06-utf-lf','dat','trg','F06-UTF-LF','DAT','TRG','RT06','Trigger',0);
    // F07','FTPGET','f07-utf-lf','dat','trg','F07-UTF-LF','DAT','TRG','RT07','Trigger',0);
    // F08','正規表現パターン','f08-utf-lf.*','dat','trg','F01-UTF-LF-*-A','DAT','TRG','RT08','Trigger',1);

    @Test
    void F01単純コピー() throws Exception {
        Exchange origin = consumer
                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");
        producer.send("file://./test/from/01?fileName=f01-utf-lf.dat&doneFileName=f01-utf-lf.trg", origin);
        Thread.sleep(WAITTIME);
        File output = new File("./test/to/01/F01-UTF-LF.DAT");
        File expected = new File("./test/testfile-utf8.dat");
        assertEquals(true, output.exists());
        assertEquals(FileUtils.readFileToString(output, "UTF-8"), (FileUtils.readFileToString(expected, "UTF-8")));
    }

    @Test
    void F02LFからCRLFに変換() throws Exception {
        Exchange origin = consumer
                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");

        producer.send("file://./test/from/02?fileName=f02-utf-lf.dat&doneFileName=f02-utf-lf.trg", origin);
        Thread.sleep(WAITTIME);
        File output = new File("./test/to/02/F02-UTF-CRLF.DAT");
        File expected = new File("./test/testfile-utf8-crlf.dat");
        assertEquals(true, output.exists());
        assertEquals(FileUtils.readFileToString(output, "UTF-8"), (FileUtils.readFileToString(expected, "UTF-8")));
    }

    @Test
    void F03UTFからEUCに変換() throws Exception {
        Exchange origin = consumer
                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");

        producer.send("file://./test/from/03?fileName=f03-utf-lf.dat&doneFileName=f03-utf-lf.trg", origin);
        Thread.sleep(WAITTIME);
        File output = new File("./test/to/03/F03-EUC-LF.DAT");
        File expected = new File("./test/testfile-euc.dat");
        assertEquals(true, output.exists());
        assertEquals(FileUtils.readFileToString(output, "euc-jp"), (FileUtils.readFileToString(expected, "euc-jp")));
    }

    @Test
    @DisplayName("EUCーCRLFからSJISLFフッタを3行削除しファイル名に基準日を付与")
    void F05EUCーCRLFからSJISLFフッタ削除() throws Exception {
        Exchange origin = consumer
                .receiveNoWait("file://./test/?fileName=testfile-euc-crlf.dat&noop=true&idempotent=false");

        producer.send("file://./test/from/05?fileName=f05-euc-crlf.dat&doneFileName=f05-euc-crlf.trg", origin);
        Thread.sleep(WAITTIME);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new Date());
        File output = new File("./test/to/05/F05-UTF-LF-3_" + today + ".DAT");
        File output2 = new File("./test/to/05/F05-UTF-LF-3_" + today + ".TRG");
        File expected = new File("./test/testfile-utf8-3.dat");
        assertEquals(true, output.exists());
        assertEquals(true, output2.exists());
        assertEquals(FileUtils.readFileToString(output, "utf8"), (FileUtils.readFileToString(expected, "utf8")));
    }

    // 'RT06','nodeA','FTPPUT','./test/from/06','utf8','LF','nodeB','./06','utf8','LF',0);
    // F06','FTPPUT','f06-utf-lf','dat','trg','F06-UTF-LF','DAT','TRG','RT06','Trigger',0);

    @Test
    @DisplayName("FTP送信（UTF8LF無変換）")
    void F06FTPPUT() throws Exception {
        Exchange origin = consumer
                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");
        producer.send("file://./test/from/06?fileName=f06-utf-lf.dat&doneFileName=f06-utf-lf.trg", origin);
        Thread.sleep(WAITTIME);

        Exchange outputEx = consumer.receiveNoWait(
                "ftp://foo1@localhost/./06?password=bar1&passiveMode=true&fileName=F06-UTF-LF.DAT&noop=true&idempotent=false&localworkdirectory=/tmp/");
        File output = outputEx.getIn().getBody(File.class);
        File expected = new File("./test/testfile-utf8.dat");
        assertEquals(true, output.exists());
        assertEquals(FileUtils.readFileToString(output, "utf8"), (FileUtils.readFileToString(expected, "utf8")));
    }

    @Test
    @DisplayName("FTPGET-FTPPUT")
    void F07FTPGETtoFTPPUT() throws Exception {
        Exchange origin = consumer
                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");
        producer.send("ftp://foo1@localhost/./07?password=bar1&passiveMode=true&fileName=f07-utf-lf.dat&doneFileName=f07-utf-lf.trg", origin);
        Thread.sleep(WAITTIME);

        Exchange outputEx = consumer.receiveNoWait(
                "ftp://foo2@localhost:121/./to/07?password=bar2&passiveMode=true&fileName=F07-UTF-LF.DAT&noop=true&idempotent=false&localworkdirectory=/tmp/");
        File output = outputEx.getIn().getBody(File.class);
        File expected = new File("./test/testfile-utf8.dat");
        assertEquals(true, output.exists());
        assertEquals(FileUtils.readFileToString(output, "utf8"), (FileUtils.readFileToString(expected, "utf8")));
    }

    // 'RT08','nodeA','正規表現パターン','./08','utf8','LF','nodeA','./test/to/08','utf8','LF',0);
    // F08','正規表現パターン','f08-utf-lf.*','dat','trg','F01-UTF-LF-*-A','DAT','TRG','RT08','Trigger',1);
    @ParameterizedTest
    @ValueSource(strings = { "lf1", "lf2", "lf3", "lg1", "lg2", "lg3", })
    void F08正規表現パターン(String strings) throws Exception {
        Exchange origin = consumer
                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");
        String sendurl = "file://./test/from/08?fileName=f08-utf-" + strings + ".dat&doneFileName=f08-utf-" + strings
                + ".trg";
        producer.send(sendurl, origin);
        Thread.sleep(WAITTIME);
        File output;
        File expected;
        output = new File("./test/to/08/F08-UTF-" + strings.toUpperCase() + "-A.DAT");
        expected = new File("./test/testfile-utf8.dat");

        if (strings.startsWith("lf")) {
            assertEquals(true, output.exists());
            assertEquals(FileUtils.readFileToString(output, "UTF-8"), (FileUtils.readFileToString(expected, "UTF-8")));
        } else {
            assertEquals(false, output.exists());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "lf1", "lf2", "lf3", "lg1", "lg2", "lg3", })
    void F09正規表現パターン2(String strings) throws Exception {
        Exchange origin = consumer
                .receiveNoWait("file://./test/?fileName=testfile-utf8.dat&noop=true&idempotent=false");
        String sendurl = "file://./test/from/09?fileName=f09-utf-" + strings + ".dat" + "&doneFileName=f09-utf-"
                + strings + ".trg";
        producer.send(sendurl, origin);
        Thread.sleep(WAITTIME);
        File output;
        File expected;
        Exchange outputEx;

        outputEx = consumer.receiveNoWait("ftp://foo1@localhost/./09?password=bar1&passiveMode=true&fileName=F09-UTF-"
                + strings.toUpperCase() + "-A.DAT&noop=true&idempotent=false&localworkdirectory=/tmp/");

        if (strings.startsWith("lf")) {
            output = outputEx.getIn().getBody(File.class);
            expected = new File("./test/testfile-utf8.dat");
            assertEquals(true, output.exists());
            assertEquals(FileUtils.readFileToString(output, "UTF-8"), (FileUtils.readFileToString(expected, "UTF-8")));
        } else {
            assertNull(outputEx);
        }

    }

}
