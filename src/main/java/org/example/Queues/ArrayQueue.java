package org.example.Queues;

import org.example.Utils.Factory;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *An implementation of the Queue<T> interface using an array
 *Simulates an infinite array using modular arithmetic since i % a.length always gives a value in the range 0,...,a.length-1.
 *Using modular arithmetic we can store the queue elements at array locations a[j % a.length],a[j+1 % a.length],......,a[(j+n-1) % a.length]
 *This treats the array 'a' like a circular array in which array indices larger than a.length-1 ``wrap around'' to the beginning of the array.
 *Leaving the only thing to worry about; taking care that the number of elements in the ArrayQueue does not exceed the size of 'a'.
 * All operations take constant amortized time.
 * @param <T> the type of data stored in this list
 */

public class ArrayQueue<T> extends AbstractQueue<T> {

    /**
     * The class of elements stored in this queue
     */
    protected Factory<T> f;

    /**
     * Array used to store elements
     */
    protected T[] a;

    /**
     * Index of next element to de-queue
     */
    protected int j;

    /**
     * Number of elements in the queue
     */
    protected int n;


    /**
     * Constructor
     * We maintain one index 'j' that keeps track of the next element to remove and an integer 'n' that counts the number of elements in the queue.
     * Initially, both 'j' and 'n' would be set to 0.
     * @param t class of elements to be stored in this queue
     */
    public ArrayQueue(Class<T> t){
        f = new Factory<>(t);
        a = f.newArray(1);
        j = 0;
        n = 0;
    }

    /**
     * First check if 'a' is full and, if necessary, call resize() to increase the size of 'a'.
     * Next, we store x in a[(j+n) % a.length] and increment 'n'
     * @param x element whose presence in this collection is to be ensured
     * @return true
     */
    @Override
    public boolean add(T x) {
        if(n+1 > a.length) resize();
        a[(j+n) % a.length] = x;
        n++;
        return true;
    }



    /**
     * First store a[j] so that we can return it later.
     * Next, we decrement 'n' and increment j % a.length by setting j = (j+1) % a.length.
     * Finally, we return the stored value of a[j].
     * If necessary, we may call resize() to decrease the size of 'a'.
     * @return a[j]
     */
    @Override
    public T remove() {
        if(n==0) throw new NoSuchElementException();
        T x = a[j];
        j = (j+1) % a.length;
        n--;
        if(a.length >= 3*n) resize();
        return x;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public boolean offer(T x) {
        return add(x);
    }

    @Override
    public T poll() {
        return n == 0 ? null : remove();
    }

    @Override
    public T peek() {
        T x = null;
        if (n > 0) {
            x = a[j];
        }
        return x;
    }

    /**
     * resize the backing array
     * allocates a new array, 'b', of size 2n and copies a[j],a[(j+1)%a.length],......,a[(j+n-1)%a.length]
     * onto b[0],b[1],...,b[n-1] and sets j=0.
     */
    private void resize() {
        T[]b = f.newArray(Math.max(1, n*2));
        for(int k = 0; k < n; k++){
            b[k] = a[(j+k) % a.length];
        }
        a = b;
        j = 0;
    }

    /**
     * Return an iterator for the elements of the queue.
     * This iterator does not support the remove operation
     */
    @Override
    public Iterator<T> iterator() {
       class QueueIterator implements Iterator<T>{
           int k;

           public QueueIterator(){
            k=0;
           }

           public boolean hasNext() {
               return (k < n);
           }

           public T next() {
               if (k > n) throw new NoSuchElementException();
               T x = a[(j+k) % a.length];
               k++;
               return x;
           }

           public void remove() {
               throw new UnsupportedOperationException();
           }
       }

        return new QueueIterator();

    }




}
