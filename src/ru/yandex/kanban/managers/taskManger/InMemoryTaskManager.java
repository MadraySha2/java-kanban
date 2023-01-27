package ru.yandex.kanban.managers.taskManger;

import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.managers.historyManager.HistoryManager;
import ru.yandex.kanban.model.Epic;
import ru.yandex.kanban.model.Status;
import ru.yandex.kanban.model.SubTask;
import ru.yandex.kanban.model.Task;
import ru.yandex.kanban.utils.TimeComparator;

import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager = Managers.getHistoryManager();
    protected final HashMap<Integer, Epic> epicsMap = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    protected final HashMap<Integer, Task> tasksMap = new HashMap<>();
    private final Comparator<Task> timeComparator = new TimeComparator();
    protected final Set<Task> priorityOfTasks = new TreeSet<>(timeComparator);
    protected int id = 0;


    @Override
    public void addNewTask(Task task) {
        if (task == null) {
            System.out.println("Нельзя добавить пустой таск!");
            return;
        }
        if (isTasksCrossing(task)) {
            return;
        }
        id += 1;
        task.setId(id);
        tasksMap.put(id, task);

    }


    @Override
    public void addNewEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Нельзя добавить пустой эпик!");
        } else {
            id += 1;
            epic.setId(id);
            epicsMap.put(id, epic);
        }

    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("Нельзя добавить пустой сабтаск!");
            return;
        }
        if (isTasksCrossing(subTask)) {
            return;
        }
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
        updateEpicTimeVariables(epic);
    }


    protected ArrayList<Status> getAllEpicsSubTasks(Epic epic) {
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


    protected void updateEpicStatus(Epic epic) {
        ArrayList<Status> epicsStatuses = getAllEpicsSubTasks(epic);
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


    private void updateEpicTimeVariables(Epic epic) {
        TreeSet<SubTask> subTasks = new TreeSet<>(timeComparator);
        subTasks.addAll(getAllEpicsSubsList(epic.getId()));
        long duration = 0L;
        for (SubTask subTask : subTasks) {
            if (subTask.getDuration() != null) {
                duration += subTask.getDuration();
            }
        }
        epic.setDuration(duration);
        subTasks.removeIf(subTask -> subTask.getStartTime() == null);
        if (subTasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }
        LocalDateTime startTime = subTasks.first().getStartTime();
        LocalDateTime endTime = subTasks.last().getEndTime();
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }

    @Override
    public void updateTask(Task updTask) {
        if (tasksMap.containsKey(updTask.getId()) && !isTasksCrossing(updTask)) {
            tasksMap.put(updTask.getId(), updTask);
        }
    }

    @Override
    public void updateSubTask(SubTask updSubTask) {
        Epic epic = epicsMap.get(updSubTask.getEpicId());
        if (subTaskMap.containsKey(updSubTask.getId()) && !isTasksCrossing(updSubTask)) {
            subTaskMap.put(updSubTask.getId(), updSubTask);
            updateEpicStatus(epic);
            updateEpicTimeVariables(epic);
        }
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
            updateEpicTimeVariables(epicsMap.get(epicID));
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

    @Override
    public HashMap<Integer, Epic> getEpicsMap() {
        return epicsMap;
    }

    @Override
    public HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    @Override
    public HashMap<Integer, Task> getTasksMap() {
        return tasksMap;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        priorityOfTasks.addAll(tasksMap.values());
        priorityOfTasks.addAll(epicsMap.values());
        priorityOfTasks.addAll(subTaskMap.values());
        return List.copyOf(priorityOfTasks);
    }

    private boolean isTasksCrossing(Task newTask) {
        getPrioritizedTasks();
        priorityOfTasks.removeIf(task -> task.getStartTime() == null);
        for (Task task : priorityOfTasks) {
            if (newTask.getStartTime() == null) {
                return false;
            } else {
                LocalDateTime checkStart = task.getStartTime();
                LocalDateTime checkEnd = task.getEndTime();
                if ((newTask.getStartTime().isAfter(checkStart) && newTask.getEndTime().isBefore(checkEnd)) ||
                        (newTask.getStartTime().isBefore(checkStart) && newTask.getEndTime().isAfter(checkEnd))
                        || (newTask.getStartTime().isBefore(checkEnd) && newTask.getEndTime().isAfter(checkEnd))) {
                    System.out.println("Лучше делать по одному делу за раз!");
                    return true;
                }
            }
        }
        return false;
    }

}


