// 1. The Controller: AdminStatisticsController.java
package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.example.snappfoodfront.Service.AdminApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.AdminDto;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class AdminStatisticsController implements Initializable {

    @FXML private Label totalUsersLabel;
    @FXML private Label totalRestaurantsLabel;
    @FXML private Label ordersTodayLabel;
    @FXML private Label totalRevenueLabel;


    private final AdminApiService adminApiService = new AdminApiService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadStatistics();
    }

    private void loadStatistics() {

        totalUsersLabel.setText("...");
        totalRestaurantsLabel.setText("...");
        ordersTodayLabel.setText("...");
        totalRevenueLabel.setText("...");

        new Thread(() -> {
            try {

                AdminDto.StatisticsResponse stats = adminApiService.getStatistics(TokenManager.getToken());


                Platform.runLater(() -> {
                    totalUsersLabel.setText(String.valueOf(stats.getTotal_users()));
                    totalRestaurantsLabel.setText(String.valueOf(stats.getTotal_restaurants()));
                    ordersTodayLabel.setText(String.valueOf(stats.getOrders_today()));


                    NumberFormat currencyFormatter = NumberFormat.getNumberInstance(Locale.US);
                    totalRevenueLabel.setText(currencyFormatter.format(stats.getTotal_revenue()));
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {

                    totalUsersLabel.setText("Error");
                    totalRestaurantsLabel.setText("Error");
                    ordersTodayLabel.setText("Error");
                    totalRevenueLabel.setText("Error");
                });
            }
        }).start();
    }
}

