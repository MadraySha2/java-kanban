package ru.yandex.kanban.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.managers.taskManger.TaskManager;

import java.io.IOException;


import static ru.yandex.kanban.utils.WriteResponseUtil.writeResponse;

public class TasksHandler implements HttpHandler {
    TaskManager taskManager;

    private final Gson gson = new Gson();
    String response;

    public TasksHandler(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getAllTasks(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);

        }
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        if (taskManager.getPrioritizedTasks().isEmpty()) {
            writeResponse(exchange, "Задач пока что нет :(", 200);
        } else {
            response = gson.toJson(taskManager.getPrioritizedTasks());
            writeResponse(exchange, response, 200);
        }
    }


}
