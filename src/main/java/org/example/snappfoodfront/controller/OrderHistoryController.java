package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.snappfoodfront.Service.OrderApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.OrderDto;
import org.example.snappfoodfront.Utils.MainViewState;
import org.example.snappfoodfront.Utils.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OrderHistoryController implements Initializable {


    @FXML public VBox ongoingOrdersContainer;
    @FXML public VBox pastOrdersContainer;

    private final OrderApiService orderService = new OrderApiService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadOrderHistory();

    }

    private void loadOrderHistory() {
        new Thread(() -> {
            try {
                final List<OrderDto.OrderResponse> allOrders = orderService.getOrderHistory(TokenManager.getToken(), null, null);

                Platform.runLater(() -> {
                    ongoingOrdersContainer.getChildren().clear();
                    pastOrdersContainer.getChildren().clear();

                    if (allOrders.isEmpty()) {
                        ongoingOrdersContainer.getChildren().add(new Label("You have no ongoing orders yet"));
                        pastOrdersContainer.getChildren().add(new Label("you have no past orders"));
                        return;
                    }

                    for (OrderDto.OrderResponse order : allOrders) {
                        switch (order.getStatus()) {
                            case "WAITING_VENDOR":
                            case "FINDING_COURIER":
                            case "ON_THE_WAY":
                                addOrderCard(order, ongoingOrdersContainer, "/view/ongoing-order-card.fxml");
                                break;

                            case "COMPLETED":
                            case "CANCELLED":
                                addOrderCard(order, pastOrdersContainer, "/view/completed-order-card.fxml");
                                break;
                        }
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    ongoingOrdersContainer.getChildren().add(new Label("Error while loading order history"));
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void addOrderCard(OrderDto.OrderResponse order, VBox container, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node cardNode = loader.load();

            if (fxmlPath.contains("ongoing")) {
                OngoingOrderCardController controller = loader.getController();
                controller.setData(order);
            } else {
                CompletedOrderCardController controller = loader.getController();
                controller.setData(order, this);
            }

            container.getChildren().add(cardNode);
        } catch (IOException | InterruptedException | OrderApiService.OrderException e) {
            e.printStackTrace();
        }
    }

    public void startReviewProcess(Long orderId) {
        MainViewState.setSelectedOrderId(orderId);
        Runnable refreshCallback = this::loadOrderHistory;

        SceneManager.showReviewWindow(refreshCallback);
    }

    @FXML
    private void refreshPage() {
        loadOrderHistory();
    }


}
