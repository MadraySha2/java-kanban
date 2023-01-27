package ru.yandex.kanban.httpServer;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.kanban.httpServer.handlers.*;
import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.managers.taskManger.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    private final HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer() throws IOException, InterruptedException {
        TaskManager taskManager = Managers.getDefault("http://localhost:8078");


        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task/", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/epic/", new EpicHandler(taskManager));
        httpServer.createContext("/tasks/subtask/", new SubTaskHandler(taskManager));
        httpServer.createContext("/tasks/subtask/epic/", new EpicsSubTasksHandler(taskManager));
        httpServer.createContext("/tasks/history/", new HistoryHandler(taskManager));
        httpServer.createContext("/tasks/", new TasksHandler(taskManager));

    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}

