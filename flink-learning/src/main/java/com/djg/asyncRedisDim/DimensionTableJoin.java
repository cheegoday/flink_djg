package com.djg.asyncRedisDim;

import com.djg.utils.EnvUtil;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Properties;

import static org.apache.flink.table.api.Expressions.$;

/**
 * 实时数仓需要事实表关联维度表，维度表一般在外部存储
 */
public class DimensionTableJoin {

    public static void main(String[] args) throws Exception {

        Tuple2<StreamExecutionEnvironment, StreamTableEnvironment> envs = EnvUtil.getEnv(1, "blink", TimeCharacteristic.ProcessingTime, CheckpointingMode.AT_LEAST_ONCE);
        StreamExecutionEnvironment env = envs.f0;
        StreamTableEnvironment tableEnv = envs.f1;



        // kafka
        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "192.168.90.71:9092");
        prop.setProperty("group.id", "daijiguo");
        prop.setProperty("auto.offset.reset", "latest");
        prop.setProperty("enable.auto.commit", "true");
        FlinkKafkaConsumer011<String> fc = new FlinkKafkaConsumer011<>("djg0112", new SimpleStringSchema(), prop);
        fc.setStartFromLatest();
        SingleOutputStreamOperator<String> source = env.addSource(fc).returns(BasicTypeInfo.STRING_TYPE_INFO);
        tableEnv.createTemporaryView("kafka_source", source, $("id"), $("proctime").proctime());



        //redis
        RedisLookupableTableSource tableSource = RedisLookupableTableSource.Builder.newBuilder()
                .withFieldNames(new String[]{"id", "name"})
                .withFieldTypes(new TypeInformation[]{Types.STRING, Types.STRING})
                .build();
        tableEnv.registerTableSource("redis_dim", tableSource);



//        tableEnv.executeSql();


        //执行sql
        String sql = "select t1.id,t2.name" +
                " from kafka_source as t1" +
                " join redis_dim FOR SYSTEM_TIME AS OF t1.proctime as t2" +
                " on t1.id = t2.id";

//        TableResult tableResult = tableEnv.sqlQuery(sql).execute();
//        tableResult.print();

        Table table = tableEnv.sqlQuery(sql);
        DataStream<Row> result = tableEnv.toAppendStream(table, Row.class);
        result.print();
        env.execute("redisAsyncDemo");
    }
}
