package com.djg.kerberos;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class KafkaConsumerKerberos {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.security.krb5.conf", System.getProperty("krb5"));
        String principal = System.getProperty("principal");
        String keytab = System.getProperty("keytab");
        String bootstrapServers = System.getProperty("bootstrapServers");
        String topic = System.getProperty("topic");

        Krb5LoginModule krb5LoginModule = new Krb5LoginModule.Builder()
                .serviceName("kafka")
                .contextName("KafkaClient")
                .principal(principal)
                .keytab(keytab).build();
        String appConf = krb5LoginModule.configToString(false);


        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // sasl
        consumerConfig.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        // dynamic jass
        consumerConfig.put(SaslConfigs.SASL_JAAS_CONFIG, appConf);

//        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);
        TopicPartition topicPartition = new TopicPartition(topic, 0);

        Set<TopicPartition> assignments = consumer.partitionsFor(topic)
                .stream()
                .map(t -> new TopicPartition(t.topic(), t.partition()))
                .collect(Collectors.toSet());
        consumer.assign(assignments);
        consumer.seekToBeginning(Collections.singleton(topicPartition));

//        consumer.seek(topicPartition, 0);


        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("topic = %s, partition = %s, offset = %d, customer = %s, record = %s\n",
                        record.topic(), record.partition(),
                        record.offset(), record.key(), record.value());
            }
            Thread.sleep(1000L);
        }
    }
}
