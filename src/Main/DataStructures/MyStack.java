package Main.DataStructures;


public class MyStack {
    private String[] data;
    private int top;
    private static final int initial_cap = 100;

    public MyStack() {
        data = new String[initial_cap];
        top = -1;
    }

    public void push(String element) {
        ensureCapacity();
        data[++top] = element;
    }

    public String pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty!");
        }
        return data[top--];
    }

    public String peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty!");
        }
        return data[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }

    public void printAll() {
        System.out.print("[");
        for (int i = top; i >= 0; i--) {
            System.out.print(data[i]);
            if (i > 0) System.out.print(", ");
        }
        System.out.println("]");
    }

    private void ensureCapacity() {
        if (top + 1 == data.length) {
            String[] newData = new String[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
    }

    public static void main(String[] args) {
        MyStack stack = new MyStack();

        stack.push("Apple");
        stack.push("Banana");
        stack.push("Mango");

        stack.printAll();

        System.out.println("Peek: " + stack.peek());
        System.out.println("Pop: " + stack.pop());
        stack.printAll();
    }
}

