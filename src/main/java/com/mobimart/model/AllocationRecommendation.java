package com.mobimart.model;

import jakarta.persistence.*;

@Entity
@Table(name = "allocation_recommendations")
public class AllocationRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Store store;

    @ManyToOne
    private PhoneModel phoneModel;

    private int recommendedQuantity;
    private double inventoryValue;
    private double expectedRevenue;
    private double stockoutRisk;
    private String reasoning;

    public AllocationRecommendation() {
    }

    public AllocationRecommendation(Store store,
                                    PhoneModel phoneModel,
                                    int recommendedQuantity,
                                    double inventoryValue,
                                    double expectedRevenue,
                                    double stockoutRisk,
                                    String reasoning) {
        this.store = store;
        this.phoneModel = phoneModel;
        this.recommendedQuantity = recommendedQuantity;
        this.inventoryValue = inventoryValue;
        this.expectedRevenue = expectedRevenue;
        this.stockoutRisk = stockoutRisk;
        this.reasoning = reasoning;
    }

    public Long getId() {
        return id;
    }

    public Store getStore() {
        return store;
    }

    public PhoneModel getPhoneModel() {
        return phoneModel;
    }

    public int getRecommendedQuantity() {
        return recommendedQuantity;
    }

    public double getInventoryValue() {
        return inventoryValue;
    }

    public double getExpectedRevenue() {
        return expectedRevenue;
    }

    public double getStockoutRisk() {
        return stockoutRisk;
    }

    public String getReasoning() {
        return reasoning;
    }
}