package ru.yandex.kanban.managers.taskManger;

import ru.yandex.kanban.exceptions.ManagerSaveException;
import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.managers.historyManager.HistoryManager;
import ru.yandex.kanban.model.*;


import java.io.*;
import java.util.*;

import static ru.yandex.kanban.utils.Converter.*;


public class FileBackedTasksManager extends InMemoryTaskManager {

    public FileBackedTasksManager(String path) {
        this.loadFromFile(new File(path));
    }

    public static void main(String[] args) {
        System.out.println("CОХРАНЕНИЕ_______________________saveFile1 - empty");
        FileBackedTasksManager fileBackedTasksManager = Managers.getFileBackedTasksManager("src/ru/yandex/kanban/resources/saveFile.csv");
        Task task6 = new Task("TaskHis6", "Task6 description", Status.NEW, Type.TASK);
        Task task7 = new Task("TaskHis7", "Task7 description", Status.NEW, Type.TASK);
        Task task8 = new Task("TaskHis8", "Task8 description", Status.NEW, Type.TASK);
        fileBackedTasksManager.addNewTask(task6);
        fileBackedTasksManager.addNewTask(task7);
        fileBackedTasksManager.addNewTask(task8);
        Epic epic10 = new Epic("EpicHis10", "Epic10 description", Status.NEW, Type.EPIC);
        Epic epic11 = new Epic("EpicHis11", "Epic11 description", Status.NEW, Type.EPIC);
        fileBackedTasksManager.addNewEpic(epic10);
        fileBackedTasksManager.addNewEpic(epic11);
        int epic10Id = 4;
        SubTask subTask10_1 = new SubTask("SubTask10 - 1", "SubTask10-1 description", Status.NEW, epic10Id, Type.SUBTASK);
        SubTask subTask10_2 = new SubTask("SubTask10 - 2", "SubTask11-2 description", Status.NEW, epic10Id, Type.SUBTASK);
        fileBackedTasksManager.addSubTask(subTask10_1);
        fileBackedTasksManager.addSubTask(subTask10_2);
        fileBackedTasksManager.getTaskById(task6.getId());
        fileBackedTasksManager.getTaskById(task8.getId());
        fileBackedTasksManager.getSubTaskById(subTask10_1.getId());
        fileBackedTasksManager.getEpicById(epic10.getId());
        System.out.println(fileBackedTasksManager.getTasksHistory());

        System.out.println("ВОСТАНОВЛЕНИЕ__________________from saveFile1");
        FileBackedTasksManager fl1 = Managers.getFileBackedTasksManager("src/ru/yandex/kanban/resources/saveFile1.csv");
        System.out.println(fl1.getAllTasks());
        System.out.println(fl1.getAllEpics());
        System.out.println(fl1.getAllSubTasks());
        System.out.println(fl1.getTasksHistory());

    }

    private void save() {
        HistoryManager historyManager = super.getHistoryManager();
        try (FileWriter writer = new FileWriter("src/ru/yandex/kanban/resources/saveFile1.csv", false)) {
            writer.write("id,type,title,status,description,EpicForST" + "\n");
            for (Task task : getAllTasks()) {
                writer.write(convert(task) + "\n");
            }
            for (Task epic : getAllEpics()) {
                writer.write(convert(epic) + "\n");
            }
            for (Task subTask : getAllSubTasks()) {
                writer.write(convert(subTask) + "\n");
            }
            writer.write("\n" + "\n");

            writer.write(convert(historyManager.getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("СОХРАНЕНИЕ НЕ ВЫПОЛНЕНО!");
        }
    }

    private void loadFromFile(File file) {
        try (Reader reader = new FileReader(file)) {
            List<Integer> history;
            BufferedReader br = new LineNumberReader(reader);
            while (br.ready()) {
                String line = br.readLine();
                if (!line.isEmpty() && !line.contains("id,type,title,status,description,EpicForST")) {
                    Task task = convert(line);
                    if (task != null) {
                        SubTask subTask;
                        Epic epic;
                        if (task.getType() == Type.EPIC) {
                            epic = (Epic) task;
                            epicsMap.put(epic.getId(), epic);
                            id += 1;
                        } else if (task.getType() == Type.SUBTASK) {
                            subTask = (SubTask) task;
                            subTaskMap.put(subTask.getId(), subTask);
                            Epic stEpic = epicsMap.get(subTask.getEpicId());
                            stEpic.addSubTaskId(subTask.getId());
                            updateEpicStatus(stEpic);
                            id += 1;
                        } else if (task.getType() == Type.TASK) {
                            tasksMap.put(task.getId(), task);
                            id += 1;
                        }
                    } else {
                        String[] hisLine = line.split(",");
                        history = convert(hisLine);
                        for (Integer id : history) {
                            if (epicsMap.containsKey(id)) {
                                getEpicById(id);
                            } else if (tasksMap.containsKey(id)) {
                                getTaskById(id);
                            } else if (subTaskMap.containsKey(id)) {
                                getSubTaskById(id);
                            }
                        }
                    }

                }
            }
            br.close();
        } catch (IOException e) {
            throw new ManagerSaveException("СЧИТЫВАНИЕ НЕ ВЫПОЛНЕНО!");
        }
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void updateTask(Task updTask) {
        super.updateTask(updTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask updSubTask) {
        super.updateSubTask(updSubTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }
}
