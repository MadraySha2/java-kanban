package ru.yandex.kanban.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.managers.taskManger.TaskManager;

import java.io.IOException;
import java.util.Optional;

import static ru.yandex.kanban.utils.WriteResponseUtil.writeResponse;

public class EpicsSubTasksHandler implements HttpHandler {
    TaskManager taskManager;
    private final Gson gson = new Gson();
    String response;

    public EpicsSubTasksHandler(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getEpicSubtasks(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);

        }

    }

    private void getEpicSubtasks(HttpExchange exchange) throws IOException {
        if (getTaskId(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор!", 400);
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getEpicById(id) != null) {
            response = gson.toJson(taskManager.getAllEpicsSubsList(id));
        } else {
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
            return;
        }
        writeResponse(exchange, response, 200);

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
