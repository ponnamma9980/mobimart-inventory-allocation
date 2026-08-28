package com.mobimart.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;
    private String locationType;
    private double incomeIndex;
    private double footfallIndex;
    private double storeSize;

    public Store() {
    }

    public Store(String name, String city, String locationType,
                 double incomeIndex, double footfallIndex, double storeSize) {
        this.name = name;
        this.city = city;
        this.locationType = locationType;
        this.incomeIndex = incomeIndex;
        this.footfallIndex = footfallIndex;
        this.storeSize = storeSize;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getLocationType() {
        return locationType;
    }

    public double getIncomeIndex() {
        return incomeIndex;
    }

    public double getFootfallIndex() {
        return footfallIndex;
    }

    public double getStoreSize() {
        return storeSize;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public void setIncomeIndex(double incomeIndex) {
        this.incomeIndex = incomeIndex;
    }

    public void setFootfallIndex(double footfallIndex) {
        this.footfallIndex = footfallIndex;
    }

    public void setStoreSize(double storeSize) {
        this.storeSize = storeSize;
    }
}