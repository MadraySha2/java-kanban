package ru.yandex.kanban.managers.historyManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class HistoryMangerTest {
    static HistoryManager testHM = Managers.getHistoryManager();
    static List<Task> checkNullHistory;
    public static Long duration = 15L;



    @AfterEach
    void afterAll() {
        testHM = Managers.getHistoryManager();
    }


    @Test
    void addTaskInHistory_rightSequence() {
        final Task task = new Task("Task1", "Task1 description", Status.NEW,duration);
        final Epic epic = new Epic("Epic1", "Epic1 description", Status.NEW);
        task.setId(1);
        epic.setId(task.getId() + 1);
        final SubTask subTask = new SubTask("SubTask1 - 1",
                "SubTask1-1 description", Status.NEW, epic.getId(), duration);

        subTask.setId(epic.getId() + 1);
        testHM.addTaskInHistory(task);
        testHM.addTaskInHistory(epic);
        testHM.addTaskInHistory(subTask);

        List<Task> testHistory = testHM.getHistory();
        List<Task> testSequence = List.of(task, epic, subTask);

        assertEquals(3, testHistory.size(), "История не сохраняется!");
        assertEquals(testSequence, testHistory, "Нарушен порядок!");


        testHM.addTaskInHistory(subTask);
        testHM.addTaskInHistory(epic);
        testHM.addTaskInHistory(task);

        testHistory = testHM.getHistory();
        testSequence = List.of(subTask, epic, task);

        assertEquals(testSequence, testHistory, "Нарушен порядок сохранения!");

    }

    @Test
    void addTaskInHistory_notDuplicate() {
        final Task task = new Task("Task1", "Task1 description", Status.NEW, duration);
        task.setId(1);
        testHM.addTaskInHistory(task);
        testHM.addTaskInHistory(task);
        testHM.addTaskInHistory(task);

        assertEquals(1, testHM.getHistory().size(), "Таска затроилась!");

    }

    @Test
    void removeTaskFromHistoryById() {
        final Task task = new Task("Task1", "Task1 description", Status.NEW, duration);
        final Epic epic = new Epic("Epic1", "Epic1 description", Status.NEW);
        task.setId(1);
        epic.setId(task.getId() + 1);
        final SubTask subTask = new SubTask("SubTask1 - 1",
                "SubTask1-1 description", Status.NEW, epic.getId(), duration);
        subTask.setId(epic.getId() + 1);
        testHM.addTaskInHistory(task);
        testHM.addTaskInHistory(epic);
        testHM.addTaskInHistory(subTask);
        assertNotEquals(checkNullHistory, testHM.getHistory(), "История не сохранилась!");
        List<Task> checkEpicDelete = List.of(task, subTask);
        testHM.remove(epic.getId());
        assertEquals(checkEpicDelete, testHM.getHistory());
        testHM.remove(task.getId());
        checkEpicDelete = List.of(subTask);
        assertEquals(checkEpicDelete, testHM.getHistory());
        testHM.remove(subTask.getId());
        assertEquals(0, testHM.getHistory().size());
    }
}