package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.snappfoodfront.Service.OrderApiService;
import org.example.snappfoodfront.model.OrderDto;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class OngoingOrderCardController {

    @FXML private Label restaurantNameLabel;
    @FXML private Label orderDateLabel;
    @FXML private VBox itemsVBox;
    @FXML private Label subtotalPriceLabel;
    @FXML private Label taxFeeLabel;
    @FXML private Label additionalFeeLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Label statusLabel;

    private final OrderApiService orderService = new OrderApiService();


    public void setData(OrderDto.OrderResponse order) throws IOException, InterruptedException, OrderApiService.OrderException {
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

                    setStatusStyle(order.getStatus());

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

    private void setStatusStyle(String status) {
        switch (status) {
            case "WAITING_VENDOR":
            case "FINDING_COURIER":
            case "ON_THE_WAY":
            case "ACCEPTED":
            case "RECEIVED":
                switch (status) {
                    case "WAITING_VENDOR" -> status = "Waiting For Vendor";
                    case "FINDING_COURIER" -> status = "Finding Courier";
                    case "ON_THE_WAY" -> status = "On the way";
                    case "ACCEPTED" -> status = "Accepted";
                    case "RECEIVED" -> status = "Received";
                }
                statusLabel.setTextFill(Color.BLUE);
                statusLabel.setText(status);
                break;
            case "COMPLETED":
                status = "Completed";
                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setText(status);
                break;
            case "CANCELLED":
                status = "Cancelled";
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText(status);
                break;
        }
    }

}