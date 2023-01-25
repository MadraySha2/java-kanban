package ru.yandex.kanban.managers.taskManger;

import ru.yandex.kanban.exceptions.ManagerSaveException;
import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.managers.historyManager.HistoryManager;
import ru.yandex.kanban.model.*;

import java.io.*;
import java.util.List;

import static ru.yandex.kanban.utils.Converter.convert;


public class FileBackedTasksManager extends InMemoryTaskManager {
    private final String path;

    public FileBackedTasksManager(String path) {
        this.path = path;
        this.loadFromFile(new File(path));

    }

    public static void main(String[] args) {
        System.out.println("CОХРАНЕНИЕ_______________________saveFile1 - empty");
        Long duration = 90L;
        Long duration1 = 120L;
        FileBackedTasksManager fileBackedTasksManager = Managers.getFileBackedTasksManager("java-kanban/src/ru/yandex/kanban/resources/saveFile1.csv");
        Epic epic1 = new Epic("Epic1", "Epic1 description", Status.NEW);
        fileBackedTasksManager.addNewEpic(epic1);

        SubTask subTask1_1 = new SubTask("SubTask1 - 1", "SubTask1-1 description", Status.NEW, epic1.getId(), duration);
        SubTask subTask1_2 = new SubTask("SubTask1 - 2", "SubTask1-2 description", Status.NEW, epic1.getId(), duration1);

        fileBackedTasksManager.addSubTask(subTask1_1);
        fileBackedTasksManager.addSubTask(subTask1_2);


        System.out.println("ВОСТАНОВЛЕНИЕ__________________from saveFile1");
        FileBackedTasksManager fl1 = Managers.getFileBackedTasksManager("java-kanban/src/ru/yandex/kanban/resources/saveFile1.csv");
        System.out.println(fl1.getAllTasks());
        System.out.println(fl1.getAllEpics());
        System.out.println(fl1.getAllSubTasks());
        System.out.println(fl1.getTasksHistory());

    }

    private void save() {
        HistoryManager historyManager = super.getHistoryManager();
        try (FileWriter writer = new FileWriter(this.path, false)) {
            writer.write("id,type,title,status,description, startTime, duration EpicForST" + "\n");
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
            int maxId = 0;
            while (br.ready()) {
                String line = br.readLine();
                if (!line.isEmpty() && !line.contains("id,type,title,status,description, startTime, duration EpicForST")) {
                    Task task = convert(line);
                    if (task != null) {
                        SubTask subTask;
                        Epic epic;
                        if (task.getType() == Type.EPIC) {
                            epic = (Epic) task;

                            epicsMap.put(task.getId(), epic);

                        } else if (task.getType() == Type.SUBTASK) {
                            subTask = (SubTask) task;
                            subTaskMap.put(task.getId(), subTask);
                            Epic stEpic = epicsMap.get(subTask.getEpicId());
                            stEpic.addSubTaskId(subTask.getId());
                            updateEpicStatus(stEpic);

                        } else if (task.getType() == Type.TASK) {
                            tasksMap.put(task.getId(), task);

                        }
                        if (task.getId() > maxId) {
                            maxId = task.getId();
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
            id = maxId;
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
    protected void updateEpicStatus(Epic epic) {
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
