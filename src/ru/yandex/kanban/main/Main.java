package ru.yandex.kanban.main;

import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;


public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

// ADD
        System.out.println("ADD");
        Task task1 = new Task("Task1", "Task1 description", Status.NEW);
        Task task2 = new Task("Task2", "Task2 description", Status.IN_PROGRESS);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        Epic epic2 = new Epic("Epic2", "Epic2 description", Status.NEW);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());

        int epic1Id = epic1.getId();
        int epic2Id = epic2.getId();

        SubTask subTask1_1 = new SubTask("SubTask1 - 1", "SubTask1-1 description", Status.NEW, epic1Id);
        SubTask subTask1_2 = new SubTask("SubTask1 - 2", "SubTask1-2 description", Status.NEW, epic1Id);
        SubTask subTask2_1 = new SubTask("SubTask2 - 1", "SubTask2 - 1 description", Status.NEW, epic2Id);
        SubTask subTask2_2 = new SubTask("SubTask2 - 2", "SubTask2 - 2 description", Status.IN_PROGRESS, epic2Id);

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

        System.out.println(taskManager.tasksMap);
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


        System.out.println(taskManager.epicsMap);
        System.out.println(taskManager.subTaskMap);

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
        System.out.println("GetByID");
        //getTaskById
        System.out.println(taskManager.getTaskById(task2.getId()));
        //getEpicById
        System.out.println(taskManager.getEpicById(epic2.getId()));
        //epicsSubsList
        System.out.println(taskManager.getAllEpicsSubsList(epic2.getId()));
        //getSubTaskById
        System.out.println(taskManager.getSubTaskById(subTask2_1.getId()));

        System.out.println("_________________________________");
//DeleteAll
        System.out.println("DELETE ALL");
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpics();

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());
    }


}
