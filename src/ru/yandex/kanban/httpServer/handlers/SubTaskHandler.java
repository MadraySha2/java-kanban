package ru.yandex.kanban.httpServer.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.managers.taskManger.TaskManager;
import ru.yandex.kanban.model.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import static ru.yandex.kanban.utils.WriteResponseUtil.writeResponse;
public class SubTaskHandler implements HttpHandler {
    TaskManager taskManager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson = new Gson();
    String response;

    public SubTaskHandler(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getSubTask(exchange);
                break;
            case "POST":
                addSubTask(exchange);
                break;
            case "DELETE":
                deleteSubTask(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);
        }
    }

    private void getSubTask(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            response = gson.toJson(taskManager.getAllSubTasks());
            writeResponse(exchange, response, 200);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор!", 400);
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getSubTaskById(id) !=null) {
            response = gson.toJson(taskManager.getSubTaskById(id));
        } else {
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
        }
        writeResponse(exchange, response, 200);
    }


    private void addSubTask(HttpExchange exchange) throws IOException {
        try {
            InputStream json = exchange.getRequestBody();
            String jsonTask = new String(json.readAllBytes(), DEFAULT_CHARSET);
            SubTask subTask = gson.fromJson(jsonTask, SubTask.class);
            if (subTask == null) {
                writeResponse(exchange, "Задача не должна быть пустой!", 400);
                return;
            }
            if (subTask.getId() == null) {
                taskManager.addSubTask(subTask);
                writeResponse(exchange, "Задача успешно добавлена!", 201);

            }
            if (taskManager.getSubTaskById(subTask.getId()) != null) {
                taskManager.updateSubTask(subTask);
                writeResponse(exchange, "Сабтаск обновлен!", 218);
            }


        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void deleteSubTask(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            taskManager.deleteAllSubTasks();
            writeResponse(exchange, "Сабы успешно удалены!", 200);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getSubTaskById(id) == null) {
            writeResponse(exchange, "Сабов с таким id не найдено!", 404);
            return;
        }
        taskManager.deleteSubTask(id);
        writeResponse(exchange, "Саб успешно удален!", 200);
    }

    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getQuery().split("=");
        try {
            return Optional.of(Integer.parseInt(pathParts[1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }



}
