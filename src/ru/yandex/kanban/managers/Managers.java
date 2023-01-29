package ru.yandex.kanban.managers;

import ru.yandex.kanban.managers.historyManager.HistoryManager;
import ru.yandex.kanban.managers.historyManager.InMemoryHistoryManager;
import ru.yandex.kanban.managers.taskManger.FileBackedTasksManager;
import ru.yandex.kanban.managers.taskManger.HttpTaskManager;
import ru.yandex.kanban.managers.taskManger.InMemoryTaskManager;
import ru.yandex.kanban.managers.taskManger.TaskManager;

import java.io.IOException;

public class Managers {
    static public HttpTaskManager getDefault(String URL) throws IOException, InterruptedException {
        return new HttpTaskManager(URL);
    }

    static public TaskManager getInMemoryTaskManger() {
        return new InMemoryTaskManager();
    }

    static public HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }

    static public FileBackedTasksManager getFileBackedTasksManager(String path) {
        return new FileBackedTasksManager(path);
    }
}
