package ru.yandex.kanban.kanban_utils.history;

import ru.yandex.kanban.model.Task;

import java.util.List;

public interface HistoryManager {


    void addTaskInHistory(Task task);

    List<Task> getHistory();
}
