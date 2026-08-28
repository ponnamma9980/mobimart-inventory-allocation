package com.mobimart.controller;

import com.mobimart.engine.BaselineEngine;
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
public class BaselineController {

    private final StoreRepository storeRepository;
    private final PhoneModelRepository phoneModelRepository;
    private final SalesHistoryRepository salesHistoryRepository;
    private final InventoryRepository inventoryRepository;

    public BaselineController(
            StoreRepository storeRepository,
            PhoneModelRepository phoneModelRepository,
            SalesHistoryRepository salesHistoryRepository,
            InventoryRepository inventoryRepository) {

        this.storeRepository = storeRepository;
        this.phoneModelRepository = phoneModelRepository;
        this.salesHistoryRepository = salesHistoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping("/api/baseline")
    public List<Map<String, Object>> getBaseline() {

        List<Store> stores =
                storeRepository.findAll();

        List<PhoneModel> phones =
                phoneModelRepository.findAll();

        List<SalesHistory> sales =
                salesHistoryRepository.findAll();

        List<Inventory> inventory =
                inventoryRepository.findAll();

        BaselineEngine engine =
                new BaselineEngine();

        return engine.calculateBaseline(
                stores,
                phones,
                sales,
                inventory
        );
    }
}