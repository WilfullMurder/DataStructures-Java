package org.example;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the List interface stores its elements in a list of r arrays called blocks that are numbered 0,1,...,r-1.
 * Therefore, r blocks contain a total of 1+2+3+...+r = r(r+1)/2 elements.
 * The total space used beyond what is required to store elements is O(sqrt(n)).
 * So, there is never more than O(sqrt(size()) space being used to store anything other than the List elements themselves.
 * As we might expect, the elements of the list are laid out in order within the blocks.
 * The list element with index 0 is stored in block 0, elements with list indices 1 and 2 are stored in block 1, elements with list indices 3, 4, and 5 are stored in block 2, and so on.
 *
 * Insertions and removals take O(size() - i) amortized time.
 * @param <T> the type of data stored in the list
 */
public class RootishArrayStack<T> extends AbstractList<T> {

    Factory<T> f;
    List<T[]> blocks;
    int n;

    public RootishArrayStack(Class<T> t){
        f = new Factory<>(t);
        n = 0;
        blocks = new ArrayList<T[]>();
    }


    /**
     * Determines the value of b.
     * If index i is in block b, then the number of elements in blocks 0,...,b-1 is b(b+1)/2.
     * Therefore, i is stored at location  $j=i-b(b+1)/2 within block b.
     * The number of elements that have indices less than or equal to i is i+1.
     * However, the number of elements in blocks 0,...,b is (b+1)(b+2)/2.
     * Therefore b is the smallest integer such that; (b+1)(b+2)/2 >= i+1 which can be rewritten as the quadratic equation: b^2+3b-2i>=0
     * The equation has two solutions:
     * b1 = (-3+sqrt(9+8i)/2), b2 = (-3-sqrt(9+8i)/2).
     * As b2 will always return a negative it makes no sense in the application.
     * As b1 will not return an integer, and we want the smallest integer b we simply want the ceiling of the equation b1.
     * @param i
     * @return
     */
    protected static int i2b(int i){
        double db = (-3.0 + Math.sqrt(9 + 8*i)) / 2.0;
        int b = (int)Math.ceil(db);
        return b;
    }


    //get(i) and set(i,x) are straightforward.
    //First compute the appropriate block b and the appropriate index j within the block and then perform the appropriate operation
    //If we use an ArrayDeque, ArrayQueue or ArrayStack then get(i) && set(i,x) run in constant time.

    /**
     * @param i index of the element to return
     * @return
     */
    @Override
    public T get(int i) {
       if(i<0 || i>n-1) throw new IndexOutOfBoundsException();
       int b = i2b(i);
       int j = i-b*(b+1)/2;
       return blocks.get(b)[j];
    }

    /**
     * @param i index of the element to replace
     * @param x element to be stored at the specified position
     * @return
     */
    public T set(int i, T x){
        if(i<0 || i>n-1) throw new IndexOutOfBoundsException();
        int b = i2b(i);
        int j = i-b*(b+1)/2;
        T y = blocks.get(b)[j];
        blocks.get(b)[j] = x;
        return y;
    }

    /**
     * First check to see if our data structure is full, by checking if the number of blocks, r, is such that r(r+1)/2 = n.
     * If so, we call grow() to add another block.
     * With this done, we shift elements with indices i,...,n-1 to the right by one position to make room for the new element with index i.
     * Ignoring the cost of the grow() operation, the cost of an add(i,x) operation is dominated by the cost of shifting and is therefore O(1+n-i), just like an ArrayStack.
     * @param i index at which the specified element is to be inserted
     * @param x element to be inserted
     */
    public void add(int i, T x){
        if(i<0 || i>n-1) throw new IndexOutOfBoundsException();
        int r = blocks.size();
        if(r*(r+1)/2 < n + 1) grow();
        n++;
        for(int j= n-1; j > i; j--){
            set(j, get(j-1));
        }
        set(i, x);
    }

    /**
     * Shifts the elements with indices i+1,...,n left by one position and then,
     * if there is more than one empty block, it calls the shrink() method to remove all but one of the unused blocks.
     * Ignoring the cost of the shrink() operation, the cost of a remove(i) operation is dominated by the cost of shifting and is therefore O(n-i).
     * @param i the index of the element to be removed
     * @return
     */
    public T remove(int i){
        if(i<0 || i>n-1) throw new IndexOutOfBoundsException();
        T x = get(i);
        for(int j = i; j<n-1; j++){
            set(j, get(j+1));
        }
        n--;
        int r = blocks.size();
        if((r-2)*(r-1)/2 >= n) shrink();
        return x;
    }


    //grow() and shrink() operations do not copy any data, only allocate or free an array of size r.
    //in some environments, this takes only constant time, in others, it may require time proportional to r.
    //@Note immediately after a call to grow() or shrink(), the final block is completely empty and all other blocks are completely full.
    //  Therefore, another call will not happen until at least r-1 elements have been added or removed.
    //  So, even if grow() or shrink() take O(r) time, this cost can be amortized over at least r-1 add(i,x) or remove(i) operations to O(1) per operation.

    /**
     * adds a new block
     */
    private void grow() {
        blocks.add(f.newArray(blocks.size()+1));
    }

    /**
     * Removes all but one of the unused blocks
     */
    private void shrink() {
        int r = blocks.size();
        while(r>0 && (r-2)*(r-1)/2 >= n){
            blocks.remove(blocks.size()-1);
            r--;
        }
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public void clear() {
        blocks.clear();
        n = 0;
    }
}
