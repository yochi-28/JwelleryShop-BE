package org.sharmas.jwelleryshopbe.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {

    private String userId;
    private List<CartItem> cartItems;

    @Data
    public static class CartItem{
        private String productId;
        private int quantity;
    }
}
