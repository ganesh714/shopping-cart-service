package com.example.cart.model;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable {
    private String sessionId;
    private List<CartItem> items;
    private double totalAmount;
    private int itemCount;

    public Cart() {
    }

    public Cart(String sessionId, List<CartItem> items, double totalAmount, int itemCount) {
        this.sessionId = sessionId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.itemCount = itemCount;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
