-- insert into Nodes(nodeId,nodeType,ipaddress,userid,passwd,mode) values (1,'Local','','','','');
-- insert into Nodes(nodeId,nodeType,ipaddress,userid,passwd,mode) values (2,'FTP','192.168.1.1','hoge','hoge','passive');

insert into Routes(routeId,routeType,dstNodeId,dstPath) values ('RT01','LOCAL',0,'./test/to');
insert into Routes(routeId,routeType,dstNodeId,dstPath) values ('RT02','FTP','Node01','/to');
insert into Routes(routeId,routeType,dstNodeId,dstPath) values ('RT03','LOCAL',0,'./test/to');
insert into Routes(routeId,routeType,dstNodeId,dstPath) values ('RT05','EXEC',0,'');

insert into Triggers(triggerFileName,routeId) values ('./test/from/testfile.trg','RT01');
insert into Triggers(triggerFileName,routeId) values ('./test/from/testfile2.trg','RT02');
insert into Triggers(triggerFileName,routeId) values ('./test/from/testfile3.trg','RT03');
insert into Triggers(triggerFileName,routeId) values ('./test/from/testfile4.trg','RT04');
insert into Triggers(triggerFileName,routeId) values ('./test/from/testfile5.trg','RT05');


insert into FTPServers(nodeId,host,userid,passwd,isPassive) values ('Node01','localhost','foo1','bar1',true);

insert into PostExec(routeId,cmd,arg1) values ('RT05','./sh/sample.sh','${FILENAME}');

insert into ConvertRules(routeId,srcCharset,srcLinefeed,dstCharset,dstLinefeed,footerdel) values ('RT01','UTF-8','LF','UTF-8','LF',0);
insert into ConvertRules(routeId,srcCharset,srcLinefeed,dstCharset,dstLinefeed,footerdel) values ('RT02','UTF-8','LF','UTF-8','LF',3);
insert into ConvertRules(routeId,srcCharset,srcLinefeed,dstCharset,dstLinefeed,footerdel) values ('RT03','EUC-JP','LF','UTF-8','CRLF',3);
insert into ConvertRules(routeId,srcCharset,srcLinefeed,dstCharset,dstLinefeed,footerdel) values ('RT04','MS932','CRLF','UTF-8','LF',0);
insert into ConvertRules(routeId,srcCharset,srcLinefeed,dstCharset,dstLinefeed,footerdel) values ('RT05','UTF-8','LF','UTF-8','LF',0);

insert into FileNameConvertRules(fileID,routeId,srcfilename,dstfilename) values (1,'RT03','testfile3.dat','TESTFILE3-${DATE}.DAT');
