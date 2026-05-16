package com.example.cart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart implements Serializable {
    private String sessionId;
    private List<CartItem> items;
    private double totalAmount;
    private int itemCount;
}
