package com.example.notificationservice;

import com.example.notificationservice.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);

    }

    @KafkaListener(topics = "notificationTopic", groupId = "notificationGroup")
    public void handleNotification(@Payload OrderPlacedEvent orderPlacedEvent){
        System.out.println(orderPlacedEvent);
        // Send out an email notification
       // log.info("Received Notification for Order - {}", orderPlacedEvent.getOrderNumber());
    }
}