package com.saga.orderservice.service;

import com.saga.common.dto.OrchestratorResponseDTO;
import com.saga.common.dto.OrderRequestDTO;
import com.saga.common.dto.OrderResponseDTO;
import com.saga.common.enums.OrderStatus;
import com.saga.orderservice.entity.Order;
import com.saga.orderservice.producer.KafkaProducer;
import com.saga.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {
    private static final Map<Integer, Double> PRODUCT_MOCK = Map.of(
            1, 20d,
            2, 30d,
            3, 50d
    );
    private final KafkaProducer kafkaProducer;

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(OrderRequestDTO orderRequestDTO) {
        Order order = orderRepository.save(dtoToEntity(orderRequestDTO));
        OrchestratorResponseDTO orchestratorResponseDTO = getOrchestratorResponseDTO(order);
        kafkaProducer.sendOrder(orchestratorResponseDTO);
        return order;
    }

    public List<OrderResponseDTO> getAll() {
        return orderRepository.findAll().stream().map(this::entityToDTO).toList();
    }

    @KafkaListener(
            topics = "${topic.name.order.updated}",
            groupId = "${spring.kafka.consumer.order-group-id}"
    )
    private void getOrderMessage(OrchestratorResponseDTO responseDTO) {
        Order order = orderRepository.findById(responseDTO.getOrderID()).orElseThrow();
        order.setStatus(responseDTO.getStatus());
        orderRepository.save(order);
    }

    private OrderResponseDTO entityToDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setUserID(order.getUserId());
        dto.setProductID(order.getProductId());
        dto.setAmount(order.getPrice());
        dto.setStatus(order.getStatus());
        dto.setOrderID(order.getId());
        return dto;
    }

    private Order dtoToEntity(OrderRequestDTO dto) {
        Order order = new Order();
        order.setId(dto.getOrderID());
        order.setUserId(dto.getUserID());
        order.setProductId(dto.getProductID());
        order.setPrice(PRODUCT_MOCK.get(dto.getProductID()));
        order.setStatus(OrderStatus.ORDER_CREATED);
        return order;
    }

    private OrchestratorResponseDTO getOrchestratorResponseDTO(Order order) {
        OrchestratorResponseDTO dto = new OrchestratorResponseDTO();
        dto.setOrderID(order.getId());
        dto.setUserID(order.getUserId());
        dto.setProductID(order.getProductId());
        dto.setAmount(PRODUCT_MOCK.get(order.getProductId()));
        dto.setStatus(order.getStatus());
        return dto;
    }
}
