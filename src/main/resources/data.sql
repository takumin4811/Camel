
insert into Routes values ('RT01','単純コピー','nodeA','./test/from/01','utf8','LF','nodeA','./test/to/01','utf8','LF',0);
insert into Routes values ('RT02','LFからCRLFに変換','nodeA','./test/from/02','utf8','LF','nodeA','./test/to/02','utf8','CRLF',0);
insert into Routes values ('RT03','UTFからEUCに変換','nodeA','./test/from/03','utf8','LF','nodeA','./test/to/03','euc-jp','LF',0);
insert into Routes values ('RT04','UTFからSJISに変換','nodeA','./test/from/04','utf8','LF','nodeA','./test/to/04','sjis','LF',0);
insert into Routes values ('RT05','EUCーCRLFからSJIS-LF、フッタ削除','nodeA','./test/from/05','euc-jp','CRLF','nodeA','./test/to/05','utf8','LF',3);
insert into Routes values ('RT06','FTPPUT','nodeA','./test/from/06','utf8','LF','nodeB','./06','utf8','LF',0);
insert into Routes values ('RT07','FTPGET-FTPPUT','nodeB','/07','utf8','LF','nodeC','to/07','utf8','LF',0);
insert into Routes values ('RT08','正規表現パターン','nodeA','./test/from/08','utf8','LF','nodeA','./test/to/08','utf8','LF',0);
insert into Routes values ('RT09','正規表現パターン2','nodeA','./test/from/09','utf8','LF','nodeB','./09','utf8','LF',0);
insert into Routes values ('RT101','単純コピーリクエスト','nodeA','./test/from/101','utf8','LF','nodeA','./test/to/101','utf8','LF',0);
insert into Routes values ('RT102','リモートからのFTP取得リクエスト','nodeB','/102','utf8','LF','nodeA','./test/to/102','utf8','LF',0);
insert into Routes values ('RT112','リモートからのSFTP取得リクエスト','nodeD','data','utf8','LF','nodeA','./test/to/112','utf8','LF',0);
insert into Routes values ('RT103','コピーリクエスト(正規表現）','nodeB','/test/from/103','utf8','LF','nodeA','./test/to/103','utf8','LF',0);
insert into Routes values ('RT16','SFTPPUT','nodeA','./test/from/16','utf8','LF','nodeD','data','utf8','LF',0);
insert into Routes values ('RT17','SFTPGET-SFTPPUT','nodeD','/data','utf8','LF','nodeE','data','utf8','LF',0);

insert into Nodes values ('nodeA','local','',null,'','',null,null,null,null,null);
insert into Nodes values ('nodeB','ftp','ftpSrv1','21','foo1','bar1',true,null,null,null,null);
insert into Nodes values ('nodeC','ftp','ftpSrv2','21','foo2','bar2',true,null,null,null,null);
insert into Nodes values ('nodeD','sftp','sftpSrv1','22','hoge1','fuga1',null,null,null,null,null);
insert into Nodes values ('nodeE','sftp','sftpSrv2','22','hoge2','fuga2',null,'squid',3128,'tako','ika');

insert into PollingNodes values ('nodeA','./test/from',true,'trg');
insert into PollingNodes values ('nodeB','/',true,'trg');
insert into PollingNodes values ('nodeC','/',true,'trg');
insert into PollingNodes values ('nodeD','/',true,'trg');
insert into PollingNodes values ('nodeE','/',true,'trg');

insert into FileInfos values ('F01','単純コピー','f01-utf-lf','dat','trg','F01-UTF-LF','DAT','','RT01','Trigger',0);
insert into FileInfos values ('F02','LFからCRLFに変換','f02-utf-lf','dat','trg','F02-UTF-CRLF','DAT','','RT02','Trigger',0);
insert into FileInfos values ('F03','UTFからEUCに変換','f03-utf-lf','dat','trg','F03-EUC-LF','DAT','','RT03','Trigger',0);
insert into FileInfos values ('F04','UTFからSJISに変換','f04-utf-lf','dat','trg','F03-SJIS-LF','DAT','','RT04','Trigger',0);
insert into FileInfos values ('F05','EUCーCRLFからSJIS-LF、フッタ削除、ファイル名に日付','f05-euc-crlf','dat','trg','F05-UTF-LF-3_${DATE}','DAT','TRG','RT05','Trigger',0);
insert into FileInfos values ('F06','FTPPUT','f06-utf-lf','dat','trg','F06-UTF-LF','DAT','TRG','RT06','Trigger',0);
insert into FileInfos values ('F07','FTPGET-FTPPUT','f07-utf-lf','dat','trg','F07-UTF-LF','DAT','TRG','RT07','Trigger',0);
insert into FileInfos values ('F08','正規表現パターン','f08-utf-lf(.*)','dat','trg','F08-UTF-LF$1-A','DAT','TRG','RT08','Trigger',1);
insert into FileInfos values ('F09','正規表現パターン2','f09-utf-lf(.*)','dat','trg','F09-UTF-LF$1-A','DAT','TRG','RT09','Trigger',1);
insert into FileInfos values ('F16','SFTPPUT','f16-utf-lf','dat','trg','F16-UTF-LF','DAT','TRG','RT16','Trigger',0);
insert into FileInfos values ('F17','SFTPGET-SFTPPUT','f17-utf-lf','dat','trg','F17-UTF-LF','DAT','TRG','RT17','Trigger',0);

insert into FileInfos values ('F101','単純コピーリクエスト','f101-utf-lf','dat','trg','F101-UTF-LF','DAT','TRG','RT101','',0);
insert into FileInfos values ('F102','リモートからのFTP取得リクエスト','f102-utf-lf','dat','trg','F102-UTF-LF','DAT','TRG','RT102','',0);
insert into FileInfos values ('F103','コピーリクエスト(正規表現）','f103-utf-lf(.*)','dat','trg','F103-UTF-LF$1','DAT','TRG','RT103','',1);
insert into FileInfos values ('F112','リモートからのSFTP取得リクエスト','f112-utf-lf','dat','trg','F112-UTF-LF','DAT','TRG','RT112','',0);

