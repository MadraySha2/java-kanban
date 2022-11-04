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
        subTask.setEpicId(epic.getId());
        epic.addSubTaskId(id);
        subTaskMap.put(id, subTask);
        updateEpicStatus(epic);
    }

    ArrayList<String> getAllEpicsSTs(Epic epic) {
        ArrayList<String> epicsStatuses = new ArrayList<>();
        for (int i = 0; i < epic.subTasksIdList.size(); i++) {
            int subId = epic.subTasksIdList.get(i);
            if (subTaskMap.containsKey(subId)) {
                epicsStatuses.add(subTaskMap.get(subId).getStatus());
            }
        }
        return epicsStatuses;
    }

    public void updateEpicStatus(Epic epic) {
        ArrayList<String> epicsStatuses = getAllEpicsSTs(epic);
        if (epicsStatuses.isEmpty()) {
            epic.setStatus("NEW");
            return;
        }
        if (epicsStatuses.contains("NEW")
                && !epicsStatuses.contains("IN_PROGRESS") && !epicsStatuses.contains("DONE")) {

            epic.setStatus("NEW");
        } else if (epicsStatuses.contains("DONE")
                && !epicsStatuses.contains("NEW") && !epicsStatuses.contains("IN_PROGRESS")) {

            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }

    void updateTask(Task updTask) {
        if (tasksMap.containsKey(updTask.getId())) {
            tasksMap.get(updTask.getId()).setTitle(updTask.getTitle());
            tasksMap.get(updTask.getId()).setDescription(updTask.getDescription());
            tasksMap.get(updTask.getId()).setStatus(updTask.getStatus());
        }
    }

    void updateSubTask(SubTask updSubTask) {
        Epic epic = epicsMap.get(updSubTask.getEpicId());
        if (subTaskMap.containsKey(updSubTask.getId())) {
            subTaskMap.get(updSubTask.getId()).setTitle(updSubTask.getTitle());
            subTaskMap.get(updSubTask.getId()).setDescription(updSubTask.getDescription());
            subTaskMap.get(updSubTask.getId()).setStatus(updSubTask.getStatus());
        }
        updateEpicStatus(epic);
    }

    String getAllEpics() {
        return epicsMap.toString();
    }

    String getAllTasks() {
        return tasksMap.toString();
    }

    String getAllSubTasks() {
        return subTaskMap.toString();
    }

    String getEpicById(int id) {
        if (!epicsMap.containsKey(id)) {
            System.out.println("404: epic not found!");
            return "";
        }
        return epicsMap.get(id).toString();
    }

    String getTaskById(int id) {
        if (!tasksMap.containsKey(id)) {
            System.out.println("404: task not found!");
            return "";
        }
        return tasksMap.get(id).toString();
    }

    String getSubTaskById(int id) {
        if (!subTaskMap.containsKey(id)) {
            System.out.println("404: task not found!");
            return "";
        }
        return subTaskMap.get(id).toString();
    }


    void deleteTask(int id) {
        tasksMap.remove(id);
    }

    void deleteEpic(int id) {
        Epic epic = epicsMap.get(id);
        ArrayList<Integer> stId = epic.subTasksIdList;
        for (int i = 0; i < subTaskMap.size(); i++) {
            if (subTaskMap.get(stId.get(i)).epicId == epic.getId()) {
                subTaskMap.remove(stId.get(i));
            }
        }
        epicsMap.remove(id);
    }

    void deleteSubTask(int id) {
        if (subTaskMap.containsKey(id)) {
            Epic epic = epicsMap.get(subTaskMap.get(id).epicId);
            epic.deleteSubTask(id);
            subTaskMap.remove(id);
        }

    }

    void deleteAllTasks() {
        tasksMap.clear();
    }

    void deleteAllSubTasks() {
        subTaskMap.clear();
    }

    void deleteAllEpics() {
        epicsMap.clear();
        deleteAllSubTasks();
    }


}
