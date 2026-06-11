package org.sharmas.jwelleryshopbe.controller;

import org.sharmas.jwelleryshopbe.models.CartModel;
import org.sharmas.jwelleryshopbe.models.ProductModel;
import org.sharmas.jwelleryshopbe.repository.CartRepository;
import org.sharmas.jwelleryshopbe.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CartController {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @GetMapping("/user/{userId}/cart")

    public ResponseEntity<?> getCart(@PathVariable String userId) {
        try {
            // Find the cart or return an empty one if the user hasn't added anything yet
            CartModel cart = cartRepository.findByUserId(userId)
                    .orElse(new CartModel(null, userId, new ArrayList<>()));

            // Enrich the cart items with live product names and prices
            List<Map<String, Object>> enrichedItems = new ArrayList<>();

            for (CartModel.CartItem item : cart.getItems()) {
                ProductModel product = productRepository.findById(item.getProductId()).orElse(null);

                if (product != null) {
                    Map<String, Object> cartItemDetails = new HashMap<>();
                    cartItemDetails.put("productId", item.getProductId());
                    cartItemDetails.put("itemCount", item.getQuantity());
                    cartItemDetails.put("productName", product.getName());
                    cartItemDetails.put("priceAtPurchase", product.getPrice()); // Always fetches the live price

                    enrichedItems.add(cartItemDetails);
                }
            }

            // Wrap in a Map so React can access response.data.items cleanly
            Map<String, Object> response = new HashMap<>();
            response.put("items", enrichedItems);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching cart.");
        }
    }

    // --- 2. REMOVE ITEM FROM CART ENDPOINT ---
    @DeleteMapping("/user/{userId}/cart/{productId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable String userId, @PathVariable String productId) {
        try {
            CartModel cart = cartRepository.findByUserId(userId).orElse(null);

            if (cart != null) {
                // Remove the item where the productId matches
                cart.getItems().removeIf(item -> item.getProductId().equals(productId));
                cartRepository.save(cart);
                return ResponseEntity.ok("Item removed successfully");
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error removing item.");
        }
    }

    @PostMapping("/user/{userId}/cart")
    public ResponseEntity<?> addItemToCart(@PathVariable String userId, @RequestBody Map<String, Object> payload) {
        try {
            String productId = (String) payload.get("productId");
            int quantity = (int) payload.get("quantity");

            // 1. Find existing cart or create a brand new one if it doesn't exist
            CartModel cart = cartRepository.findByUserId(userId)
                    .orElse(new CartModel(null, userId, new ArrayList<>()));

            // 2. Check if the product already exists inside the cart array
            boolean itemExists = false;
            for (CartModel.CartItem item : cart.getItems()) {
                if (item.getProductId().equals(productId)) {
                    item.setQuantity(item.getQuantity() + quantity); // Increment item count
                    itemExists = true;
                    break;
                }
            }

            // 3. If it's a completely new item, instantiate and push it to the list
            if (!itemExists) {
                CartModel.CartItem newItem = new CartModel.CartItem();
                newItem.setProductId(productId);
                newItem.setQuantity(quantity);
                cart.getItems().add(newItem);
            }

            // 4. Save modifications back to your database provider
            cartRepository.save(cart);
            return ResponseEntity.ok(cart);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating your database cart.");
        }
    }
}