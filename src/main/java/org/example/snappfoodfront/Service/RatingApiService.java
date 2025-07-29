package org.example.snappfoodfront.Service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.snappfoodfront.model.ErrorResponseDto;
import org.example.snappfoodfront.model.RatingDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RatingApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String SERVER_URL = "http://localhost:8080";

    public boolean hasOrderBeenReviewed(String token, Long orderId) throws IOException, InterruptedException, RatingException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/ratings/" + orderId + "/check"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to check review status");
        }
        return gson.fromJson(response.body(), Boolean.class);
    }

    public void submitRating(String token, RatingDTO.Request requestDto) throws IOException, InterruptedException, RatingException {
        String requestBody = gson.toJson(requestDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/ratings"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RatingException(errorDto);
        }

    }

    public RatingDTO.ItemRatings getComments(String token, Long itemId) throws IOException, InterruptedException, RatingException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/ratings/items/" + itemId))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new RatingException(errorDto);
        }

        return gson.fromJson(response.body(), RatingDTO.ItemRatings.class);

    }

    @Getter
    @AllArgsConstructor
    public static class RatingException extends Exception {
        private ErrorResponseDto errorResponseDto;
        private int errorCode;

        public RatingException(ErrorResponseDto errorResponseDto) {
            super(errorResponseDto.getError());
            this.errorResponseDto = errorResponseDto;
        }
    }

}
