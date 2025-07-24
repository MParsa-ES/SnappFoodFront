package org.example.snappfoodfront.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.snappfoodfront.Utils.CartManager;
import org.example.snappfoodfront.model.FoodItemDto;

public class CartItemCardController {

    @FXML private Label foodNameLabel;
    @FXML private TextField quantityField;
    @FXML private Label totalPriceLabel;

    private FoodItemDto.Response foodItem;
    private CartController cartController; // ارجاع به کنترلر اصلی سبد خرید

    public void setData(FoodItemDto.Response foodItem, int quantity, CartController cartController) {
        this.foodItem = foodItem;
        this.cartController = cartController;
        foodNameLabel.setText(foodItem.getName());
        updateFields(quantity);
    }

    @FXML
    private void handleIncrement() {
        int newQuantity = Integer.parseInt(quantityField.getText()) + 1;
        if (newQuantity <= foodItem.getSupply()) {
            updateCartAndUI(newQuantity);
        }
    }

    @FXML
    private void handleDecrement() {
        int newQuantity = Integer.parseInt(quantityField.getText()) - 1;
        if (newQuantity >= 0) { // اجازه می‌دهیم به صفر برسد
            updateCartAndUI(newQuantity);
        }
    }

    @FXML
    private void handleRemove() {
        updateCartAndUI(0);
    }

    private void updateCartAndUI(int newQuantity) {
        try {
            CartManager.addItem(foodItem, newQuantity); // addItem صفر را به عنوان حذف مدیریت می‌کند
            updateFields(newQuantity);
            cartController.updateCartView(); // به کنترلر اصلی اطلاع بده که خود را آپدیت کند
        } catch (CartManager.DifferentRestaurantException e) {
            // این خطا در اینجا رخ نمی‌دهد چون آیتم از قبل در سبد است
        }
    }

    private void updateFields(int quantity) {
        quantityField.setText(String.valueOf(quantity));
        totalPriceLabel.setText(String.format("%,d T", foodItem.getPrice() * quantity));
    }
}
