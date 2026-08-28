package com.mobimart.engine;

import com.mobimart.model.Inventory;
import com.mobimart.model.SalesHistory;

import java.time.LocalDate;
import java.util.*;

public class PerformanceEngine {

    public Map<String, Object> calculatePerformance(
            List<SalesHistory> salesHistory,
            List<Inventory> inventory) {

        LocalDate today = LocalDate.now();
        LocalDate fourWeeksAgo = today.minusDays(28);

        int totalUnitsSold = 0;
        double totalRevenue = 0;

        for (SalesHistory sale : salesHistory) {

            if (sale.getSaleDate() == null) {
                continue;
            }

            if (!sale.getSaleDate().isBefore(fourWeeksAgo)
                    && !sale.getSaleDate().isAfter(today)) {

                totalUnitsSold += sale.getUnitsSold();
                totalRevenue += sale.getRevenue();
            }
        }

        int totalInventoryUnits = 0;
        double inventoryValue = 0;

        for (Inventory item : inventory) {

            if (item.getPhoneModel() == null) {
                continue;
            }

            totalInventoryUnits += item.getQuantity();

            inventoryValue +=
                    item.getQuantity()
                            * item.getPhoneModel().getPrice();
        }

        double averageWeeklySales =
                totalUnitsSold / 4.0;

        double weeksOfCover =
                averageWeeklySales > 0
                        ? totalInventoryUnits / averageWeeklySales
                        : 0;

        double capitalTurns =
                inventoryValue > 0
                        ? (totalRevenue * 13) / inventoryValue
                        : 0;

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "period",
                "Last 4 Weeks"
        );

        result.put(
                "totalUnitsSold",
                totalUnitsSold
        );

        result.put(
                "totalRevenue",
                round(totalRevenue)
        );

        result.put(
                "averageWeeklySales",
                round(averageWeeklySales)
        );

        result.put(
                "currentInventoryUnits",
                totalInventoryUnits
        );

        result.put(
                "currentInventoryValue",
                round(inventoryValue)
        );

        result.put(
                "weeksOfCover",
                round(weeksOfCover)
        );

        result.put(
                "capitalTurnsAnnualized",
                round(capitalTurns)
        );

        return result;
    }

    private double round(double value) {

        return Math.round(value * 100.0) / 100.0;
    }
}