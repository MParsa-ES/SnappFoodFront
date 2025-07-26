package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.example.snappfoodfront.Service.CourierApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.OrderDto;

public class DeliveryCardController {

    // --- FXML Injections ---
    @FXML
    private Label orderIdLabel;

    @FXML
    private Label statusBadge;

    @FXML
    private Label pickupRestaurantLabel;

    @FXML
    private Label customerNameLabel;

    @FXML
    private Label deliveryAddressLabel;

    @FXML
    private Label feeLabel;

    @FXML
    private HBox actionBox;

    @FXML
    private JFXButton actionButton;

    private OrderDto.OrderResponse order;
    private Runnable refreshCallback;
    private Runnable refreshAvailableDeliveries;
    private final CourierApiService courierApiService = new CourierApiService();

    public void setData(OrderDto.OrderResponse order, Runnable refreshCallback, Runnable refreshAvailableDeliveries) {
        this.order = order;
        this.refreshCallback = refreshCallback;
        this.refreshAvailableDeliveries = refreshAvailableDeliveries;

        orderIdLabel.setText("#" + order.getId());
        feeLabel.setText("Fee: " + order.getCourier_fee());
        deliveryAddressLabel.setText(order.getDelivery_address());

        new Thread(() -> {
            try {
                String restaurantName = courierApiService.getRestaurant(TokenManager.getToken(), order.getVendor_id()).getName();
                String customerName = courierApiService.getCustomer(TokenManager.getToken(), order.getCustomer_id()).getFull_name();

                Platform.runLater(() -> {
                    pickupRestaurantLabel.setText(restaurantName);
                    customerNameLabel.setText(customerName);
                });
            } catch (Exception e) {
                System.err.println("Error getting customer or restaurant name :" + e.getMessage());

                Platform.runLater(() -> {
                    pickupRestaurantLabel.setText("Not found");
                    customerNameLabel.setText("Not found");
                });
            }
        }).start();

        updateCardState(order.getStatus());
    }

    private void updateCardState(String status) {
        statusBadge.setText(status.replace("_", " "));
        statusBadge.getStyleClass().clear();
        statusBadge.getStyleClass().add("status-badge");

        actionButton.setOnAction(null);

        switch (status.toLowerCase()) {
            case "finding_courier":
                statusBadge.getStyleClass().add("status-pending");
                actionButton.setText("Accept");
                actionButton.setOnAction(event -> handleUpdateStatus("ON_THE_WAY"));
                actionButton.setDisable(false);
                break;
            case "on_the_way":
                statusBadge.getStyleClass().add("status-accepted");
                actionButton.setText("Mark as Delivered");
                actionButton.setOnAction(event -> handleUpdateStatus("COMPLETED"));
                actionButton.setDisable(false);
                break;
            case "delivered":
            case "completed":
                statusBadge.getStyleClass().add("status-delivered");
                actionButton.setText("Completed");
                actionButton.setDisable(true);
                break;
            default:
                actionBox.setVisible(false);
                break;
        }
    }

    private void handleUpdateStatus(String newStatus) {
        new Thread(() -> {
            try {
                OrderDto.OrderStatusChangeRequest changeRequest = new OrderDto.OrderStatusChangeRequest(newStatus);
                courierApiService.updateDeliveryStatus(TokenManager.getToken(), order.getId(), changeRequest);
                Platform.runLater(() -> {
                    refreshCallback.run();
                    refreshAvailableDeliveries.run();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
