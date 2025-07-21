package org.example.snappfoodfront.Utils;

import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


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
    private static Long selectedRestaurantId;
    @Getter
    @Setter
    public static boolean hasState = false;
    @Getter
    @Setter
    public static boolean cameFromFavorites = false;

    public static void saveState(String searchTerm, Integer minPrice, Integer maxPrice, List<String> keywords, Long restaurantId) {
        lastSearchTerm = searchTerm;
        lastMinPrice = minPrice;
        lastMaxPrice = maxPrice;
        lastKeywords = keywords;
        selectedRestaurantId = restaurantId;
        hasState = true;
    }

    public static void clearState() {
        lastSearchTerm = null;
        lastMinPrice = null;
        lastMaxPrice = null;
        lastKeywords = null;
        selectedRestaurantId = null;
        cameFromFavorites = false;
        hasState = false;
    }

}