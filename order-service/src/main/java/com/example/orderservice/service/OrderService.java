package com.example.orderservice.service;

import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.event.OrderPlacedEvent;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private static final String TOPIC = "notificationTopic";

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream()
                                                          .map(orderLineItemsDto -> mapToDto(orderLineItemsDto))
                                                          .toList();
        order.setOrderLineItemsList(orderLineItems);


        List<String> skuCodes = order.getOrderLineItemsList().stream()
                                     .map(orderLineItems1 -> orderLineItems1.getSkuCode()).toList();
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                                                                     .uri("http://inventory-service/api" + "/inventory",
                                                                             uriBuilder -> uriBuilder
                                                                                     .queryParam("skuCode", skuCodes)
                                                                                     .build()).retrieve()
                                                                     .bodyToMono(InventoryResponse[].class).block();

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                                           .allMatch(inventoryResponse -> inventoryResponse.isInStock());

        if (allProductsInStock) {
            orderRepository.save(order);
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderNumber(order.getOrderNumber());
            kafkaTemplate.send(TOPIC, orderPlacedEvent);
            return "Order placed successfully";
        } else {
            return "Product is not available";
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;
    }
}
