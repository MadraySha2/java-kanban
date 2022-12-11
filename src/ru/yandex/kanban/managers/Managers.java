package ru.yandex.kanban.managers;

import ru.yandex.kanban.managers.historyManager.HistoryManager;
import ru.yandex.kanban.managers.historyManager.InMemoryHistoryManager;
import ru.yandex.kanban.managers.taskManger.TaskManager;
import ru.yandex.kanban.managers.taskManger.InMemoryTaskManager;

public class Managers {
    static public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static public HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
