package org.example.Heaps;

import org.example.Utils.DefaultComparator;
import org.example.Utils.Factory;

import java.util.*;

/**
 * This class implements a priority queue as a class binary heap
 * stored implicitly in an array
 */
public class BinaryHeap<T> extends AbstractQueue<T> {

    Factory<T> f;

    Comparator<T> c;

    protected T[] a;

    protected int n;

    public BinaryHeap(Class<T> tClass){
        this(tClass, new DefaultComparator<T>());
    }
    public BinaryHeap(Class<T> tClass, Comparator<T> comparator){
        c = comparator;
        f= new Factory<>(tClass);
        a = f.newArray(1);
        n = 0;
    }
    /**
     * Create a new binary heap by heapifying a
     */
    public BinaryHeap(T[] a) {
        this(a, new DefaultComparator<T>());
    }
    public BinaryHeap(T[] a, Comparator<T> c){
        this.c = c;
        this.a = a;
        n = a.length;
        for(int i = n/2-1; i>= 0; i--){
            trickleDown(i);
        }
    }

    protected void trickleDown(int i) {
        do{
            int j = -1;
            int right = right(i);
            if(right < n && c.compare(a[right], a[i]) < 0){
                int left = left(i);
                if(c.compare(a[left], a[right]) < 0){
                    j = left;
                    System.out.println("L");
                }else {
                    j = right;
                    System.out.println("R");
                }
            } else {
                int left = left(i);
                if (left < n && c.compare(a[left], a[i]) < 0) {
                    j = left;
                    System.out.println("L");
                }
            }
            if(j >= 0) swap(i, j);
            i = j;
        } while(i >= 0);
    }

    protected int right(int i) {
        return (i*2)+2;
    }
    protected int left(int i) {
        return (i*2)+1;
    }
    protected int parent(int i){
        return (i-1)/2;
    }
    protected void swap(int i, int j) {
        System.out.println("X" + a[j]);
        T x = a[i];
        a[i] = a[j];
        a[j] = x;
    }

    public void clear(){
        a = f.newArray(1);
        n = 0;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public boolean offer(T x) {
        return add(x);
    }

    public boolean add(T x){
        if(n + 1 > a.length) resize();
        a[n++] = x;
        System.out.println("H" + n);
        bubbleUp(n-1);
        System.out.println("I" + x);
        return true;
    }

    protected void resize() {
        T[] b = f.newArray(Math.max(2*n, 1));
        System.arraycopy(a, 0, b, 0, n);
        a = b;
    }

    protected void bubbleUp(int i){
        int p = parent(i);
        while(i > 0 && c.compare(a[i], a[p]) < 0){
            swap(i, p);
            i = p;
            p = parent(i);
        }
    }

    @Override
    public T poll() {
        return remove();
    }

    public T remove(){
        T x = a[0];
        System.out.println("M" + x);
        a[0] = a[--n];
        trickleDown(0);
        if(3*n < a.length) resize();
        System.out.println("X" + a[0]);
        return x;
    }

    @Override
    public T peek() {
        return a[0];
    }

    /**
     * An implementation of the heap sort algorithm
     */
    public static <T extends Comparable<T>> void sort(T[] a){
        sort(a, new DefaultComparator<T>());
    }

    public static <T> void sort(T[] a, Comparator<T> c){
        BinaryHeap<T> h = new BinaryHeap<T>(a, c);
        while(h.n < h.a.length){
            h.swap(++h.n, 0);
            h.bubbleUp(0);
        }
        Collections.reverse(Arrays.asList(a));
    }

    @Override
    public Iterator<T> iterator() {
        class PQI implements Iterator<T>{
            int i;

            public PQI(){
                i = 0;
            }

            @Override
            public boolean hasNext() {
                return i < n;
            }

            @Override
            public T next() {
                return a[i++];
            }
            public void remove(){
                throw new UnsupportedOperationException();
            }
        }
        return new PQI();
    }

    //keys 91 1 45 41 44 73 30 94 43 4
    public static void main(String[] args){
        BinaryHeap<Integer> heap = new BinaryHeap<Integer>(Integer.class);
        System.out.println("\nHeap Trace");
        heap.add(91);
        heap.add(1);
        heap.add(45);
        heap.add(41);
        heap.add(44);
        heap.add(73);
        heap.add(30);
        heap.add(94);
        heap.add(43);
        heap.add(4);

        System.out.println("\nHeap Build");
        Integer[] a = {91, 1, 45, 41,44,73,30,94,43,4};
        BinaryHeap.sort(a);

        System.out.println("\nHeap Sort Trace");
        for (int i = 0; i < a.length; i++){
            heap.remove();
        }

    }
}
