package com.djg.kerberos;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaProducerKerberos {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerKerberos.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.setProperty("java.security.krb5.conf", System.getProperty("krb5"));
        String principal  = System.getProperty("principal");
        String keytab = System.getProperty("keytab");
        String bootstrapServers = System.getProperty("bootstrapServers");
        String topic = System.getProperty("topic");
        String message = System.getProperty("message");

        Krb5LoginModule krb5LoginModule = new Krb5LoginModule.Builder()
                .serviceName("kafka")
                .contextName("KafkaClient")
                .principal(principal)
                .keytab(keytab).build();
        String appConf = krb5LoginModule.configToString(false);


        Properties producerConfig = new Properties();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // sasl
        producerConfig.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        // dynamic jass
        producerConfig.put(SaslConfigs.SASL_JAAS_CONFIG, appConf);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(producerConfig);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        Future<RecordMetadata>  future = producer.send(record);
        System.out.println(future.get());
    }
}
