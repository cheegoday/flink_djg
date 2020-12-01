package com.djg.flink.custom.kafka;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;

import java.util.Properties;

/**
 * 从Kafka中读取数据的Source，可以并行的Source，并且可以实现ExactlyOnce
 */
public class KafkaKerberos {


    public static class MyCountAggregate implements AggregateFunction<RegistryRecord, Tuple2<String, Long>, Tuple2<String, Long>> {
        @Override
        public Tuple2<String, Long> createAccumulator() {
            return new Tuple2(null, 0L);
        }

        @Override
        public Tuple2<String, Long> add(RegistryRecord value, Tuple2<String, Long> accumulator) {
            accumulator.f0 = value.getUser();
            accumulator.f1 = accumulator.f1 + value.getPrice();
            return accumulator;
        }

        @Override
        public Tuple2<String, Long> getResult(Tuple2<String, Long> accumulator) {
            return accumulator;
        }

        @Override
        public Tuple2<String, Long> merge(Tuple2<String, Long> a, Tuple2<String, Long> b) {
            return new Tuple2(a.f0, (Long)a.f1 + (Long)b.f1);
        }
    }

    private static Properties getProducerProperties(String bootstrapServers){
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", bootstrapServers);
        props.setProperty("security.protocol", "SASL_PLAINTEXT");
        props.setProperty("sasl.kerberos.service.name", "kafka");
        props.setProperty("sasl.mechanism", "GSSAPI");
        return props;
    }


    private static Properties getConsumerProperties(String bootstrapServers){
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", bootstrapServers);
        props.setProperty("group.id", "daijiguo");
        props.setProperty("auto.offset.reset", "latest");
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("security.protocol", "SASL_PLAINTEXT");
        props.setProperty("sasl.kerberos.service.name", "kafka");
        props.setProperty("sasl.mechanism", "GSSAPI");
        return props;
    }




    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.out.println("incorrect,Usage:[ absent of bootstrapServers 、inTopic、outTopic ]");
            return;
        }
        String bootstrapServers = args[0];
        String inTopic = args[1];
        String outTopic = args[2];
        String offset = args[3];
        System.out.println(String.format("subscribe message from topic-%s in cluster:%s", bootstrapServers, inTopic));


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(10, 2000));
        // kafka source
        FlinkKafkaConsumer011<RegistryRecord> kafkaSource = new FlinkKafkaConsumer011<RegistryRecord>(inTopic, new RegistryRecordDeserialization(), getConsumerProperties(bootstrapServers));
        if (offset.equals("earliest")){
            kafkaSource.setStartFromEarliest();
        }else if (offset.equals("latest")){
            kafkaSource.setStartFromLatest();
        }else {
            System.out.println("please choose offset between 'earliest' and 'latest' !");
        }

        DataStream<RegistryRecord> sourceStream = env.addSource(kafkaSource);
        DataStream<Tuple2<String, Long>> accStream = sourceStream.keyBy(RegistryRecord::getUser)
                .timeWindow(Time.seconds(2))
                .aggregate(new MyCountAggregate());

        // kafka sink
        KeyedSerializationSchema<Tuple2<String, Long>> kafkaSerializationSchema = new RegistryRecordSerialization(outTopic);
        FlinkKafkaProducer011<Tuple2<String, Long>> kafkaProducer = new FlinkKafkaProducer011<Tuple2<String, Long>>(outTopic, kafkaSerializationSchema, getProducerProperties(bootstrapServers));

        accStream.addSink(kafkaProducer);
        env.execute("KafkaSource");
    }
}
