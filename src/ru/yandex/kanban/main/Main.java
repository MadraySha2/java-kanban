package ru.yandex.kanban.main;

import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.managers.historyManager.FileBackedTasksManager;
import ru.yandex.kanban.managers.taskManger.TaskManager;
import ru.yandex.kanban.model.*;


public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

// ADD
        System.out.println("ADD");
        Task task1 = new Task("Task1", "Task1 description", Status.NEW, Type.TASK);
        Task task2 = new Task("Task2", "Task2 description", Status.IN_PROGRESS, Type.TASK);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW, Type.EPIC);
        Epic epic2 = new Epic("Epic2", "Epic2 description", Status.NEW, Type.EPIC);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());

        int epic1Id = epic1.getId();
        int epic2Id = epic2.getId();

        SubTask subTask1_1 = new SubTask("SubTask1 - 1", "SubTask1-1 description", Status.NEW, epic1Id, Type.SUBTASK);
        SubTask subTask1_2 = new SubTask("SubTask1 - 2", "SubTask1-2 description", Status.NEW, epic1Id, Type.SUBTASK);
        SubTask subTask2_1 = new SubTask("SubTask2 - 1", "SubTask2 - 1 description", Status.NEW, epic2Id, Type.SUBTASK);
        SubTask subTask2_2 = new SubTask("SubTask2 - 2", "SubTask2 - 2 description", Status.IN_PROGRESS, epic2Id, Type.SUBTASK);

        taskManager.addSubTask(subTask1_1);
        taskManager.addSubTask(subTask1_2);
        taskManager.addSubTask(subTask2_1);
        taskManager.addSubTask(subTask2_2);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());

        System.out.println("_________________________________");
//UPD
        System.out.println("UPD");
        // tasksUPD
        task1.setNewStatus(Status.IN_PROGRESS);
        task2.setNewStatus(Status.DONE);
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        System.out.println(taskManager.getAllTasks());
        //sub + epic UPD
        subTask1_1.setTitle("SubTask1 - 1u");
        subTask1_1.setNewStatus(Status.IN_PROGRESS);
        subTask1_2.setNewStatus(Status.DONE);
        subTask2_1.setNewStatus(Status.DONE);
        subTask2_2.setNewStatus(Status.DONE);

        taskManager.updateSubTask(subTask1_1);
        taskManager.updateSubTask(subTask1_2);
        taskManager.updateSubTask(subTask2_1);
        taskManager.updateSubTask(subTask2_2);


        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());

        System.out.println("_________________________________");
//DeleteByID
        System.out.println("DeleteByID");
        //taskDelete
        taskManager.deleteTask(task1.getId());
        //epicDelete
        taskManager.deleteEpic(epic1.getId());
        //subTaskDeleteByID
        taskManager.deleteSubTask(subTask1_1.getId());

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());

        System.out.println("_________________________________");
//GetByID
        System.out.println("GET EMPTY HISTORY");
        System.out.println(taskManager.getTasksHistory());
        System.out.println("GetByID");
        //getTaskById
        //getEpicById
        System.out.println(taskManager.getTaskById(task2.getId()));
        System.out.println(taskManager.getEpicById(epic2.getId()));
        //epicsSubsList
        System.out.println(taskManager.getAllEpicsSubsList(epic2.getId()));
        //getSubTaskById
        System.out.println(taskManager.getSubTaskById(subTask2_1.getId()));
        System.out.println(taskManager.getTaskById(task2.getId()));

        System.out.println("_________________________________");
        System.out.println("GET FULL HISTORY");
        System.out.println(taskManager.getTasksHistory());
//DeleteAll
        System.out.println("DELETE ALL");
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpics();
        System.out.println(taskManager.getTasksHistory());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getTasksHistory());

// HistoryTest
        Task task6 = new Task("TaskHis6", "Task6 description", Status.NEW, Type.TASK);
        Task task7 = new Task("TaskHis7", "Task7 description", Status.NEW, Type.TASK);
        Task task8 = new Task("TaskHis8", "Task8 description", Status.NEW, Type.TASK);
        taskManager.addNewTask(task6);
        taskManager.addNewTask(task7);
        taskManager.addNewTask(task8);
        Epic epic10 = new Epic("EpicHis10", "Epic10 description", Status.NEW, Type.EPIC);
        Epic epic11 = new Epic("EpicHis11", "Epic11 description", Status.NEW, Type.EPIC);
        taskManager.addNewEpic(epic10);
        taskManager.addNewEpic(epic11);
        int epic10Id = epic10.getId();
        SubTask subTask10_1 = new SubTask("SubTask10 - 1", "SubTask10-1 description", Status.NEW, epic10Id, Type.SUBTASK);
        SubTask subTask10_2 = new SubTask("SubTask10 - 2", "SubTask11-2 description", Status.NEW, epic10Id, Type.SUBTASK);
        taskManager.addSubTask(subTask10_1);
        taskManager.addSubTask(subTask10_2);


        System.out.println(taskManager.getTaskById(task8.getId()));
        System.out.println(taskManager.getTaskById(task6.getId()));
        System.out.println(taskManager.getTaskById(task7.getId()));
        System.out.println(taskManager.getEpicById(epic11.getId()));
        System.out.println(taskManager.getSubTaskById(subTask10_1.getId()));
        System.out.println(taskManager.getEpicById(epic10.getId()));
        taskManager.deleteEpic(epic10.getId());
        System.out.println(taskManager.getSubTaskById(subTask10_2.getId()));
        System.out.println(taskManager.getTaskById(task8.getId()));
        System.out.println("____________________________________");
        System.out.println(taskManager.getTasksHistory());
    }




}
