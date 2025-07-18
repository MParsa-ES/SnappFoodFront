package org.example.snappfoodfront.Service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.snappfoodfront.model.ErrorResponseDto;
import org.example.snappfoodfront.model.ProfileDto;
import org.example.snappfoodfront.model.UserLoginDto;
import org.example.snappfoodfront.model.UserRegisterDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String SERVER_URL = "http://localhost:8080";

    public void validatePhone(String phone) throws IOException, InterruptedException, AuthException {

        UserRegisterDto.ValidateRequest validateRequest = new UserRegisterDto.ValidateRequest(phone);
        String requestBody = gson.toJson(validateRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/auth/validate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AuthException(errorResponseDto, response.statusCode());
        }

    }

    public UserRegisterDto.Response signUp(UserRegisterDto.Request requestDto) throws IOException, InterruptedException, AuthException {

        String requestBody = gson.toJson(requestDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AuthException(errorResponseDto);
        }

        return gson.fromJson(response.body(), UserRegisterDto.Response.class);

    }

    public UserLoginDto.Response login(String phoneNumber, String password) throws IOException, InterruptedException, AuthException {

        UserLoginDto.Request requestDto = new UserLoginDto.Request(phoneNumber, password);
        String requestBody = gson.toJson(requestDto);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AuthException(errorResponseDto);
        }

        return gson.fromJson(response.body(), UserLoginDto.Response.class);

    }

    public void logout(String token) throws IOException, InterruptedException, AuthException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/auth/logout"))
                .header("Authorization", token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            ErrorResponseDto errorResponseDto = gson.fromJson(response.body(), ErrorResponseDto.class);
            throw new AuthException(errorResponseDto);
        }

    }

    @Getter
    @AllArgsConstructor
    public static class AuthException extends Exception {
        private ErrorResponseDto errorResponseDto;
        private int errorCode;

        public AuthException(ErrorResponseDto errorResponseDto) {
            super(errorResponseDto.getError());
            this.errorResponseDto = errorResponseDto;
        }
    }

}
