package mirea.structures;

public class Node<E> {
    Node<E> prev;
    Node<E> next;
    E value;

    public Node(Node<E> prev, E value, Node<E> next) {
        this.prev = prev;
        this.next = next;
        this.value = value;
    }
}
