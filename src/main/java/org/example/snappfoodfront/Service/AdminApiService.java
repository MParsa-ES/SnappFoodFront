package org.example.snappfoodfront.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.snappfoodfront.Utils.LocalDateAdapter;
import org.example.snappfoodfront.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
    private final String SERVER_URL = "http://localhost:8080";


    public List<UserLoginDto.UserData> getAllUsers(String token) throws IOException, InterruptedException, AdminException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/admin/users"))
                .GET()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AdminException(errorResponseDto);
        }

        return gson.fromJson(response.body(), new TypeToken<>() {});
    }

    public MessageDto updateUserApprovalStatus(String token, Long userId, String newStatus) throws InterruptedException, IOException, AdminException {

        AdminDto.UpdateUserApprovalDto updateUserApprovalDto = new AdminDto.UpdateUserApprovalDto(newStatus);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/admin/users/" + userId + "/status"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(gson.toJson(updateUserApprovalDto)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token).build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AdminException(errorResponseDto);
        }

        return gson.fromJson(response.body(), MessageDto.class);
    }

    public MessageDto deleteUser(String token, Long userId) throws InterruptedException, IOException, AdminException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/admin/users/" + userId + "/remove"))
                .DELETE()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AdminException(errorResponseDto);
        }

        return gson.fromJson(response.body(), MessageDto.class);
    }

    public List<OrderDto.OrderResponse> getAllOrders(String token, String search, String vendor, String courier, String customer, String status) throws InterruptedException, IOException, AdminException {

        StringBuilder uri = new StringBuilder(SERVER_URL + "/admin/orders");

        Map<String, String> params = new HashMap<>();

        if (search != null && !search.isEmpty()) {
            params.put("search", search);

        }
        if (vendor != null && !vendor.isEmpty()) {
            params.put("vendor", vendor);
        }
        if (courier != null && !courier.isEmpty()) {
            params.put("courier", courier);
        }
        if (customer != null && !customer.isEmpty()) {
            params.put("customer", customer);
        }
        if (status != null && !status.isEmpty()) {
            params.put("status", status);
        }

        if (!params.isEmpty()) {
            uri.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uri.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString()))
                .GET()
                .header("Authorization", "Bearer " + token).build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AdminException(errorResponseDto);
        }

        return gson.fromJson(response.body(), new TypeToken<>() {});

    }

    public List<CouponDto.Response> getAllCoupons(String token) throws InterruptedException, IOException, AdminException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/admin/coupons"))
                .GET()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AdminException(errorResponseDto);
        }

        return gson.fromJson(response.body(), new TypeToken<>() {});
    }

    public MessageDto deleteCoupon(String token, Long couponId) throws InterruptedException, IOException, AdminException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/admin/coupons/" + couponId))
                .DELETE()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AdminException(errorResponseDto);
        }

        return gson.fromJson(response.body(), MessageDto.class);
    }

    public CouponDto.Response createCoupon(String token, CouponDto.Request couponCreateRequest) throws InterruptedException, IOException, AdminException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/admin/coupons"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(couponCreateRequest)))
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AdminException(errorResponseDto);
        }

        return gson.fromJson(response.body(), new TypeToken<>() {});

    }

    public CouponDto.Response updateCoupon(String token, Long couponId, CouponDto.Request couponUpdateRequest) throws InterruptedException, IOException, AdminException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/admin/coupons/" + couponId))
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(couponUpdateRequest)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AdminException(errorResponseDto);
        }

        return gson.fromJson(response.body(), CouponDto.Response.class);
    }

    public List<TransactionDto.PaymentResponseDTO> getTransactions(String token, String search, String user, String method, String status) throws InterruptedException, IOException, AdminException {

        StringBuilder uri = new StringBuilder(SERVER_URL + "/admin/transactions");

        Map<String, String> params = new HashMap<>();

        if (search != null && !search.isEmpty()) {
            params.put("search", search);
        }
        if (user != null && !user.isEmpty()) {
            params.put("user", user);
        }
        if (method != null && !method.isEmpty()) {
            params.put("method", method);
        }
        if (status != null && !status.isEmpty()) {
            params.put("status", status);
        }

        if (!params.isEmpty()) {
            uri.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uri.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString()))
                .GET()
                .header("Authorization", "Bearer " + token).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AdminException(errorResponseDto);
        }

        return gson.fromJson(response.body(), new TypeToken<>() {});
    }

    @Getter
    @AllArgsConstructor
    public static class AdminException extends Exception {
        private ErrorResponseDto errorResponseDto;
        private int errorCode;

        public AdminException(ErrorResponseDto errorResponseDto) {
            super(errorResponseDto.getError());
            this.errorResponseDto = errorResponseDto;
        }
    }
}
