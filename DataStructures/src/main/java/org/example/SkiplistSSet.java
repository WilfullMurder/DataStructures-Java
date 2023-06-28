package org.example;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

/**
 * Uses a skiplist structure to implement the SSet interface.
 * When used in this way, the list L_0 stores the elements of the SSet in sorted order.
 * @param <T>
 */
public class SkiplistSSet<T> implements SSet<T> {

    protected Comparator<T> c;

    /**
     * This Node sits on the left side of the skiplist
     * Acts as a dummy node for the list
     */
    protected Node<T> sentinel;

    /**
     * The max height of any element
     * The height of a skiplist is the height of its tallest node.
     */
    int h;

    /**
     * The number of elements stored in the skiplist.
     */
    int n;

    /**
     * A source of random numbers
     */
    Random rand;

    /**
     * Used by add(x) method
     */
    protected Node<T>[] stack;

    public SkiplistSSet(Comparator<T> c){
        this.c = c;
        n = 0;
        sentinel = new Node<T>(null, 32);
        stack = (Node<T>[])Array.newInstance(Node.class, sentinel.next.length);
        h = 0;
        rand = new Random();
    }

    public SkiplistSSet(){this(new DefaultComparator<T>());}

    /**
     * Find node<T>, u, that precedes the value x in the skip list
     *  follows the search path for the smallest value y such that y >= x.
     * @param x the value to search for
     * @return a node<T>, u, that maximises u.x subject to the constraint that:
     *          u.x < x --- or sentinel if u.x >= x for all node<T>'s x
     */
    protected Node<T> findPredNode(T x){
        Node<T> u = sentinel;
        int r = h;
        while (r >= 0){
            while (u.next[r] != null && c.compare(u.next[r].x, x) < 0){
                u = u.next[r];
            }
            r--;
        }
        return u;
    }

    /**
     * Before we can add an element, we need to simulate tossing coins to determine the height, k, of a new node.
     * We do so by picking a random integer, z, and counting the number of trailing 1's in the binary representation of z.
     * @Note this code will never generate a number, k > 32.
     * @return the number of coin tosses - 1
     */
    int pickHeight(){
        int z = rand.nextInt();
        int k = 0;
        int m = 1;
        while((z & m) != 0){
            k++;
            m <<= 1;
        }
        return k;
    }

    public Finger getFinger(){return new Finger();}

    public T find(Finger f, T x){
        int r = 0;
        Node<T> u = f.s[r];
        //find an edge that passes over x
        while(r < h
                && ((u != sentinel && c.compare(x, u.x) <= 0)
                || (u.next[r] != null && c.compare(x, u.next[r].x) > 0))){
            u = f.s[++r];
        }
        r--;
        while(r >= 0){
            while(u.next[r] != null && c.compare(u.next[r].x, x) < 0){
                u = u.next[r];
            }
            f.s[r] = u;
            r--;
        }
        return u.next[0] == null ? null : u.next[0].x;
    }

    @Override
    public Comparator<? super T> comparator() {
        return c;
    }

    @Override
    public int size() {
        return n;
    }

    /**
     * When situated at some node, u, in L_r, we look right to u.next[r].x.
     * If x > u.next[r].x, then we take a step to the right in L_r; otherwise, we move down into L_r-1.
     * Each step (right or down) in this search takes only constant time.
     * So, the expected running time is O(log n).
     * @param x
     * @return
     */
    @Override
    public T find(T x) {
        Node<T> u = findPredNode(x);
        return u.next[0] == null ? null : u.next[0].x;
    }

    @Override
    public T findGE(T x) {
        if(x == null){ //return first node<T>
            return sentinel.next[0] == null ? null : sentinel.next[0].x;
        }
        return find(x);
    }

    @Override
    public T findLT(T x) {
        if(x == null) { //return last node<T>
            Node<T> u = sentinel;
            int r = h;
            while(r >= 0){
                while (u.next[r] != null){
                    u = u.next[r];
                }
                r--;
            }
            return u.x;
        }
        return findPredNode(x).x;
    }

    /**
     * We search for x and then splice x into a few lists L_0,...,L_k, where k is selected using the pickHeight() method.
     * We do this by using an array, stack, that tracks the nodes at which the search path goes down from some list L_r into L_r-1.
     *  More precisely, stack[r] is the node in L_r where the search path proceeded down into L_r-1.
     *  The nodes that we modify to insert x are precisely the nodes stack[0],...,stack[k].
     * @param x
     * @return
     */
    @Override
    public boolean add(T x) {
        Node<T> u = sentinel;
        int r = h;
        int comp = 0;
        while(r >= 0){
            while(u.next[r] != null && (comp = c.compare(u.next[r].x, x) ) < 0){
                u = u.next[r];
            }
            if(u.next[r] != null && comp == 0) return false;
            stack[r--] = u; //going down, store u
        }
        Node<T> w = new Node<T>(x, pickHeight());
        while( h < w.height()){
            stack[++h] = sentinel; //height increased
        }
        for(int i = 0; i < w.next.length; i++){
            w.next[i] = stack[i].next[i];
            stack[i].next[i] = w;
        }
        n++;
        return true;

    }

    /**
     * Similar to add, except there is no need for stack to track the search path,
     * as the removal can be done whilst following the search path.
     * We search for x and each time the search moves downward from node u,
     * we check if u.next.x = x and if so we splice u out of the list.
     * @param x value to be removed
     * @return true if the value was removed
     */
    @Override
    public boolean remove(T x) {
        boolean isRemoved = false;
        Node<T> u = sentinel;
        int r = h;
        int comp = 0;
        while(r >= 0){
            while (u.next[r] != null && (comp = c.compare(u.next[r].x,x)) < 0){
                u = u.next[r];
            }
            if(u.next[r] != null && comp == 0){
                isRemoved = true;
                u.next[r] = u.next[r].next[r];
                if(u == sentinel && u.next[r] == null) h--; //height has gone down
            }
            r--;
        }
        if(isRemoved) n--;
        return isRemoved;
    }

    @Override
    public void clear() {
        n = 0;
        h = 0;
        Arrays.fill(sentinel.next, null);
    }

    @Override
    public Iterator<T> iterator(T x) {
        return iterator(findPredNode(x));
    }

    @Override
    public Iterator<T> iterator() {
        return iterator(sentinel);
    }

    protected Iterator<T> iterator(Node<T> u){
        class SkiplistIterator implements Iterator<T>{

            Node<T> u, prev;

            public SkiplistIterator(Node<T> u){
                this.u = u;
                prev = null;
            }

            @Override
            public boolean hasNext() {
                return u.next[0] != null;
            }

            @Override
            public T next() {
                prev = u;
                u = u.next[0];
                return u.x;
            }

            public void remove(){
                SkiplistSSet.this.remove(prev.x);
            }
        }
        return new SkiplistIterator(u);
    }

    protected static class Node<T>{
        T x;
        Node<T>[] next;
        public Node(T ix, int h){
            x = ix;
            next = (Node<T>[]) Array.newInstance(Node.class, h+1);
        }

        public int height(){return next.length - 1;}
    }

    public class Finger{
        protected Node<T>[] s;

        public Finger(){
            s = (Node<T>[])Array.newInstance(Node.class, h+1);
            for(int r = 0; r <= h; r++) s[r] = sentinel;
        }
    }


    public static void main(String[] args){
        int n = 100000;
        SkiplistSSet<Integer> sl = new SkiplistSSet<Integer>();
        System.out.println("Adding " + n + " elements");
        for(int i = 0; i < n; i++){
            sl.add(2*i);
        }
        System.out.println("Searching");
        for(int i = 0; i < 2*n; i++){
            Integer x = sl.find(i);
            Utils.myassert(i > 2*(n-1) || Math.abs(x - i) <= 1);
        }
        System.out.println("Searching (sequential - with finger)");
        SkiplistSSet<Integer>.Finger f = sl.getFinger();
        for(int i = 0; i < 2*n; i++){
            Integer x = sl.find(f, i);
            Utils.myassert(i > 2*(n-1) || Math.abs(x - i) <= 1);
        }
        System.out.println("Searching (random - with finger)");
        Random r = new Random();
        for(int i = 0; i < 2*n; i++){
            int j = r.nextInt(2*n);
            Integer x = sl.find(f, j);
            Utils.myassert(j > 2*(n-1) || Math.abs(x - j) <= 1);
        }
        System.out.println("Removing");
        for(int i =0; i < n/2; i++){
            sl.remove(4*i);
        }
        System.out.println("Verifying");
        for(int i = 0; i < 2*n; i++){
            Integer x = sl.find(i);
            Utils.myassert(i > 2*(n-1) || Math.abs(x - i) <= 3);
        }
        System.out.println("Completed - size() = " + sl.size());
    }

}
