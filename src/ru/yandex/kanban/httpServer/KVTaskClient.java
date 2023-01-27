package ru.yandex.kanban.httpServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {
    private final String apiToken;

    private final String URL;

    public KVTaskClient(String serverURL) throws IOException, InterruptedException {
        this.URL = serverURL;

        URI uri = URI.create(this.URL + "/register");


        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString()
        );
        apiToken = response.body();
    }

    public void put(String key, String json) {
        URI uri = URI.create(this.URL + "/save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() != 201) {
                System.out.println("Ошибка сохранения! Код ответа: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JsonArray load(String key) {
        URI uri = URI.create(this.URL + "/load/" + key + "?API_TOKEN=" + apiToken);
        JsonArray loadedArray;

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() != 200) {
                System.out.println("Ошибка загрузки! Код ответа: " + response.statusCode());
                return null;
            }
            loadedArray = JsonParser.parseString(response.body()).getAsJsonArray();
            return loadedArray;
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время запроса произошла ошибка");
            return null;
        }
    }
}
