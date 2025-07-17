package org.example.snappfoodfront.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.example.snappfoodfront.Service.AuthApiService;
import org.example.snappfoodfront.Utils.Methods;
import org.example.snappfoodfront.Utils.RegistrationContext;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.UserRegisterDto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;

public class ProfileCompletionController implements Initializable {

    @FXML public TextField fullNameField;
    @FXML public TextField emailField;
    @FXML public VBox bankInfo;
    @FXML public TextField bank_name;
    @FXML public TextField account_number;
    @FXML public TextArea addressField;
    @FXML public Button imageSelect;
    @FXML public Label imageLabel;
    @FXML public Button SignUpButton;
    @FXML public Label errorLabel;

    private final AuthApiService authService = new AuthApiService();

    private static final String DASHBOARD_VIEW_PATH = "/view/customer-main-view.fxml";

    private String fullName;
    private String email;
    private String bankName;
    private String accountNumber;
    private String address;
    private String profileImageBase64;
    private String phone;
    private String password;
    private String role;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            profileImageBase64 = loadDefaultImageBase64();
        } catch (IOException e) {
            imageLabel.setText("Error");
            e.printStackTrace();
        }

        role = RegistrationContext.getRole();

        switch (role) {
            case "BUYER":
                addressField.setVisible(true);
                addressField.setManaged(true);

                bankInfo.setVisible(false);
                bankInfo.setManaged(false);
                break;

            case "COURIER":
                addressField.setVisible(false);
                addressField.setManaged(false);

                bankInfo.setVisible(true);
                bankInfo.setManaged(true);
                break;

            case "SELLER":
                addressField.setVisible(true);
                addressField.setManaged(true);

                bankInfo.setVisible(true);
                bankInfo.setManaged(true);
                break;
        }
    }

    private String loadDefaultImageBase64() throws IOException {

        String defaultImagePath = "/images/default-avatar.png";

        try (InputStream inputStream = getClass().getResourceAsStream(defaultImagePath)) {

            if (inputStream == null) {
                throw new IOException("Cannot find default avatar image at path: " + defaultImagePath);
            }

            byte[] fileContent = inputStream.readAllBytes();
            return Base64.getEncoder().encodeToString(fileContent);
        }
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
            } catch (IOException e) {
                imageLabel.setText("Error loading image");
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void handleSignUp(ActionEvent event) throws IOException, InterruptedException, AuthApiService.AuthException {

        fullName = fullNameField.getText();
        address = addressField.getText();
        phone = RegistrationContext.getPhone();
        password = RegistrationContext.getPassword();
        role = RegistrationContext.getRole();
        if (!account_number.getText().isEmpty()) {accountNumber = account_number.getText();}
        if  (!bank_name.getText().isEmpty()) {bankName = bank_name.getText();}
        if (!emailField.getText().isEmpty()) {email = emailField.getText();}

        UserRegisterDto.Request requestDto = new UserRegisterDto.Request(
                fullName,
                phone,
                email,
                password,
                role,
                address,
                profileImageBase64,
                new UserRegisterDto.Request.BankInfoDto(bankName, accountNumber)
        );

        try {
            UserRegisterDto.Response response = authService.signUp(requestDto);
            String token = response.getToken();
            TokenManager.clearToken();
            TokenManager.saveToken(token);
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Sign up successful");
            SceneManager.closeCurrentStage(errorLabel);
            SceneManager.showWindow(DASHBOARD_VIEW_PATH, "SnappFood", "dashboard", 1024, 720);
        } catch (IOException | InterruptedException e) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Sign up failed");
        } catch (AuthApiService.AuthException e) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText(e.getMessage());
        }

    }


}
