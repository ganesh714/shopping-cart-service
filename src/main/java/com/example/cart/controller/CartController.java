package com.example.cart.controller;

import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import com.example.cart.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @PostMapping("/{sessionId}/items")
    public ResponseEntity<Cart> addItem(@PathVariable String sessionId, @RequestBody CartItem item) {
        Cart updatedCart = cartService.addItem(sessionId, item);
        return new ResponseEntity<>(updatedCart, HttpStatus.CREATED);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<Cart> getCart(@PathVariable String sessionId) {
        Cart cart = cartService.getCart(sessionId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{sessionId}/items/{productId}")
    public ResponseEntity<Cart> removeItem(@PathVariable String sessionId, @PathVariable String productId) {
        Cart updatedCart = cartService.removeItem(sessionId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clearCart(@PathVariable String sessionId) {
        cartService.clearCart(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cache-stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = cartService.getCacheStats();
        return ResponseEntity.ok(stats);
    }
}
