package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.snappfoodfront.Service.AdminApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.TransactionDto;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionManagementController implements Initializable {

    @FXML
    private TableView<TransactionDto.PaymentResponseDTO> transactionsTableView;

    @FXML
    private TableColumn<TransactionDto.PaymentResponseDTO, Long> idColumn;

    @FXML
    private TableColumn<TransactionDto.PaymentResponseDTO, Long> orderIdColumn;

    @FXML
    private TableColumn<TransactionDto.PaymentResponseDTO, Long> userIdColumn;

    @FXML
    private TableColumn<TransactionDto.PaymentResponseDTO, String> methodColumn;

    @FXML
    private TableColumn<TransactionDto.PaymentResponseDTO, String> statusColumn;

    @FXML
    private TextField searchField;

    @FXML
    private TextField searchUserField;

    @FXML
    private JFXCheckBox walletMethodCheck;

    @FXML
    private JFXCheckBox onlineMethodCheck;

    @FXML
    private JFXCheckBox successStatusCheck;

    @FXML
    private JFXCheckBox failedStatusCheck;

    private final AdminApiService adminApiService = new AdminApiService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("order_id"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("method"));

        setupStatusColumn();

        loadTransactions();
    }

    @FXML
    void handleFilter(ActionEvent event) {
        loadTransactions();
    }

    private void loadTransactions() {

        String searchFilter = searchField.getText();
        String userFilter = searchUserField.getText();


        String methodFilter;
        if (walletMethodCheck.isSelected() && !onlineMethodCheck.isSelected()) {
            methodFilter = "wallet";
        } else if (!walletMethodCheck.isSelected() && onlineMethodCheck.isSelected()) {
            methodFilter = "online";
        } else {
            methodFilter = null;
        }


        String statusFilter;
        if (successStatusCheck.isSelected() && !failedStatusCheck.isSelected()) {
            statusFilter = "success";
        } else if (!successStatusCheck.isSelected() && failedStatusCheck.isSelected()) {
            statusFilter = "failed";
        } else {
            statusFilter = null;
        }


        new Thread(() -> {
            try {

                List<TransactionDto.PaymentResponseDTO> transactions = adminApiService.getTransactions(
                        TokenManager.getToken(),
                        searchFilter,
                        userFilter,
                        methodFilter,
                        statusFilter
                );
                Platform.runLater(() -> {
                    transactionsTableView.setItems(FXCollections.observableArrayList(transactions));
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error getting transactions :" + e.getMessage());
            }
        }).start();
    }

    private void setupStatusColumn() {
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label statusBadge = new Label(status);
                    statusBadge.getStyleClass().add("status-badge");

                    if ("success".equalsIgnoreCase(status)) {
                        statusBadge.getStyleClass().add("status-accepted"); // Green
                    } else if ("failed".equalsIgnoreCase(status)) {
                        statusBadge.getStyleClass().add("status-cancelled"); // Red
                    }
                    setGraphic(statusBadge);
                }
            }
        });
    }
}
