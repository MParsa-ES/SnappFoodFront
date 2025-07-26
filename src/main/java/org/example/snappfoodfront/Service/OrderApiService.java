package org.example.snappfoodfront.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.snappfoodfront.Utils.LocalDateAdapter;
import org.example.snappfoodfront.model.CouponDto;
import org.example.snappfoodfront.model.ErrorResponseDto;
import org.example.snappfoodfront.model.OrderDto;
import org.example.snappfoodfront.model.TransactionDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class OrderApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
    private final String SERVER_URL = "http://localhost:8080";

    public BigDecimal getWalletBalance(String token) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/wallet/balance"))
                .header("Authorization", "Bearer " + token)
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return gson.fromJson(response.body(), BigDecimal.class);

    }

    public void topUpWallet(String token, BigDecimal amount) throws IOException, InterruptedException, OrderException {

        TransactionDTO.TopUpRequestDTO topUpRequestDTO = new TransactionDTO.TopUpRequestDTO(amount);
        String requestBody = gson.toJson(topUpRequestDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/wallet/top-up"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new OrderException(errorResponseDto);
        }

    }

    public CouponDto.Response checkCoupon(String token, String coupon_code) throws IOException, InterruptedException, OrderException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/coupon?coupon_code=" + coupon_code))
                .header("Authorization", "Bearer " + token)
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new OrderException(errorResponseDto);
        }

        return gson.fromJson(response.body(), CouponDto.Response.class);

    }

    public OrderDto.OrderResponse submitOrder(String token, OrderDto.CreateRequest requestDto) throws IOException, InterruptedException, OrderException {

        String requestBody = gson.toJson(requestDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/orders"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestDto)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new OrderException(errorResponseDto);
        }

        return gson.fromJson(response.body(), OrderDto.OrderResponse.class);

    }

    public TransactionDTO.PaymentResponseDTO payment(String token, TransactionDTO.PaymentRequestDTO requestDto) throws IOException, InterruptedException, OrderException {

        String requestBody = gson.toJson(requestDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/payment/online"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new OrderException(errorResponseDto);
        }

        return gson.fromJson(response.body(), TransactionDTO.PaymentResponseDTO.class);

    }

    public OrderDto.NamesResponse getNames(OrderDto.NamesRequest requestDto) throws IOException, InterruptedException, OrderException {

        String requestBody = gson.toJson(requestDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/orders/names"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new OrderException(errorResponseDto);
        }

        return gson.fromJson(response.body(), OrderDto.NamesResponse.class);

    }

    public List<OrderDto.OrderResponse> getOrderHistory(String token, String vendorName, String searchQuery) throws IOException, InterruptedException, OrderException {

        StringBuilder urlBuilder = new StringBuilder(SERVER_URL + "/orders/history");
        List<String> params = new ArrayList<>();

        if (vendorName != null && !vendorName.isBlank()) {
            params.add("vendor=" + URLEncoder.encode(vendorName, StandardCharsets.UTF_8));
        }
        if (searchQuery != null && !searchQuery.isBlank()) {
            params.add("search=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8));
        }

        if (!params.isEmpty()) {
            urlBuilder.append("?").append(String.join("&", params));
        }

        String finalUrl = urlBuilder.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(finalUrl))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new OrderException(errorResponseDto);
        }

        return gson.fromJson(response.body(), new TypeToken<List<OrderDto.OrderResponse>>(){}.getType());
    }

    @Getter
    @AllArgsConstructor
    public static class OrderException extends Exception {
        private ErrorResponseDto errorResponseDto;
        private int errorCode;

        public OrderException(ErrorResponseDto errorResponseDto) {
            super(errorResponseDto.getError());
            this.errorResponseDto = errorResponseDto;
        }
    }

}
