package org.example.Queues;

import org.example.Utils.Factory;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DualArrayDeque that achieves the same performance bounds as an ArrayDeque by using two ArrayStacks.
 * Implements the List<T> interface using two Lists so that insertion/deletions at the front or back of the list are fast.
 * Although the asymptotic performance of the DualArrayDeque is no better than that of the ArrayDeque, it is still worth studying,
 * since it offers a good example of how to make a sophisticated data structure by combining two simpler data structures.
 *
 * DualArrayDeque does not explicitly store the number, 'n', of elements it contains.
 * It doesn't need to, since it contains n=front.size() + back.size() elements.
 *
 * @param <T> the type of data stored in this list
 */
public class DualArrayDeque<T> extends AbstractList<T> {
    /**
     * The class of objects stored in this structure
     */
    protected Factory<T> f;

    /**
     * The front "half" of the deque.
     */
    protected List<T> front;

    /**
     * the back "half" of the deque.
     */
    protected List<T> back;

    public DualArrayDeque(Class<T> t){
        f = new Factory<>(t);
        front = newStack();
        back = newStack();
    }

    /**
     * Create a new empty List data structure.
     * Subclasses should override this if they want to use different structures for front and back.
     * @return new empty List data structure.
     */
    protected List<T> newStack(){return new ArrayList<T>();}

    // Front stores the list elements that whose indices are 0,...,front.size()-1, but stores them in reverse order.
    // Back contains list elements with indices in front.size(),...,size()-1 in the normal order.
    // So get(i) and set(i,x) translate into appropriate calls to get(i) or set(i,x) on either front or back, which take O(1) time per operation.

    /**
     * @Note If an index i<front.size(), then it corresponds to the element of front at position front.size()-i-1, since the elements of front are stored in reverse order.
     * @param i index of the element to return
     * @return
     */
    @Override
    public T get(int i) {
        if(i < front.size()){
            return front.get(front.size()-i-1);
        } else{
            return back.get(i-front.size());
        }
    }

    /**
     * @Note If an index i<front.size(), then it corresponds to the element of front at position front.size()-i-1, since the elements of front are stored in reverse order.
     * @param i index of the element to replace
     * @param x element to be stored at the specified position
     * @return
     */
    public T set(int i, T x){
        if(i < front.size()){
            return front.set(front.size()-i-1, x);
        } else{
            return front.set(i - front.size(), x);
        }
    }

    /**
     * Manipulates either front or back, as appropriate.
     * @Note Performs re-balancing of the two ArrayStacks front and back, by calling the balance() method.
     * Running time of add(i,x), if we ignore the cost of the call to balance(), is O(1+min{i,n-1}).
     * @param i index at which the specified element is to be inserted
     * @param x element to be inserted
     */
    @Override
    public void add(int i, T x) {
        if(i < front.size()){
            front.add(front.size()-i, x);
        } else{
            back.add(i- front.size(), x);
        }
        balance();
    }

    /**
     * operation and its analysis resemble the add(i,x) operation and analysis
     * @param i the index of the element to be removed
     * @return
     */
    public T remove(int i){
        T x;
        if(i < front.size()){
            x = front.remove(front.size()-i-1);
        } else{
            x = back.remove(i-front.size());
        }
        balance();
        return x;
    }

    /**
     * Ensures that, unless size()<2, front.size() and back.size() do not differ by more than a factor of 3.
     * In particular, 3*front.size() >= back.size() and 3*back.size() >= front.size().
     * If the operation does re-balancing, then it moves O(n) elements and takes O(n) time.
     * This is bad, since balance() is called with each call to add(i,x) and remove(i).
     * However, on average,balance() only spends a constant amount of time per operation;
     * If an empty DualArrayDeque is created and any sequence of m>=1 calls to add(i,x) and remove(i) are performed,
     * then the total time spent during all calls to balance() is O(m).
     * If balance() is forced to shift elements,
     * then the number of add(i,x) and remove(i) operations since the last time any elements were shifted by balance() is at least n/2-1.
     */
    private void balance() {
        int n = size();
        if(3*front.size() < back.size()){
            int s = n/2 - front.size();
            List<T> l1 = newStack();
            List<T> l2 = newStack();
            l1.addAll(back.subList(0, s));
            Collections.reverse(l1);
            l1.addAll(front);
            l2.addAll(back.subList(s, back.size()));
            front = l1;
            back = l2;
        } else if (3*back.size()< front.size()) {
            int s = front.size() - n/2;
            List<T> l1 = newStack();
            List<T> l2 = newStack();
            l1.addAll(front.subList(s, front.size()));
            l2.addAll(front.subList(0, s));
            Collections.reverse(l2);
            l2.addAll(back);
            front = l1;
            back = l2;
        }
    }

    @Override
    public int size() {
        return front.size() + back.size();
    }
}
