package ru.yandex.kanban.managers;
// Сорри, не до конца понимаю с пакетами и их распределением :(

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.managers.taskManger.TaskManager;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public abstract class TaskManagerTest<T extends TaskManager> {
    public abstract T createManager() throws IOException, InterruptedException;


    T testTaskManager;
    public static Long duration = 15L;


    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        testTaskManager = createManager();
        testTaskManager.deleteAllTasks();
        testTaskManager.deleteAllSubTasks();
        testTaskManager.deleteAllEpics();


    }

    @Test
    void addTaskToTasksMap() {
        Task task1 = new Task("Task1", "Task1 description", Status.NEW, duration);
        testTaskManager.addNewTask(task1);

        assertNotNull(testTaskManager.getAllTasks(), "Задание не сохранилось!");

        final int taskId = task1.getId();
        Task testTask = testTaskManager.getTaskById(taskId);

        assertEquals("Task1", testTask.getTitle(), "Задание не совпадает!");
        assertEquals(1, testTaskManager.getAllTasks().size(), "Кол-во заданий неправильное!");
        testTaskManager.addNewTask(null);
        testTask = testTaskManager.getTaskById(taskId + 1);
        assertEquals(1, testTaskManager.getAllTasks().size(), "Кол-во заданий неправильное!");
        assertNull(testTask, "Добавился таск равный null!");
    }

    @Test
    void addEpicToEpicMap() {
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        testTaskManager.addNewEpic(epic1);

        assertNotNull(testTaskManager.getAllEpics(), "Задание не сохранилось!");

        final int taskId = epic1.getId();
        Task testTask = testTaskManager.getEpicById(taskId);

        assertEquals("Epic1", testTask.getTitle(), "Задание не совпадает!");
        assertEquals(1, testTaskManager.getAllEpics().size(), "Кол-во заданий неправильное!");

        testTaskManager.addNewEpic(null);
        testTask = testTaskManager.getEpicById(taskId + 1);
        assertEquals(1, testTaskManager.getAllEpics().size(), "Кол-во заданий неправильное!");
        assertNull(testTask, "Добавился эпик равный null!");
    }

    @Test
    void addSubTaskToSubTasksMapAndEpic() {
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        testTaskManager.addNewEpic(epic1);
        final int epicId = epic1.getId();
        SubTask subTask1_1 = new SubTask("SubTask1 - 1",
                "SubTask1-1 description", Status.NEW, epicId, 10L);
        subTask1_1.setStartTime(LocalDateTime.now().plusMinutes(30));
        testTaskManager.addSubTask(subTask1_1);

        assertNotNull(testTaskManager.getAllSubTasks(), "Задание не сохранилось!");

        final int taskId = subTask1_1.getId();
        SubTask testTask = testTaskManager.getSubTaskById(taskId);

        assertEquals("SubTask1 - 1", testTask.getTitle(), "Задание не совпадает!");
        assertEquals(1, testTaskManager.getAllSubTasks().size(), "Кол-во заданий неправильное!");
        assertEquals(subTask1_1.getId(), epic1.getSubTaskIDs().get(0), "Задание не сохранилось в эпик!");

        testTaskManager.addSubTask(null);
        testTask = testTaskManager.getSubTaskById(taskId + 1);
        assertEquals(1, testTaskManager.getAllSubTasks().size(), "Кол-во заданий неправильное!");
        assertNull(testTask, "Добавился сабтаск равный null!");
        SubTask subTask1_2 = new SubTask("SubTask1 - 1",
                "SubTask1-1 description", Status.NEW, epicId, 10L);
        subTask1_2.setStartTime(null);
        testTaskManager.addSubTask(subTask1_2);
        SubTask subTask1_3 = new SubTask("SubTask1 - 1",
                "SubTask1-1 description", Status.NEW, epicId);
        subTask1_1.setStartTime(null);
        testTaskManager.updateSubTask(subTask1_1);
        testTaskManager.updateSubTask(subTask1_2);
        testTaskManager.addSubTask(subTask1_3);
        assertEquals(20, epic1.getDuration());
        assertNull(epic1.getEndTime());
    }

    @Test
    void updateTask() {
        Task task1 = new Task("Task1", "Task1 description", Status.NEW, duration);
        testTaskManager.addNewTask(task1);
        task1.setNewStatus(Status.DONE);
        testTaskManager.updateTask(task1);

        final Status testStatus = testTaskManager.getTaskById(task1.getId()).getStatus();
        assertEquals(Status.DONE, testStatus, "Задание не обновилось!");

        task1.setDescription("TEST");
        testTaskManager.updateTask(task1);
        final String testDescription = testTaskManager.getTaskById(task1.getId()).getDescription();
        assertEquals("TEST", testDescription, "Задание не обновилось!");
        task1.setTitle("TEST");
        final String testTitle = testTaskManager.getTaskById(task1.getId()).getTitle();
        assertEquals("TEST", testTitle, "Задание не обновилось!");

    }

    @Test
    void updateSubTask() {
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        testTaskManager.addNewEpic(epic1);
        final int epicId = epic1.getId();
        SubTask sub1_1 = new SubTask("SubTask1 - 1", "SubTask1-1 description", Status.NEW, epicId, duration);
        sub1_1.setStartTime(LocalDateTime.now().plusMinutes(30));
        testTaskManager.addSubTask(sub1_1);
        sub1_1.setNewStatus(Status.DONE);
        testTaskManager.updateSubTask(sub1_1);

        final Status testStatus = testTaskManager.getSubTaskById(sub1_1.getId()).getStatus();
        assertEquals(Status.DONE, testStatus, "Задание не обновилось!");
        assertEquals(Status.DONE, epic1.getStatus(), "Статус связанного эпика не обновился!");

        sub1_1.setDescription("TEST");
        testTaskManager.updateSubTask(sub1_1);

        final String testDescription = testTaskManager.getSubTaskById(sub1_1.getId()).getDescription();
        assertEquals("TEST", testDescription, "Задание не обновилось!");
        sub1_1.setTitle("TEST");

        final String testTitle = testTaskManager.getSubTaskById(sub1_1.getId()).getTitle();
        assertEquals("TEST", testTitle, "Задание не обновилось!");


    }

    @Test
    void returnEpicById() {
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        testTaskManager.addNewEpic(epic1);
        final int id = epic1.getId();
        final Epic testEpic = testTaskManager.getEpicById(id);

        assertNull(testTaskManager.getEpicById(id + 1), "Эпик не существует, но возвращается!");
        assertEquals("Epic1", testEpic.getTitle(), "Возращется неправильный эпик!");

        List<Task> testEpicInHistory = testTaskManager.getTasksHistory();
        assertEquals("Epic1", testEpicInHistory.get(0).getTitle(), "Просмотренный эпик не сохранился в истории!");
    }

    @Test
    void returnTaskById() {
        Task task1 = new Task("Task1", "Task1 description", Status.NEW, duration);
        testTaskManager.addNewTask(task1);
        final int id = task1.getId();

        assertNull(testTaskManager.getTaskById(id + 1), "Таск не существует, но возвращается!");
        final Task testTask = testTaskManager.getTaskById(id);

        assertEquals("Task1", testTask.getTitle(), "Возращется неправильный таск!");

        List<Task> testTaskInHistory = testTaskManager.getTasksHistory();

        assertEquals("Task1", testTaskInHistory.get(0).getTitle(), "Просмотренный таск не сохранился в истории!");
    }

    @Test
    void returnSubTaskById() {
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        testTaskManager.addNewEpic(epic1);
        final int id = epic1.getId();
        SubTask sub1_1 = new SubTask("SubTask1 - 1", "SubTask1-1 description", Status.NEW, id, duration);
        sub1_1.setStartTime(LocalDateTime.now().plusMinutes(120));
        testTaskManager.addSubTask(sub1_1);
        final int stId = sub1_1.getId();

        assertNull(testTaskManager.getSubTaskById(stId + 1), "Сабтаск не существует, но возвращается!");

        final SubTask testTask = testTaskManager.getSubTaskById(stId);
        assertEquals("SubTask1 - 1", testTask.getTitle(), "Возращется неправильный сабтаск!");

        List<Task> testTaskInHistory = testTaskManager.getTasksHistory();
        assertEquals(sub1_1, testTaskInHistory.get(0), "Просмотренный сабтаск не сохранился в истории!");
    }

    @Test
    void deleteTask() {
        Task task1 = new Task("Task1", "Task1 description", Status.NEW, duration);
        testTaskManager.addNewTask(task1);
        final int id = task1.getId();
        testTaskManager.deleteTask(id);

        assertNull(testTaskManager.getTaskById(id), "Задание не удалилось!");
    }

    @Test
    void deleteEpic() {
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        testTaskManager.addNewEpic(epic1);
        final int id = epic1.getId();
        testTaskManager.deleteEpic(id);

        assertNull(testTaskManager.getEpicById(id), "Эпик не удалился!");
    }

    @Test
    void deleteSubTask() {
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        testTaskManager.addNewEpic(epic1);
        final int epicId = epic1.getId();
        SubTask sub1_1 = new SubTask("SubTask1 - 1", "SubTask1-1 description", Status.NEW, epicId, duration);
        sub1_1.setStartTime(LocalDateTime.now().plusMinutes(120));
        testTaskManager.addSubTask(sub1_1);
        final int id = sub1_1.getId();
        testTaskManager.deleteSubTask(id);

        assertNull(testTaskManager.getSubTaskById(id), "Сабтаск не удалился!");
        assertEquals(0, epic1.getSubTaskIDs().size(), "Сабтаск не удалился из эпика!");
        assertFalse(testTaskManager.getTasksHistory().contains(sub1_1), "Сабтаск не удалился из истории!");
    }

    @Test
    void deleteAllTasks() {
        Task task1 = new Task("Task1", "Task1 description", Status.NEW, duration);
        Task task2 = new Task("Task2", "Task2 description", Status.IN_PROGRESS, duration);
        testTaskManager.addNewTask(task1);
        testTaskManager.addNewTask(task2);
        testTaskManager.deleteAllTasks();

        assertEquals(0, testTaskManager.getAllTasks().size(), "Не все задания удалились!");
    }

    @Test
    void deleteAllSubTasks() {
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        testTaskManager.addNewEpic(epic1);
        final int id = epic1.getId();
        SubTask subTask1_1 = new SubTask("SubTask1 - 1", "SubTask1-1 description", Status.NEW, id, duration);
        SubTask subTask1_2 = new SubTask("SubTask1 - 2", "SubTask1-2 description", Status.NEW, id, duration);
        subTask1_1.setStartTime(LocalDateTime.now().plusMinutes(30));
        subTask1_2.setStartTime(LocalDateTime.now().plusMinutes(60));
        testTaskManager.addSubTask(subTask1_1);
        testTaskManager.addSubTask(subTask1_2);
        testTaskManager.getSubTaskById(subTask1_1.getId());
        testTaskManager.getSubTaskById(subTask1_2.getId());
        testTaskManager.deleteAllSubTasks();


        assertEquals(0, testTaskManager.getAllSubTasks().size(), "Не все сабтаски удалены!");
        assertEquals(0, epic1.getSubTasksIdList().size(), "Не все сабтаски удалены из эпика!");
        assertFalse(testTaskManager.getTasksHistory().contains(subTask1_1), "Сабтаск1 не удалился из истории!");
        assertFalse(testTaskManager.getTasksHistory().contains(subTask1_2), "Сабтаск2 не удалился из истории!");
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        Epic epic2 = new Epic("Epic2", "Epic2 description", Status.NEW);
        testTaskManager.addNewEpic(epic1);
        testTaskManager.addNewEpic(epic2);
        testTaskManager.deleteAllEpics();

        assertEquals(0, testTaskManager.getAllEpics().size(), "Не все эпики удалились!");
    }

    @Test
    void getHistoryManager() {
        assertEquals(Managers.getHistoryManager().getClass(), testTaskManager.getHistoryManager().getClass());
    }

    @Test
    void setTasksToPrioritizedList() {
        Task task1 = new Task("Task1", "Task1 description", Status.NEW, 10L);
        Task task2 = new Task("Task2", "Task2 description", Status.NEW, 30L);
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        task1.setStartTime(LocalDateTime.now().plusMinutes(300));
        testTaskManager.addNewTask(task1);
        task2.setStartTime(task2.getStartTime().plusMinutes(92));
        testTaskManager.addNewTask(task2);
        testTaskManager.addNewEpic(epic1);
        List<Task> checkPriority = List.of(epic1, task2, task1);
        assertEquals(checkPriority, testTaskManager.getPrioritizedTasks());
    }

    @Test
    void checkTaskCrossingAnotherTasks() {
        Task task1 = new Task("Task1", "Task1 description", Status.NEW, 10L);
        Task task2 = new Task("Task1", "Task1 description", Status.NEW, 30L);
        task2.setStartTime(task1.getStartTime());


        testTaskManager.addNewTask(task1);
        testTaskManager.addNewTask(task2);


        assertEquals(1, testTaskManager.getAllTasks().size());

        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        Epic epic2 = new Epic("Epic1", "Epic1 description", Status.NEW);


        testTaskManager.addNewEpic(epic1);
        testTaskManager.addNewEpic(epic2);

        assertEquals(2, testTaskManager.getAllEpics().size());
        SubTask subTask1_1 = new SubTask("SubTask1 - 1",
                "SubTask1-1 description", Status.NEW, epic2.getId(), 20L);
        subTask1_1.setStartTime(task1.getStartTime());

        SubTask subTask1_2 = new SubTask("SubTask1 - 2",
                "SubTask1-2 description", Status.NEW, epic2.getId(), 5L);
        subTask1_2.setStartTime(LocalDateTime.now().plusMinutes(20));

        testTaskManager.addSubTask(subTask1_1);
        testTaskManager.addSubTask(subTask1_2);

        assertEquals(1, epic2.getSubTasksIdList().size(), "Сабтаска с некорректным временем добавилась!");
        assertEquals(1, testTaskManager.getAllSubTasks().size(), "Сабтаска с некорректным временем добавилась!");

    }
}


