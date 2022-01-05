package com.xxw.collection.list.arraylist;

public interface MyList<E> {

    void add(E e);

    E get(int index);

    void remove(int index);

    void insert(int index, E e);

    boolean contains(Object o);

    int size();

    boolean isEmpty();

    void clearList();
}
