package com.djg.asyncRedisSink;

import com.djg.utils.EnvUtil;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class AsyncRedisSinkDemo {

    public static void main(String[] args) throws Exception {
        Tuple2<StreamExecutionEnvironment, StreamTableEnvironment> envs = EnvUtil.getEnv(1, "blink", TimeCharacteristic.ProcessingTime, CheckpointingMode.EXACTLY_ONCE);
        StreamExecutionEnvironment env = envs.f0;
        StreamTableEnvironment tEnv = envs.f1;


        String kafkaDDLSql =
                "CREATE TABLE source_kafka_table (\n" +
                        "  ename VARCHAR COMMENT '名字',\n" +
                        "  esex VARCHAR COMMENT '性别',\n" +
                        "  ebirthday VARCHAR COMMENT '生日',\n" +
                        "  eno VARCHAR COMMENT '学号',\n" +
                        "  eincome VARCHAR COMMENT '收入',\n" +
                        "  eteam VARCHAR COMMENT '团队',\n" +
                        "  proctime as PROCTIME()\n" +
                        ")\n" +
                        "WITH (\n" +
                        "  'connector.type' = 'kafka', -- 使用 kafka connector\n" +
                        "  'connector.version' = '0.11',  -- kafka 版本\n" +
                        "  'connector.topic' = 'djg1010',  -- kafka topic\n" +
                        "  'connector.startup-mode' = 'latest-offset', -- 从最新的 offset 开始读取\n" +
                        "  'connector.properties.zookeeper.connect' = '192.168.90.71:2181/kafka',\n" +
                        "  'connector.properties.bootstrap.servers' = '192.168.90.71:9092',\n" +
                        "  'connector.startup-mode' = 'latest-offset',\n" +
                        "  'format.type' = 'json',  -- 数据源格式为 json\n" +
                        "  'format.derive-schema' = 'true' -- 从 DDL schema 确定 json 解析规则\n" +
                        ")";


        tEnv.executeSql(kafkaDDLSql);


        String redisDDLSql =
                "CREATE TABLE sink_redis_table\n" +
                        "(\n" +
                        "    `ename` STRING,\n" +
                        "    `einfo` STRING\n" +
                        ")\n" +
                        "WITH (\n" +
                        "    'connector.type' = 'redis',\n" +
                        "    'redis-mode' = 'cluster',\n" +
                        "    'redis.ip' = '192.168.90.40',\n" +
                        "    'redis.port' = '6379',\n" +
                        "    'command' = 'SET'\n" +
                        ")";

        tEnv.executeSql(redisDDLSql);


        String insertSql = "insert into sink_redis_table select ename, eno from source_kafka_table";




        // TableResult#print 或者 TableResult#collect负责从流中手机返回数据，只有在开启checkpoint且在exactly-once语义下，才能使用这种方式
        // 参考：http://apache-flink.147419.n8.nabble.com/flink-1-11-executeSql-kafka-print-td5367.html#a5370
//        String selectSql = "select ename, eno from source_kafka_table";
//        TableResult result = tEnv.sqlQuery(selectSql).execute();
//        result.print();





        DataStream<Row> adaptStream = tEnv.toAppendStream(tEnv.sqlQuery(insertSql), Row.class);
        adaptStream.print();
        env.execute();

    }

}
