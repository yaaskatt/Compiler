package mirea.structures;

public class CustomList<E> {
    int size = 0;
    Node<E> first = null;
    Node<E> last = null;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean add(E o) {
        linkLast(o);
        return true;
    }

    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.value == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.value)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    private E unlink(Node<E> x) {
        E element = x.value;
        Node<E> next = x.next;
        Node<E> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.value = null;
        size--;
        return element;
    }

    public E get(int index) {
        checkElementIndex(index);
        return node(index).value;
    }

    private void checkElementIndex(int index) {
        if ((index < 0) || (index >= size))
            throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size);
    }

    private Node<E> node(int index) {
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    private void linkLast(E o){
        Node<E> l = last;
        Node<E> newNode = new Node<>(l, o, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
    }

    public Boolean contains(E e) {
        Node<E> node = first;
        while (node != null){
            if (node.value.equals(e)) return true;
            node = node.next;
        }
        return false;
    }
}
