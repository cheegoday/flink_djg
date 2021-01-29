package com.djg.kafkaKerberos;

import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;
import org.apache.flink.api.java.tuple.Tuple2;

public class RegistryRecordSerialization implements KeyedSerializationSchema<Tuple2<String, Long>> {

    private String topic;


    public RegistryRecordSerialization(String topic) {
        this.topic = topic;
    }


    @Override
    public byte[] serializeKey(Tuple2<String, Long> element) {
        return element.f0.getBytes();
    }

    @Override
    public byte[] serializeValue(Tuple2<String, Long> element) {

        return ("key: " + element.f0 + ", value: " + String.valueOf(element.f1)).getBytes();
    }

    @Override
    public String getTargetTopic(Tuple2<String, Long> element) {
        return topic;
    }
}
