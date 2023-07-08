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
    dstPath varchar(30),
    isBinary boolean,
    charset  varchar(10)
);

create table if not exists Triggers(
    triggerFileName varchar(100) primary key ,
    routeId varchar(8)
);

