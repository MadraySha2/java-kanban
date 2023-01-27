package ru.yandex.kanban.managers.taskManger;

import ru.yandex.kanban.managers.historyManager.HistoryManager;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    void addNewTask(Task task);

    void addNewEpic(Epic epic);

    void addSubTask(SubTask subTask);

    void updateTask(Task updTask);

    void updateSubTask(SubTask updSubTask);

    ArrayList<Epic> getAllEpics();

    ArrayList<Task> getAllTasks();

    ArrayList<SubTask> getAllSubTasks();

    Epic getEpicById(int id);

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();

    ArrayList<SubTask> getAllEpicsSubsList(int epicId);

    List<Task> getTasksHistory();

    HashMap<Integer, Epic> getEpicsMap();

    HashMap<Integer, SubTask> getSubTaskMap();

    HashMap<Integer, Task> getTasksMap();
    List<Task> getPrioritizedTasks();
    HistoryManager getHistoryManager();
}
