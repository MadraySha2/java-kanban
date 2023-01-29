package ru.yandex.kanban.managers.taskManager;

import ru.yandex.kanban.managers.Managers;
import ru.yandex.kanban.managers.TaskManagerTest;
import ru.yandex.kanban.managers.taskManger.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    protected InMemoryTaskManagerTest() {
    }

    @Override
    public InMemoryTaskManager createManager() {

        return (InMemoryTaskManager) Managers.getInMemoryTaskManger();
    }
}