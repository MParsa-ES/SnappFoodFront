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
    private CartController cartController;

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
        if (newQuantity >= 0) {
            updateCartAndUI(newQuantity);
        }
    }

    @FXML
    private void handleRemove() {
        updateCartAndUI(0);
    }

    private void updateCartAndUI(int newQuantity) {
        try {
            CartManager.addItem(foodItem, newQuantity);
            updateFields(newQuantity);
            cartController.updateCartView();
        } catch (CartManager.DifferentRestaurantException e) {}
    }

    private void updateFields(int quantity) {
        quantityField.setText(String.valueOf(quantity));
        totalPriceLabel.setText(String.format("%,d T", foodItem.getPrice() * quantity));
    }
}
