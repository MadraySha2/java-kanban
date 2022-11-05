import typesOfTasks.kanban.Epic;
import typesOfTasks.kanban.SubTask;
import typesOfTasks.kanban.Task;

import java.util.*;

public class TaskManager {
    HashMap<Integer, Epic> epicsMap = new HashMap<>();
    HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    HashMap<Integer, Task> tasksMap = new HashMap<>();
    int id = 0;

    public void newTask(Task task) {
        id += 1;
        task.setId(id);
        tasksMap.put(id, task);
    }

    public void newEpic(Epic epic) {
        id += 1;
        epic.setId(id);
        epicsMap.put(id, epic);

    }

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

    public ArrayList<Task.Status> getAllEpicsSTs(Epic epic) {
        ArrayList<Task.Status> epicsStatuses = new ArrayList<>();
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

    public void updateEpicStatus(Epic epic) {
        ArrayList<Task.Status> epicsStatuses = getAllEpicsSTs(epic);
        if (epicsStatuses.isEmpty()) {
            epic.setNewStatus(Task.Status.NEW);
            return;
        }
        if (epicsStatuses.contains(Task.Status.NEW)
                && !epicsStatuses.contains(Task.Status.IN_PROGRESS) && !epicsStatuses.contains(Task.Status.DONE)) {
            epic.setNewStatus(Task.Status.NEW);
        } else if (epicsStatuses.contains(Task.Status.DONE)
                && !epicsStatuses.contains(Task.Status.NEW) && !epicsStatuses.contains(Task.Status.IN_PROGRESS)) {
            epic.setNewStatus(Task.Status.DONE);
        } else {
            epic.setNewStatus(Task.Status.IN_PROGRESS);
        }
    }

    public void updateTask(Task updTask) {
        if (tasksMap.containsKey(updTask.getId())) {
            tasksMap.get(updTask.getId()).setTitle(updTask.getTitle());
            tasksMap.get(updTask.getId()).setDescription(updTask.getDescription());
            tasksMap.get(updTask.getId()).setNewStatus(updTask.getStatus());
        }
    }

    public void updateSubTask(SubTask updSubTask) {
        Epic epic = epicsMap.get(updSubTask.getEpicId());
        if (subTaskMap.containsKey(updSubTask.getId())) {
            subTaskMap.get(updSubTask.getId()).setTitle(updSubTask.getTitle());
            subTaskMap.get(updSubTask.getId()).setDescription(updSubTask.getDescription());
            subTaskMap.get(updSubTask.getId()).setNewStatus(updSubTask.getStatus());
        }
        updateEpicStatus(epic);
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epicsMap;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasksMap;
    }

    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTaskMap;
    }

    public Epic getEpicById(int id) {
        if (!epicsMap.containsKey(id)) {
            System.out.println("404: epic not found!");
            return null;
        }
        return epicsMap.get(id);
    }

    public Task getTaskById(int id) {
        if (!tasksMap.containsKey(id)) {
            System.out.println("404: task not found!");
            return null;
        }
        return tasksMap.get(id);
    }

    public SubTask getSubTaskById(int id) {
        if (!subTaskMap.containsKey(id)) {
            System.out.println("404: task not found!");
            return null;
        }
        return subTaskMap.get(id);
    }


    public void deleteTask(int id) {
        tasksMap.remove(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epicsMap.get(id);
        ArrayList<Integer> stId = epic.getSubTasksIdList();
        for (Integer integer : stId) {
            subTaskMap.remove(integer);
        }
        epicsMap.remove(id);
    }

    public void deleteSubTask(int id) {
        if (subTaskMap.containsKey(id)) {
            Epic epic = epicsMap.get(subTaskMap.get(id).getEpicId());
            epic.deleteSubTask(id);
            subTaskMap.remove(id);
            updateEpicStatus(epic);
        }

    }

    public void deleteAllTasks() {
        tasksMap.clear();
    }

    public void deleteAllSubTasks() {
        subTaskMap.clear();
        for (Integer epicID : epicsMap.keySet()) {
            epicsMap.get(epicID).setSubTasksIdList(new ArrayList<>());
            updateEpicStatus(epicsMap.get(epicID));
        }
    }

    public void deleteAllEpics() {
        epicsMap.clear();
        deleteAllSubTasks();
    }

}
