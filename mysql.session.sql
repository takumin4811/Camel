SELECT Nodes.nodeId,nodetype,host,port,userid,passwd,isPassive,pollingDirectory,PolingRecursive,PollingTrgName 
FROM PollingNodes inner join Nodes on Nodes.nodeId=PollingNodes.nodeId where nodeType="local";

SELECT fileId,srcNodeId,srcPath,srcfileName,srcFileNameExt,srcFileNameTrgExt,srcLinefeed,srcCharset,routeId  from FilesRoutes where srcNodeId='nodeA' and srcPath='./test/from/06' and srcfilename='f06-utf-lf' and srcFileNameExt='dat';
SELECT fileId,srcNodeId,srcPath,srcfileName,srcFileNameExt,srcFileNameTrgExt,routeId from FilesRoutes where RegexKbn=1 

cp test/tesftile-utf8.dat test/from/16/f16-utf-lf.dat
touch test/from/16/f16-utf-lf.trg

sftp -o "ProxyCommand connect -H tako@squid:3128 %h %p" hoge1@sftpSrv1
 
