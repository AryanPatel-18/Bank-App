package Main.DataStructures;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MyArrayList {
    private String[] data;
    private int size;
    private static final int initial_cap = 10;

    public MyArrayList() {
        data = new String[initial_cap];
        size = 0;
    }


    public void add(String element) {
        ensureCapacity();
        data[size++] = element;
    }

    public void add(int index, String element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        ensureCapacity();
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = element;
        size++;
    }

    private void ensureCapacity() {
        if (size == data.length) {
            String[] newData = new String[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
    }

    public void printAll() {
        System.out.print("[");
        for (int i = 0; i < size; i++) {
            System.out.print(data[i]);
            if (i < size - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    public ObservableList<String> toObservableList() {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (int i = 0; i < size; i++) {
            list.add(data[i]);
        }
        return list;
    }
}

