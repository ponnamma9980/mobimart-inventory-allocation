package com.mobimart.service;

import com.mobimart.model.Inventory;
import com.mobimart.model.PhoneModel;
import com.mobimart.model.SalesHistory;
import com.mobimart.model.Store;
import com.mobimart.repository.InventoryRepository;
import com.mobimart.repository.PhoneModelRepository;
import com.mobimart.repository.SalesHistoryRepository;
import com.mobimart.repository.StoreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class DataGeneratorService implements CommandLineRunner {

    private final StoreRepository storeRepository;
    private final PhoneModelRepository phoneModelRepository;
    private final SalesHistoryRepository salesHistoryRepository;
    private final InventoryRepository inventoryRepository;

    public DataGeneratorService(
            StoreRepository storeRepository,
            PhoneModelRepository phoneModelRepository,
            SalesHistoryRepository salesHistoryRepository,
            InventoryRepository inventoryRepository) {

        this.storeRepository = storeRepository;
        this.phoneModelRepository = phoneModelRepository;
        this.salesHistoryRepository = salesHistoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /*
     * ---------------------------------------------------------
     * GENERATE STORES
     * ---------------------------------------------------------
     */

    public void generateStores() {

        List<Store> stores = new ArrayList<>();

        stores.add(new Store("MobiMart Jayanagar", "Bangalore",
                "Premium", 1.00, 0.95, 1200));

        stores.add(new Store("MobiMart Whitefield", "Bangalore",
                "Premium", 0.95, 0.90, 1100));

        stores.add(new Store("MobiMart Koramangala", "Bangalore",
                "Premium", 1.00, 0.98, 1300));

        stores.add(new Store("MobiMart Malleshwaram", "Bangalore",
                "Urban", 0.85, 0.82, 1000));

        stores.add(new Store("MobiMart Marathahalli", "Bangalore",
                "Urban", 0.88, 0.92, 1050));

        stores.add(new Store("MobiMart Indiranagar", "Bangalore",
                "Premium", 1.00, 0.96, 1250));

        stores.add(new Store("MobiMart Electronic City", "Bangalore",
                "Urban", 0.82, 0.88, 1000));

        stores.add(new Store("MobiMart Hebbal", "Bangalore",
                "Urban", 0.80, 0.78, 950));

        stores.add(new Store("MobiMart Mysore Central", "Mysore",
                "Tier-2", 0.68, 0.75, 900));

        stores.add(new Store("MobiMart Mysore Kuvempunagar", "Mysore",
                "Tier-2", 0.65, 0.68, 850));

        stores.add(new Store("MobiMart Hubli", "Hubli",
                "Tier-2", 0.62, 0.78, 950));

        stores.add(new Store("MobiMart Dharwad", "Hubli",
                "Tier-2", 0.60, 0.65, 800));

        stores.add(new Store("MobiMart Tumkur", "Tumkur",
                "Tier-2", 0.58, 0.70, 850));

        stores.add(new Store("MobiMart Davangere", "Davangere",
                "Tier-2", 0.55, 0.72, 850));

        stores.add(new Store("MobiMart Shivamogga", "Shivamogga",
                "Tier-2", 0.57, 0.68, 800));

        stores.add(new Store("MobiMart Belgaum", "Belgaum",
                "Tier-2", 0.63, 0.76, 900));

        stores.add(new Store("MobiMart Bellary", "Bellary",
                "Tier-2", 0.54, 0.70, 800));

        stores.add(new Store("MobiMart Hassan", "Hassan",
                "Tier-2", 0.56, 0.62, 750));

        stores.add(new Store("MobiMart Mandya", "Mandya",
                "Tier-3", 0.50, 0.60, 700));

        stores.add(new Store("MobiMart Chitradurga", "Chitradurga",
                "Tier-3", 0.48, 0.58, 700));

        stores.add(new Store("MobiMart Raichur", "Raichur",
                "Tier-3", 0.45, 0.65, 750));

        stores.add(new Store("MobiMart Kolar", "Kolar",
                "Tier-3", 0.52, 0.61, 700));

        stores.add(new Store("MobiMart Udupi", "Udupi",
                "Tier-2", 0.64, 0.73, 850));

        stores.add(new Store("MobiMart Mangalore", "Mangalore",
                "Premium", 0.82, 0.86, 1000));

        stores.add(new Store("MobiMart Chikkamagaluru", "Chikkamagaluru",
                "Tier-3", 0.50, 0.57, 700));

        storeRepository.saveAll(stores);
    }

    /*
     * ---------------------------------------------------------
     * GENERATE PHONE MODELS
     * ---------------------------------------------------------
     */

    public void generatePhoneModels() {

        List<PhoneModel> phones = new ArrayList<>();

        String[] brands = {
                "Samsung", "Apple", "Xiaomi", "OnePlus", "Vivo",
                "Oppo", "Realme", "Motorola", "Nokia", "iQOO"
        };

        double[] prices = {
                6999, 7999, 8999, 9999, 10999,
                11999, 12999, 13999, 14999, 15999,
                17999, 19999, 21999, 23999, 25999,
                27999, 29999, 32999, 35999, 39999,
                44999, 49999, 54999, 59999, 64999,
                69999, 74999, 79999, 89999, 99999,
                109999, 119999, 129999, 139999, 149999
        };

        int count = 1;

        for (int i = 0; i < 60; i++) {

            String brand = brands[i % brands.length];

            double price = prices[i % prices.length];

            String category;

            if (price < 12000) {
                category = "Budget";
            } else if (price < 25000) {
                category = "Mid-Range";
            } else if (price < 50000) {
                category = "Premium";
            } else {
                category = "Flagship";
            }

            int launchMonth = (i % 12) + 1;

            Integer successorMonth = null;

            if (i % 3 == 0 && launchMonth <= 9) {
                successorMonth = launchMonth + 3;
            }

            phones.add(new PhoneModel(
                    brand + " Mobi " + count,
                    brand,
                    price,
                    category,
                    launchMonth,
                    successorMonth
            ));

            count++;
        }

        phoneModelRepository.saveAll(phones);
    }

    /*
     * ---------------------------------------------------------
     * GENERATE 12 MONTHS SALES HISTORY
     * ---------------------------------------------------------
     */

    public void generateSalesHistory() {

        List<Store> stores = storeRepository.findAll();
        List<PhoneModel> phones = phoneModelRepository.findAll();

        List<SalesHistory> sales = new ArrayList<>();

        Random random = new Random(42);

        LocalDate startDate =
                LocalDate.now().minusWeeks(52);

        for (int week = 0; week < 52; week++) {

            LocalDate saleDate =
                    startDate.plusWeeks(week);

            int month =
                    saleDate.getMonthValue();

            double festiveMultiplier = 1.0;

            /*
             * Dussehra / Diwali period
             */
            if (month == 10 || month == 11) {
                festiveMultiplier = 2.5;
            }

            for (Store store : stores) {

                for (PhoneModel phone : phones) {

                    double demand = 2.0;

                    /*
                     * STORE PROFILE
                     */

                    if (store.getLocationType().equals("Premium")) {

                        if (phone.getCategory().equals("Flagship")) {
                            demand *= 4.0;
                        } else if (phone.getCategory().equals("Premium")) {
                            demand *= 2.5;
                        } else {
                            demand *= 0.8;
                        }

                    } else if (store.getLocationType().equals("Urban")) {

                        if (phone.getCategory().equals("Mid-Range")) {
                            demand *= 2.5;
                        } else if (phone.getCategory().equals("Premium")) {
                            demand *= 1.8;
                        } else {
                            demand *= 1.3;
                        }

                    } else {

                        /*
                         * Tier-2 / Tier-3
                         */

                        if (phone.getCategory().equals("Budget")) {
                            demand *= 3.5;
                        } else if (phone.getCategory().equals("Mid-Range")) {
                            demand *= 2.5;
                        } else {
                            demand *= 0.5;
                        }
                    }

                    /*
                     * INCOME + FOOTFALL
                     */

                    demand *= store.getIncomeIndex();
                    demand *= store.getFootfallIndex();

                    /*
                     * LAUNCH BOOST
                     */

                    int weeksSinceLaunch =
                            week -
                                    ((phone.getLaunchMonth() - 1) * 4);

                    if (weeksSinceLaunch >= 0
                            && weeksSinceLaunch <= 8) {

                        demand *= 1.6;
                    }

                    /*
                     * AGING MODEL
                     */

                    if (weeksSinceLaunch > 20) {
                        demand *= 0.65;
                    }

                    /*
                     * SUCCESSOR CANNIBALISATION
                     */

                    if (phone.getSuccessorLaunchMonth() != null) {

                        int successorWeek =
                                (phone.getSuccessorLaunchMonth() - 1) * 4;

                        if (week >= successorWeek - 4) {
                            demand *= 0.55;
                        }
                    }

                    /*
                     * FESTIVE EFFECT
                     */

                    demand *= festiveMultiplier;

                    /*
                     * RANDOM REALISTIC VARIATION
                     */

                    double variation =
                            0.75 +
                                    (random.nextDouble() * 0.5);

                    int unitsSold =
                            Math.max(
                                    0,
                                    (int) Math.round(
                                            demand * variation
                                    )
                            );

                    if (unitsSold > 0) {

                        double revenue =
                                unitsSold * phone.getPrice();

                        sales.add(
                                new SalesHistory(
                                        store,
                                        phone,
                                        saleDate,
                                        unitsSold,
                                        revenue
                                )
                        );
                    }
                }
            }
        }

        salesHistoryRepository.saveAll(sales);
    }

    /*
     * ---------------------------------------------------------
     * GENERATE STARTING INVENTORY
     * ---------------------------------------------------------
     *
     * Inventory is based on expected demand for each
     * store + phone combination.
     *
     * Premium stores hold more premium / flagship phones.
     * Tier-2 / Tier-3 stores hold more budget / mid-range.
     *
     * This gives the allocation engine a realistic
     * "current stock" position to work from.
     * ---------------------------------------------------------
     */

    public void generateInventory() {

        List<Store> stores =
                storeRepository.findAll();

        List<PhoneModel> phones =
                phoneModelRepository.findAll();

        List<Inventory> inventory =
                new ArrayList<>();

        Random random =
                new Random(100);

        for (Store store : stores) {

            for (PhoneModel phone : phones) {

                double baseStock;

                String category =
                        phone.getCategory().toLowerCase();

                String location =
                        store.getLocationType().toLowerCase();

                /*
                 * PREMIUM STORES
                 */

                if (location.contains("premium")) {

                    if (category.contains("flagship")) {
                        baseStock = 12;
                    } else if (category.contains("premium")) {
                        baseStock = 15;
                    } else if (category.contains("mid")) {
                        baseStock = 8;
                    } else {
                        baseStock = 4;
                    }

                }

                /*
                 * URBAN STORES
                 */

                else if (location.contains("urban")) {

                    if (category.contains("flagship")) {
                        baseStock = 6;
                    } else if (category.contains("premium")) {
                        baseStock = 10;
                    } else if (category.contains("mid")) {
                        baseStock = 14;
                    } else {
                        baseStock = 8;
                    }

                }

                /*
                 * TIER-2 / TIER-3
                 */

                else {

                    if (category.contains("budget")) {
                        baseStock = 18;
                    } else if (category.contains("mid")) {
                        baseStock = 15;
                    } else if (category.contains("premium")) {
                        baseStock = 6;
                    } else {
                        baseStock = 2;
                    }
                }

                /*
                 * STORE SIZE EFFECT
                 */

                double sizeFactor =
                        store.getStoreSize() / 1000.0;

                baseStock *= sizeFactor;

                /*
                 * FOOTFALL EFFECT
                 */

                baseStock *=
                        (0.75 + store.getFootfallIndex() * 0.5);

                /*
                 * LIFECYCLE EFFECT
                 *
                 * Older / successor-risk phones
                 * receive slightly less inventory.
                 */

                if (phone.getSuccessorLaunchMonth() != null) {
                    baseStock *= 0.70;
                }

                /*
                 * RANDOM VARIATION
                 */

                double variation =
                        0.80 +
                                random.nextDouble() * 0.40;

                int quantity =
                        Math.max(
                                0,
                                (int) Math.round(
                                        baseStock * variation
                                )
                        );

                inventory.add(
                        new Inventory(
                                store,
                                phone,
                                quantity
                        )
                );
            }
        }

        inventoryRepository.saveAll(inventory);
    }

    /*
     * ---------------------------------------------------------
     * APPLICATION STARTUP
     * ---------------------------------------------------------
     */

    @Override
    public void run(String... args) {

        generateStores();

        generatePhoneModels();

        generateSalesHistory();

        generateInventory();
    }
}