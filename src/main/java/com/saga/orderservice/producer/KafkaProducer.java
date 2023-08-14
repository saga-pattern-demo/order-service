package com.saga.orderservice.producer;

import com.saga.common.dto.OrchestratorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, OrchestratorResponseDTO> kafkaTemplate;
    private final String orderCreatedTopic;

    public KafkaProducer(KafkaTemplate<String, OrchestratorResponseDTO> kafkaTemplate,
                         @Value("${topic.name.order.created}") String orderCreatedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderCreatedTopic = orderCreatedTopic;
    }

    public void sendOrder(OrchestratorResponseDTO payload) {
        log.info("sending to order topic={}, payload={}", orderCreatedTopic, payload);
        kafkaTemplate.send(orderCreatedTopic, payload);
    }
}
