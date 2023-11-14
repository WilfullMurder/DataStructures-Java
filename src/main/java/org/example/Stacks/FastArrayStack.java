package org.example.Stacks;

/**
 * Subclass of ArrayStack that overrides some functions for speed.
 * Most of the work done by an ArrayStack involves the shifting and copying of data (by add(i,x), remove(i) and resize()).
 * In the super implementation, this was done using for loops.
 * Turns out that many programming environments have specific functions that are very efficient at copying and moving blocks of data.
 * In Java there is the System.arraycopy(s,i,d,j,n) method.
 * Although using this functions does not asymptotically decrease the running times, it can still be a worthwhile optimization.
 * The use of the native System.arraycopy(s,i,d,j,n) resulted in speedups of a factor between 2 and 3, depending on the types of operations performed.
 * Mileage may vary.
 * @param <T>
 */
public class FastArrayStack<T>  extends ArrayStack<T>{
    /**
     * Constructor
     * @param t the type of objects that are stored in this list
     */
    public FastArrayStack(Class<T> t) {super(t);}

    @Override
    protected void resize() {
        T[] b = f.newArray(Math.max(2*n, 1));
        System.arraycopy(a, 0, b, 0, n);
        a=b;
    }

    @Override
    protected void resize(int nn) {
        T[] b = f.newArray(nn);
        System.arraycopy(a, 0, b, 0, n);
        a=b;
    }

    public void add(int i, T x){
        if(i<0 || i > n-1) throw new IndexOutOfBoundsException();
        if(n + 1 > a.length) resize();
        System.arraycopy(a, i+1, a, i, n-i-1);
        a[i] = x;
        n++;
    }

    public T remove(int i){
        if(i<0 || i > n-1) throw new IndexOutOfBoundsException();
        T x = a[i];
        System.arraycopy(a, i+1, a, i, n-i-1);
        n--;
        if(a.length >= 3*n) resize();
        return x;
    }

}
