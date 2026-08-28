package com.mobimart.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Store store;

    @ManyToOne
    private PhoneModel phoneModel;

    private int quantity;

    public Inventory() {
    }

    public Inventory(Store store, PhoneModel phoneModel, int quantity) {
        this.store = store;
        this.phoneModel = phoneModel;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}