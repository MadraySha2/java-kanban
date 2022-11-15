package ru.yandex.kanban.kanban_utils.history;

import ru.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> tasksHistory = new ArrayList<>();

    @Override
    public void addTaskInHistory(Task task) {
        if (tasksHistory.size() >= 10) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }


    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }
}
