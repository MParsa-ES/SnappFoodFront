package org.example.snappfoodfront.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.snappfoodfront.model.AdminDto;
import org.example.snappfoodfront.model.ErrorResponseDto;
import org.example.snappfoodfront.model.MessageDto;
import org.example.snappfoodfront.model.UserLoginDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AdminApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
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
