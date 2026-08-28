package com.mobimart.engine;

import com.mobimart.model.Inventory;
import com.mobimart.model.PhoneModel;
import com.mobimart.model.SalesHistory;
import com.mobimart.model.Store;

import java.time.LocalDate;
import java.util.*;

public class EvaluationEngine {

    private static final double BUDGET = 40_000_000.0;

    public Map<String, Object> evaluate(
            List<Store> stores,
            List<PhoneModel> phones,
            List<SalesHistory> salesHistory,
            List<Inventory> inventory) {

        /*
         * Run the existing intelligent allocation engine.
         */
        AllocationEngine allocationEngine =
                new AllocationEngine();

        List<Map<String, Object>> smartAllocation =
                allocationEngine.calculateAllocation(
                        stores,
                        phones,
                        salesHistory,
                        inventory
                );

        /*
         * Build naive baseline:
         * allocate proportional to last month's sales.
         */
        List<Map<String, Object>> baselineAllocation =
                calculateNaiveBaseline(
                        stores,
                        phones,
                        salesHistory,
                        inventory
                );

        /*
         * Calculate metrics for both approaches.
         */
        Map<String, Object> smartMetrics =
                calculateMetrics(
                        smartAllocation,
                        salesHistory,
                        inventory
                );

        Map<String, Object> baselineMetrics =
                calculateMetrics(
                        baselineAllocation,
                        salesHistory,
                        inventory
                );

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "budget",
                BUDGET
        );

        result.put(
                "smartAllocation",
                smartAllocation
        );

        result.put(
                "baselineAllocation",
                baselineAllocation
        );

        result.put(
                "smartMetrics",
                smartMetrics
        );

        result.put(
                "baselineMetrics",
                baselineMetrics
        );

        result.put(
                "comparison",
                buildComparison(
                        smartMetrics,
                        baselineMetrics
                )
        );

        return result;
    }

    /*
     * =========================================================
     * NAIVE BASELINE
     * =========================================================
     *
     * The baseline uses last month's sales only.
     */
    private List<Map<String, Object>> calculateNaiveBaseline(
            List<Store> stores,
            List<PhoneModel> phones,
            List<SalesHistory> salesHistory,
            List<Inventory> inventory) {

        List<Map<String, Object>> candidates =
                new ArrayList<>();

        LocalDate latestDate =
                salesHistory.stream()
                        .map(SalesHistory::getSaleDate)
                        .max(LocalDate::compareTo)
                        .orElse(LocalDate.now());

        LocalDate monthStart =
                latestDate.minusDays(30);

        /*
         * First calculate total last-month sales value.
         */
        double totalSalesValue = 0;

        for (SalesHistory sale : salesHistory) {

            if (!sale.getSaleDate().isBefore(monthStart)
                    && !sale.getSaleDate().isAfter(latestDate)) {

                totalSalesValue +=
                        sale.getUnitsSold()
                                * sale.getPhoneModel().getPrice();
            }
        }

        if (totalSalesValue <= 0) {
            return candidates;
        }

        /*
         * Allocate budget proportional to last-month sales.
         */
        for (Store store : stores) {

            for (PhoneModel phone : phones) {

                double salesValue =
                        getLastMonthSalesValue(
                                store,
                                phone,
                                salesHistory,
                                monthStart,
                                latestDate
                        );

                if (salesValue <= 0) {
                    continue;
                }

                double proportionalBudget =
                        BUDGET
                                * (salesValue / totalSalesValue);

                int units =
                        (int) (
                                proportionalBudget
                                        / phone.getPrice()
                        );

                if (units <= 0) {
                    continue;
                }

                /*
                 * Don't allocate more than the
                 * estimated two-week requirement.
                 */
                double weeklySales =
                        getAverageWeeklySales(
                                store,
                                phone,
                                salesHistory
                        );

                int target =
                        (int) Math.ceil(
                                weeklySales * 2
                        );

                int current =
                        getCurrentInventory(
                                store,
                                phone,
                                inventory
                        );

                int need =
                        Math.max(
                                0,
                                target - current
                        );

                units =
                        Math.min(
                                units,
                                need
                        );

                if (units <= 0) {
                    continue;
                }

                double value =
                        units
                                * phone.getPrice();

                Map<String, Object> item =
                        new LinkedHashMap<>();

                item.put(
                        "store",
                        store.getName()
                );

                item.put(
                        "phone",
                        phone.getName()
                );

                item.put(
                        "units",
                        units
                );

                item.put(
                        "value",
                        round(value)
                );

                item.put(
                        "reason",
                        "Naive baseline: allocation is proportional "
                                + "to last month's sales."
                );

                candidates.add(item);
            }
        }

        return candidates;
    }

    /*
     * =========================================================
     * METRICS
     * =========================================================
     */

    private Map<String, Object> calculateMetrics(
            List<Map<String, Object>> allocation,
            List<SalesHistory> salesHistory,
            List<Inventory> inventory) {

        double allocationValue = 0;

        int allocatedUnits = 0;

        for (Map<String, Object> item : allocation) {

            allocationValue +=
                    ((Number)
                            item.get(
                                    item.containsKey("allocationValue")
                                            ? "allocationValue"
                                            : "value"
                            ))
                            .doubleValue();

            allocatedUnits +=
                    ((Number)
                            item.get(
                                    item.containsKey("recommendedUnits")
                                            ? "recommendedUnits"
                                            : "units"
                            ))
                            .intValue();
        }

        /*
         * Calculate recent demand.
         */
        LocalDate latestDate =
                salesHistory.stream()
                        .map(SalesHistory::getSaleDate)
                        .max(LocalDate::compareTo)
                        .orElse(LocalDate.now());

        LocalDate fourWeeksAgo =
                latestDate.minusWeeks(4);

        int totalDemand = 0;

        int stockoutUnits = 0;

        double currentCapital = 0;

        double deadStockValue = 0;

        for (Inventory item : inventory) {

            if (item.getPhoneModel() == null) {
                continue;
            }

            int current =
                    item.getQuantity();

            double price =
                    item.getPhoneModel().getPrice();

            currentCapital +=
                    current * price;

            int recentSales =
                    getSalesUnits(
                            item.getStore(),
                            item.getPhoneModel(),
                            salesHistory,
                            fourWeeksAgo,
                            latestDate
                    );

            totalDemand += recentSales;

            /*
             * Approximate stockout exposure:
             * recent demand greater than available stock.
             */
            if (recentSales > current) {

                stockoutUnits +=
                        recentSales - current;
            }

            /*
             * Dead stock:
             * inventory with no sales in the
             * recent four-week period.
             */
            if (recentSales == 0 && current > 0) {

                deadStockValue +=
                        current * price;
            }
        }

        double stockoutRate =
                totalDemand > 0
                        ? (
                        stockoutUnits
                                * 100.0
                                / totalDemand
                )
                        : 0;

        double deadStockPercentage =
                currentCapital > 0
                        ? (
                        deadStockValue
                                * 100.0
                                / currentCapital
                )
                        : 0;

        double weeklyDemand =
                totalDemand / 4.0;

        double weeksOfCover =
                weeklyDemand > 0
                        ? currentCapital
                        / estimateWeeklyInventoryValue(
                        salesHistory,
                        inventory
                )
                        : 0;

        double markdownLoss =
                calculateMarkdownExposure(
                        inventory
                );

        double capitalTurns =
                currentCapital > 0
                        ? estimateAnnualSalesValue(
                        salesHistory
                )
                        / currentCapital
                        : 0;

        Map<String, Object> metrics =
                new LinkedHashMap<>();

        metrics.put(
                "allocationValue",
                round(allocationValue)
        );

        metrics.put(
                "allocatedUnits",
                allocatedUnits
        );

        metrics.put(
                "stockoutRate",
                round(stockoutRate)
        );

        metrics.put(
                "weeksOfCover",
                round(weeksOfCover)
        );

        metrics.put(
                "deadStockPercentage",
                round(deadStockPercentage)
        );

        metrics.put(
                "markdownLoss",
                round(markdownLoss)
        );

        metrics.put(
                "capitalTurns",
                round(capitalTurns)
        );

        return metrics;
    }

    /*
     * =========================================================
     * COMPARISON
     * =========================================================
     */

    private Map<String, Object> buildComparison(
            Map<String, Object> smart,
            Map<String, Object> baseline) {

        Map<String, Object> comparison =
                new LinkedHashMap<>();

        double smartStockout =
                ((Number)
                        smart.get("stockoutRate"))
                        .doubleValue();

        double baselineStockout =
                ((Number)
                        baseline.get("stockoutRate"))
                        .doubleValue();

        double smartDead =
                ((Number)
                        smart.get("deadStockPercentage"))
                        .doubleValue();

        double baselineDead =
                ((Number)
                        baseline.get("deadStockPercentage"))
                        .doubleValue();

        double smartMarkdown =
                ((Number)
                        smart.get("markdownLoss"))
                        .doubleValue();

        double baselineMarkdown =
                ((Number)
                        baseline.get("markdownLoss"))
                        .doubleValue();

        double smartTurns =
                ((Number)
                        smart.get("capitalTurns"))
                        .doubleValue();

        double baselineTurns =
                ((Number)
                        baseline.get("capitalTurns"))
                        .doubleValue();

        comparison.put(
                "stockoutRateImprovement",
                round(
                        baselineStockout
                                - smartStockout
                )
        );

        comparison.put(
                "deadStockImprovement",
                round(
                        baselineDead
                                - smartDead
                )
        );

        comparison.put(
                "markdownSavings",
                round(
                        baselineMarkdown
                                - smartMarkdown
                )
        );

        comparison.put(
                "capitalTurnImprovement",
                round(
                        smartTurns
                                - baselineTurns
                )
        );

        comparison.put(
                "winner",
                determineWinner(
                        smart,
                        baseline
                )
        );

        return comparison;
    }

    private String determineWinner(
            Map<String, Object> smart,
            Map<String, Object> baseline) {

        double smartStockout =
                ((Number)
                        smart.get("stockoutRate"))
                        .doubleValue();

        double baselineStockout =
                ((Number)
                        baseline.get("stockoutRate"))
                        .doubleValue();

        double smartDead =
                ((Number)
                        smart.get("deadStockPercentage"))
                        .doubleValue();

        double baselineDead =
                ((Number)
                        baseline.get("deadStockPercentage"))
                        .doubleValue();

        double smartMarkdown =
                ((Number)
                        smart.get("markdownLoss"))
                        .doubleValue();

        double baselineMarkdown =
                ((Number)
                        baseline.get("markdownLoss"))
                        .doubleValue();

        int score = 0;

        if (smartStockout < baselineStockout) {
            score++;
        }

        if (smartDead < baselineDead) {
            score++;
        }

        if (smartMarkdown < baselineMarkdown) {
            score++;
        }

        if (score >= 2) {
            return "SMART_ENGINE";
        }

        return "BASELINE";
    }

    /*
     * =========================================================
     * HELPERS
     * =========================================================
     */

    private double getLastMonthSalesValue(
            Store store,
            PhoneModel phone,
            List<SalesHistory> salesHistory,
            LocalDate start,
            LocalDate end) {

        double value = 0;

        for (SalesHistory sale : salesHistory) {

            if (!Objects.equals(
                    sale.getStore().getId(),
                    store.getId())) {
                continue;
            }

            if (!Objects.equals(
                    sale.getPhoneModel().getId(),
                    phone.getId())) {
                continue;
            }

            if (!sale.getSaleDate().isBefore(start)
                    && !sale.getSaleDate().isAfter(end)) {

                value +=
                        sale.getUnitsSold()
                                * phone.getPrice();
            }
        }

        return value;
    }

    private double getAverageWeeklySales(
            Store store,
            PhoneModel phone,
            List<SalesHistory> salesHistory) {

        return salesHistory.stream()

                .filter(s ->
                        s.getStore() != null
                                && s.getPhoneModel() != null)

                .filter(s ->
                        Objects.equals(
                                s.getStore().getId(),
                                store.getId()
                        ))

                .filter(s ->
                        Objects.equals(
                                s.getPhoneModel().getId(),
                                phone.getId()
                        ))

                .mapToInt(
                        SalesHistory::getUnitsSold
                )

                .average()
                .orElse(0);
    }

    private int getCurrentInventory(
            Store store,
            PhoneModel phone,
            List<Inventory> inventory) {

        return inventory.stream()

                .filter(i ->
                        i.getStore() != null
                                && i.getPhoneModel() != null)

                .filter(i ->
                        Objects.equals(
                                i.getStore().getId(),
                                store.getId()
                        ))

                .filter(i ->
                        Objects.equals(
                                i.getPhoneModel().getId(),
                                phone.getId()
                        ))

                .mapToInt(
                        Inventory::getQuantity
                )
                .sum();
    }

    private int getSalesUnits(
            Store store,
            PhoneModel phone,
            List<SalesHistory> salesHistory,
            LocalDate start,
            LocalDate end) {

        return salesHistory.stream()

                .filter(s ->
                        s.getStore() != null
                                && s.getPhoneModel() != null)

                .filter(s ->
                        Objects.equals(
                                s.getStore().getId(),
                                store.getId()
                        ))

                .filter(s ->
                        Objects.equals(
                                s.getPhoneModel().getId(),
                                phone.getId()
                        ))

                .filter(s ->
                        !s.getSaleDate().isBefore(start)
                                && !s.getSaleDate().isAfter(end)
                )

                .mapToInt(
                        SalesHistory::getUnitsSold
                )

                .sum();
    }

    private double estimateWeeklyInventoryValue(
            List<SalesHistory> salesHistory,
            List<Inventory> inventory) {

        double weeklyValue =
                salesHistory.stream()
                        .mapToDouble(
                                s ->
                                        s.getRevenue()
                        )
                        .sum()
                        / 52.0;

        return Math.max(
                weeklyValue,
                1
        );
    }

    private double estimateAnnualSalesValue(
            List<SalesHistory> salesHistory) {

        return salesHistory.stream()
                .mapToDouble(
                        SalesHistory::getRevenue
                )
                .sum();
    }

    private double calculateMarkdownExposure(
            List<Inventory> inventory) {

        double exposure = 0;

        for (Inventory item : inventory) {

            PhoneModel phone =
                    item.getPhoneModel();

            if (phone == null
                    || item.getQuantity() <= 0) {
                continue;
            }

            Integer successor =
                    phone.getSuccessorLaunchMonth();

            if (successor != null) {

                double value =
                        item.getQuantity()
                                * phone.getPrice();

                exposure +=
                        value * 0.20;
            }
        }

        return exposure;
    }

    private double round(double value) {

        return Math.round(
                value * 100.0
        ) / 100.0;
    }
}