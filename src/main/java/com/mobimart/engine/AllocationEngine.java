package com.mobimart.engine;

import com.mobimart.model.Inventory;
import com.mobimart.model.PhoneModel;
import com.mobimart.model.SalesHistory;
import com.mobimart.model.Store;

import java.util.*;

public class AllocationEngine {

    private static final double INVENTORY_BUDGET = 40_000_000;

    // Maximum amount of chain budget one store can consume
    private static final double STORE_BASELINE_CAP = 0.08;

    public List<Map<String, Object>> calculateAllocation(
            List<Store> stores,
            List<PhoneModel> phones,
            List<SalesHistory> salesHistory,
            List<Inventory> inventory) {

        List<AllocationCandidate> candidates =
                new ArrayList<>();

        /*
         * ---------------------------------------------------------
         * STEP 1
         * BUILD STORE + PHONE OPPORTUNITIES
         * ---------------------------------------------------------
         */

        for (Store store : stores) {

            for (PhoneModel phone : phones) {

                double weeklySales =
                        getAverageWeeklySales(
                                store,
                                phone,
                                salesHistory
                        );

                if (weeklySales <= 0) {
                    continue;
                }

                /*
                 * Target = approximately 2 weeks of demand.
                 */
                int targetUnits =
                        (int) Math.ceil(
                                weeklySales * 2
                        );

                /*
                 * Current inventory.
                 */
                int currentInventory =
                        getCurrentInventory(
                                store,
                                phone,
                                inventory
                        );

                /*
                 * Only replenish if current stock is
                 * below target stock.
                 */
                int replenishmentNeed =
                        Math.max(
                                0,
                                targetUnits - currentInventory
                        );

                if (replenishmentNeed <= 0) {
                    continue;
                }

                double storeScore =
                        calculateStoreScore(store);

                double stockoutScore =
                        calculateStockoutScore(phone);

                double lifecycleScore =
                        calculateLifecycleScore(phone);

                /*
                 * Priority is based on:
                 *
                 * demand
                 * store attractiveness
                 * stockout impact
                 * lifecycle risk
                 *
                 * Replenishment need is also considered so
                 * we don't prioritize products that already
                 * have enough stock.
                 */
                double priorityScore =
                        weeklySales
                                * storeScore
                                * stockoutScore
                                * lifecycleScore
                                * replenishmentNeed;

                candidates.add(
                        new AllocationCandidate(
                                store,
                                phone,
                                weeklySales,
                                targetUnits,
                                currentInventory,
                                replenishmentNeed,
                                priorityScore
                        )
                );
            }
        }

        /*
         * ---------------------------------------------------------
         * STEP 2
         * GLOBAL PRIORITY SORT
         * ---------------------------------------------------------
         */

        candidates.sort(
                Comparator
                        .comparingDouble(
                                AllocationCandidate::getPriorityScore
                        )
                        .reversed()
        );

        /*
         * ---------------------------------------------------------
         * STEP 3
         * CHAIN-WIDE BUDGET
         * ---------------------------------------------------------
         */

        double remainingBudget =
                INVENTORY_BUDGET;

        Map<Long, Double> storeAllocation =
                new HashMap<>();

        Map<String, Map<String, Object>> allocations =
                new LinkedHashMap<>();

        /*
         * ---------------------------------------------------------
         * STEP 4
         * BASELINE ALLOCATION
         * ---------------------------------------------------------
         *
         * Give every important store/product opportunity
         * a reasonable chance before concentrating the
         * remaining money on the highest priorities.
         * ---------------------------------------------------------
         */

        for (AllocationCandidate candidate : candidates) {

            if (remainingBudget <= 0) {
                break;
            }

            Store store =
                    candidate.getStore();

            PhoneModel phone =
                    candidate.getPhone();

            Long storeId =
                    store.getId();

            double currentStoreAllocation =
                    storeAllocation.getOrDefault(
                            storeId,
                            0.0
                    );

            double storeCap =
                    INVENTORY_BUDGET
                            * STORE_BASELINE_CAP;

            double availableForStore =
                    storeCap
                            - currentStoreAllocation;

            if (availableForStore <= 0) {
                continue;
            }

            /*
             * Conservative baseline:
             * approximately 75% of weekly sales.
             */
            int baselineUnits =
                    Math.min(
                            candidate.getReplenishmentNeed(),
                            Math.max(
                                    1,
                                    (int) Math.ceil(
                                            candidate.getWeeklySales()
                                                    * 0.75
                                    )
                            )
                    );

            double value =
                    baselineUnits
                            * phone.getPrice();

            value =
                    Math.min(
                            value,
                            availableForStore
                    );

            value =
                    Math.min(
                            value,
                            remainingBudget
                    );

            int units =
                    (int) (
                            value
                                    / phone.getPrice()
                    );

            if (units <= 0) {
                continue;
            }

            value =
                    units
                            * phone.getPrice();

            addAllocation(
                    allocations,
                    candidate,
                    units,
                    value,
                    buildBaselineReason(
                            candidate,
                            units,
                            value
                    )
            );

            remainingBudget -= value;

            storeAllocation.put(
                    storeId,
                    currentStoreAllocation
                            + value
            );
        }

        /*
         * ---------------------------------------------------------
         * STEP 5
         * PRIORITY ALLOCATION
         * ---------------------------------------------------------
         */

        for (AllocationCandidate candidate : candidates) {

            if (remainingBudget <= 0) {
                break;
            }

            PhoneModel phone =
                    candidate.getPhone();

            String key =
                    candidate.getStore().getId()
                            + "-"
                            + phone.getId();

            int alreadyAllocated =
                    allocations.containsKey(key)
                            ? ((Number)
                            allocations
                                    .get(key)
                                    .get("recommendedUnits"))
                            .intValue()
                            : 0;

            int remainingNeed =
                    candidate.getReplenishmentNeed()
                            - alreadyAllocated;

            if (remainingNeed <= 0) {
                continue;
            }

            /*
             * Maximum 5% of chain budget for one
             * priority opportunity.
             */
            double opportunityCap =
                    INVENTORY_BUDGET
                            * 0.05;

            double value =
                    remainingNeed
                            * phone.getPrice();

            value =
                    Math.min(
                            value,
                            opportunityCap
                    );

            value =
                    Math.min(
                            value,
                            remainingBudget
                    );

            int units =
                    (int) (
                            value
                                    / phone.getPrice()
                    );

            if (units <= 0) {
                continue;
            }

            value =
                    units
                            * phone.getPrice();

            addAllocation(
                    allocations,
                    candidate,
                    units,
                    value,
                    buildPriorityReason(
                            candidate,
                            value
                    )
            );

            remainingBudget -= value;
        }

        /*
         * ---------------------------------------------------------
         * STEP 6
         * FINAL RESPONSE
         * ---------------------------------------------------------
         */

        List<Map<String, Object>> result =
                new ArrayList<>(
                        allocations.values()
                );

        result.sort(
                (a, b) ->
                        Double.compare(
                                ((Number)
                                        b.get("priorityScore"))
                                        .doubleValue(),

                                ((Number)
                                        a.get("priorityScore"))
                                        .doubleValue()
                        )
        );

        return result;
    }

    /*
     * ---------------------------------------------------------
     * CURRENT INVENTORY
     * ---------------------------------------------------------
     */

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

    /*
     * ---------------------------------------------------------
     * SALES
     * ---------------------------------------------------------
     */

    private double getAverageWeeklySales(
            Store store,
            PhoneModel phone,
            List<SalesHistory> salesHistory) {

        return salesHistory.stream()

                .filter(s -> s.getStore() != null)

                .filter(s -> s.getPhoneModel() != null)

                .filter(s ->
                        Objects.equals(
                                s.getStore().getId(),
                                store.getId()
                        )
                )

                .filter(s ->
                        Objects.equals(
                                s.getPhoneModel().getId(),
                                phone.getId()
                        )
                )

                .mapToInt(
                        SalesHistory::getUnitsSold
                )

                .average()

                .orElse(0);
    }

    /*
     * ---------------------------------------------------------
     * STORE PROFILE
     * ---------------------------------------------------------
     */

    private double calculateStoreScore(
            Store store) {

        /*
         * Normalize store size.
         *
         * Typical store sizes are around 700-1300 sq ft.
         * This prevents store size from dominating the
         * other factors.
         */
        double normalizedSize =
                Math.min(
                        store.getStoreSize()
                                / 1200.0,
                        1.20
                );

        double score =
                0.40
                        * store.getIncomeIndex()

                        + 0.35
                        * store.getFootfallIndex()

                        + 0.25
                        * normalizedSize;

        String location =
                store.getLocationType() == null
                        ? ""
                        : store.getLocationType()
                        .toLowerCase();

        if (location.contains("premium")
                || location.contains("mall")
                || location.contains("high")) {

            score *= 1.10;
        }

        return Math.max(
                score,
                0.10
        );
    }

    /*
     * ---------------------------------------------------------
     * STOCKOUT IMPACT
     * ---------------------------------------------------------
     */

    private double calculateStockoutScore(
            PhoneModel phone) {

        String category =
                phone.getCategory() == null
                        ? ""
                        : phone.getCategory()
                        .toLowerCase();

        if (category.contains("budget")) {
            return 1.30;
        }

        if (category.contains("mid")) {
            return 1.25;
        }

        if (category.contains("premium")) {
            return 1.10;
        }

        if (category.contains("flagship")) {
            return 1.00;
        }

        return 1.10;
    }

    /*
     * ---------------------------------------------------------
     * PRODUCT LIFECYCLE
     * ---------------------------------------------------------
     */

    private double calculateLifecycleScore(
            PhoneModel phone) {

        Integer successor =
                phone.getSuccessorLaunchMonth();

        if (successor == null) {
            return 1.00;
        }

        /*
         * Successor exists, so reduce fresh-stock priority.
         */
        return 0.90;
    }

    /*
     * ---------------------------------------------------------
     * ADD / MERGE
     * ---------------------------------------------------------
     */

    private void addAllocation(
            Map<String, Map<String, Object>> allocations,
            AllocationCandidate candidate,
            int units,
            double value,
            String reason) {

        String key =
                candidate.getStore().getId()
                        + "-"
                        + candidate.getPhone().getId();

        Map<String, Object> recommendation =
                allocations.get(key);

        if (recommendation == null) {

            recommendation =
                    new LinkedHashMap<>();

            recommendation.put(
                    "store",
                    candidate.getStore().getName()
            );

            recommendation.put(
                    "city",
                    candidate.getStore().getCity()
            );

            recommendation.put(
                    "locationType",
                    candidate.getStore()
                            .getLocationType()
            );

            recommendation.put(
                    "phone",
                    candidate.getPhone().getName()
            );

            recommendation.put(
                    "category",
                    candidate.getPhone().getCategory()
            );

            recommendation.put(
                    "averageWeeklySales",
                    round(
                            candidate.getWeeklySales()
                    )
            );

            recommendation.put(
                    "currentInventory",
                    candidate.getCurrentInventory()
            );

            recommendation.put(
                    "targetInventory",
                    candidate.getTargetUnits()
            );

            recommendation.put(
                    "replenishmentNeed",
                    candidate.getReplenishmentNeed()
            );

            recommendation.put(
                    "recommendedUnits",
                    0
            );

            recommendation.put(
                    "pricePerUnit",
                    candidate.getPhone()
                            .getPrice()
            );

            recommendation.put(
                    "allocationValue",
                    0.0
            );

            recommendation.put(
                    "priorityScore",
                    round(
                            candidate.getPriorityScore()
                    )
            );

            recommendation.put(
                    "reason",
                    reason
            );

            allocations.put(
                    key,
                    recommendation
            );
        }

        int existingUnits =
                ((Number)
                        recommendation
                                .get("recommendedUnits"))
                        .intValue();

        double existingValue =
                ((Number)
                        recommendation
                                .get("allocationValue"))
                        .doubleValue();

        recommendation.put(
                "recommendedUnits",
                existingUnits + units
        );

        recommendation.put(
                "allocationValue",
                existingValue + value
        );
    }

    /*
     * ---------------------------------------------------------
     * BASELINE REASON
     * ---------------------------------------------------------
     */

    private String buildBaselineReason(
            AllocationCandidate candidate,
            int units,
            double value) {

        double expectedTwoWeekSales =
                candidate.getWeeklySales()
                        * 2
                        * candidate.getPhone()
                        .getPrice();

        return
                "Replenishment: current stock is "
                        + candidate.getCurrentInventory()
                        + " units versus a "
                        + candidate.getTargetUnits()
                        + "-unit two-week target. "
                        + units
                        + " units worth ₹"
                        + format(value)
                        + " are recommended to reduce stockout risk. "
                        + "Expected two-week sales value is approximately ₹"
                        + format(expectedTwoWeekSales)
                        + ".";
    }

    /*
     * ---------------------------------------------------------
     * PRIORITY REASON
     * ---------------------------------------------------------
     */

    private String buildPriorityReason(
            AllocationCandidate candidate,
            double value) {

        double expectedSalesValue =
                candidate.getWeeklySales()
                        * 2
                        * candidate.getPhone()
                        .getPrice();

        if (candidate.getWeeklySales() >= 10) {

            return
                    "High-priority replenishment: "
                            + "current stock of "
                            + candidate.getCurrentInventory()
                            + " is below the "
                            + candidate.getTargetUnits()
                            + "-unit target. Strong demand and "
                            + "stockout impact support approximately ₹"
                            + format(expectedSalesValue)
                            + " of two-week sales value; ₹"
                            + format(value)
                            + " is committed within the chain-wide cap.";
        }

        if (candidate.getWeeklySales() >= 5) {

            return
                    "Priority replenishment: current inventory of "
                            + candidate.getCurrentInventory()
                            + " units is below target. "
                            + "Moderate demand supports healthy stock "
                            + "cover while limiting capital exposure to ₹"
                            + format(value)
                            + ".";
        }

        return
                "Controlled replenishment: current inventory of "
                        + candidate.getCurrentInventory()
                        + " units is below target, but lower demand "
                        + "justifies limiting the new capital commitment "
                        + "to ₹"
                        + format(value)
                        + ".";
    }

    /*
     * ---------------------------------------------------------
     * ROUND / FORMAT
     * ---------------------------------------------------------
     */

    private double round(double value) {

        return Math.round(
                value * 100.0
        ) / 100.0;
    }

    private String format(double value) {

        return String.format(
                Locale.US,
                "%,.0f",
                value
        );
    }

    /*
     * ---------------------------------------------------------
     * INTERNAL CANDIDATE
     * ---------------------------------------------------------
     */

    private static class AllocationCandidate {

        private final Store store;
        private final PhoneModel phone;
        private final double weeklySales;
        private final int targetUnits;
        private final int currentInventory;
        private final int replenishmentNeed;
        private final double priorityScore;

        public AllocationCandidate(
                Store store,
                PhoneModel phone,
                double weeklySales,
                int targetUnits,
                int currentInventory,
                int replenishmentNeed,
                double priorityScore) {

            this.store = store;
            this.phone = phone;
            this.weeklySales = weeklySales;
            this.targetUnits = targetUnits;
            this.currentInventory = currentInventory;
            this.replenishmentNeed = replenishmentNeed;
            this.priorityScore = priorityScore;
        }

        public Store getStore() {
            return store;
        }

        public PhoneModel getPhone() {
            return phone;
        }

        public double getWeeklySales() {
            return weeklySales;
        }

        public int getTargetUnits() {
            return targetUnits;
        }

        public int getCurrentInventory() {
            return currentInventory;
        }

        public int getReplenishmentNeed() {
            return replenishmentNeed;
        }

        public double getPriorityScore() {
            return priorityScore;
        }
    }
}