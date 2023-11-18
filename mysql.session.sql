SELECT Nodes.nodeId,nodetype,host,port,userid,passwd,isPassive,pollingDirectory,PolingRecursive,PollingTrgName 
FROM PollingNodes inner join Nodes on Nodes.nodeId=PollingNodes.nodeId where nodeType="local";

SELECT fileId,srcNodeId,srcPath,srcfileName,srcFileNameExt,srcFileNameTrgExt,srcLinefeed,srcCharset,routeId  from FilesRoutes where srcNodeId='nodeA' and srcPath='./test/from/06' and srcfilename='f06-utf-lf' and srcFileNameExt='dat';
SELECT fileId,srcNodeId,srcPath,srcfileName,srcFileNameExt,srcFileNameTrgExt,routeId from FilesRoutes where RegexKbn=1 

cp test/tesftile-utf8.dat test/from/16/f16-utf-lf.dat
touch test/from/16/f16-utf-lf.trg

sftp -o "ProxyCommand connect -H tako@squid:3128 %h %p" hoge1@sftpSrv1
 
java.lang.AssertionError: Response body expected:
[{"status":"OK","message":"Request is Completed. 1 File Transfered","tranferedFileList":[{"consumedFileName":"f113-utf-lf.dat","producedFileName":"./test/to/113/F113-UTF-LF.DAT","result":"OK"}]}] but was:
[{"status":"OK","message":"Request is Completed. 1 File Transfered","tranferedFileList":[{"consumedFileName":"f113-utf-lf.dat","producedFileName":"./test/to/113/F113-UTF-LF$1.DAT","result":"OK"}]}]


 {CamelFileAbsolute=false, 
 CamelFileAbsolutePath=data/f113-utf-lf.dat, 
 CamelFileHost=sftpSrv3, 
 CamelFileLastModified=1700260469000, 
 CamelFileLength=5510, 
 CamelFileLocalWorkPath=null, 
 CamelFileName=F113-UTF-LF$1.DAT, CamelFileNameConsumed=f113-utf-lf.dat, 
 CamelFileNameOnly=f113-utf-lf.dat, 
 CamelFileNameProduced=./test/to/113/F113-UTF-LF$1.DAT, 
 CamelFileParent=data, CamelFilePath=data//f113-utf-lf.dat,
 CamelFileRelativePath=f113-utf-lf.dat, 
 CamelFtpReplyCode=0, 
 CamelFtpReplyString=OK, 
 CamelMessageTimestamp=1700260469000, 
 routeInfo=RouteInfo(srcFileInfo=SrcFileInfo(fileId=F113, routeId=RT113, srcNodeId=nodeF, srcPath=data, srcfileName=f113-utf-lf, srcFileNameExt=dat, srcFileNameTrgExt=trg, srcLinefeed=LF, srcCharset=utf8, regexKbn=0), dstFileInfo=DstFileInfo(fileId=F113, routeId=RT113, dstNodeId=nodeA, dstPath=./test/to/113, dstfileName=F113-UTF-LF$1, dstFileNameExt=DAT, dstFileNameTrgExt=TRG, dstLinefeed=LF, dstCharset=utf8, footerdel=0, regexKbn=0), srcNodeType=SFTP, dstNodeType=LOCAL), 
 targetStr=file:./test/to/113?flatten=true&doneFileName=${file:name.noext}.TRG} {CamelFileAbsolute=false, CamelFileAbsolutePath=data/f113-utf-lf.dat, CamelFileHost=sftpSrv3, CamelFileLastModified=1700260469000, CamelFileLength=5510, CamelFileLocalWorkPath=null, CamelFileName=F113-UTF-LF$1.DAT, CamelFileNameConsumed=f113-utf-lf.dat, CamelFileNameOnly=f113-utf-lf.dat, CamelFileNameProduced=./test/to/113/F113-UTF-LF$1.DAT, CamelFileParent=data, CamelFilePath=data//f113-utf-lf.dat, CamelFileRelativePath=f113-utf-lf.dat, CamelFtpReplyCode=0, CamelFtpReplyString=OK, CamelMessageTimestamp=1700260469000, routeInfo=RouteInfo(srcFileInfo=SrcFileInfo(fileId=F113, routeId=RT113, srcNodeId=nodeF, srcPath=data, srcfileName=f113-utf-lf, srcFileNameExt=dat, srcFileNameTrgExt=trg, srcLinefeed=LF, srcCharset=utf8, regexKbn=0), dstFileInfo=DstFileInfo(fileId=F113, routeId=RT113, dstNodeId=nodeA, dstPath=./test/to/113, dstfileName=F113-UTF-LF$1, dstFileNameExt=DAT, dstFileNameTrgExt=TRG, dstLinefeed=LF, dstCharset=utf8, footerdel=0, regexKbn=0), srcNodeType=SFTP, dstNodeType=LOCAL), targetStr=file:./test/to/113?flatten=true&doneFileName=${file:name.noext}.TRG}