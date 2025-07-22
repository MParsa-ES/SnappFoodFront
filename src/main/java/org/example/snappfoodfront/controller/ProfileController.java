package org.example.snappfoodfront.controller;


import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.tools.Utils;
import org.example.snappfoodfront.Service.AuthApiService;
import org.example.snappfoodfront.Service.ProfileApiService;
import org.example.snappfoodfront.Utils.Methods;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.ProfileDto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML public Circle imageView;
    @FXML public TextField fullNameField;
    @FXML public TextField phoneField;
    @FXML public TextField emailField;
    @FXML public CustomTextField roleField;
    @FXML public VBox bankInfo;
    @FXML public TextField bankNameField;
    @FXML public TextField accountNumberField;
    @FXML public TextArea addressField;
    @FXML public Button imageSelect;
    @FXML public Label imageLabel;
    @FXML public JFXButton confirmButton;
    @FXML public Label errorLabel;
    @FXML public JFXButton goBackButton;
    @FXML public JFXButton logoutButton;

    private final ProfileApiService profileService = new ProfileApiService();

    private String oldPhone;

    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String bankName;
    private String accountNumber;
    private String address;
    private String profileImageBase64;


    private static final String CUSTOMER_MAIN_VIEW_PATH = "/view/customer-main-view.fxml";
    private static final String SELLER_MAIN_VIEW_PATH = "/view/SellerViews/seller-main-view.fxml";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Methods.filterPhoneField(phoneField);
        String token = TokenManager.getToken();
        new Thread(() -> {
            try {
                final ProfileDto profileDto = profileService.getProfile(token);
                id = profileDto.getId();
                oldPhone = profileDto.getPhone();
                profileImageBase64 = profileDto.getProfileImageBase64();
                Image profileImage = Methods.convertToImage(profileImageBase64);
                imageView.setFill(new ImagePattern(profileImage));
                fullNameField.setText(profileDto.getFull_name());
                phoneField.setText(profileDto.getPhone());
                String role = profileDto.getRole();
                roleField.setText(profileDto.getRole());
                if (profileDto.getEmail() != null) {
                    emailField.setText(profileDto.getEmail());
                }
                switch (role) {
                    case "BUYER":
                        addressField.setVisible(true);
                        addressField.setManaged(true);
                        addressField.setText(profileDto.getAddress());

                        bankInfo.setVisible(false);
                        bankInfo.setManaged(false);
                        break;

                    case "COURIER":
                        addressField.setVisible(false);
                        addressField.setManaged(false);

                        bankInfo.setVisible(true);
                        bankInfo.setManaged(true);
                        bankNameField.setText(profileDto.getBank_info().getBank_name());
                        accountNumberField.setText(profileDto.getBank_info().getAccount_number());
                        break;

                    case "SELLER":
                        addressField.setVisible(true);
                        addressField.setManaged(true);
                        addressField.setText(profileDto.getAddress());

                        bankInfo.setVisible(true);
                        bankInfo.setManaged(true);
                        bankNameField.setText(profileDto.getBank_info().getBank_name());
                        accountNumberField.setText(profileDto.getBank_info().getAccount_number());
                        break;
                }
            } catch (AuthApiService.AuthException e) {
                e.printStackTrace();
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> errorLabel.setText("Unable to connect to server"));
            }

        }).start();

    }

    @FXML
    protected void handleConfirm(ActionEvent event) throws AuthApiService.AuthException, IOException, InterruptedException {

        String token = TokenManager.getToken();

        fullName = fullNameField.getText();
        phone = phoneField.getText();
        email = emailField.getText();
        bankName = bankNameField.getText();
        accountNumber = accountNumberField.getText();
        address = addressField.getText();
        String role = roleField.getText();


        if (fullName.isEmpty() || phone.isEmpty() || (bankName.isEmpty() && (role.equals("COURIER") || role.equals("SELLER")))
        || (accountNumber.isEmpty() && (role.equals("COURIER") || role.equals("SELLER"))) || (address.isEmpty() && (role.equals("BUYER") || role.equals("SELLER")))) {
            errorLabel.setText("Please fill all the required fields");
        }


        ProfileDto profileDto = new ProfileDto(id, fullName, phone, email, role, address, profileImageBase64, bankName, accountNumber);

        new Thread(() -> {

            try {
            profileService.updateProfile(token, profileDto);
            Platform.runLater(() -> {
                errorLabel.setTextFill(Color.GREEN);
                errorLabel.setText("Profile updated successfully");
                if (!oldPhone.equals(profileDto.getPhone())) {
                    SceneManager.logout(errorLabel);
                }
            });
            } catch (AuthApiService.AuthException e) {
                e.printStackTrace();
                Platform.runLater(() -> errorLabel.setText(e.getMessage()));

            } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> errorLabel.setText("Unable to connect to server"));
            }

        }).start();

    }

    @FXML
    protected void goBack(ActionEvent event) throws IOException {
        SceneManager.closeCurrentStage(goBackButton);
        if (roleField.getText().equals("BUYER")) {
            SceneManager.showWindow(CUSTOMER_MAIN_VIEW_PATH, "SnappFood", "dashboard", 1050, 720);
        } else if (roleField.getText().equals("SELLER")) {
            SceneManager.showWindow(SELLER_MAIN_VIEW_PATH, "SnappFood", "dashboard", 1050, 720);
        }
    }

    @FXML
    protected void logout(ActionEvent event) throws IOException {
        SceneManager.logout(logoutButton);
    }

    @FXML
    protected void handleImageSelect() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(imageSelect.getScene().getWindow());

        if (selectedFile != null) {
            imageLabel.setText(selectedFile.getName());

            try {
                byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                profileImageBase64 = Base64.getEncoder().encodeToString(fileContent);
                imageView.setFill(new ImagePattern(new ImagePattern(Methods.convertToImage(profileImageBase64)).getImage()));
            } catch (IOException e) {
                imageLabel.setText("Error loading image");
                e.printStackTrace();
            }
        }
    }

}
