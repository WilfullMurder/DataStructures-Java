package org.example.Queues;

import org.example.Utils.Factory;

import java.util.AbstractList;


/**
 * This structure implements the List interface by using the same circular array technique used to represent an ArrayQueue.
 * Allows for efficient addition and removal at both ends.
 * The List item of rank 'i' is stored at a[(j+i)%a.length].
 * Insertions and removals at position 'i' take O(1+min{i, size()-i}) amortized time.
 * @param <T> the type of data stored in this list
 */
public class ArrayDeque<T> extends AbstractList<T> {

    /**
     * The class of elements stored in this queue
     */
    Factory<T> f;
    /**
     * Array used to store elements
     */
    protected T[] a;
    /**
     * Index of next element to de-queue
     */
    int j;
    /**
     * Number of elements in the queue
     */
    int n;


    /**
     * Constructor
     */
    public ArrayDeque(Class<T> t){
        f = new Factory<>(t);
        a = f.newArray(1);
        j = 0;
        n = 0;
    }

    //The get(i) and set(i,x) operations on an ArrayDeque are straightforward.
    //They get or set the array element a[(j+i)%a.length].

    /**
     * Get the element at a[(j+i)%a.length]
     * @param i the given index
     * @return the element
     */
    public T get(int i){
        if(i < 0 || i > n-1) throw new IndexOutOfBoundsException();
        return a[(j+i) % a.length];
    }

    /**
     * Set the element at a[(j+i)%a.length]
     * @param i the given index
     * @param x the element to set
     * @return the last element a[(j+i)%a.length]
     */
    public T set(int i, T x){
        if(i < 0 || i > n-1) throw new IndexOutOfBoundsException();
        T y = a[(j+i) % a.length];
        a[(j+i) % a.length] = x;
        return y;
    }

    /**
     * We want the add(i, x) operation to be fast when 'i' is small (close to 0) or when 'i' is large (close to 'n').
     * Therefore, we check if 'i'<'n'/2.
     * If so, we shift the elements a[0],...,a[i-1] left by one position.
     * Otherwise ('i' >= 'n'/2), we shift the elements a[i],...,a[n-1] right by one position.
     * Shifting in this way, we guarantee that add(i,x) never has to shift more than min{i, n-i} elements.
     * Thus, the running time of the add(i,x) operation (ignoring the cost of a resize()) is O(1 + min{i, n-i}).
     * @param i the given index
     * @param x the element to add
     */
    public void add(int i, T x){
        if(i < 0 || i > n) throw new IndexOutOfBoundsException();
        if(n+1 > a.length) resize();
        if(i < n/2) { // shift a[0],..,a[i-1] left one position
            j = (j==0) ? a.length - 1 : j - 1; //(j-1)%a.length
            for(int k = 0; k<= i-1; k++){
                a[(j+k)%a.length] = a[(j+k+1)%a.length];
            }
        } else { // shift a[i],..,a[n-1] right one position
            for(int k = n; k > i; k--){
                a[(j+k%a.length)] = a[(j+k-1)%a.length];
            }
        }
        a[(j+i)%a.length] = x;
        n++;
    }


    /**
     * Implementation of the remove(i) operation is similar to add(i,x).
     * It either shifts elements a[0],...,a[i-1] right by one position or,
     * shifts the elements a[i+1]}},...,a[n-1] left by one position depending on whether 'i'<n/2.
     * This means that remove(i) never spends more than O(1+min{i,n-i}) time to shift elements.
     * @param i
     * @return
     */
    public T remove(int i){
        if(i < 0 || i > n-1) throw new IndexOutOfBoundsException();
        T x = a[(j+i)%a.length];
        if(i < n/2) { // shift a[0],..,a[i-1] right one position
            for(int k = i; k > 0; k--){
                a[(j+k)%a.length] = a[(j+k-1)%a.length];
            }
            j = (j+1) % a.length;
        } else { // shift a[i+1],..,a[n-1] left one position
            for(int k = i; k < n-1; k++){
                a[(j+k)%a.length] = a[(j+k+1)%a.length];
            }
        }
        n--;
        if(3*n < a.length) resize();
        return x;
    }

    /**
     * resize the backing array
     */
    private void resize() {
        T[] b = f.newArray(Math.max(2*n,1));
        for (int k = 0; k < n; k++)
            b[k] = a[(j+k) % a.length];
        a = b;
        j = 0;
    }

    public void clear(){
        n = 0;
        resize();
    }

    @Override
    public int size() {
        return n;
    }


}
