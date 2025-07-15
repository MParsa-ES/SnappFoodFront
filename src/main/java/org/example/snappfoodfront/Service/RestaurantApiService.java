package org.example.snappfoodfront.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.snappfoodfront.model.ErrorResponseDto;
import org.example.snappfoodfront.model.RestaurantDto;

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

        return List.of(gson.fromJson(response.body(), listType));

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
