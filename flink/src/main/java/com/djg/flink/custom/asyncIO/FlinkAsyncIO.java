
package com.djg.flink.custom.asyncIO;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Author: Wang Pei
 * Summary:
 * 用Async I/O实现流表与维表Join
 */
public class FlinkAsyncIO {
    public static void main(String[] args) throws Exception {

        String kafkaBootstrapServers = "localhost:9092";
        String kafkaGroupID = "async-test";
        String kafkaAutoOffsetReset = "latest";
        String kafkaTopic = "asyncio";
        int kafkaParallelism = 2;

        String esHost = "localhost";
        Integer esPort = 9200;
        String esUser = "";
        String esPassword = "";
        String esIndex = "dim_user";
        String esType = "dim_user";


        /**Flink DataStream 运行环境*/
        Configuration config = new Configuration();
        config.setInteger(RestOptions.PORT, 8081);
        config.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER, true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(config);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);


        /**添加数据源*/
        Properties kafkaProperties = new Properties();
        kafkaProperties.put("bootstrap.servers", kafkaBootstrapServers);
        kafkaProperties.put("group.id", kafkaGroupID);
        kafkaProperties.put("auto.offset.reset", kafkaAutoOffsetReset);
        FlinkKafkaConsumer010<String> kafkaConsumer = new FlinkKafkaConsumer010<>(kafkaTopic, new SimpleStringSchema(), kafkaProperties);
        kafkaConsumer.setCommitOffsetsOnCheckpoints(true);
        SingleOutputStreamOperator<String> source = env.addSource(kafkaConsumer).name("KafkaSource").setParallelism(kafkaParallelism);

        //数据转换
        SingleOutputStreamOperator<Tuple4<String, String, String, Integer>> sourceMap = source.map((MapFunction<String, Tuple4<String, String, String, Integer>>) value -> {
            Tuple4<String, String, String, Integer> output = new Tuple4<>();
            try {
                JSONObject obj = JSON.parseObject(value);
                output.f0 = obj.getString("userID");
                output.f1 = obj.getString("eventTime");
                output.f2 = obj.getString("eventType");
                output.f3 = obj.getInteger("productID");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output;
        }).returns(new TypeHint<Tuple4<String, String, String, Integer>>() {
        }).
        assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Tuple4<String, String, String, Integer>>(Time.seconds(3)) {
            @Override
            public long extractTimestamp(Tuple4<String, String, String, Integer> element) {
                System.out.println(element.f1);
                return Long.parseLong(element.f1);
            }
        }).returns(new TypeHint<Tuple4<String, String, String, Integer>>() {
        }).name("Map: ExtractTransform");

        //过滤掉异常数据
        SingleOutputStreamOperator<Tuple4<String, String, String, Integer>> sourceFilter = sourceMap.filter((FilterFunction<Tuple4<String, String, String, Integer>>) value -> value.f3 != null).name("Filter: FilterExceptionData");


//        SingleOutputStreamOperator<Tuple4<String, String, String, Integer>> xStream = sourceFilter.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Tuple4<String, String, String, Integer>>(Time.seconds(3)) {
//
//            @Override
//            public long extractTimestamp(Tuple4<String, String, String, Integer> element) {
//                System.out.println(element.f1);
//                return Long.parseLong(element.f1);
//            }
//        }).keyBy(0).window(TumblingEventTimeWindows.of(Time.seconds(10))).reduce(new ReduceFunction<Tuple4<String, String, String, Integer>>() {
//            @Override
//            public Tuple4<String, String, String, Integer> reduce(Tuple4<String, String, String, Integer> in, Tuple4<String, String, String, Integer> out) throws Exception {
//                System.out.println("fuck off");
//                return in;
//            }
//
//            ;
//        });


        //Timeout: 超时时间 默认异步I/O请求超时时，会引发异常并重启或停止作业。 如果要处理超时，可以重写AsyncFunction#timeout方法。
        //Capacity: 并发请求数量
        /**
         * Async IO实现流表与维表Join
         */
        SingleOutputStreamOperator<Tuple5<String, String, String, Integer, Integer>> result = AsyncDataStream.unorderedWait(sourceFilter, new ElasticsearchAsyncFunction(esHost, esPort, esUser, esPassword, esIndex, esType), 500, TimeUnit.MILLISECONDS, 10).name("Join: JoinWithDim");


        /**结果输出*/
        result.print().name("PrintToConsole");

        env.execute();
    }}

