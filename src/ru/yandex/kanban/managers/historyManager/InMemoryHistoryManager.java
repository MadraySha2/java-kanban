package ru.yandex.kanban.managers.historyManager;


import ru.yandex.kanban.model.Task;

import java.util.*;

public class InMemoryHistoryManager<T> implements HistoryManager {

    private final HashMap<Integer, Node<T>> historyNodes = new HashMap<>();
    private Node<T> head;
    private Node<T> tail;

    @Override
    public void addTaskInHistory(Task task) {
        if (historyNodes.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (historyNodes.containsKey(id)) {
            removeNode(historyNodes.get(id));
        }
        else {
            System.out.println("Нельзя удалить несуществующий таск!");
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node<T> oldTail = tail;
        final Node<T> newNode = new Node<>(tail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        historyNodes.put(task.getId(), newNode);
    }

    private void removeNode(Node<T> node) {
        if (node != null) {
            final Node<T> next = node.next;
            final Node<T> prev = node.prev;
            historyNodes.remove(node.data.getId());
            node.data = null;
            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (head == node) {
                head = next;
                head.prev = null;
            } else if (tail == node) {
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }

    }

    private List<Task> getTasks() {
        List<Task> tasksHistList = new ArrayList<>();
        Node<T> node = head;
        while (node != null) {
            tasksHistList.add(node.data);
            node = node.next;
        }
        return tasksHistList;
    }


    static class Node<T> {
        private Task data;
        private Node<T> next;
        private Node<T> prev;

        public Node(Node<T> prev, Task data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

    }
}