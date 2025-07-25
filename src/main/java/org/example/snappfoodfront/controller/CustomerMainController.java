package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.controlsfx.control.textfield.CustomTextField;
import org.example.snappfoodfront.Service.AuthApiService;
import org.example.snappfoodfront.Service.OrderApiService;
import org.example.snappfoodfront.Service.ProfileApiService;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.CartManager;
import org.example.snappfoodfront.Utils.MainViewState;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.BuyerDto;
import org.example.snappfoodfront.model.RestaurantDto;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class CustomerMainController implements Initializable {

    private static final String PROFILE_VIEW_PATH = "/view/profile-view.fxml";

    private final RestaurantApiService restaurantService = new RestaurantApiService();
    private final OrderApiService orderService = new OrderApiService();
    private final ProfileApiService profileService = new ProfileApiService();

    @FXML public JFXButton profileButton;
    @FXML public JFXButton logoutButton;
    @FXML public Label messageLabel;
    @FXML public FontIcon messageIcon;
    @FXML public VBox restaurantContainer;
    @FXML public JFXHamburger hamburger;
    @FXML public Region region;
    @FXML public VBox filterPanel;
    @FXML public TabPane tabPane;
    @FXML public CustomTextField searchBar;
    @FXML public FontIcon searchIcon;
    @FXML public VBox favoriteList;
    @FXML public Tab favoritesTab;

    @FXML public CustomTextField minPriceField;
    @FXML public CustomTextField maxPriceField;
    @FXML public JFXCheckBox kababBox;
    @FXML public JFXCheckBox poloBox;
    @FXML public JFXCheckBox khoreshtBox;
    @FXML public JFXCheckBox daryaiiBox;
    @FXML public JFXCheckBox fastFoodBox;
    @FXML public JFXCheckBox sokhariBox;
    @FXML public JFXCheckBox pizzaBox;
    @FXML public JFXCheckBox burgerBox;
    @FXML public Label walletLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String token = TokenManager.getToken();
        try {
            CartManager.setBuyerAddress(profileService.getProfile(token).getAddress());
        } catch (IOException | InterruptedException | AuthApiService.AuthException e) {
            e.printStackTrace();
        }
        getFavoriteRestaurantIds();
        setWalletBalance();

        if (MainViewState.cameFromFavorites) {

            tabPane.getSelectionModel().select(favoritesTab);
            loadFavoriteRestaurants();

            MainViewState.clearState();
            return;
        }

        if (MainViewState.hasState) {

            List<String> keywords = MainViewState.getLastKeywords();

            searchBar.setText(MainViewState.getLastSearchTerm());
            minPriceField.setText(MainViewState.getLastMinPrice().toString());
            maxPriceField.setText(MainViewState.getLastMaxPrice().toString());
            if (keywords.size() > 0) {
                if (keywords.contains("kebab")) {kababBox.setSelected(true);}
                if (keywords.contains("polo")) {poloBox.setSelected(true);}
                if (keywords.contains("khoresht")) {khoreshtBox.setSelected(true);}
                if (keywords.contains("daryaii")) {daryaiiBox.setSelected(true);}
                if (keywords.contains("fastfood")) {fastFoodBox.setSelected(true);}
                if (keywords.contains("sokhari")) {sokhariBox.setSelected(true);}
                if (keywords.contains("pizza")) {pizzaBox.setSelected(true);}
                if (keywords.contains("burger")) {burgerBox.setSelected(true);}
            }

            filterPanel.setVisible(false);
            filterPanel.setManaged(false);

            try {
                handleSearchAndFilter();
            } catch (IOException | RestaurantApiService.RestaurantException | InterruptedException e) {
                e.printStackTrace();
            }

            MainViewState.clearState();

        } else {
            filterPanel.setVisible(false);
            filterPanel.setManaged(false);
            loadAllRestaurants();
        }

    }

    private void loadAllRestaurants() {

        new Thread(() -> {
            try {

                final List<RestaurantDto.Response> restaurantList = restaurantService.getAllRestaurants();

                Platform.runLater(() -> {

                    restaurantContainer.getChildren().clear();

                    if(restaurantList.isEmpty()) {
                        messageLabel.setTextFill(Color.RED);
                        messageLabel.setFont(Font.font(18));
                        messageLabel.setText("No restaurants found");
                        messageLabel.setStyle("-fx-font-wegiht: bold");
                        messageIcon.setIconLiteral("fas-store-alt-slash");
                    }else {
                        messageLabel.setTextFill(Color.BLACK);
                        messageLabel.setFont(Font.font(14));
                        messageLabel.setText("Found " + restaurantList.size() + " restaurants");
                        messageLabel.setStyle("-fx-font-wegiht: bold");
                        messageIcon.setIconLiteral("fas-store-alt");
                    }

                    listRestaurants(restaurantList, restaurantContainer);

                });

            } catch (Exception e) {
                System.err.println("error while loading restaurants from server" + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    protected void toggleHamburger() {
        if(filterPanel.isVisible()) {
            Platform.runLater(() -> {
                region.setPrefWidth(400);
                filterPanel.setVisible(false);
                filterPanel.setManaged(false);

            });
        } else if (!filterPanel.isVisible()) {
            Platform.runLater(() -> {
                region.setPrefWidth(250);
                filterPanel.setVisible(true);
                filterPanel.setManaged(true);
            });
        }
    }

    @FXML
    protected void handleFavoriteRestaurants() {

        Platform.runLater(() -> {
            region.setPrefWidth(400);
            filterPanel.setVisible(false);
            filterPanel.setManaged(false);

            searchBar.setDisable(true);
            searchIcon.setDisable(true);
        });

        loadFavoriteRestaurants();

    }

    @FXML
    protected void loadFavoriteRestaurants() {

        new Thread(() -> {

            try {
                final List<RestaurantDto.Response> restaurantList = restaurantService.getFavoriteRestaurants(TokenManager.getToken());


                    Platform.runLater(() -> {

                        favoriteList.getChildren().clear();

                        if(restaurantList.isEmpty()) {
                            Label errorLabel = new Label();
                            errorLabel.setText("No restaurants found");
                            errorLabel.setStyle("-fx-font-wegiht: bold");
                            errorLabel.setFont(Font.font(18));
                            favoriteList.getChildren().clear();
                            favoriteList.getChildren().add(errorLabel);
                        }

                        listRestaurants(restaurantList, favoriteList);

                    });
            } catch (IOException | InterruptedException | RestaurantApiService.RestaurantException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Label errorLabel = new Label();
                    errorLabel.setStyle("-fx-font-wegiht: bold");
                    errorLabel.setFont(Font.font(24));
                    errorLabel.setText(e.getMessage());
                    favoriteList.getChildren().add(errorLabel);
                });
            }

        }).start();

    }

    @FXML
    protected void handleSearchAndFilter() throws IOException, RestaurantApiService.RestaurantException, InterruptedException {

        int minPrice = 0;
        int maxPrice = 0;
        if (!minPriceField.getText().isEmpty()) {
            minPrice = Integer.parseInt(minPriceField.getText());
        }
        if (!maxPriceField.getText().isEmpty()) {
            maxPrice = Integer.parseInt(maxPriceField.getText());
        }

        String search = searchBar.getText();

        List<String> keywords = new ArrayList<>();
        if (kababBox.isSelected()) { keywords.add("کباب"); }
        if (poloBox.isSelected()) { keywords.add("پلو"); }
        if (khoreshtBox.isSelected()) { keywords.add("خورشت"); }
        if (daryaiiBox.isSelected()) { keywords.add("دریایی"); }
        if (fastFoodBox.isSelected()) { keywords.add("فست فود"); }
        if (sokhariBox.isSelected()) { keywords.add("سوخاری"); }
        if (pizzaBox.isSelected()) { keywords.add("پیتزا"); }
        if (burgerBox.isSelected()) { keywords.add("برگر"); }

        BuyerDto.ItemSearch request = new BuyerDto.ItemSearch(search, minPrice, maxPrice, keywords);

        new Thread(() -> {

            try {
                final List<RestaurantDto.Response> restaurantList = restaurantService.searchRestaurants(request);

                Platform.runLater(() -> {
                    restaurantContainer.getChildren().clear();

                    if(restaurantList.isEmpty()) {
                        Label errorLabel = new Label();
                        errorLabel.setText("No restaurants found");
                        errorLabel.setStyle("-fx-font-wegiht: bold");
                        errorLabel.setFont(Font.font(18));
                        restaurantContainer.getChildren().add(errorLabel);
                    }

                    listRestaurants(restaurantList, restaurantContainer);
                });
            } catch (RestaurantApiService.RestaurantException | IOException | InterruptedException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Label errorLabel = new Label();
                    errorLabel.setStyle("-fx-font-wegiht: bold");
                    errorLabel.setFont(Font.font(24));
                    errorLabel.setText(e.getMessage());
                    restaurantContainer.getChildren().clear();
                    restaurantContainer.getChildren().add(errorLabel);
                });
            }

        }).start();

    }

    private void listRestaurants(List<RestaurantDto.Response> restaurantList, VBox restaurantContainer) {
        for (RestaurantDto.Response restaurant : restaurantList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/restaurant-card.fxml"));
                Node restaurantCardNode = loader.load();
                final RestaurantDto.Response currentRestaurant = restaurant;

                RestaurantMainCardController cardController = loader.getController();
                cardController.setData(currentRestaurant);

                restaurantContainer.getChildren().add(restaurantCardNode);
                restaurantCardNode.setOnMouseClicked(event -> {
                    try {
                        handleRestaurantClicked(restaurantCardNode, currentRestaurant);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                System.err.println("error while loading restaurant's card" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void handleRestaurantClicked(Node restaurantNode, RestaurantDto.Response restaurant) throws IOException {

        int minPrice = 0;
        int maxPrice = 0;
        if (!minPriceField.getText().isEmpty()) {
            minPrice = Integer.parseInt(minPriceField.getText());
        }
        if (!maxPriceField.getText().isEmpty()) {
            maxPrice = Integer.parseInt(maxPriceField.getText());
        }
        String search = searchBar.getText();
        List<String> keywords = new ArrayList<>();
        if (kababBox.isSelected()) { keywords.add("کباب"); }
        if (poloBox.isSelected()) { keywords.add("پلو"); }
        if (khoreshtBox.isSelected()) { keywords.add("خورشت"); }
        if (daryaiiBox.isSelected()) { keywords.add("دریایی"); }
        if (fastFoodBox.isSelected()) { keywords.add("فست فود"); }
        if (sokhariBox.isSelected()) { keywords.add("سوخاری"); }
        if (pizzaBox.isSelected()) { keywords.add("پیتزا"); }
        if (burgerBox.isSelected()) { keywords.add("برگر"); }

        boolean isFilterActive = (search != null && !search.isBlank()) ||
                (minPrice > 0) || (maxPrice > 0) || (!keywords.isEmpty());


        if (isFilterActive) {
            MainViewState.saveState(search, minPrice, maxPrice, keywords);
        }

        boolean isFavorite = MainViewState.getFavoriteRestaurantIds().contains(restaurant.getId());

        MainViewState.setCameFromFavorites(isFavorite);
        MainViewState.setSelectedRestaurant(restaurant);
        SceneManager.closeCurrentStage(restaurantNode);
        SceneManager.showWindow("/view/restaurant-main-view.fxml", "SnappFood", "SnappFood", 1050, 720);

    }

    @FXML
    protected void handleMainPage() {
        getFavoriteRestaurantIds();
        Platform.runLater(() -> {
            region.setPrefWidth(410);

            searchBar.setDisable(false);
            searchIcon.setDisable(false);
            loadAllRestaurants();
        });
    }
    private void setWalletBalance() {
        try {
            CartManager.setWalletBalance(orderService.getWalletBalance(TokenManager.getToken()));
            Platform.runLater(() -> {
                walletLabel.setText("Wallet Balance: " + CartManager.getWalletBalance() + " T");
            });
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Platform.runLater(() -> walletLabel.setText(e.getMessage()));
        }
    }

    private void getFavoriteRestaurantIds() {
        String token = TokenManager.getToken();
        new Thread(() -> {
            try {
                final List<RestaurantDto.Response> restaurants = restaurantService.getFavoriteRestaurants(token);
                for (RestaurantDto.Response restaurant : restaurants) {
                    MainViewState.favoriteRestaurantIds.add(restaurant.getId());
                }
            } catch (IOException | RestaurantApiService.RestaurantException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    protected void goToProfile(ActionEvent event) throws IOException {
        SceneManager.closeCurrentStage(profileButton);
        Platform.runLater(() -> SceneManager.showWindow(PROFILE_VIEW_PATH, "Profile", "profile", 1050, 720));
    }

    @FXML
    protected void logout(ActionEvent event) throws IOException {
        SceneManager.logout(logoutButton);
    }

}
