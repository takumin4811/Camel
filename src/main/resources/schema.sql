create table if not exists FTPServers(
    nodeId varchar(8) primary key ,
    host varchar(15),
    userid varchar(10),
    passwd varchar(10),
    isPassive boolean
);

create table if not exists Routes(
    routeId varchar(8) primary key ,
    routeType varchar(10),
    dstNodeId varchar(8),
    dstPath varchar(30)
);

create table if not exists ConvertRules(
    routeId varchar(8) primary key ,
    srcCharset varchar(10),
    srcLinefeed varchar(8),
    dstCharset varchar(10),
    dstLinefeed varchar(8),
    footerdel int
);

create table if not exists FileNameConvertRules(
    fileId int primary key ,
    routeId varchar(8) ,
    srcfilename varchar(30),
    dstfilename varchar(30)
);



create table if not exists Triggers(
    triggerFileName varchar(100) primary key ,
    routeId varchar(8)
);

create table if not exists PostExec(
    routeId varchar(8) primary key ,
    cmd varchar(100),
    arg1 varchar(100)
);
