package org.example.snappfoodfront.Service;

import com.google.gson.Gson;
import org.example.snappfoodfront.model.ErrorResponseDto;
import org.example.snappfoodfront.model.ProfileDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProfileApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String SERVER_URL = "http://localhost:8080";

    public ProfileDto getProfile(String token) throws IOException, InterruptedException, AuthApiService.AuthException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/auth/profile"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AuthApiService.AuthException(errorResponseDto);
        }

        return gson.fromJson(response.body(), ProfileDto.class);

    }

    public void updateProfile(String token, ProfileDto profile) throws IOException, InterruptedException, AuthApiService.AuthException {

        String requestBody = gson.toJson(profile);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/auth/profile"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AuthApiService.AuthException(errorResponseDto);
        }


    }

}
