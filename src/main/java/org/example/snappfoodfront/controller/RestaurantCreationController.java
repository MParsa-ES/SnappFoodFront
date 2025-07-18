package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import lombok.Setter;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.Methods;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.RestaurantDto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;

public class RestaurantCreationController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField additionalFeeTextField;

    @FXML
    private TextArea addressTextField;

    @FXML
    private Button chooseLogoButton;

    @FXML
    private HBox feedbackBox;

    @FXML
    private Label feedbackLabel;

    @FXML
    private ImageView logoImage;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField taxFeeTextField;

    @FXML
    private Button createRestaurantButton;


    private String logoBase64;

    private final RestaurantApiService restaurantApiService;


    @Setter
    private static RestaurantDto.Response  restaurantToEdit;


    public RestaurantCreationController() {
        this.restaurantApiService = new RestaurantApiService();
    }


    private String loadDefaultLogoBase64() throws IOException {

        String defaultImagePath = "/images/default-logo.jpg";

        try (InputStream inputStream = getClass().getResourceAsStream(defaultImagePath)) {

            if (inputStream == null) {
                throw new IOException("Cannot find default avatar image at path: " + defaultImagePath);
            }

            byte[] fileContent = inputStream.readAllBytes();
            logoImage.setImage(new Image(new ByteArrayInputStream(fileContent)));
            return Base64.getEncoder().encodeToString(fileContent);
        }
    }


    @FXML
    private void handleChoosingLogo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Logo Image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(chooseLogoButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                logoBase64 = Base64.getEncoder().encodeToString(fileContent);
                logoImage.setImage(new Image(new ByteArrayInputStream(fileContent)));

            } catch (IOException e) {
                feedbackLabel.setTextFill(Color.RED);
                feedbackLabel.setManaged(true);
                feedbackLabel.setVisible(true);
                feedbackLabel.setText("Error loading image");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCreateRestaurant(ActionEvent event) {

        String name = nameTextField.getText();
        String phone = phoneTextField.getText();
        String taxFee = taxFeeTextField.getText();
        String additionalFee = additionalFeeTextField.getText();
        String address = addressTextField.getText();


        if (name.isBlank()) {
            showFeedback("Please enter a valid name", true);
            return;
        } else if (phone.isBlank()) {
            showFeedback("Please enter a valid phone number", true);
            return;
        } else if (address.isBlank()) {
            showFeedback("Please enter a valid address", true);
            return;
        } else if (additionalFee.isBlank()) {
            showFeedback("Please enter a valid additional fee", true);
            return;
        } else if (taxFee.isBlank()) {
            showFeedback("Please enter a valid tax fee", true);
            return;
        }

        RestaurantDto.Request requestDto = new RestaurantDto.Request();
        requestDto.setName(name);
        requestDto.setPhone(phone);
        requestDto.setAddress(address);

        requestDto.setTax_fee(Integer.parseInt(taxFee));
        requestDto.setAdditional_fee(Integer.parseInt(additionalFee));

        requestDto.setLogoBase64(logoBase64);

        new Thread(() -> {
            try {

                if (restaurantToEdit == null) {
                    restaurantApiService.addRestaurant(TokenManager.getToken(), requestDto);
                    cleanup();
                    Platform.runLater(() -> {
                        showFeedback("Restaurant Created", false);
                        SceneManager.closeCurrentStage((Node) event.getSource());
                        SceneManager.showWindow("/view/SellerViews/seller-main-view.fxml", "SnappFood", "Main Seller view", 1024, 720);
                    });


                } else {
                    restaurantApiService.updateRestaurant(TokenManager.getToken(), restaurantToEdit.getId(), requestDto);
                    Platform.runLater(() -> {
                        showFeedback("Restaurant Edited", false);
                        SceneManager.closeCurrentStage((Node) event.getSource());
                        SceneManager.showWindow("/view/SellerViews/seller-main-view.fxml", "SnappFood", "Main Seller view", 1024, 720);
                    });

                }



            } catch (RestaurantApiService.RestaurantException e) {
                Platform.runLater(() -> showFeedback(e.getErrorResponseDto().getError(), true));

                e.printStackTrace();
                System.err.println("Restaurant creation or failed");
            } catch (Exception e) {

                Platform.runLater(() -> showFeedback("System error while creating or editing the restaurant", true));

                e.printStackTrace();
                System.err.println("Restaurant creation or edit failed with exception");
            }
        }).start();

    }

    public void initialize(URL location, ResourceBundle resources) {

        // making so that theses fields only accept numbers
        Methods.filterPhoneField(phoneTextField);
        Methods.filterPhoneField(additionalFeeTextField);
        Methods.filterPhoneField(taxFeeTextField);


        // adding a circle to the restaurant logo
        Circle clip = new Circle(40, 40, 40);
        logoImage.setClip(clip);


        // binding the activation of button to fields
        BooleanBinding formIsInvalid =
                nameTextField.textProperty().isEmpty()
                        .or(phoneTextField.textProperty().isEmpty())
                        .or(addressTextField.textProperty().isEmpty())
                        .or(taxFeeTextField.textProperty().isEmpty())
                        .or(additionalFeeTextField.textProperty().isEmpty());

        createRestaurantButton.disableProperty().bind(formIsInvalid);

        if (restaurantToEdit != null) {

            titleLabel.setText("Edit Restaurant");
            createRestaurantButton.setText("Save Changes");


            nameTextField.setText(restaurantToEdit.getName());
            phoneTextField.setText(restaurantToEdit.getPhone());
            addressTextField.setText(restaurantToEdit.getAddress());
            taxFeeTextField.setText(String.valueOf(restaurantToEdit.getTax_fee()));
            additionalFeeTextField.setText(String.valueOf(restaurantToEdit.getAdditional_fee()));

            byte[] imageBytes = Base64.getDecoder().decode(restaurantToEdit.getLogoBase64());
            logoImage.setImage(new Image(new ByteArrayInputStream(imageBytes)));


        } else {

            titleLabel.setText("Create a New Restaurant");
            createRestaurantButton.setText("Create Restaurant");
            try {
                logoBase64 = loadDefaultLogoBase64();
            } catch (IOException e) {
                logoBase64 = "";
                e.printStackTrace();
                System.err.println("Error loading default logo");
            }
        }

    }


    private void showFeedback(String message, boolean isError) {
        feedbackLabel.setText(message);


        feedbackBox.getStyleClass().removeAll("feedback-box-success", "feedback-box-error");
        if (isError) {
            feedbackBox.getStyleClass().add("feedback-box-error");
        } else {
            feedbackBox.getStyleClass().add("feedback-box-success");
        }


        feedbackBox.setVisible(true);
        feedbackBox.setManaged(true);
    }

    private void cleanup() {
        restaurantToEdit = null;
    }


}
