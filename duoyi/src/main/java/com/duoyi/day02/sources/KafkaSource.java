package com.duoyi.day02.sources;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.util.Properties;

/**
 * 从Kafka中读取数据的Source，可以并行的Source，并且可以实现ExactlyOnce
 */
public class KafkaSource {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties props = new Properties();
        //指定Kafka的Broker地址
        props.setProperty("bootstrap.servers", "192.168.90.71:9092");
        //指定组ID
        props.setProperty("group.id", "djg");
        //如果没有记录偏移量，第一次从最开始消费
        props.setProperty("auto.offset.reset", "earliest");
        //kafka的消费者不自动提交偏移量
        //props.setProperty("enable.auto.commit", "false");


        //KafkaSource
        FlinkKafkaConsumer011<String> kafkaSource = new FlinkKafkaConsumer011<>(
                "djg0223",
                new SimpleStringSchema(),
                props);

        //Source
        DataStream<String> lines = env.addSource(kafkaSource);

        //Sink
        lines.print();

        env.execute("KafkaSource");
    }
}
