package ru.yandex.kanban.managers.historyManager;

import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.managers.taskManger.InMemoryTaskManager;
import ru.yandex.kanban.model.*;

import java.io.*;
import java.util.*;


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

    public void save() {
        HistoryManager historyManager = super.getHistoryManager();
        try (FileWriter writer = new FileWriter("src/ru/yandex/kanban/resources/saveFile1.csv", false)) {
            writer.write("id,type,title,status,description,EpicForST" + "\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Task epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Task subTask : getAllSubTasks()) {
                writer.write(toString(subTask) + "\n");
            }
            writer.write("\n" + "\n");

            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("СОХРАНЕНИЕ НЕ ВЫПОЛНЕНО!");
        }
    }

    public void loadFromFile(File file) {
        try (Reader reader = new FileReader(file)) {
            List<Integer> history;
            BufferedReader br = new LineNumberReader(reader);
            while (br.ready()) {
                String line = br.readLine();
                if (!line.isEmpty() && !line.contains("id,type,title,status,description,EpicForST")) {
                    Task task = fromString(line);
                    if (task == null) {
                        history = historyFromString(line);
                        for (Integer id : history) {
                            if (epicsMap.containsKey(id)) {
                                getEpicById(id);
                            } else if (tasksMap.containsKey(id)) {
                                getTaskById(id);
                            } else if (subTaskMap.containsKey(id)) {
                                getSubTaskById(id);
                            }
                        }
                    } else {
                        SubTask subTask;
                        Epic epic;
                        if (task instanceof Epic) {
                            epic = (Epic) task;
                            epicsMap.put(epic.getId(), epic);
                        } else if (task instanceof SubTask) {
                            subTask = (SubTask) task;
                            subTaskMap.put(subTask.getId(), subTask);
                        } else if (task.getType() == Type.TASK) {
                            tasksMap.put(task.getId(), task);
                        }
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            throw new ManagerSaveException("СЧИТЫВАНИЕ НЕ ВЫПОЛНЕНО!");
        }


    }

    private String toString(Task task) {
        if (task.getType().equals(Type.SUBTASK)) {
            SubTask subTask = (SubTask) task;
            return subTask.getId().toString() + "," + subTask.getType() + "," + subTask.getTitle()
                    + "," + subTask.getStatus() + "," + subTask.getDescription() + "," + subTask.getEpicId();
        }
        return task.getId().toString() + "," + task.getType()
                + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription();
    }


    private Task fromString(String value) {
        try {
            String[] taskStrings = value.split(",");
            int id = Integer.parseInt(taskStrings[0]);
            Type newType = parseType(taskStrings[1]);
            if (newType == null) {
                return null;
            }
            String title = taskStrings[2];
            Status newStatus = parseStatus(taskStrings[3]);
            if (newStatus == null) {
                return null;
            }
            String description = taskStrings[4];
            if (newType == Type.SUBTASK) {
                int epicId = Integer.parseInt(taskStrings[5]);
                SubTask subTask = new SubTask(title, description, newStatus, epicId, newType);
                subTask.setId(id);
                return subTask;
            } else if (newType == Type.EPIC) {
                Epic epic = new Epic(title, description, newStatus, newType);
                epic.setId(id);
                return epic;
            }
            Task task = new Task(title, description, newStatus, newType);
            task.setId(id);
            return task;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    static String historyToString(HistoryManager historyManager) {
        List<Task> hisTasks = historyManager.getHistory();
        StringBuilder builder = new StringBuilder();
        for (Task task : hisTasks) {
            builder.append(task.getId()).append(", ");
        }
        return builder.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> loadedHistory = new ArrayList<>();
        if (value == null) {
            return null;
        }
        String[] values = value.split(",");
        for (String s : values) {
            if (!s.isEmpty() && !s.isBlank()) {
                loadedHistory.add(Integer.parseInt(s.trim())); //здесь trim() избыточен, если мы предполагаем, что никто не изменяет сам файл вручную, но все же :)
            }
        }
        return loadedHistory;
    }

    private Type parseType(String typeString) {

        switch (typeString) {
            case "TASK":
                return Type.TASK;
            case "EPIC":
                return Type.EPIC;
            case "SUBTASK":
                return Type.SUBTASK;
            default:
                return null;
        }
    }

    private Status parseStatus(String statusString) {
        switch (statusString) {
            case "NEW":
                return Status.NEW;
            case "DONE":
                return Status.DONE;
            case "IN_PROGRESS":
                return Status.IN_PROGRESS;
        }
        return null;
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


}
