package org.example;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Collection;

/**
 * ArrayStack implements the list interface using a backing array 'a',  The list element with index i is stored in a[i].
 * At most times, 'a' is larger than strictly necessary, so an integer, n, is used to keep track of the number of elements actually stored in 'a'.
 * In this way, the list elements are stored in a[0],..., a[n-1] and, at all times, a.length <= n.
 *
 * @param <T> the type of object stored in the list
 */
public class ArrayStack<T> extends AbstractList<T> {


    /**
     * keeps track of the class of objects we store
     */
    Factory<T> f;

    /**
     * The array used to store elements
     */
    T[] a;

    /**
     * The number of elements stored
     */
    int n;


    /**
     * Constructor
     * @param t the type of objects that are stored in this list
     */
    public ArrayStack(Class<T> t){
        f = new Factory<>(t);
    }

    public int size(){
        return n;
    }

    /**
     * check if a given index is out of bounds
     * @param i the given index
     */
    private void checkOutOfBounds(int i){
        if (i<0 || i > n- 1){
            throw new IndexOutOfBoundsException();
        }
    }

    //Accessing and modifying the elements of an ArrayStack using get(i) and set(i,x) is trivial.
    //After performing any necessary bounds-checking we simply return or set, respectively, a[i]

    @Override
    public T get(int i) {
        checkOutOfBounds(i);
        return a[i];
    }

    public T set(int i, T x){
        checkOutOfBounds(i);
        T y = a[i];
        a[i] = x;
        return y;
    }



    /**
     * we first check if 'a' is already full. If so, we call the method resize() to increase the size of 'a'.
     * after a call to resize(), we can be sure that a.length > n.
     * we now shift the elements a[i],...,a[n-1] right by one position to make room for x, set a[i] equal to x, and increment n.
     * @param i index at which the specified element is to be inserted
     * @param x element to be inserted
     */
    //Ignoring the cost of the potential call to resize(), then the cost of the add(i,x) operation is proportional to the number of elements we have to shift to make room for x.
    //Therefore, the cost of this operation (ignoring the cost of resizing a) is O(n-i).
    public void add(int i, T x){
        checkOutOfBounds(i);
        if(n+1 > a.length) resize();
        for(int j = n; j > i; j--){
            a[j] = a[j-1];
            a[i] = x;
            n++;
        }
    }

    /**
     *We shift the elements a[i+1],...,a[n-1] left by one position (overwriting a[i]) and decrease the value of n.
     *After doing this, we check if n is getting much smaller than a.length by checking if a.length >= 3*n.
     *If so, then we call resize() to reduce the size of 'a'.
     * @param i the index of the element to be removed
     * @return
     */
    //If we ignore the cost of the resize() method, the cost of a remove(i) operation is proportional to the number of elements we shift, which is O(n-i).
    public T remove(int i){
        checkOutOfBounds(i);
        T x = a[i];
        for(int j = i; j < n-1; j++){
            a[j] = a[j+1];
        }
        n--;
        if(a.length >= 3*n) resize();
        return x;
    }

    /**
     * Resize the backing array.
     * Allocates a new array 'b'' whose size is 2n and copies the n elements of 'a' into the first n positions in 'b', and then sets 'a' to 'b'
     */
    protected void resize() {
        T[] b = f.newArray(Math.max(n*2, 1));
        for(int i =0; i < n; i++){
            b[i] = a[i];
        }
        a = b;
    }

    //Most of the work done by an ArrayStack involves the shifting and copying of data (by add(i,x), remove(i) and resize()).
    //In the implementations above, this was done using for loops.
    //Turns out that many programming environments have specific functions that are very efficient at copying and moving blocks of data.
    //In Java there is the System.arraycopy(s,i,d,j,n) method.
    //Although using this functions does not asymptotically decrease the running times, it can still be a worthwhile optimization.
    //The use of the native System.arraycopy(s,i,d,j,n) resulted in speedups of a factor between 2 and 3, depending on the types of operations performed.
    //Mileage may vary.

    /**
     * Resize the backing array.
     */
    protected void resize(int nn){
        T[] b = f.newArray(nn);
        System.arraycopy(a, 0, b, 0, n);
        a = b;
    }
    public void addOptimised(int i, T x){
        checkOutOfBounds(i);
        if(n + 1 > a.length) resize();
        System.arraycopy(a, i+1, a, i, n-i-1);
        a[i] = x;
        n++;
    }
    public T removeOptimised(int i){
        checkOutOfBounds(i);
        T x = a[i];
        System.arraycopy(a, i+1, a, i, n-i-1);
        n--;
        if(a.length >= 3*n) resize();
        return x;
    }

    //The following methods are not strictly necessary.
    //The parent class, AbstractList, has default implementations of them, but
    //our implementations are more efficient - especially addAll

    /**
     *Small optimization for a frequently used method
     */
    @Override
    public boolean add(T x) {
        if(n + 1 > a.length) resize();
        a[n++] = x;
        return true;
    }

    /**
     * We override addAll because AbstractList implements it by repeated calls to add(i,x), which can take time O(size()*c.size()).
     * This happens, for example, when i = 0.
     * This version takes time O(size() + c.size()).
     */
    @Override
    public boolean addAll(int i, Collection<? extends T> c) {
        checkOutOfBounds(i);
        int k = c.size();
        if(n + k > a.length) resize(2*(n+k));
        for(int j = n+k-1; j >= i+k; j--){
            a[j] = a[j-k];
        }
        for(T x : c){
            a[i++] =x;
        }
        n+=k;
        return true;
    }

    /**
     * We override this method because AbstractList implements by repeated calls to remove(size()), which takes O(size()) time.
     * This implementation runs in O(1) time.
     */
    @Override
    public void clear() {
        n=0;
        resize();
    }
}
