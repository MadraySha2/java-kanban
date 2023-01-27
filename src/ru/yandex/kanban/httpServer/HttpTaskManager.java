package ru.yandex.kanban.httpServer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ru.yandex.kanban.managers.taskManger.FileBackedTasksManager;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {
    KVTaskClient kv;
    Gson json = new Gson();

    public HttpTaskManager(String URL) throws IOException, InterruptedException {
        this.kv = new KVTaskClient(URL);
        load();
    }

    @Override
    protected void save() {
        kv.put("t", json.toJson(super.getAllTasks()));
        kv.put("e", json.toJson(super.getAllEpics()));
        kv.put("s", json.toJson(super.getAllSubTasks()));
        kv.put("h", json.toJson(super.getTasksHistory()));
    }

    @Override
    protected void load() {
        JsonArray loadedArray = kv.load("t");
        if (loadedArray == null) {
            return;
        }
        for (JsonElement jsonTask : loadedArray) {
            Task loadedTask = json.fromJson(jsonTask, Task.class);
            super.addNewTask(loadedTask);
        }
        loadedArray = kv.load("e");
        if (loadedArray == null) {
            return;
        }
        for (JsonElement jsonTask : loadedArray) {
            Epic loadedEpic = json.fromJson(jsonTask, Epic.class);
            super.addNewEpic(loadedEpic);
        }
        loadedArray = kv.load("s");
        if (loadedArray == null) {
            return;
        }
        for (JsonElement jsonTask : loadedArray) {
            SubTask loadedTask = json.fromJson(jsonTask, SubTask.class);
            super.addSubTask(loadedTask);
        }
        loadedArray = kv.load("h");
        if (loadedArray == null) {
            return;
        }
        for (JsonElement jsonTaskId : loadedArray) {
            int loadedId = jsonTaskId.getAsInt();
            if (epicsMap.containsKey(loadedId)) {
                getEpicById(loadedId);
            } else if (tasksMap.containsKey(loadedId)) {
                getTaskById(loadedId);
            } else if (subTaskMap.containsKey(loadedId)) {
                getSubTaskById(loadedId);
            }
        }

    }
}
