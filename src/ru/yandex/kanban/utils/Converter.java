package ru.yandex.kanban.utils;

import ru.yandex.kanban.model.*;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    static public String convert(Task task) {
        if (task.getType().equals(Type.SUBTASK)) {
            SubTask subTask = (SubTask) task;
            return subTask.getId().toString() + "," + subTask.getType() + "," + subTask.getTitle()
                    + "," + subTask.getStatus() + "," + subTask.getDescription() + "," + subTask.getEpicId();
        }
        return task.getId().toString() + "," + task.getType()
                + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription();
    }


    static public Task convert(String value) {
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

    public static String convert(List<Task> historyValue) {
        StringBuilder builder = new StringBuilder();
        for (Task task : historyValue) {
            builder.append(task.getId()).append(", ");
        }
        return builder.toString();
    }

    public static List<Integer> convert(String[] values) {
        List<Integer> loadedHistory = new ArrayList<>();
        if (values == null) {
            return null;
        }
        for (String s : values) {
            if (!s.isEmpty() && !s.isBlank()) {
                loadedHistory.add(Integer.parseInt(s.trim())); //?????????? trim() ??????????????????, ???????? ???? ????????????????????????, ?????? ?????????? ???? ???????????????? ?????? ???????? ??????????????, ???? ?????? ???? :)
            }
        }
        return loadedHistory;
    }

    static private Type parseType(String typeString) {
        try {
            return Type.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            return null;
        }

    }

    static private Status parseStatus(String statusString) {
        try {
            return Status.valueOf(statusString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
