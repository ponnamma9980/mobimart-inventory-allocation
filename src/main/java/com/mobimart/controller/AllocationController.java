package com.mobimart.controller;

import com.mobimart.engine.AllocationEngine;
import com.mobimart.model.Inventory;
import com.mobimart.model.PhoneModel;
import com.mobimart.model.SalesHistory;
import com.mobimart.model.Store;
import com.mobimart.repository.InventoryRepository;
import com.mobimart.repository.PhoneModelRepository;
import com.mobimart.repository.SalesHistoryRepository;
import com.mobimart.repository.StoreRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AllocationController {

    private final StoreRepository storeRepository;
    private final PhoneModelRepository phoneModelRepository;
    private final SalesHistoryRepository salesHistoryRepository;
    private final InventoryRepository inventoryRepository;

    public AllocationController(
            StoreRepository storeRepository,
            PhoneModelRepository phoneModelRepository,
            SalesHistoryRepository salesHistoryRepository,
            InventoryRepository inventoryRepository) {

        this.storeRepository = storeRepository;
        this.phoneModelRepository = phoneModelRepository;
        this.salesHistoryRepository = salesHistoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping("/api/allocation")
    public List<Map<String, Object>> getAllocation() {

        List<Store> stores = storeRepository.findAll();
        List<PhoneModel> phones = phoneModelRepository.findAll();
        List<SalesHistory> sales = salesHistoryRepository.findAll();
        List<Inventory> inventory = inventoryRepository.findAll();

        if (stores.isEmpty()) {
            return List.of();
        }

        AllocationEngine engine = new AllocationEngine();

        return engine.calculateAllocation(
                stores,
                phones,
                sales,
                inventory
        );
    }
}