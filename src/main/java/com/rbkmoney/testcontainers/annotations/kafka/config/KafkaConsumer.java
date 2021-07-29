package com.rbkmoney.testcontainers.annotations.kafka.config;

import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.thrift.TBase;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class KafkaConsumer<T extends TBase<?, ?>> {

    private final String bootstrapAddress;
    private final AbstractThriftDeserializer<T> deserializer;

    public void read(String topic, MessageListener<String, T> messageListener) {
        ConcurrentMessageListenerContainer<String, T> container = new ConcurrentMessageListenerContainer<>(
                consumerFactory(),
                containerProperties(topic, messageListener));
        container.start();
    }

    private ContainerProperties containerProperties(String topic, MessageListener<String, T> messageListener) {
        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setMessageListener(messageListener);
        return containerProperties;
    }

    private DefaultKafkaConsumerFactory<String, T> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), deserializer);
    }

    private Map<String, Object> consumerConfig() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }
}