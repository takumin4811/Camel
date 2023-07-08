-- insert into Nodes(nodeId,nodeType,ipaddress,userid,passwd,mode) values (1,'Local','','','','');
-- insert into Nodes(nodeId,nodeType,ipaddress,userid,passwd,mode) values (2,'FTP','192.168.1.1','hoge','hoge','passive');

insert into Routes(routeId,routeType,dstNodeId,dstPath) values ('RT01','LOCAL',0,'/tmp/to');
insert into Routes(routeId,routeType,dstNodeId,dstPath,isBinary,charset) values ('RT02','FTP','Node01','/to',true,'utf-8');
insert into Routes(routeId,routeType,dstNodeId,dstPath,isBinary,charset) values ('RT03','FTP','Node01','/to',true,'utf-8');
insert into Routes(routeId,routeType,dstNodeId,dstPath) values ('RT04','LOCAL',0,'/tmp/to');
insert into Routes(routeId,routeType,dstNodeId,dstPath) values ('RT05','EXEC',0,'/tmp/to');

insert into Triggers(triggerFileName,routeId) values ('/tmp/from/testfile.trg','RT01');
insert into Triggers(triggerFileName,routeId) values ('/tmp/from/testfile2.trg','RT02');
insert into Triggers(triggerFileName,routeId) values ('from/testfile4.trg','RT04');
insert into Triggers(triggerFileName,routeId) values ('/tmp/from/ExecType.trg','RT05');

insert into FTPServers(nodeId,host,userid,passwd,isPassive) values ('Node01','localhost','test','hoge',true);

