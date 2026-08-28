package com.mobimart.engine;

import com.mobimart.model.Inventory;
import com.mobimart.model.PhoneModel;
import com.mobimart.model.Store;

import java.util.*;

public class EolRiskEngine {

    private static final double MARKDOWN_15 = 0.15;
    private static final double MARKDOWN_20 = 0.20;
    private static final double MARKDOWN_30 = 0.30;

    private static final double TRANSFER_COST_PER_UNIT = 550.0;

    private static final int RISK_WINDOW_MONTHS = 3;

    public List<Map<String, Object>> analyzeEolRisk(
            List<Store> stores,
            List<PhoneModel> phones,
            List<Inventory> inventory) {

        List<Map<String, Object>> result =
                new ArrayList<>();

        int currentMonth =
                Calendar.getInstance()
                        .get(Calendar.MONTH) + 1;

        for (Inventory item : inventory) {

            if (item.getStore() == null
                    || item.getPhoneModel() == null) {
                continue;
            }

            PhoneModel phone =
                    item.getPhoneModel();

            Store store =
                    item.getStore();

            int quantity =
                    item.getQuantity();

            // No stock = no EOL exposure
            if (quantity <= 0) {
                continue;
            }

            Integer successorMonth =
                    phone.getSuccessorLaunchMonth();

            // No known successor
            if (successorMonth == null) {
                continue;
            }

            int monthsUntilSuccessor =
                    calculateMonthsUntilSuccessor(
                            currentMonth,
                            successorMonth
                    );

            // Only consider phones within EOL risk window
            if (monthsUntilSuccessor > RISK_WINDOW_MONTHS) {
                continue;
            }

            double phonePrice =
                    phone.getPrice();

            double inventoryValue =
                    quantity * phonePrice;

            double markdownRate =
                    determineMarkdownRate(
                            monthsUntilSuccessor
                    );

            double markdownLoss =
                    inventoryValue * markdownRate;

            double transferCost =
                    quantity * TRANSFER_COST_PER_UNIT;

            /*
             * Estimated financial exposure if stock
             * is held after the successor arrives.
             */
            double holdRisk =
                    inventoryValue * 0.20;

            String recommendedAction;

            double recommendedCost;

            /*
             * Select the option with the lowest
             * estimated financial exposure.
             */
            if (markdownLoss <= transferCost
                    && markdownLoss <= holdRisk) {

                recommendedAction = "MARKDOWN";
                recommendedCost = markdownLoss;

            } else if (transferCost <= holdRisk) {

                recommendedAction = "TRANSFER";
                recommendedCost = transferCost;

            } else {

                recommendedAction = "HOLD";
                recommendedCost = holdRisk;
            }

            Map<String, Object> recommendation =
                    new LinkedHashMap<>();

            recommendation.put(
                    "store",
                    store.getName()
            );

            recommendation.put(
                    "city",
                    store.getCity()
            );

            recommendation.put(
                    "phone",
                    phone.getName()
            );

            recommendation.put(
                    "brand",
                    phone.getBrand()
            );

            recommendation.put(
                    "category",
                    phone.getCategory()
            );

            recommendation.put(
                    "currentInventory",
                    quantity
            );

            recommendation.put(
                    "pricePerUnit",
                    phonePrice
            );

            recommendation.put(
                    "inventoryValue",
                    round(inventoryValue)
            );

            recommendation.put(
                    "successorLaunchMonth",
                    successorMonth
            );

            recommendation.put(
                    "monthsUntilSuccessor",
                    monthsUntilSuccessor
            );

            recommendation.put(
                    "riskLevel",
                    determineRiskLevel(
                            monthsUntilSuccessor
                    )
            );

            recommendation.put(
                    "markdownRate",
                    markdownRate
            );

            recommendation.put(
                    "markdownCost",
                    round(markdownLoss)
            );

            recommendation.put(
                    "transferCost",
                    round(transferCost)
            );

            recommendation.put(
                    "holdRisk",
                    round(holdRisk)
            );

            recommendation.put(
                    "recommendedAction",
                    recommendedAction
            );

            recommendation.put(
                    "recommendedCost",
                    round(recommendedCost)
            );

            recommendation.put(
                    "reason",
                    buildReason(
                            phone,
                            store,
                            quantity,
                            inventoryValue,
                            monthsUntilSuccessor,
                            markdownLoss,
                            transferCost,
                            holdRisk,
                            recommendedAction
                    )
            );

            result.add(recommendation);
        }

        /*
         * Sort:
         * HIGH risk first,
         * then MEDIUM,
         * then LOW.
         *
         * Within the same risk level,
         * higher inventory value comes first.
         */
        result.sort(
                new Comparator<Map<String, Object>>() {

                    @Override
                    public int compare(
                            Map<String, Object> a,
                            Map<String, Object> b) {

                        String riskA =
                                (String) a.get("riskLevel");

                        String riskB =
                                (String) b.get("riskLevel");

                        int riskComparison =
                                Integer.compare(
                                        riskRank(riskA),
                                        riskRank(riskB)
                                );

                        if (riskComparison != 0) {
                            return riskComparison;
                        }

                        double valueA =
                                ((Number)
                                        a.get("inventoryValue"))
                                        .doubleValue();

                        double valueB =
                                ((Number)
                                        b.get("inventoryValue"))
                                        .doubleValue();

                        return Double.compare(
                                valueB,
                                valueA
                        );
                    }
                }
        );

        return result;
    }

    /*
     * ---------------------------------------------------------
     * MONTH CALCULATION
     * ---------------------------------------------------------
     */

    private int calculateMonthsUntilSuccessor(
            int currentMonth,
            int successorMonth) {

        int difference =
                successorMonth - currentMonth;

        if (difference < 0) {
            difference += 12;
        }

        return difference;
    }

    /*
     * ---------------------------------------------------------
     * MARKDOWN RATE
     * ---------------------------------------------------------
     */

    private double determineMarkdownRate(
            int monthsUntilSuccessor) {

        if (monthsUntilSuccessor <= 1) {
            return MARKDOWN_30;
        }

        if (monthsUntilSuccessor == 2) {
            return MARKDOWN_20;
        }

        return MARKDOWN_15;
    }

    /*
     * ---------------------------------------------------------
     * RISK LEVEL
     * ---------------------------------------------------------
     */

    private String determineRiskLevel(
            int monthsUntilSuccessor) {

        if (monthsUntilSuccessor <= 1) {
            return "HIGH";
        }

        if (monthsUntilSuccessor == 2) {
            return "MEDIUM";
        }

        return "LOW";
    }

    /*
     * ---------------------------------------------------------
     * RISK SORTING
     * ---------------------------------------------------------
     */

    private int riskRank(
            String riskLevel) {

        if ("HIGH".equals(riskLevel)) {
            return 1;
        }

        if ("MEDIUM".equals(riskLevel)) {
            return 2;
        }

        return 3;
    }

    /*
     * ---------------------------------------------------------
     * REASON
     * ---------------------------------------------------------
     */

    private String buildReason(
            PhoneModel phone,
            Store store,
            int quantity,
            double inventoryValue,
            int monthsUntilSuccessor,
            double markdownLoss,
            double transferCost,
            double holdRisk,
            String action) {

        return
                "EOL risk: "
                        + store.getName()
                        + " holds "
                        + quantity
                        + " units of "
                        + phone.getName()
                        + " worth ₹"
                        + format(inventoryValue)
                        + ". Successor is expected in approximately "
                        + monthsUntilSuccessor
                        + " month(s). "
                        + "Markdown cost is ₹"
                        + format(markdownLoss)
                        + ", transfer cost is ₹"
                        + format(transferCost)
                        + ", and estimated hold risk is ₹"
                        + format(holdRisk)
                        + ". "
                        + action
                        + " has the lowest estimated financial exposure.";
    }

    /*
     * ---------------------------------------------------------
     * ROUND
     * ---------------------------------------------------------
     */

    private double round(double value) {

        return Math.round(
                value * 100.0
        ) / 100.0;
    }

    /*
     * ---------------------------------------------------------
     * FORMAT
     * ---------------------------------------------------------
     */

    private String format(double value) {

        return String.format(
                Locale.US,
                "%,.0f",
                value
        );
    }
}