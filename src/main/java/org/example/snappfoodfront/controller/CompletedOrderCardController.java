package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.snappfoodfront.Service.OrderApiService;
import org.example.snappfoodfront.model.OrderDto;
import org.example.snappfoodfront.Utils.SceneManager; // برای باز کردن پنجره ثبت نظر

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class CompletedOrderCardController {


    @FXML private Label restaurantNameLabel;
    @FXML private Label orderDateLabel;
    @FXML private VBox itemsVBox;
    @FXML public Label subtotalPriceLabel;
    @FXML public Label taxFeeLabel;
    @FXML public Label additionalFeeLabel;
    @FXML public Label totalPriceLabel;
    @FXML private JFXButton reviewButton;

    private OrderDto.OrderResponse order;

    private final OrderApiService orderService = new OrderApiService();


    public void setData(OrderDto.OrderResponse order) throws IOException, InterruptedException, OrderApiService.OrderException {
        this.order = order;

        new Thread(() -> {
            try {
                OrderDto.NamesRequest request = new OrderDto.NamesRequest(order.getVendor_id(), order.getItem_ids());
                final OrderDto.NamesResponse names = orderService.getNames(request);

                Platform.runLater(() -> {
                    restaurantNameLabel.setText(names.getRestaurant_name());
                    orderDateLabel.setText("Date: " + order.getCreated_at());
                    subtotalPriceLabel.setText(String.format("%,.0f T", order.getRaw_price()));
                    taxFeeLabel.setText(String.format("%,.0f T", order.getTax_fee()));
                    additionalFeeLabel.setText(String.format("%,.0f T", order.getAdditional_fee()));
                    totalPriceLabel.setText(String.format("%,.0f T", order.getPay_price()));

                    if ("COMPLETED".equals(order.getStatus())) {
                        reviewButton.setVisible(true);
                        reviewButton.setManaged(true);
                    } else {
                        reviewButton.setVisible(false);
                        reviewButton.setManaged(false);
                    }

                    itemsVBox.getChildren().clear();
                    for (String itemName : names.getItem_names()) {
                        Label itemLabel = new Label("- " + itemName);
                        itemsVBox.getChildren().add(itemLabel);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> restaurantNameLabel.setText("Error loading data."));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleReview() {
        System.out.println("دکمه ثبت نظر برای سفارش با ID: " + order.getId() + " کلیک شد.");
        // در اینجا، شما باید یک پنجره جدید برای ثبت نظر باز کنید
        // و ID سفارش را به کنترلر آن پنجره پاس دهید.
        // SceneManager.showSubmitReviewWindow(order.getId());
    }

    // ... (متد setStatusStyle مانند کنترلر قبلی)
}