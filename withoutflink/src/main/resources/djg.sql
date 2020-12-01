CREATE TABLE djg0920
(
    eno       VARCHAR,
    ename     VARCHAR,
    esex      VARCHAR,
    ebirthday VARCHAR,
    eteam     VARCHAR,
    eincome   VARCHAR
)
WITH (
    'connector' = 'kafka',
    'topic' = 'djg0920',
    'properties.bootstrap.servers' = '192.168.90.71:9092',
    'properties.group.id' = 'testGroup',
    'format' = 'debezium-json' ,
    'scan.startup.mode' = 'earliest-offset'
);

CREATE TABLE djg_sink
(
    eno       VARCHAR,
    ename     VARCHAR,
    esex      VARCHAR,
    ebirthday VARCHAR,
    eteam     VARCHAR,
    eincome   VARCHAR,
    PRIMARY KEY (eno) NOT ENFORCED
)
WITH (
    'connector' = 'jdbc',
    'url' = 'jdbc:mysql://192.168.1.10:3306/lb_stream_test?characterEncoding=UTF8',
    'username' = 'dipper',
    'password' = 'ide@123',
    'table-name' = 'TABLE_SINK_SYNC'
);

insert into djg_sink
select eno, ename, esex, ebirthday, eteam, eincome
from djg0920;