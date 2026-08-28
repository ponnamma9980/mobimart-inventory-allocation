package com.mobimart.controller;

import com.mobimart.engine.PerformanceEngine;
import com.mobimart.model.Inventory;
import com.mobimart.model.SalesHistory;
import com.mobimart.repository.InventoryRepository;
import com.mobimart.repository.SalesHistoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PerformanceController {

    private final SalesHistoryRepository salesHistoryRepository;
    private final InventoryRepository inventoryRepository;

    public PerformanceController(
            SalesHistoryRepository salesHistoryRepository,
            InventoryRepository inventoryRepository) {

        this.salesHistoryRepository =
                salesHistoryRepository;

        this.inventoryRepository =
                inventoryRepository;
    }

    @GetMapping("/api/performance")
    public Map<String, Object> getPerformance() {

        List<SalesHistory> sales =
                salesHistoryRepository.findAll();

        List<Inventory> inventory =
                inventoryRepository.findAll();

        PerformanceEngine engine =
                new PerformanceEngine();

        return engine.calculatePerformance(
                sales,
                inventory
        );
    }
}