package org.sharmas.jwelleryshopbe.controller;

import org.sharmas.jwelleryshopbe.models.ProductModel;
import org.sharmas.jwelleryshopbe.models.PurchaseModel;
import org.sharmas.jwelleryshopbe.models.PurchaseRequest;
import org.sharmas.jwelleryshopbe.models.CartModel; // 👈 1. IMPORT CART MODEL
import org.sharmas.jwelleryshopbe.repository.ProductRepository;
import org.sharmas.jwelleryshopbe.repository.PurchaseRepository;
import org.sharmas.jwelleryshopbe.repository.CartRepository; // 👈 2. IMPORT CART REPOSITORY
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PurchaseController {

    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final CartRepository cartRepository; // 👈 4. DECLARE THE CART REPOSITORY FIELD

    // 5. UPDATE CONSTRUCTOR FOR DEPENDENCY INJECTION
    public PurchaseController(ProductRepository productRepository,
                              PurchaseRepository purchaseRepository,
                              CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
        this.cartRepository = cartRepository;
    }

    @GetMapping("/user/orders/{userId}")
    public ResponseEntity<List<PurchaseModel>> getUserOrders(@PathVariable String userId) {
        List<PurchaseModel> userOrders = purchaseRepository.findByUserId(userId);
        return ResponseEntity.ok(userOrders);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutCart(@RequestBody PurchaseRequest request) {

        List<PurchaseModel.PurchaseItem> finalizedItems = request.getCartItems().stream()
                .map(cartItem -> {
                    ProductModel realProduct = productRepository.findById(cartItem.getProductId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Product not found: " + cartItem.getProductId()
                            ));

                    var receiptItem = new PurchaseModel.PurchaseItem();
                    receiptItem.setProductId(realProduct.getId());
                    receiptItem.setItemCount(cartItem.getQuantity());
                    receiptItem.setPriceAtPurchase(realProduct.getPrice());

                    return receiptItem;
                })
                .collect(Collectors.toList());

        double grandTotal = finalizedItems.stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getItemCount())
                .sum();

        var newOrder = new PurchaseModel();
        newOrder.setUserId(request.getUserId());
        newOrder.setItems(finalizedItems);
        newOrder.setTotalAmount(grandTotal);
        newOrder.setOrderStatus(PurchaseModel.Status.PENDING);

        // Save order receipt record into Database
        purchaseRepository.save(newOrder);

        // 👈 6. CLEAR DATABASE CART ON SUCCESSFUL ORDER PLACEMENT
        try {
            cartRepository.findByUserId(request.getUserId()).ifPresent(cart -> {
                cart.setItems(new ArrayList<>()); // Wipe items list empty
                cartRepository.save(cart); // Update database document structure
            });
        } catch (Exception e) {
            System.err.println("Warning: Order saved but failed to clear user cart collection data: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }
}
