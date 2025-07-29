package org.example.snappfoodfront.Utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.snappfoodfront.model.FoodItemDto;
import org.example.snappfoodfront.model.OrderDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CartManager {

    private static final Map<Long, CartItem> items = new HashMap<>();
    @Getter
    @Setter
    private static Long currentRestaurantId = null;
    @Getter
    @Setter
    private static Long appliedCouponId = null;
    @Getter
    @Setter
    private static BigDecimal walletBalance = null;
    @Getter
    @Setter
    private static String buyerAddress = null;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class CartItem {
        private FoodItemDto.Response foodItem;
        private int quantity;
    }

    public static void addItem(FoodItemDto.Response foodItem, int quantity) throws DifferentRestaurantException {
        if (quantity <= 0) {
            removeItem(foodItem.getId());
            return;
        }

        if (items.isEmpty()) {
            currentRestaurantId = foodItem.getVendor_id();
        } else if (!currentRestaurantId.equals(foodItem.getVendor_id())) {
            throw new DifferentRestaurantException("You are trying to add more than one restaurant to the cart. Do you want to clear your cart now?");
        }

        CartItem cartItem = items.get(foodItem.getId());
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
        } else {
            items.put(foodItem.getId(), new CartItem(foodItem, quantity));
        }
    }

    public static void removeItem(Long foodItemId) {
        items.remove(foodItemId);
        if (items.isEmpty()) {
            currentRestaurantId = null;
        }
    }

    public static int getItemQuantity(Long foodItemId) {
        CartItem item = items.get(foodItemId);
        return (item != null) ? item.getQuantity() : 0;
    }

    public static List<CartItem> getCartItems() {
        return new ArrayList<>(items.values());
    }

    public static void clearCart() {
        items.clear();
        currentRestaurantId = null;
        appliedCouponId = null;
    }

    public static class DifferentRestaurantException extends Exception {
        public DifferentRestaurantException(String message) {
            super(message);
        }
    }
}