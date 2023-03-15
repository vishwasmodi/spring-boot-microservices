package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    @SneakyThrows
    @Transactional()
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        log.info("Wait started");
//        Thread.sleep(10000);
        log.info("Wait ended");
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                                  .map(inventory -> InventoryResponse.builder().skuCode(inventory.getSkuCode())
                                                                     .isInStock(inventory.getQuantity() > 0).build())
                                  .toList();
    }
}