package ru.yandex.kanban.managers.taskManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.managers.TaskManagerTest;
import ru.yandex.kanban.managers.taskManger.FileBackedTasksManager;
import ru.yandex.kanban.managers.taskManger.TaskManager;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;
import ru.yandex.kanban.utils.Converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InFileTaskManagerTest extends TaskManagerTest<TaskManager> {
    final static String path = "java-kanban/src/ru/yandex/kanban/resources/testSave1.csv";
    final static File file = new File(path);
    public Long duration = 15L;

    @Override
    public FileBackedTasksManager createManager() {
        return Managers.getFileBackedTasksManager(path);
    }

    public final TaskManager fl = createManager();
    public static List<Task> checkNullList;

    @BeforeAll
    static void beforeAll() {
        checkNullList = new ArrayList<>();
    }

    @AfterEach
    void afterEach() {
        fl.deleteAllEpics();
        fl.deleteAllTasks();
        fl.deleteAllSubTasks();

    }

    @Test
    void shouldSaveAllTasksAndHistoryInFile() throws IOException {

        Task task1 = new Task("Task1", "Task1 description", Status.NEW, duration);
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        fl.addNewTask(task1);
        fl.addNewEpic(epic1);
        List<String> strings = Files.readAllLines(Path.of(path));

        assertTrue(file.length() != 0);
        assertEquals("id,type,title,status,description, startTime, duration EpicForST", strings.get(0));
        assertEquals(task1, Converter.convert(strings.get(1)), "Таск не правильно записан в файл!");
        assertEquals(epic1, Converter.convert(strings.get(2)), "Эпик не правильно записан в файл!");

        Task taskTest = fl.getTaskById(task1.getId());
        Task epicTest = fl.getEpicById(epic1.getId());

        assertEquals(task1, taskTest, "Возвращает неверную таску!");
        assertEquals(epicTest, epic1, "Возвращает неверный эпик!");

        final FileBackedTasksManager fileBackedTest = Managers.getFileBackedTasksManager(path);
        Task taskLoaded = fileBackedTest.getTaskById(task1.getId());
        Task epicLoaded = fileBackedTest.getEpicById(epic1.getId());

        assertEquals(task1, taskLoaded, "Возвращает неверную таску при загрузке из файла!");
        assertEquals(epic1, epicLoaded, "Возвращает неверный эпик при загрузке из файла!!");
    }


    @Test
    void shouldReturnEmptyListsOfTasksAndHistory() {
        List<Task> nullTasksList = fl.getAllTasks();
        List<Epic> nullEpicsList = fl.getAllEpics();
        List<SubTask> nullSubTasksList = fl.getAllSubTasks();
        List<Task> nullHistoryList = fl.getTasksHistory();

        assertEquals(checkNullList, nullTasksList, "Не работает с пустым списком тасков!");
        assertEquals(checkNullList, nullEpicsList, "Не работает с пустым списком эпиков!");
        assertEquals(checkNullList, nullSubTasksList, "Не работает с пустым списком сабтасков!");
        assertEquals(checkNullList, nullHistoryList, "Не работает с пустым списком истории!");


    }

    @Test
    void shouldReturnHistoryInRightSequence() {
        Task task1 = new Task("Task1", "Task1 description", Status.NEW, duration);
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        fl.addNewTask(task1);
        fl.addNewEpic(epic1);
        fl.getTaskById(task1.getId());
        fl.getEpicById(epic1.getId());
        List<Task> checkList = List.of(task1, epic1);

        assertNotEquals(checkNullList, fl.getTasksHistory(), "История не сохраняется!");
        assertEquals(checkList, fl.getTasksHistory(), "История не в том порядке!");

    }

}
