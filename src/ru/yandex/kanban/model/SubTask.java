package ru.yandex.kanban.model;

import java.time.Duration;

public class SubTask extends Task {
    private final Integer epicId;
    private static final Type TYPE = Type.SUBTASK;
    public SubTask (String newTitle, String newDescription, Status newStatus, int newEpicId) {
        super(newTitle, newDescription, newStatus);
        this.epicId = newEpicId;
    }
    public SubTask(String newTitle, String newDescription, Status newStatus, int newEpicId, Long newDuration) {
        super(newTitle, newDescription, newStatus, newDuration);
        this.epicId = newEpicId;
    }

    public int getEpicId() {
        return epicId;
    }
    public Type getType() {
        return TYPE;
    }
    @Override
    public String toString() {
        return "SubTask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", epicId=" + epicId +
                ", status='" + getStatus() + ", startTime='" + getStartTime() + ", duration='" + getDuration()
                + ", endTime='" + getEndTime() + '\'' +
                '}';
    }
}