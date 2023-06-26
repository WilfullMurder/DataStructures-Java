package org.example;

import java.util.AbstractQueue;
import java.util.Iterator;

/**
 * This structure implements the List interface by using the same circular array technique used to represent an ArrayQueue.
 * Allows for efficient addition and removal at both ends.
 */
public class ArrayDeque<T> extends AbstractQueue<T> {

    T[] a;

    int j;

    int n;




    public T get(int i){
        if(i < 0 || i > n-1) throw new IndexOutOfBoundsException();
        return a[(j+i) % a.length];
    }

    public T set(int i, T x){
        if(i < 0 || i > n-1) throw new IndexOutOfBoundsException();
        T y = a[(j+i) % a.length];
        a[(j+i) % a.length] = x;
        return y;
    }






    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean offer(T t) {
        return false;
    }

    @Override
    public T poll() {
        return null;
    }

    @Override
    public T peek() {
        return null;
    }
}
