package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.example.snappfoodfront.Service.OrderApiService;
import org.example.snappfoodfront.Utils.Methods;
import org.example.snappfoodfront.Utils.TokenManager;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class WalletTopUpController implements Initializable {

    @FXML
    private TextField amountField;

    @FXML
    private Label statusLabel;

    private final OrderApiService orderService = new OrderApiService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Methods.applyThousandSeparator(amountField);
    }

    @FXML
    private void handleTopUp() {

        String amountText = amountField.getText().replaceAll("[,]", "");

        if (amountText.isEmpty()) {
            statusLabel.setText("Please enter an amount.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid amount format.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        new Thread(() -> {
            try {
                String token = TokenManager.getToken();
                orderService.topUpWallet(token, amount);

                Platform.runLater(() -> {
                    statusLabel.setText("Wallet topped up successfully!");
                    statusLabel.setTextFill(Color.GREEN);
                    amountField.clear();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Failed to top-up wallet. Please try again.");
                    statusLabel.setTextFill(Color.RED);
                });
                e.printStackTrace();
            }
        }).start();
    }
}