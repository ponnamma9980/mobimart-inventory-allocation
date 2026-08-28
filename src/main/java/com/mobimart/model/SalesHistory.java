package com.mobimart.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sales_history")
public class SalesHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Store store;

    @ManyToOne
    private PhoneModel phoneModel;

    private LocalDate saleDate;
    private int unitsSold;
    private double revenue;

    public SalesHistory() {
    }

    public SalesHistory(Store store, PhoneModel phoneModel,
                        LocalDate saleDate, int unitsSold,
                        double revenue) {
        this.store = store;
        this.phoneModel = phoneModel;
        this.saleDate = saleDate;
        this.unitsSold = unitsSold;
        this.revenue = revenue;
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

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public int getUnitsSold() {
        return unitsSold;
    }

    public double getRevenue() {
        return revenue;
    }
}
