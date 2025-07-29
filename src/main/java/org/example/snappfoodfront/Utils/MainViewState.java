package org.example.snappfoodfront.Utils;

import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import org.example.snappfoodfront.model.FoodItemDto;
import org.example.snappfoodfront.model.RestaurantDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainViewState {
    @Getter
    @Setter
    private static String lastSearchTerm;
    @Getter
    @Setter
    private static Integer lastMinPrice;
    @Getter
    @Setter
    private static Integer lastMaxPrice;
    @Getter
    @Setter
    private static List<String> lastKeywords;
    @Getter
    @Setter
    private static RestaurantDto.Response selectedRestaurant;
    @Getter
    @Setter
    public static boolean hasState = false;
    @Getter
    @Setter
    public static boolean cameFromFavorites = false;
    @Getter
    @Setter
    public static Set<Long> favoriteRestaurantIds = new HashSet<>();
    @Getter
    @Setter
    public static FoodItemDto.Response selectedFoodItem;
    @Getter
    @Setter
    private static Long selectedOrderId;
    @Getter
    @Setter
    private static Long ratingId;
    @Getter
    @Setter
    private static boolean isEditingReview = false;

    public static void saveState(String searchTerm, Integer minPrice, Integer maxPrice, List<String> keywords) {
        lastSearchTerm = searchTerm;
        lastMinPrice = minPrice;
        lastMaxPrice = maxPrice;
        lastKeywords = keywords;
        hasState = true;
    }

    public static void clearState() {
        lastSearchTerm = null;
        lastMinPrice = null;
        lastMaxPrice = null;
        lastKeywords = null;
        selectedRestaurant = null;
        cameFromFavorites = false;
        hasState = false;
    }

}