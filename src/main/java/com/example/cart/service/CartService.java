package com.example.cart.service;

import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MeterRegistry meterRegistry;
    private static final String CART_PREFIX = "cart:";

    public CartService(RedisTemplate<String, Object> redisTemplate, MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.meterRegistry = meterRegistry;
    }

    public Cart addItem(String sessionId, CartItem item) {
        String key = CART_PREFIX + sessionId;
        
        // Get existing item from hash if it exists
        Object existingObj = redisTemplate.opsForHash().get(key, item.getProductId());
        
        if (existingObj != null) {
            // Convert to CartItem (Redis handles JSON conversion via serializer)
            CartItem existingItem = (CartItem) existingObj;
            // Update quantity
            item.setQuantity(item.getQuantity() + existingItem.getQuantity());
        }

        // Save/Update in Redis Hash
        redisTemplate.opsForHash().put(key, item.getProductId(), item);
        
        // Reset TTL to 30 minutes
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        
        return getCart(sessionId);
    }

    public Cart getCart(String sessionId) {
        String key = CART_PREFIX + sessionId;
        
        // Get all items from the hash
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        
        // Record hit/miss for statistics
        if (entries.isEmpty()) {
            meterRegistry.counter("cache.gets", "cache", "carts", "result", "miss").increment();
        } else {
            meterRegistry.counter("cache.gets", "cache", "carts", "result", "hit").increment();
        }

        List<CartItem> items = new ArrayList<>();
        double totalAmount = 0;
        
        for (Object value : entries.values()) {
            CartItem item = (CartItem) value;
            items.add(item);
            totalAmount += item.getPrice() * item.getQuantity();
        }

        Cart cart = new Cart();
        cart.setSessionId(sessionId);
        cart.setItems(items);
        cart.setItemCount(items.size());
        cart.setTotalAmount(totalAmount);
        
        return cart;
    }

    public Cart removeItem(String sessionId, String productId) {
        String key = CART_PREFIX + sessionId;
        
        // Remove field from hash
        redisTemplate.opsForHash().delete(key, productId);
        
        // Reset TTL
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        
        return getCart(sessionId);
    }

    public void clearCart(String sessionId) {
        String key = CART_PREFIX + sessionId;
        redisTemplate.delete(key);
    }

    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get total number of active carts
        // Note: This scans all keys starting with "cart:*". 
        // In a real production app with millions of keys, this might be slow.
        Long totalCarts = redisTemplate.getConnectionFactory().getConnection().dbSize();
        // Alternatively, count keys matching pattern
        // Long totalCarts = (long) redisTemplate.keys(CART_PREFIX + "*").size();
        
        stats.put("totalCarts", totalCarts);

        // Calculate hit rate from Micrometer
        double hits = 0;
        double misses = 0;
        
        try {
            hits = meterRegistry.get("cache.gets").tag("result", "hit").counter().count();
            misses = meterRegistry.get("cache.gets").tag("result", "miss").counter().count();
        } catch (Exception e) {
            // Stats not available yet
        }

        double hitRate = -1.0;
        if (hits + misses > 0) {
            hitRate = hits / (hits + misses);
        }
        
        stats.put("hitRate", hitRate);
        
        return stats;
    }
}
