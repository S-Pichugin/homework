package my;

public class CustomStack<T> {
    private CustomLinkedList<T> list = new CustomLinkedList<>();

    public void push(T item) {
        list.addLast(item);
    }

    public T pop() {
        if (list.isEmpty()) throw new java.util.NoSuchElementException("Stack is empty");
        return list.remove(list.size() - 1);
    }

    public T peek() {
        if (list.isEmpty()) throw new java.util.NoSuchElementException("Stack is empty");
        return list.get(list.size() - 1);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }
} 