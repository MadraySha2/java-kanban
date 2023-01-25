package ru.yandex.kanban.utils;

import ru.yandex.kanban.model.Task;

import java.util.Comparator;

public class TimeComparator implements Comparator<Task> {

    @Override
    public int compare(Task t1, Task t2) {
        if (t1 != null && t2 != null) {
            if (t1.getStartTime() == null) {
                return -1;
            } else {
             if (t2.getStartTime() == null || t1.getStartTime().isAfter(t2.getStartTime())) {
                    return 1;
                } else if (t1.getStartTime().equals(t2.getStartTime())) {
                    return 0;
                } else {
                    return -1;
                }
            }
        } else {
            return 1;
        }
    }
}
