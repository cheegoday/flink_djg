package cn._51doit.flink.daijiguo.sql;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;


public class SqlDemo {


    public static void main(String[] args) throws Exception {


        // set up execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.disableOperatorChaining();


        StreamTableEnvironment tEnv;
        // use blink planner in streaming mode
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .useBlinkPlanner()
                .build();
        tEnv = StreamTableEnvironment.create(env, settings);


        String ddlSql = "CREATE TABLE source_kafka_table (\n" +
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
                "  'connector.version' = 'universal',  -- kafka 版本\n" +
                "  'connector.topic' = 'djg1010',  -- kafka topic\n" +
                "  'connector.startup-mode' = 'latest-offset', -- 从最新的 offset 开始读取\n" +
                "  'connector.properties.zookeeper.connect' = 'localhost:2181',\n" +
                "  'connector.properties.bootstrap.servers' = 'localhost:9092',\n" +
                "  'connector.startup-mode' = 'earliest-offset',\n" +
                "  'format.type' = 'json',  -- 数据源格式为 json\n" +
                "  'format.derive-schema' = 'true' -- 从 DDL schema 确定 json 解析规则\n" +
                ")";


        tEnv.executeSql(ddlSql);

//        String querySql = "select TUMBLE_END(proctime, INTERVAL '1' MINUTE) as winEnd,eteam,count(*) as pv from source_kafka_table \n" +
//                "group by TUMBLE(proctime, INTERVAL '1' MINUTE), eteam";


        String querySql = "select split_index(plat1,'_',0) as plat2,sum(pv) from (\n" +
                "\n" +
                "  select TUMBLE_END(proctime, INTERVAL '1' MINUTE) as winEnd, plat1,count(*) as pv from (\n" +
                "\n" +
                "    -- 最内层，将分组的key，也就是plat加上一个随机数打散\n" +
                "    select eteam || '_' || cast(cast(RAND()*100 as int) as string) as plat1, proctime from source_kafka_table \n" +
                "\n" +
                ") group by TUMBLE(proctime, INTERVAL '1' MINUTE), plat1\n" +
                "\n" +
                ") group by winEnd, split_index(plat1,'_',0)";


        Table result = tEnv.sqlQuery(querySql);

        DataStream<Tuple2<Boolean, Row>> adaptStream = tEnv.toRetractStream(result, Row.class);

        adaptStream.print();
        env.execute();

    }
}
