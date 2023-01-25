package ru.yandex.kanban.managers.historyManager;

import ru.yandex.kanban.model.Task;

import java.util.List;

public interface HistoryManager {


    void addTaskInHistory(Task task);

    void remove(int id);

    List<Task> getHistory();
}
