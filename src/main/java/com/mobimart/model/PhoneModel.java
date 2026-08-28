package com.mobimart.model;

import jakarta.persistence.*;

@Entity
@Table(name = "phone_models")
public class PhoneModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String brand;
    private double price;
    private String category;
    private int launchMonth;
    private Integer successorLaunchMonth;

    public PhoneModel() {
    }

    public PhoneModel(String name, String brand, double price,
                      String category, int launchMonth,
                      Integer successorLaunchMonth) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.category = category;
        this.launchMonth = launchMonth;
        this.successorLaunchMonth = successorLaunchMonth;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public int getLaunchMonth() {
        return launchMonth;
    }

    public Integer getSuccessorLaunchMonth() {
        return successorLaunchMonth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLaunchMonth(int launchMonth) {
        this.launchMonth = launchMonth;
    }

    public void setSuccessorLaunchMonth(Integer successorLaunchMonth) {
        this.successorLaunchMonth = successorLaunchMonth;
    }
}