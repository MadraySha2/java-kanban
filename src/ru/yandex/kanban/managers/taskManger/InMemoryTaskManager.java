package ru.yandex.kanban.managers.taskManger;

import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.managers.historyManager.HistoryManager;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager = Managers.getHistoryManager();
    private final HashMap<Integer, Epic> epicsMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private final HashMap<Integer, Task> tasksMap = new HashMap<>();
    private int id = 0;

    @Override
    public void addNewTask(Task task) {
        id += 1;
        task.setId(id);
        tasksMap.put(id, task);

    }

    @Override
    public void addNewEpic(Epic epic) {
        id += 1;
        epic.setId(id);
        epicsMap.put(id, epic);

    }

    @Override
    public void addSubTask(SubTask subTask) {
        Epic epic = epicsMap.get(subTask.getEpicId());
        id += 1;
        if (epic == null) {
            System.out.println("Такой эпик еще не создан!" + subTask.getEpicId());
            return;
        }
        subTask.setId(id);
        epic.addSubTaskId(id);
        subTaskMap.put(id, subTask);
        updateEpicStatus(epic);

    }

    @Override
    public ArrayList<Status> getAllEpicsSTs(Epic epic) {
        ArrayList<Status> epicsStatuses = new ArrayList<>();
        if (epic.getSubTaskIDs().isEmpty()) {
            return new ArrayList<>();
        }
        for (int i = 0; i < epic.getSubTaskIDs().size(); i++) {
            Integer subId = epic.getSubTaskIDs().get(i);
            if (subTaskMap.containsKey(subId)) {
                epicsStatuses.add(subTaskMap.get(subId).getStatus());
            }

        }
        return epicsStatuses;
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        ArrayList<Status> epicsStatuses = getAllEpicsSTs(epic);
        if (epicsStatuses.isEmpty()) {
            epic.setNewStatus(Status.NEW);
            return;
        }
        if (epicsStatuses.contains(Status.NEW)
                && !epicsStatuses.contains(Status.IN_PROGRESS)
                && !epicsStatuses.contains(Status.DONE)) {
            epic.setNewStatus(Status.NEW);
        } else if (epicsStatuses.contains(Status.DONE)
                && !epicsStatuses.contains(Status.NEW)
                && !epicsStatuses.contains(Status.IN_PROGRESS)) {
            epic.setNewStatus(Status.DONE);
        } else {
            epic.setNewStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void updateTask(Task updTask) {
        if (tasksMap.containsKey(updTask.getId())) {
            tasksMap.get(updTask.getId()).setTitle(updTask.getTitle());
            tasksMap.get(updTask.getId()).setDescription(updTask.getDescription());
            tasksMap.get(updTask.getId()).setNewStatus(updTask.getStatus());
        }
    }

    @Override
    public void updateSubTask(SubTask updSubTask) {
        Epic epic = epicsMap.get(updSubTask.getEpicId());
        if (subTaskMap.containsKey(updSubTask.getId())) {
            subTaskMap.get(updSubTask.getId()).setTitle(updSubTask.getTitle());
            subTaskMap.get(updSubTask.getId()).setDescription(updSubTask.getDescription());
            subTaskMap.get(updSubTask.getId()).setNewStatus(updSubTask.getStatus());
        }
        updateEpicStatus(epic);
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Integer task : tasksMap.keySet()) {
            tasksList.add(tasksMap.get(task));
        }
        return tasksList;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        for (Integer subTask : subTaskMap.keySet()) {
            subTasksList.add(subTaskMap.get(subTask));
        }
        return subTasksList;
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epicsMap.containsKey(id)) {
            System.out.println("404: epic not found!");
            return null;
        }
        historyManager.addTaskInHistory(epicsMap.get(id));
        return epicsMap.get(id);
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasksMap.containsKey(id)) {
            System.out.println("404: task not found!");
            return null;
        }
        historyManager.addTaskInHistory(tasksMap.get(id));
        return tasksMap.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (!subTaskMap.containsKey(id)) {
            System.out.println("404: task not found!");
            return null;
        }
        historyManager.addTaskInHistory(subTaskMap.get(id));
        return subTaskMap.get(id);
    }

    @Override
    public void deleteTask(int id) {
        tasksMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epicsMap.get(id);
        ArrayList<Integer> stId = epic.getSubTasksIdList();
        for (Integer integer : stId) {
            subTaskMap.remove(integer);
            historyManager.remove(integer);
        }
        epicsMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        if (subTaskMap.containsKey(id)) {
            Epic epic = epicsMap.get(subTaskMap.get(id).getEpicId());
            epic.deleteSubTask(id);
            subTaskMap.remove(id);
            updateEpicStatus(epic);
            historyManager.remove(id);
        }

    }

    @Override
    public void deleteAllTasks() {
        for (int id : tasksMap.keySet()) {
            historyManager.remove(id);
        }
        tasksMap.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (int id : subTaskMap.keySet()) {
            historyManager.remove(id);
        }
        subTaskMap.clear();
        for (Integer epicID : epicsMap.keySet()) {
            epicsMap.get(epicID).setSubTasksIdList(new ArrayList<>());
            updateEpicStatus(epicsMap.get(epicID));
        }
    }

    @Override
    public void deleteAllEpics() {
        for (int id : epicsMap.keySet()) {
            historyManager.remove(id);
        }
        epicsMap.clear();
        deleteAllSubTasks();
    }

    @Override
    public ArrayList<SubTask> getAllEpicsSubsList(int epicId) {
        ArrayList<SubTask> epicsSubTasksList = new ArrayList<>();
        Epic epic = epicsMap.get(epicId);
        for (int i = 0; i < epic.getSubTaskIDs().size(); i++) {
            epicsSubTasksList.add(subTaskMap.get(epic.getSubTaskIDs().get(i)));
        }
        return epicsSubTasksList;
    }

    @Override
    public List<Task> getTasksHistory() {
        return historyManager.getHistory();
    }


}
