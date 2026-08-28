package com.mobimart.engine;

import com.mobimart.model.Inventory;
import com.mobimart.model.PhoneModel;
import com.mobimart.model.SalesHistory;
import com.mobimart.model.Store;

import java.time.LocalDate;
import java.util.*;

public class BaselineEngine {

    private static final double BUDGET = 40_000_000.0;

    public List<Map<String, Object>> calculateBaseline(
            List<Store> stores,
            List<PhoneModel> phones,
            List<SalesHistory> salesHistory,
            List<Inventory> inventory) {

        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusDays(30);

        List<Map<String, Object>> candidates =
                new ArrayList<>();

        /*
         * NAIVE BASELINE:
         *
         * Allocate proportionally to each
         * store-phone combination's sales
         * during the previous 30 days.
         */

        double totalSales = 0;

        Map<String, Integer> salesMap =
                new HashMap<>();

        for (SalesHistory sale : salesHistory) {

            if (sale.getStore() == null
                    || sale.getPhoneModel() == null
                    || sale.getSaleDate() == null) {
                continue;
            }

            if (sale.getSaleDate().isBefore(oneMonthAgo)
                    || sale.getSaleDate().isAfter(today)) {
                continue;
            }

            String key =
                    sale.getStore().getId()
                            + "-"
                            + sale.getPhoneModel().getId();

            int units =
                    salesMap.getOrDefault(key, 0)
                            + sale.getUnitsSold();

            salesMap.put(key, units);

            totalSales += sale.getUnitsSold();
        }

        if (totalSales <= 0) {
            return List.of();
        }

        /*
         * Allocate the ₹4 crore budget
         * proportional to last-month sales.
         */

        for (Store store : stores) {

            for (PhoneModel phone : phones) {

                String key =
                        store.getId()
                                + "-"
                                + phone.getId();

                int lastMonthSales =
                        salesMap.getOrDefault(
                                key,
                                0
                        );

                if (lastMonthSales <= 0) {
                    continue;
                }

                double allocationShare =
                        lastMonthSales / totalSales;

                double allocationValue =
                        BUDGET * allocationShare;

                int recommendedUnits =
                        (int) (
                                allocationValue
                                        / phone.getPrice()
                        );

                if (recommendedUnits <= 0) {
                    continue;
                }

                allocationValue =
                        recommendedUnits
                                * phone.getPrice();

                int currentInventory =
                        getCurrentInventory(
                                store,
                                phone,
                                inventory
                        );

                Map<String, Object> result =
                        new LinkedHashMap<>();

                result.put(
                        "store",
                        store.getName()
                );

                result.put(
                        "city",
                        store.getCity()
                );

                result.put(
                        "phone",
                        phone.getName()
                );

                result.put(
                        "category",
                        phone.getCategory()
                );

                result.put(
                        "lastMonthSales",
                        lastMonthSales
                );

                result.put(
                        "currentInventory",
                        currentInventory
                );

                result.put(
                        "recommendedUnits",
                        recommendedUnits
                );

                result.put(
                        "pricePerUnit",
                        phone.getPrice()
                );

                result.put(
                        "allocationValue",
                        round(allocationValue)
                );

                result.put(
                        "salesSharePercent",
                        round(
                                allocationShare * 100
                        )
                );

                result.put(
                        "reason",
                        "Naive baseline: "
                                + "allocation is based only on "
                                + lastMonthSales
                                + " units sold during the previous "
                                + "30 days. "
                                + "The ₹4 crore budget is distributed "
                                + "proportionally to historical sales."
                );

                candidates.add(result);
            }
        }

        /*
         * Highest sales share first.
         */

        candidates.sort(
                (a, b) ->
                        Double.compare(
                                ((Number)
                                        b.get("salesSharePercent"))
                                        .doubleValue(),

                                ((Number)
                                        a.get("salesSharePercent"))
                                        .doubleValue()
                        )
        );

        /*
         * Make absolutely sure the final
         * allocation does not exceed ₹4 crore.
         */

        List<Map<String, Object>> finalResult =
                new ArrayList<>();

        double remainingBudget = BUDGET;

        for (Map<String, Object> item : candidates) {

            double value =
                    ((Number)
                            item.get("allocationValue"))
                            .doubleValue();

            if (value > remainingBudget) {

                double price =
                        ((Number)
                                item.get("pricePerUnit"))
                                .doubleValue();

                int units =
                        (int)
                                (remainingBudget / price);

                if (units <= 0) {
                    continue;
                }

                value = units * price;

                item.put(
                        "recommendedUnits",
                        units
                );

                item.put(
                        "allocationValue",
                        round(value)
                );
            }

            finalResult.add(item);

            remainingBudget -= value;

            if (remainingBudget <= 0) {
                break;
            }
        }

        return finalResult;
    }

    private int getCurrentInventory(
            Store store,
            PhoneModel phone,
            List<Inventory> inventory) {

        return inventory.stream()
                .filter(i -> i.getStore() != null)
                .filter(i -> i.getPhoneModel() != null)
                .filter(i ->
                        Objects.equals(
                                i.getStore().getId(),
                                store.getId()
                        )
                )
                .filter(i ->
                        Objects.equals(
                                i.getPhoneModel().getId(),
                                phone.getId()
                        )
                )
                .mapToInt(
                        Inventory::getQuantity
                )
                .sum();
    }

    private double round(double value) {

        return Math.round(
                value * 100.0
        ) / 100.0;
    }
}