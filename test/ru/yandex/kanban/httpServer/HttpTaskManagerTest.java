package ru.yandex.kanban.httpServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.managers.taskManger.TaskManager;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest {
    private static final KVServer kvServer;

    static {
        try {
            kvServer = new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected  static  TaskManager tm;



    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
            kvServer.start();
        tm = Managers.getDefault("http://localhost:8078");
    }

    @AfterAll
    public static void afterAll() {
        kvServer.stop();

    }



    @Test
    void addNewTasksAndHistory()  {

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

    }

    @Test
    void deleteAllTasksAndGetNullTasks() throws IOException, InterruptedException {

        tm.deleteAllTasks();
        tm.deleteAllEpics();
        tm.deleteAllSubTasks();
        assertEquals(0, tm.getAllTasks().size(), "Таски не удалились");
        assertEquals(0, tm.getAllEpics().size(), "Эпики не удалились");
        assertEquals(0, tm.getAllSubTasks().size(), "Сабы не удалились");

    }
}