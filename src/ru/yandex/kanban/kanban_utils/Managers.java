package ru.yandex.kanban.kanban_utils;

import ru.yandex.kanban.kanban_utils.history.HistoryManager;
import ru.yandex.kanban.kanban_utils.history.InMemoryHistoryManager;
import ru.yandex.kanban.kanban_utils.taskManger.TaskManager;
import ru.yandex.kanban.kanban_utils.taskManger.InMemoryTaskManager;

public class Managers {
    static public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static public HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
