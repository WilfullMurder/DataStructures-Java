package org.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Hash tables are an efficient method of storing a small number, n, of integers from a large range 𝑈 = {0,...,2^w-1}.
 * The term hash table includes a broad range of data structures.
 * This class focuses on one of the two most common implementations of hash tables: hashing with chaining, using multiplicative hashing.
 * Very often hash tables store types of data that are not integers.
 * In this case, an integer hash code is associated with each data item and is used in the hash table.
 * Some methods used in this class require random choices of integers in some specific range.
 * Some of these "random" integers are hard-coded constants.
 * These constants were obtained using random bits generated from atmospheric noise.
 * @param <T>
 */

public class ChainedHashTable<T> implements USet<T> {

    /**
     * The hash table
     */
    List<T>[] t;

    /**
     * The "dimension" of the table (table.length = 2^d)
     */
    int d;

    /**
     * total number of elements in the has table
     */
    int n;

    /**
     * the multiplier
     */
    int z;

    /**
     * The number of bits in an int
     */
    protected static final int w = 32;

    public ChainedHashTable(){
        d = 1;
        t = allocTable(1<<d);
        Random r  = new Random();
        z = r.nextInt() | 1;
    }

    /**
     * Allocate and initialize a new empty hash table
     * @param s new size
     * @return new table
     */
    private List<T>[] allocTable(int s) {
        List<T>[] tab = new ArrayList[s];
        for(int i = 0; i < s;  i++){
            tab[i] = new ArrayList<T>();
        }
        return tab;
    }

    @Override
    public int size() {
        return n;
    }

    /**
     * First check if the length of t needs to be increased and, if so, we grow t.
     * Then we hash x to get an integer, i, in the range {0,...,t.length-1}, and we append x to the list t[i].
     * Besides growing, the only other work done when adding a new value x involves appending x to the list t[hash(x)].
     * @param x
     * @return
     */
    @Override
    public boolean add(T x) {
       if(find(x) != null) return false; // x already exists
       if(n+1 > t.length) resize();
       t[hash(x)].add(x);
       n++;
       return true;
    }

    /**
     * The performance of the hash table depends critically on the choice of the hash function.
     * A good hash function will spread the elements evenly among the t.length lists,
     * so that the expected size of the list t[hash(x)] is O(n/t.length) = O(1).
     * Conversely, a bad hash function will hash all values (including x) to the same table location,
     * in which case the size of the list t[hash(x)] will be n.
     * Multiplicative hashing is an efficient method of generating hash values based on modular arithmetic and integer division.
     * It uses the div operator, which calculates the integral part of the quotient, while discarding the remainder.
     * Formally, for any integers a >= 0 and b >= 1, a div b = ⎣a/b⎦.
     * In multiplicative hashing, we use a hash table of size 2^d for some integer d (dimension).
     * The formula for hashing an integer x∈{0,...,2^w-1} is: hash(x) = ((zx)mod2^w)div2^w-d.
     * Here, z is a randomly chosen odd integer in {1,...,2^w-1}.
     * This hash function can be realised very efficiently by observing that, by default,
     * operations on integers are already done modulo 2^w where w is the number of bits in an integer.
     * Furthermore, integer division by 2^w-d is equivalent to dropping the rightmost w-d bits in binary representation (bit shift >>>).
     * In this way, the code that implements the above formula is simpler than the formula itself.
     * @param x
     * @return
     */
    private int hash(Object x) {
        return (z*x.hashCode()) >>> (w-d);
    }

    /**
     * Resize the table so it has size 2^d.
     * Growing the table, if necessary, involves doubling the length of t and reinserting all elements into the new table.
     * This strategy is exactly the same as the one used in the implementation of ArrayStack and the result applies:
     * the cost of growing is only constant when amortized over a sequence of insertions.
     */
    private void resize() {
        d = 1;
        while(1<<d <= n) d++;
        n = 0;
        List<T>[] oldTable = t;
        t = allocTable(1<<d);
        for(int i = 0; i < oldTable.length; i++){
            for(T x: oldTable[i]){
                add(x);
            }
        }
    }

    /**
     * To remove an element, x, from the hash table, we iterate over the list t[hash(x)] until we find x so that we can remove it.
     * This takes O(n_has(x)) time, where n_i denotes the length of the list stored at t[i]
     * @param x
     * @return
     */
    @Override
    public T remove(T x) {
        Iterator<T> it = t[hash(x)].iterator();
        while(it.hasNext()){
            T y = it.next();
            if(y.equals(x)){
                it.remove();
                n--;
                return y;
            }
        }
        return null;
    }

    /**
     * Searching for the element, x, in a hash table is similar.
     * We perform a linear search on the list t[hash(x)].
     * This takes time proportional to the length of the list t[hash(x)].
     * @param x
     * @return
     */
    @Override
    public T find(Object x) {
       for(T y : t[hash(x)]){
           if(y.equals(x)) return y;
       }
       return null;
    }

    @Override
    public void clear() {
        d = 1;
        t = allocTable(1<<d);
        n = 0;
    }

    @Override
    public Iterator<T> iterator() {
        class IT implements Iterator<T> {

            int i, j;
            int ilast, jlast;

            IT(){
                i = 0;
                j = 0;
                while(i < t.length && t[i].isEmpty()){
                    i++;
                }

            }

            protected void jumpToNext(){
                while(i < t.length && j + 1 > t[i].size()){
                    j=0;
                    i++;
                }
            }

            @Override
            public boolean hasNext() {
                return i < t.length;
            }

            @Override
            public T next() {
               ilast = i;
               jlast = j;
               T x = t[i].get(j);
               j++;
               jumpToNext();
               return x;
            }
            public void remove() {ChainedHashTable.this.remove(t[ilast].get(jlast));}
        }
        return new IT();
    }


    public static void main(String[] args){
        int n = 100000;
        ChainedHashTable<Integer> t = new ChainedHashTable<Integer>();
        for(int  i= 0; i < n; i++){
            t.add(i*2);
        }
        for(int i = 0; i < 2*n; i++){
            Integer x = t.find(i);
            if(i % 2 == 0){
                assert(x.intValue() == i);
            } else{
                assert(x == null);
            }
        }
    }
}
