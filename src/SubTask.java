public class SubTask extends Task {
    protected int epicId;


    SubTask(String newTitle, String newDescription, String newStatus, int newEpicId) {
        super(newTitle, newDescription, newStatus);
        this.epicId = newEpicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}
