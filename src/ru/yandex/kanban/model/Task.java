package ru.yandex.kanban.model;
import java.time.Duration;
import java.time.LocalDateTime;



public class Task {
    private String title;
    private String description;
    private Integer id;
    private Status status;
    private static final Type TYPE = Type.TASK;
    private Duration duration;
    private LocalDateTime startTime;

    public Task (String newTitle, String newDescription, Status newStatus) {
        this.title = newTitle;
        this.description = newDescription;
        this.status = newStatus;

    }

    public Task(String newTitle, String newDescription, Status newStatus, long newDuration) {
        this.title = newTitle;
        this.description = newDescription;
        this.status = newStatus;
        this.duration = Duration.ofMinutes(newDuration);
        setStartTime(LocalDateTime.now());
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public Type getType() {
        return TYPE;
    }


    public void setNewStatus(Status newStatus) {
        this.status = newStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return title.equals(task.title) && description.equals(task.description) && status.equals(task.status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + ", startTime='" + startTime + ", duration='" + duration + ", endTime='" + getEndTime() + '\'' +
                '}';
    }


    public Long getDuration() {
        if (duration == null) {
            return 0L;
        }
        return duration.toMinutes();
    }

    public void setDuration(long duration) {
        this.duration = Duration.ofMinutes(duration);
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }




}