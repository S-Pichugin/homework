package my;

public class CustomQueue<T> {
    private CustomLinkedList<T> list = new CustomLinkedList<>();

    public void offer(T item) {
        list.addLast(item);
    }

    public T poll() {
        if (list.isEmpty()) return null;
        return list.remove(0);
    }

    public T peek() {
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }
} 