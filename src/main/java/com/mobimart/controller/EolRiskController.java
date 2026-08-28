package com.mobimart.controller;

import com.mobimart.engine.EolRiskEngine;
import com.mobimart.model.Inventory;
import com.mobimart.model.PhoneModel;
import com.mobimart.model.Store;
import com.mobimart.repository.InventoryRepository;
import com.mobimart.repository.PhoneModelRepository;
import com.mobimart.repository.StoreRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class EolRiskController {

    private final StoreRepository storeRepository;
    private final PhoneModelRepository phoneModelRepository;
    private final InventoryRepository inventoryRepository;

    public EolRiskController(
            StoreRepository storeRepository,
            PhoneModelRepository phoneModelRepository,
            InventoryRepository inventoryRepository) {

        this.storeRepository = storeRepository;
        this.phoneModelRepository = phoneModelRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping("/api/eol-risk")
    public List<Map<String, Object>> getEolRisk() {

        List<Store> stores =
                storeRepository.findAll();

        List<PhoneModel> phones =
                phoneModelRepository.findAll();

        List<Inventory> inventory =
                inventoryRepository.findAll();

        EolRiskEngine engine =
                new EolRiskEngine();

        return engine.analyzeEolRisk(
                stores,
                phones,
                inventory
        );
    }
}