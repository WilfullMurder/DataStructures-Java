package org.example;

import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Implementation of the List Interface as a doubly-linked circular list (DLList).
 * A dummy node (a node that does not contain any data, but acts as a placeholder so that there are no special nodes) is used to simplify code.
 * Every node has both a next and a prev, with dummy acting as the node that follows the last node in the list and that precedes the first node in the list.
 * In this way, the nodes of the list are (doubly-)linked into a cycle.
 *
 * @param <T>
 */
public class DoublyLinkedList<T> extends AbstractSequentialList<T> {

    class Node{
        T x;
        Node prev, next;
    }

    /**
     * number of nodes in the list
     */
    int n;

    /**
     * The dummy node.
     * dummy.next = first, dummy.prev = last
     */
    protected Node dummy;


    public DoublyLinkedList(){
        dummy = new Node();
        dummy.next = dummy;
        dummy.prev = dummy;
        n = 0;
    }

    /**
     * Get the ith node in the list
     * Finding the node with a particular index in a DLList is easy;
     * we can either start at the head of the list dummy.next and work forward, or start at the tail of the list dummy.prev and work backward.
     * This allows us to reach the ith node in O(1 + min{i, n-i}) time.
     * @param i the index of the node to get
     * @return the node with index i
     */
    public Node getNode(int i){
        Node p = null;
        if(i < n / 2){
            p = dummy.next;
            for(int j = 0; j < i; j++){
                p = p.next;
            }
        } else{
            p = dummy;
            for(int j = n; j > i; j--){
                p = p.prev;
            }
        }
        return p;
    }

    /**
     * Find the ith node and then get its value
     * Running time of this operation is dominated by the time it takes to find the ith node, and is therefore O(1 + min{i, n-i}) time.
     * @param i index of the element to return
     * @return
     */
    public T get(int i){
        if (i < 0 || i > n - 1) throw new IndexOutOfBoundsException();
        return getNode(i).x;
    }

    /**
     * Find the ith node and then set its value
     * Running time of this operation is dominated by the time it takes to find the ith node, and is therefore O(1 + min{i, n-i}) time.
     * @param i index of the element to replace
     * @param x element to be stored at the specified position
     * @return
     */
    public T set(int i, T x){
        if (i < 0 || i > n - 1) throw new IndexOutOfBoundsException();
        Node u = getNode(i);
        T y = u.x;
        u.x = x;
        return y;
    }


    /**
     * Add a new node containing x before the node w.
     * If we have a reference to a node w in a DLList and we want to insert a node u before w,
     * then this is just a matter of setting u.next = w, u.prev = w.prev, and then adjusting u.prev.next and u.next.prev.
     * Thanks to the dummy node, there is no need to worry about w.prev or w.next not existing.
     * @param w the node to insert the new node before.
     * @param x the value to store in the new node
     * @return the newly created and inserted node
     */
    public Node addBefore(Node w, T x){
        Node u = new Node();
        u.x = x;
        u.prev = w.prev;
        u.next = w;
        u.next.prev = u;
        u.prev.next = u;
        n++;
        return u;
    }

    /**
     * Trivial to implement. We find the ith node in the DLList and insert a new node u that contains x just before it.
     * The only non-constant part of the running time is the time it takes to find the ith node (using getNode(i)).
     * Thus, runs in O(1 + min{i, n-i}) time.
     * @param i index at which the specified element is to be inserted
     * @param x element to be inserted
     */
    public void add(int i, T x){
        if (i < 0 || i > n - 1) throw new IndexOutOfBoundsException();
        addBefore(getNode(i), x);
    }

    /**
     * Remove node w from the list.
     * We only need to adjust pointers at w.next and w.prev so that they skip over w.
     * Again, the use of the dummy node eliminates the need to consider any special cases.
     * @param w the node to remove
     */
    public void remove(Node w){
        w.prev.next = w.next;
        w.next.prev = w.prev;
        n--;
    }

    /**
     * Operation is trivial. We find the node with index i and remove it.
     * The only expensive part is finding theith node using getNode(i), so runs in O(1 + min{i, n-i}) time.
     * @param i the index of the element to be removed
     * @return
     */
    public T remove(int i){
        if (i < 0 || i > n - 1) throw new IndexOutOfBoundsException();
        Node w = getNode(i);
        remove(w);
        return w.x;
    }

    public void clear(){
        dummy.next = dummy;
        dummy.prev = dummy;
        n = 0;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        if (i < 0 || i > n - 1) throw new IndexOutOfBoundsException();
        return new Iterator(this,  i);
    }


    class Iterator implements ListIterator<T>{

        /**
         * The list we are iterating over
         */
        DoublyLinkedList<T> l;

        /**
         * The node whose value is returned by next()
         */
        Node p;

        /**
         * The last node whose value was returned by next() or previous()
         */
        Node last;

        /**
         * The index of p
         */
        int i;

        Iterator(DoublyLinkedList<T> il, int ii){
            l = il;
            i = ii;
            p = l.getNode(i);
        }


        @Override
        public boolean hasNext() {
            return p != dummy;
        }

        @Override
        public T next() {
            T x = p.x;
            last = p;
            p = p.next;
            i++;
            return x;
        }

        @Override
        public boolean hasPrevious() {
            return p.prev != dummy;
        }

        @Override
        public T previous() {
            p = p.prev;
            last = p;
            i--;
            return p.x;
        }

        @Override
        public int nextIndex() {
            return i;
        }

        @Override
        public int previousIndex() {
            return i-1;
        }

        @Override
        public void remove() {
            if(p == last){
                p = p.next;
            }
            DoublyLinkedList.this.remove(last);
        }

        @Override
        public void set(T x) {
            last.x = x;
        }

        @Override
        public void add(T x) {
            DoublyLinkedList.this.addBefore(p, x);
       }
    }


}
