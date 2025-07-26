package org.example.snappfoodfront.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.snappfoodfront.Utils.LocalDateAdapter;
import org.example.snappfoodfront.model.OrderDto;
import org.example.snappfoodfront.model.RestaurantDto;
import org.example.snappfoodfront.model.UserLoginDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourierApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
    private final String SERVER_URL = "http://localhost:8080";

    public List<OrderDto.OrderResponse> getAvailableDeliveries(String token) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/deliveries/available"))
                .GET()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return gson.fromJson(response.body(), new TypeToken<>() {});
    }

    public OrderDto.OrderResponse updateDeliveryStatus(String token, Long orderId, OrderDto.OrderStatusChangeRequest statusChangeRequest) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/deliveries/" + orderId))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(gson.toJson(statusChangeRequest)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());

        return gson.fromJson(response.body(), OrderDto.OrderResponse.class);
    }

    public List<OrderDto.OrderResponse> getDeliveryHistory(String token, String search, String vendor, String user, String status) throws IOException, InterruptedException {

        StringBuilder uri = new StringBuilder(SERVER_URL + "/deliveries/history");

        Map<String, String> params = new HashMap<>();

        if (search != null && !search.isEmpty()) {
            params.put("search", search);
        }
        if (vendor != null && !vendor.isEmpty()) {
            params.put("vendor", vendor);
        }
        if (user != null && !user.isEmpty()) {
            params.put("user", user);
        }
        if (status != null && !status.isEmpty()) {
            params.put("status", status);
        }

        if (!params.isEmpty()) {
            uri.append("?");
            boolean isFirst = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!isFirst) {
                    uri.append("&");
                }
                uri.append(entry.getKey()).append("=").append(entry.getValue());
                isFirst = false;
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString()))
                .GET()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return gson.fromJson(response.body(), new TypeToken<>() {});

    }

    public RestaurantDto.Response getRestaurant(String token, Long restaurantId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/restaurants/" + restaurantId))
                .GET()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return gson.fromJson(response.body(), RestaurantDto.Response.class);
    }

    public UserLoginDto.UserData getCustomer(String token, Long customerId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/auth/" + customerId))
                .GET()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return gson.fromJson(response.body(), UserLoginDto.UserData.class);
    }


}
