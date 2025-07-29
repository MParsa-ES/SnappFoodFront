package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.snappfoodfront.Service.AdminApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.CouponDto;
import org.example.snappfoodfront.Utils.Methods;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class AddCouponController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private JFXTextField codeField;
    @FXML private JFXTextField userCountField;
    @FXML private JFXTextField valueField;
    @FXML private JFXTextField minPriceField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ToggleGroup typeToggleGroup;
    @FXML private JFXRadioButton fixedTypeRadio;
    @FXML private JFXRadioButton percentTypeRadio;
    @FXML private HBox feedbackBox;
    @FXML private Label feedbackLabel;
    @FXML private JFXButton saveButton;


    private final AdminApiService adminApiService = new AdminApiService();
    private CouponDto.Response couponToEdit;


    public void initialize(URL location, ResourceBundle resources) {

        Methods.filterPhoneField(userCountField);
        Methods.filterPhoneField(minPriceField);
        Methods.filterPhoneField(valueField);

        if (this.couponToEdit != null) {
            codeField.setText(couponToEdit.getCoupon_code());
            userCountField.setText(String.valueOf(couponToEdit.getUser_count()));
            minPriceField.setText(String.valueOf(couponToEdit.getMin_price()));
            valueField.setText(String.valueOf(couponToEdit.getValue()));
            startDatePicker.setValue(couponToEdit.getStart_date());
            endDatePicker.setValue(couponToEdit.getEnd_date());

            if (couponToEdit.getType().equals("FIXED")) {
                fixedTypeRadio.setSelected(true);
            } else {
                percentTypeRadio.setSelected(true);
            }
        }

    }


    public void initDataForEdit(CouponDto.Response coupon) {
        this.couponToEdit = coupon;

        titleLabel.setText("Edit Coupon");
        codeField.setText(coupon.getCoupon_code());
        userCountField.setText(String.valueOf(coupon.getUser_count()));
        valueField.setText(String.valueOf(coupon.getValue()));
        minPriceField.setText(String.valueOf(coupon.getMin_price()));
        startDatePicker.setValue(coupon.getStart_date());
        endDatePicker.setValue(coupon.getEnd_date());

        if ("percent".equalsIgnoreCase(coupon.getType())) {
            percentTypeRadio.setSelected(true);
        } else {
            fixedTypeRadio.setSelected(true);
        }

        saveButton.setText("Save Changes");
    }


    @FXML
    void handleSaveButton(ActionEvent event) {
        if (codeField.getText().isBlank() || valueField.getText().isBlank() || minPriceField.getText().isBlank() ||
                startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showFeedback("Please complete all fields.", true);
            return;
        }

        CouponDto.Request couponDto = new CouponDto.Request();
        try {
            couponDto.setCoupon_code(codeField.getText());
            couponDto.setUser_count(Integer.parseInt(userCountField.getText()));
            couponDto.setValue(BigDecimal.valueOf(Double.parseDouble(valueField.getText())));
            couponDto.setMin_price(BigDecimal.valueOf(Integer.parseInt(minPriceField.getText())));
            couponDto.setStart_date(startDatePicker.getValue());
            couponDto.setEnd_date(endDatePicker.getValue());

            JFXRadioButton selectedRadio = (JFXRadioButton) typeToggleGroup.getSelectedToggle();
            couponDto.setType(selectedRadio.getText().toLowerCase());
        } catch (NumberFormatException e) {
            showFeedback("Value and Min. Purchase must be valid numbers.", true);
            return;
        }

        new Thread(() -> {
            try {
                if (couponToEdit == null) {

                    adminApiService.createCoupon(TokenManager.getToken(), couponDto);
                } else {

                    adminApiService.updateCoupon(TokenManager.getToken(), couponToEdit.getId(), couponDto);
                }

                Platform.runLater(this::closeWindow);
            } catch (Exception e) {
                Platform.runLater(() -> showFeedback("Error: " + e.getMessage(), true));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void handleCancelButton(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showFeedback(String message, boolean isError) {
        feedbackLabel.setText(message);
        feedbackBox.getStyleClass().removeAll("feedback-box-success", "feedback-box-error");
        feedbackBox.getStyleClass().add(isError ? "feedback-box-error" : "feedback-box-success");
        feedbackBox.setVisible(true);
        feedbackBox.setManaged(true);
    }
}
