package com.xxw.collection.list.linkedList;

public class Node<E> {

    private Node prev;

    private E e;

    private Node next;

    public Node(Node prev, E obj, Node next) {
        this.prev = prev;
        this.e = obj;
        this.next = next;
    }
}
