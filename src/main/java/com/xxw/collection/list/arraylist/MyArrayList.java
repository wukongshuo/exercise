package com.xxw.collection.list.arraylist;

public class MyArrayList<E> implements MyList<E>{

    /**
     * https://blog.csdn.net/m0_37499059/article/details/80612779
     */

    /**
     * 默认大小
     */
    private static final int DEFAULT_SIZE = 10;
    /**
     * 数组元素
     */
    private Object[] elements = null;
    /**
     *
     */
    private int capacity;
    /**
     *
     */
    private int current;

    @Override
    public void add(E e) {

    }

    @Override
    public E get(int index) {
        return null;
    }

    @Override
    public void remove(int index) {

    }

    @Override
    public void insert(int index, E e) {

    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clearList() {

    }
}
