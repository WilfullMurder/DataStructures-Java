package org.example.Lists;

import java.lang.reflect.Array;
import java.lang.IllegalStateException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;


/**
 * A beautiful data structure which has a variety of applications.
 * Using a skiplist we can implement a List that has O(log n) time implementations of get(x), set(i, x), add(i, x), and remove(i).
 * We can also implement an SSet in which all operations run in O(log n) expected time.
 * Conceptually, a skiplist is a sequence of singly-linked lists L_0,...,L_h.
 * Each list L_r contains a subset of the items inL_r-1.
 * We start with the input list L_0 that contains n items and construct L_1 from L_0, L_2 from L_1, and so on.
 * The items in L_r are obtained by tossing a coin for each element, x, in L_r-1 and including x in L_r if the coin turns up as heads.
 * This process ends when we create a list L_r that is empty.
 * The key property of skiplists is that there is a short path, called the search path, from the sentinel in L_h to every node in L_0.
 * To construct a search path for a node, u, is easy:
 *  Start in the top left corner of your skiplist (the sentinel in L_h) and always go right unless that would overshoot u,
 *  in which case you should take a step down into the list below.
 * @param <T> the type of data stored in this list
 */
public class SkiplistList<T> extends AbstractList<T> {

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
     * A source of random numbers.
     */
    Random rand;


    public SkiplistList(){
        n = 0;
        sentinel = new Node<>(null, 32);
        h = 0;
        rand = new Random(0);
    }

    /**
     * Find the node that precedes list index i in the skiplist.
     * @param i the index to find
     * @return the predecessor of the node at index i or the final node if 'i' exceeds size() - 1.
     */
    protected Node<T> findPred(int i){
        Node<T> u = sentinel;
        int r = h;
        int j = -1; // index of the current node in list 0
        while (r >= 0){
            while(u.next[r] != null && j + u.length[r] < i){
                j += u.length[r];
                u = u.next[r];
            }
            r--;
        }
        return u;
    }

    /**
     * Since the hardest part of the operation is finding the ith node in L_0,
     *  this operation runs in O(log n) time.
     * @param i index of the element to return
     * @return
     */
    @Override
    public T get(int i) {
        if (i < 0 || i > n-1) throw new IndexOutOfBoundsException();
        return findPred(i).next[0].x;
    }

    /**
     * Since the hardest part of the operation is finding the ith node in L_0,
     *  this operation runs in O(log n) time.
     * @param i index of the element to replace
     * @param x element to be stored at the specified position
     * @return
     */
    @Override
    public T set(int i, T x) {
        if (i < 0 || i > n-1) throw new IndexOutOfBoundsException();
        Node<T> u = findPred(i);
        T y = u.x;
        u.x = x;
        return y;
    }
    /**
     * @param i index at which the specified element is to be inserted
     * @param x element to be inserted
     */
    @Override
    public void add(int i, T x) {
        if (i < 0 || i > n) throw new IndexOutOfBoundsException();
        Node<T> w = new Node<T>(x, pickHeight());
        if(w.height() > h) h = w.height();
        add(i, w);
    }

    /**
     * We follow the search path for the node at position i.
     * Each time the search path takes a step down from a node, u,
     *  at level r we decrement the length of the edge leaving u at that level.
     * We also check if u.next[r] is the element of rank i and, if so,
     *  splice it out of the list at that level.
     * @param i the index of the element to be removed
     * @return the value that was removed
     */
    @Override
    public T remove(int i) {
        if (i < 0 || i > n-1) throw new IndexOutOfBoundsException();
        T x = null;
        Node<T> u = sentinel;
        int r = h;
        int j = -1; // index of node u
        while(r >= 0){
            while(u.next[r] != null && j+u.length[r] < i){
                j += u.length[r];
                u = u.next[r];
            }
            u.length[r]--; //for the node being removed
            if(j+u.length[r] + 1 == i && u.next[r] != null){
                x = u.next[r].x;
                u.length[r] += u.next[r].length[r];
                u.next[r] = u.next[r].next[r];
                if(u == sentinel && u.next[r] == null) h--;
            }
            r--;
        }
        n--;
        return x;
    }

    /**
     * Adding an element at a position, i, is fairly simple.
     * Unlike in a skiplistSSet, we are sure that a new node will actually be added,
     *  so we can do the addition at the same time as we search for the new node's location.
     * We first pick the height, k, of the newly inserted node, w, and then follow the search path for 'i'.
     * Any time the search path moves down from L_r with r <= k, we splice w into L_r.
     * The only extra care needed is to ensure that the lengths of edges are updated properly.
     * @Note Each time the search path goes down at a node, u, in L_r, the length of the edge u.next[r] increases by one,
     *   since we are adding an element below that edge position 'i'.
     * While following the search path we are already keeping track of the position, j, of u in L_0.
     * Therefore, we know the length of the edge from u to w is i - j.
     * We can also deduce the length of the edge from w to z from the length, ℓ, of the edge from u to z.
     * So, we can splice in w and update the lengths of the edges in constant time.
     * @param i index at which the specified element is to be inserted
     * @param w the node to be inserted
     * @return the last node at position i
     */
    protected Node<T> add(int i, Node<T> w) {
        Node<T> u = sentinel;
        int k = w.height();
        int r = h;
        int j = -1; // index of u
        while(r >=0){
            while(u.next[r] != null && j+u.length[r] < i){
                j += u.length[r];
                u = u.next[r];
            }
            u.length[r]++; //accounts for new node in list 0
            if(r <= k){
                w.next[r] = u.next[r];
                u.next[r] = w;
                w.length[r] = u.length[r] - (i - j);
                u.length[r] = i - j;
            }
            r--;
        }
        n++;
        return u;
    }

    /**
     * Before we can add an element, we need to simulate tossing coins to determine the height, k, of a new node.
     * We do so by picking a random integer, z, and counting the number of trailing 1's in the binary representation of z.
     * @Note this code will never generate a number, k > 32.
     *@return the number of coin tosses - 1

     */
    protected int pickHeight() {
        int z = rand.nextInt();
        int k = 0;
        int m = 1;
        while((z & m) != 0){
            k++;
            m <<= 1;
        }
        return k;
    }

    @Override
    public void clear() {
        n = 0;
        h = 0;
        Arrays.fill(sentinel.length, 0);
        Arrays.fill(sentinel.next, null);
    }

    @Override
    public int size() {
        return n;
    }

    public Iterator<T> iterator(){
        class SkiplistIterator implements Iterator<T> {

            Node<T> u;

            int i;

            boolean isRemovable;

            public SkiplistIterator(){
                u = sentinel;
                i = -1;
                isRemovable = false;
            }

            @Override
            public boolean hasNext() {
                return u.next[0] != null;
            }

            @Override
            public T next() {
               if(u.next[0] == null) throw new NoSuchElementException();
               u = u.next[0];
               i++;
               isRemovable = true;
               return u.x;
            }
            public void remove(){
                if(!isRemovable) throw new IllegalStateException();
                SkiplistList.this.remove(i);
                i--;
                isRemovable = false;
            }
        }
        return new SkiplistIterator();
    }


    class Node<T>{
        T x;
        Node<T>[] next;

        int[] length;

        /**
         * We need a way to follow the search path for the ith element in L_0.
         * Easiest way is to define the notion of the length of an edge in some list, L_r.
         * We define the length of every edge in L_0 as 1.
         * The length of an edge, e, in L_r, r > 0, is defined as the sum of lengths of the edges below e in L_r-1.
         * Equivalently, the length of e is the number of edges in L_0 below e.
         * Since the edges of skiplists are stored in arrays, the lengths can be stored the same way.
         * The useful property of this definition of length is that,
         *  if we are currently at a node at position j in L_0, and we follow an edge of length ℓ,
         *  then we move to a node whose position is, in L_0, is j + ℓ.
         * In this way, while following a search path, we can keep track of position, j, of the current node in L_0.
         * When at a node, u, in L_r, we go right if j + length of edge u.next[r] < i.
         * Otherwise, we go down to L_r-1.
         * @param ix
         * @param h
         */
        Node(T ix, int h){
            x = ix;
            next = (Node<T>[]) Array.newInstance(Node.class, h+1);
            length = new int[h+1];
        }
        int height(){
            return next.length - 1;
        }
    }


    public static void main(String[] args){
        int n = 20;
        List<Integer> l = new SkiplistList<Integer>();
        System.out.println("Populating list");
        for(int i = 0; i < n; i++){
            l.add(i);
        }
        System.out.println(l);
        System.out.println("Adding list * -1");
        for(int i = -1; i > -n; i--){
            l.add(0, i);
        }
        System.out.println(l);
        System.out.println("Splicing new elements");
        for(int i = 0; i < n; i++){
            l.add(n+i, 1000+i);
        }
        System.out.println(l);
    }

}
