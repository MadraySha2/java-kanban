package ru.yandex.kanban.httpServer;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private static final KVServer kvServer;


    static {
        try {
            kvServer = new KVServer();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpTaskServer httpTS;
    ;
    private static Gson gson;

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        kvServer.start();
        httpTS = new HttpTaskServer();
        httpTS.start();
        gson = new Gson();
    }

    @AfterAll
    static void tearDown() {
        httpTS.stop();
        kvServer.stop();
    }

    @Test
    void addTasksToTaskServerOrUpdate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Task task1 = new Task("Task1", "Task1 description", Status.NEW);
        task1.setId(1);
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        epic1.setId(2);
        int epicId = epic1.getId();
        SubTask sub1_1 = new SubTask("SubTask1 - 1", "SubTask1-1 description", Status.NEW, epicId);
        sub1_1.setId(3);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(218, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/epic/");
        json = gson.toJson(epic1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(sub1_1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response2 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(218, response2.statusCode());


        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response1 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(218, response1.statusCode());


    }

    @Test
    void getAllTasksAndTasks_byId() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals("Task1", task.getTitle());

        url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());


        url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        request1 = HttpRequest.newBuilder().uri(url).GET().build();
        response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
        Epic epic = gson.fromJson(response1.body(), Epic.class);
        assertEquals("Epic1", epic.getTitle());


        url = URI.create("http://localhost:8080/tasks/subtasck/");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());


        url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        request2 = HttpRequest.newBuilder().uri(url).GET().build();
        response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
        SubTask subTask = gson.fromJson(response2.body(), SubTask.class);
        assertEquals("SubTask1 - 1", subTask.getTitle());
    }

    @Test
    void deleteTaskAll_byId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Task task1 = new Task("Task1", "Task1 description", Status.NEW);
        task1.setId(1);
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        epic1.setId(2);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        json = gson.toJson(epic1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача успешно удалена!", response1.body());

        url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задачи успешно удалены!", response2.body());

        url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals("Эпик успешно удален!", response3.body());

        url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals("Эпики успешно удалены!", response4.body());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
        assertEquals("Сабы успешно удалены!", response6.body());

    }
}