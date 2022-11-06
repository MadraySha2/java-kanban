package ru.yandex.kanban.model;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String newTitle, String newDescription, Status newStatus, int newEpicId) {
        super(newTitle, newDescription, newStatus);
        this.epicId = newEpicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", epicId=" + epicId +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
