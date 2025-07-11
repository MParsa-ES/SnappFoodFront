package org.example.snappfoodfront.Service;

import com.google.gson.Gson;
import lombok.Getter;
import org.example.snappfoodfront.model.ErrorResponseDto;
import org.example.snappfoodfront.model.UserLoginDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String SERVER_URL = "http://localhost:8080";


    public UserLoginDto.Response login(String phoneNumber, String password) throws IOException, InterruptedException, LoginException {

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
            throw new LoginException(errorResponseDto);
        }

        return gson.fromJson(response.body(), UserLoginDto.Response.class);

    }


    @Getter
    public class LoginException extends Exception {

        private ErrorResponseDto errorResponseDto;

        public LoginException(ErrorResponseDto errorResponseDto) {
            super(errorResponseDto.getError());
            this.errorResponseDto = errorResponseDto;
        }
    }

}
