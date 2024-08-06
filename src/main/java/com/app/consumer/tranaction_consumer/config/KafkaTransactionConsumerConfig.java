package com.app.consumer.tranaction_consumer.config;

import java.util.HashMap;
import java.util.Map;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.app.consumer.tranaction_consumer.entity.ConsumerTransaction;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;


@Configuration
@EnableKafka
public class KafkaTransactionConsumerConfig {

    @Bean
    @Qualifier("producer")
    public KafkaTemplate<String,ConsumerTransaction> kafkaTemplate(){
            return new KafkaTemplate<>(kafkaProducerFactory());
    }

    @Bean
    public ProducerFactory<String,ConsumerTransaction> kafkaProducerFactory() {
        Map<String,Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "locallhost:9092");
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public ConsumerFactory<String, ConsumerTransaction> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "trans-group");
    
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class.getName());
    
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); 
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ConsumerTransaction.class.getName());
        config.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000");
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "3000");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerTransaction> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConsumerTransaction> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


}
