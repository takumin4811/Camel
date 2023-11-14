Drop table if exists FileInfos;
create table if not exists FileInfos(
    fileId varchar(20) primary key,
    fileIdName varchar(100) ,
    srcFileName varchar(50),
    srcFileNameExt varchar(5),
    srcFileNameTrgExt varchar(5),
    dstFileName varchar(50),
    dstFileNameExt varchar(5),
    dstFileNameTrgExt varchar(5),
    routeId varchar(20),
    KidoType varchar(10),
    RegexKbn int
);
Drop table if exists Routes;
create table if not exists Routes(
    routeId varchar(20) primary key,
    routeIdName varchar(100) ,
    srcNodeId varchar(8),
    srcPath varchar(30),
    srcCharset varchar(10),
    srcLinefeed varchar(8),
    dstNodeId varchar(8),
    dstPath varchar(30),
    dstCharset varchar(10),
    dstLinefeed varchar(8),
    footerdel int
);
Drop table if exists Nodes;
create table if not exists Nodes(
    nodeId varchar(8) primary key,
    nodetype varchar(8),
    host varchar(15),
    port int(5),
    userid varchar(10),
    passwd varchar(10),
    isPassive boolean,
    proxyhost varchar(15),
    proxyport int(5),
    proxyuserid varchar(10),
    proxypasswd varchar(10)
);

Drop table if exists PollingNodes;
create table if not exists PollingNodes(
    nodeId varchar(8) primary key,
    pollingDirectory varchar(20),
    PollingRecursive boolean,
    PollingTrgName varchar(10)
);


Drop view if exists FilesRoutes;
create view FilesRoutes as 
select f.KidoType, f.fileId,f.RegexKbn,r.routeId,
r.srcNodeId,r.srcPath,f.srcFileName,f.srcFileNameExt,f.srcFileNameTrgExt,r.srcCharset,r.srcLineFeed,
r.dstNodeId,r.dstPath,f.dstFileName,f.dstFileNameExt,f.dstFileNameTrgExt,r.dstCharset,r.dstLineFeed
,r.footerdel from FileInfos as f inner join Routes as r on f.routeId=r.routeId;

Drop table if exists TransferLogs;
create table if not exists TransferLogs(
    id int primary key,
    requestId int,
    routeId varchar(20),
    routeIdName varchar(100) ,
    fileId varchar(20) ,
    fileIdName varchar(100) ,
    srcNodeId varchar(8),
    srcPath varchar(30),
    srcFileName varchar(50),
    srcFileNameExt varchar(5),
    srcFileNameTrgExt varchar(5),
    srcCharset varchar(10),
    srcLinefeed varchar(8),
    dstNodeId varchar(8),
    dstPath varchar(30),
    dstFileName varchar(50),
    dstFileNameExt varchar(5),
    dstFileNameTrgExt varchar(5),
    dstCharset varchar(10),
    dstLinefeed varchar(8),
    footerdel int,
    KidoType varchar(10),
    result varchar(10),
    starttime timestamp,
    endtime timestamp
);
