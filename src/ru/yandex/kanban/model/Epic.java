package ru.yandex.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksIdList = new ArrayList<>();
    private static final Type TYPE = Type.EPIC;

    private LocalDateTime endTime;

    public Epic (String newTitle, String newDescription, Status newStatus) {
        super(newTitle,  newDescription,  newStatus);
    }

    public ArrayList<Integer> getSubTasksIdList() {
        return subTasksIdList;
    }

    public void setSubTasksIdList(ArrayList<Integer> subTasksIdList) {
        this.subTasksIdList = subTasksIdList;
    }

    public void addSubTaskId(int subTaskId) {
        subTasksIdList.add(subTaskId);
    }

    public ArrayList<Integer> getSubTaskIDs() {
        return getSubTasksIdList();
    }

    public void deleteSubTask(Integer subId) {
        subTasksIdList.remove(subId);
    }

    public Type getType() {
        return TYPE;
    }
    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }



    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", SubTaskID's = " + getSubTaskIDs() + ", startTime='" + getStartTime()
                + ", duration='" + getDuration() + ", endTime='" + getEndTime() +
                '}';
    }


}