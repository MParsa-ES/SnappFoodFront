package org.example.snappfoodfront.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.snappfoodfront.Utils.ItemListDeserializer;
import org.example.snappfoodfront.model.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RestaurantApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String SERVER_URL = "http://localhost:8080";


    public List<RestaurantDto.Response> getMyRestaurants(String token) throws IOException, InterruptedException, RestaurantException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants/mine"))
                .header("Authorization", "Bearer " + token)
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        Type listType = new TypeToken<List<RestaurantDto.Response>>(){}.getType();

        return gson.fromJson(response.body(), listType);

    }


    public RestaurantDto.Response addRestaurant(String token, RestaurantDto.Request restaurantDto) throws RestaurantException, IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(restaurantDto)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        return gson.fromJson(response.body(), RestaurantDto.Response.class);

    }

    public RestaurantDto.Response updateRestaurant(String token, Long restaurantId, RestaurantDto.Request restaurantDto) throws RestaurantException, IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants/" + restaurantId))
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(restaurantDto)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        return gson.fromJson(response.body(), RestaurantDto.Response.class);

    }

    public List<RestaurantDto.Response> getAllRestaurants() throws IOException, RestaurantException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/vendors/all"))
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        return gson.fromJson(response.body(), new TypeToken<List<RestaurantDto.Response>>(){}.getType());

    }

    public List<FoodItemDto.Response> getAllFoodItems(String token, Long restaurantId) throws IOException, RestaurantException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants/" + restaurantId + "/items"))
                .GET()
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        return gson.fromJson(response.body(), new TypeToken<List<FoodItemDto.Response>>(){}.getType());
    }

    public MessageDto deleteFoodItem(String token, Long restaurantId, Long foodItemId) throws IOException, RestaurantException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants/" + restaurantId + "/item/" + foodItemId))
                .DELETE()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        return gson.fromJson(response.body(), MessageDto.class);

    }

    public FoodItemDto.Response addFoodItem(String token, Long restaurantId, FoodItemDto.Request foodItemDto) throws RestaurantException, IOException, InterruptedException {


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants/" + restaurantId + "/item"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(foodItemDto)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        return gson.fromJson(response.body(), FoodItemDto.Response.class);

    }

    public FoodItemDto.Response editFoodItem(String token, Long restaurantId, Long foodId, FoodItemDto.Request foodItemDto) throws RestaurantException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants/" + restaurantId + "/item/" + foodId))
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(foodItemDto)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        return gson.fromJson(response.body(), FoodItemDto.Response.class);

    }

    public BuyerDto.ItemList getMenusWithItems(String token, Long restaurantId) throws IOException, RestaurantException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/vendors/" + restaurantId))
                .GET()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        String responseBody = response.body();

        Gson customGson = new GsonBuilder()
                .registerTypeAdapter(BuyerDto.ItemList.class, new ItemListDeserializer())
                .create();

        return customGson.fromJson(responseBody, BuyerDto.ItemList.class);
    }

    public MenuDto.Response addMenu(String token, Long restaurantId, MenuDto.Request menuDto) throws IOException, RestaurantException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants/" + restaurantId + "/menu"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(menuDto)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        return gson.fromJson(response.body(), MenuDto.Response.class);
    }

    public MessageDto deleteMenu(String token, Long restaurantId, String menuTitle) throws IOException, RestaurantException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants/" + restaurantId + "/menu/" + menuTitle))
                .DELETE()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        return gson.fromJson(response.body(), MessageDto.class);

    }

    public MessageDto addFoodToMenu(String token, Long restaurantId, String menuTitle, MenuDto.AddItemRequest itemRequest) throws IOException, RestaurantException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants/" + restaurantId + "/menu/" + menuTitle))
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(itemRequest)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        return gson.fromJson(response.body(), MessageDto.class);

    }

    public MessageDto deleteFoodFromMenu(String token, Long restaurantId, String menuTitle, Long itemId) throws IOException, RestaurantException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants/" + restaurantId + "/menu/" + menuTitle + "/" + itemId))
                .DELETE()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RestaurantException(errorResponseDto);
        }

        return gson.fromJson(response.body(), MessageDto.class);

    }

    @Getter
    @AllArgsConstructor
    public static class RestaurantException extends Exception {
        private ErrorResponseDto errorResponseDto;
        private int errorCode;

        public RestaurantException(ErrorResponseDto errorResponseDto) {
            super(errorResponseDto.getError());
            this.errorResponseDto = errorResponseDto;
        }
    }

}
