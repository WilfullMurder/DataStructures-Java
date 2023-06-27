package org.example;

import java.util.AbstractList;
import java.util.AbstractQueue;
import java.util.Iterator;

/**
 * SinglyLinkedList (SLList) is an implementation of a FIFO Queue as an SLList.
 * This also includes the stack operations push and pop, which operate on the head of the queue.
 * SLList is a sequence of nodes.
 * Each node u stores a data value (u.x) and a reference (u.next) to the next node in the sequence.
 * For efficiency, an SLList uses variables head and tail to keep track of the first and last node in the sequence,
 * as well as an integer 'n' to keep track of the length of the sequence.
 *
 * @param <T> the type of data stored in the list
 */
public class SinglyLinkedList<T> extends AbstractQueue<T> {


    class Node{
        T x;
        Node next;
    }

    /**
     * front of the queue
     */
    Node head;
    /**
     * back of the queue
     */
    Node tail;
    /**
     * number of elements in the queue
     */
    int n;

    /**
     * Simply creates a new node u with data value x, sets u.next to the old head of the list and makes u the new head of the list.
     * Finally, it increments n since the size of the SLList has increased by one
     * @param x
     * @return
     */
    public T push(T x){
        Node u = new Node();
        u.x = x;
        u.next = head;
        head = u;
        if(n==0) tail = u; //SLList is empty so, head is also tail.
        n++;
        return x;
    }

    /**
     * After checking that the SLList is not empty, removes the head by setting head=head.next and decrementing 'n'.
     * A special case occurs when the last element is being removed, in which case tail is set to null.
     * @return
     */
    public T pop(){
        if(n == 0) return null; //SLList is empty so, nothing to pop.
        T x = head.x;
        head = head.next;
        if(--n == 0) tail = null; //We've hit the last element.
        return x;
    }

    //QUEUE OPERATIONS
        //An SLList can also implement the fifo queue operations add(x) and remove() in constant time.

    /**
     *  Removals are done from the head of the list, and are identical to the pop() operation:
     * @return
     */
    public T remove(){
        if(n == 0) return null; //SLList is empty so, nothing to pop.
        T x = head.x;
        head = head.next;
        if(--n == 0) tail = null; //We've hit the last element.
        return x;
    }

    /**
     * Done at the tail of the list.
     * In most cases, this is done by setting tail.next = u, where u is the newly created node that contains x.
     * However, a special case occurs when n=0, in which case tail=head=null.
     * In this case, both tail and head are set to u.
     * @param x element whose presence in this collection is to be ensured
     * @return
     */
    @Override
    public boolean add(T x) {
        Node u = new Node();
        u.x = x;
        if(n==0){
            head = u;
        }else {
            tail.next = u;
        }
        tail = u;
        n++;
        return true;
    }

    @Override
    public T poll() {
        if(n == 0) return null;
        T x = head.x;
        head = head.next;
        if(--n == 0) tail = null;
        return x;
    }

    @Override
    public T peek() {
        return head.x;
    }

    @Override
    public boolean offer(T x) {
        return add(x);
    }

    @Override
    public Iterator<T> iterator() {
        class SLIterator implements Iterator<T>{
            protected  Node p;

            public SLIterator(){p = head;}

            @Override
            public boolean hasNext() {
                return p != null;
            }

            @Override
            public T next() {
                T x = p.x;
                p = p.next;
                return x;
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
        return new SLIterator();
    }

    @Override
    public int size() {
        return n;
    }
}
