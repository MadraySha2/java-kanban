package ru.yandex.kanban.managers.httpManager;

import org.junit.jupiter.api.Test;
import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.managers.TaskManagerTest;
import ru.yandex.kanban.managers.taskManger.HttpTaskManager;
import ru.yandex.kanban.managers.taskManger.TaskManager;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {


    static {
        TaskManagerTest.kvServer.start();
    }


    @Override
    public HttpTaskManager createManager() {
        try {
            return Managers.getDefault("http://localhost:8078");
        } catch (IOException | InterruptedException e) {
            System.out.println("1111");
            return null;
        }
    }

    final TaskManager tm = createManager();

    @Test
    void addNewTasksAndHistory() throws IOException, InterruptedException {

        Task task1 = new Task("Task1", "Task1 description", Status.NEW);
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        tm.addNewEpic(epic1);
        int epicId = epic1.getId();
        SubTask sub1_1 = new SubTask("SubTask1 - 1", "SubTask1-1 description", Status.NEW, epicId);
        tm.addNewTask(task1);
        tm.addSubTask(sub1_1);

        assertEquals(1, tm.getAllTasks().size(), "Сохранилось неверное кол-во тасков");
        assertEquals(1, tm.getAllEpics().size(), "Сохранилось неверное кол-во Эпиков");
        assertEquals(1, tm.getAllSubTasks().size(), "Сохранилось неверное кол-во сабов");

        assertEquals("Task1", tm.getTaskById(task1.getId()).getTitle(), "Cохранен неверный таск");
        assertEquals("Epic1", tm.getEpicById(epicId).getTitle(), "Cохранен неверный эпик");
        assertEquals("SubTask1 - 1", tm.getSubTaskById(sub1_1.getId()).getTitle(),
                "Cохранен неверный таск");
        assertEquals("Task1", tm.getTasksHistory().get(0).getTitle());

        HttpTaskManager mt = new HttpTaskManager("http://localhost:8078");
        System.out.println(mt.getAllEpics());

    }


    @Test
    void deleteAllTasksAndGetNullTasks() {
        tm.deleteAllTasks();
        tm.deleteAllEpics();
        tm.deleteAllSubTasks();
        assertEquals(0, tm.getAllTasks().size(), "Таски не удалились");
        assertEquals(0, tm.getAllEpics().size(), "Эпики не удалились");
        assertEquals(0, tm.getAllSubTasks().size(), "Сабы не удалились");


    }
}