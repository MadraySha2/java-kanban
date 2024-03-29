package ru.yandex.kanban.httpServer.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.managers.taskManger.TaskManager;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static ru.yandex.kanban.utils.WriteResponseUtil.writeResponse;

public class EpicHandler implements HttpHandler {
    TaskManager taskManager;
    private final Gson gson = new Gson();
    String response;

    public EpicHandler(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getEpic(exchange);
                break;
            case "POST":
                addEpic(exchange);
                break;
            case "DELETE":
                deleteEpic(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);
        }
    }

    private void getEpic(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            response = gson.toJson(taskManager.getAllEpics());
            writeResponse(exchange, response, 200);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор!", 400);
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getEpicById(id) != null) {
            response = gson.toJson(taskManager.getEpicById(id));
        } else {
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
        }
        writeResponse(exchange, response, 200);
    }


    private void addEpic(HttpExchange exchange) throws IOException {
        try {
            InputStream json = exchange.getRequestBody();
            String jsonTask = new String(json.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(jsonTask, Epic.class);
            if (epic == null) {
                writeResponse(exchange, "Задача не должна быть пустой!", 400);
                return;
            }
            if (epic.getId() != null && taskManager.getAllEpics().contains(epic)) {
                if (!epic.getSubTasksIdList().isEmpty()) {
                    for (SubTask s : taskManager.getAllEpicsSubsList(epic.getId())) {
                        taskManager.updateSubTask(s);
                    }
                }
                writeResponse(exchange, "Эпик обновлен!", 218);
                return;
            }
            taskManager.addNewEpic(epic);
            writeResponse(exchange, "Задача успешно добавлена!", 201);

        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void deleteEpic(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            taskManager.deleteAllEpics();
            writeResponse(exchange, "Задачи успешно удалены!", 200);
            return;
        }
        if (!getTaskId(exchange).isPresent()) {
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getEpicById(id) == null) {
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
            return;
        }
        taskManager.deleteEpic(id);
        writeResponse(exchange, "Задача успешно удалена!", 200);
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
