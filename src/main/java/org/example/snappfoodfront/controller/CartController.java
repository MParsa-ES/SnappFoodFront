package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.snappfoodfront.Service.OrderApiService;
import org.example.snappfoodfront.Utils.CartManager;
import org.example.snappfoodfront.Utils.MainViewState;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    @FXML public Label additionalLabel;
    @FXML public Label taxLabel;
    @FXML public TextField couponField;
    @FXML public Label messageLabel;
    @FXML public JFXButton placeOrderButton;
    @FXML public JFXButton closeButton;
    @FXML public JFXComboBox<String> methodBox;
    @FXML private VBox cartItemsContainer;
    @FXML private Label totalPriceLabel;
    @FXML private Label errorLabel;

    private final OrderApiService orderService = new OrderApiService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        methodBox.getItems().addAll("Wallet", "Online");
        methodBox.getSelectionModel().selectFirst();
        updateCartView();

    }



    public void updateCartView() {
        cartItemsContainer.getChildren().clear();
        List<CartManager.CartItem> cartItems = CartManager.getCartItems();

        if (cartItems.isEmpty()) {
            cartItemsContainer.getChildren().add(new Label("Your cart is empty."));
            totalPriceLabel.setText("Total: 0 T");
            return;
        }

        int totalPrice = 0;
        for (CartManager.CartItem cartItem : cartItems) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/cart-item-view.fxml"));
                Node cardNode = loader.load();

                CartItemCardController controller = loader.getController();
                controller.setData(cartItem.getFoodItem(), cartItem.getQuantity(), this);

                cartItemsContainer.getChildren().add(cardNode);

                totalPrice += cartItem.getFoodItem().getPrice() * cartItem.getQuantity();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int off = 0;
        int additionalFee = MainViewState.getSelectedRestaurant().getAdditional_fee();
        int taxFee = MainViewState.getSelectedRestaurant().getTax_fee();
        totalPrice +=  additionalFee + taxFee - off;

        additionalLabel.setText(additionalFee + " T");
        taxLabel.setText(taxFee + " T");
        totalPriceLabel.setText(String.format("Total: %d T", totalPrice));
    }

    @FXML
    private void checkCoupon(ActionEvent event) {

        String couponCode = couponField.getText();
        if (couponCode.isEmpty()) {
            Platform.runLater(() -> {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("invalid code");
            });
            return;
        }

        new Thread(() -> {
            try {
                CouponDto.Response coupon = orderService.checkCoupon(TokenManager.getToken(), couponCode);
                CartManager.setAppliedCouponId(coupon.getId());
                Platform.runLater(() -> {
                    messageLabel.setTextFill(Color.GREEN);
                    messageLabel.setText("Coupon applied");
                });
            } catch (IOException | OrderApiService.OrderException | InterruptedException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText("invalid code");
                });
            }
        }).start();

    }

    @FXML
    private void closeCart(ActionEvent event) {
        CartManager.setAppliedCouponId(null);
        SceneManager.closeCurrentStage(closeButton);
    }

    @FXML
    private void handlePlaceOrder() {
        if (CartManager.getCartItems().isEmpty()) {
            errorLabel.setText("Your cart is empty");
            return;
        }

        // غیرفعال کردن دکمه برای جلوگیری از کلیک‌های تکراری
        placeOrderButton.setDisable(true);
        errorLabel.setText("Submitting order...");

        // اجرای عملیات در یک ترد جدید
        new Thread(() -> {
            try {
                // --- مرحله ۱: ساخت سفارش ---
                String token = TokenManager.getToken();

                // ساخت DTO برای ایجاد سفارش
                List<CartManager.CartItem> cartItems = CartManager.getCartItems();

                // ۲. یک لیست جدید از نوعی که بک‌اند انتظار دارد، می‌سازیم
                ArrayList<OrderDto.OrderItemRequest> orderItems = new ArrayList<>();

                // ۳. در یک حلقه، لیست را به فرمت صحیح تبدیل می‌کنیم
                for (CartManager.CartItem cartItem : cartItems) {
                    orderItems.add(
                            new OrderDto.OrderItemRequest(
                                    cartItem.getFoodItem().getId(), // فقط ID غذا را پاس می‌دهیم
                                    cartItem.getQuantity()          // و تعداد آن را
                            )
                    );
                }

                OrderDto.CreateRequest requestDto = new OrderDto.CreateRequest(
                        CartManager.getBuyerAddress(),
                        CartManager.getCurrentRestaurantId(),
                        CartManager.getAppliedCouponId(),
                        orderItems
                );

                // ارسال درخواست ساخت سفارش
                OrderDto.OrderResponse createdOrder = orderService.submitOrder(token, requestDto);
                Platform.runLater(() -> errorLabel.setText("Order submitted. processing payment..."));

                // --- مرحله ۲: پردازش پرداخت ---
                TransactionDTO.PaymentRequestDTO paymentRequestDto = new TransactionDTO.PaymentRequestDTO(
                        createdOrder.getId(),
                        methodBox.getValue().toUpperCase()
                );

                // ارسال درخواست پرداخت
                orderService.payment(token, paymentRequestDto);

                // --- مرحله ۳: موفقیت نهایی ---
                Platform.runLater(() -> {
                    CartManager.clearCart(); // پاک کردن سبد خرید
                    showSuccessAlert("your order has been placed");
                    SceneManager.closeCurrentStage(placeOrderButton); // بستن پنجره سبد خرید
                });

            } catch (Exception e) {
                // مدیریت هرگونه خطا در فرآیند
                Platform.runLater(() -> {
                    errorLabel.setText(e.getMessage());
                    placeOrderButton.setDisable(false); // فعال کردن دوباره دکمه
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}