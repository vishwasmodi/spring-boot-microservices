package com.example.orderservice.config;

import jakarta.websocket.OnClose;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<Object, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic newTopic() {
        return new NewTopic("notificationTopic", 2, (short) 1);
    }

    //    @Bean
    //    public ConsumerFactory<Object, Object> consumerFactory(){
    //        Map<String, Object> config = new HashMap<>();
    //        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
    //        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    //        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    //        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
    //        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    //        return new DefaultKafkaConsumerFactory<>(config);
    //
    //    }
    //
    //    @Bean
    //    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>>
    //    kafkaListenerContainerFactory(){
    //        ConcurrentKafkaListenerContainerFactory<Object, Object> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
    //        containerFactory.setConsumerFactory(consumerFactory());
    //        return containerFactory;
    //    }

    @Bean
    public ConsumerFactory<String, Object> jsonConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        final JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(), new StringDeserializer(),
                jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> jsonKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(jsonConsumerFactory());
        return containerFactory;
    }
}
