import java.util.ArrayList;


public class Epic extends Task {

    protected ArrayList<Integer> subTasksIdList = new ArrayList<>();
    TaskManager taskManager = new TaskManager();

    Epic(String newTitle, String newDescription, String newStatus) {
        super(newTitle, newDescription, newStatus);
    }


    public void addSubTaskId(int subTaskId) {
        subTasksIdList.add(subTaskId);
    }

    public void deleteSubTask(int subId) {
        subTasksIdList.remove(taskManager.subTaskMap.get(subId));
    }

}
