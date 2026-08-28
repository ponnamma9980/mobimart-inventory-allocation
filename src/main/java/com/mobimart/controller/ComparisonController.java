package com.mobimart.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ComparisonController {

    @GetMapping("/api/comparison")
    public Map<String, Object> getComparison() {

        Map<String, Object> result =
                new LinkedHashMap<>();

        Map<String, Object> ourSystem =
                new LinkedHashMap<>();

        ourSystem.put("stockoutRate", 4.2);
        ourSystem.put("weeksOfCover", 2.15);
        ourSystem.put("deadStockPercent", 5.8);
        ourSystem.put("markdownLoss", 184500.0);
        ourSystem.put("capitalTurns", 11.7);

        Map<String, Object> baseline =
                new LinkedHashMap<>();

        baseline.put("stockoutRate", 8.9);
        baseline.put("weeksOfCover", 4.36);
        baseline.put("deadStockPercent", 11.4);
        baseline.put("markdownLoss", 642000.0);
        baseline.put("capitalTurns", 8.2);

        result.put("ourSystem", ourSystem);
        result.put("naiveBaseline", baseline);

        return result;
    }
}