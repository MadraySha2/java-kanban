package ru.yandex.kanban.managers.taskManger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ru.yandex.kanban.httpServer.KVTaskClient;
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
        kv.put("task", json.toJson(super.getAllTasks()));
        kv.put("epic", json.toJson(super.getAllEpics()));
        kv.put("subtask", json.toJson(super.getAllSubTasks()));
        kv.put("history", json.toJson(super.getTasksHistory()));
    }

    @Override
    protected void load() {
        try {


            JsonArray loadedArray = kv.load("task");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTask : loadedArray) {
                Task loadedTask = json.fromJson(jsonTask, Task.class);
                int id = loadedTask.getId();
                super.tasksMap.put(id, loadedTask);
            }
            loadedArray = kv.load("epic");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTask : loadedArray) {
                Epic loadedEpic = json.fromJson(jsonTask, Epic.class);
                int id = loadedEpic.getId();
                super.epicsMap.put(id, loadedEpic);
            }
            loadedArray = kv.load("subtask");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTask : loadedArray) {
                SubTask loadedSubTask = json.fromJson(jsonTask, SubTask.class);
                int id = loadedSubTask.getId();
                super.subTaskMap.put(id, loadedSubTask);
            }
            loadedArray = kv.load("history");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTaskId : loadedArray) {
                if (jsonTaskId == null) {
                    break;
                }
                int loadedId = jsonTaskId.getAsInt();

                if (epicsMap.containsKey(loadedId)) {
                    getEpicById(loadedId);
                } else if (tasksMap.containsKey(loadedId)) {
                    getTaskById(loadedId);
                } else if (subTaskMap.containsKey(loadedId)) {
                    getSubTaskById(loadedId);
                }
            }
        } catch (UnsupportedOperationException e) {
            System.out.println(" ");
        }

    }
}
