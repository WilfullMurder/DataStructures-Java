package org.example.Lists;


import org.example.Utils.Factory;
import org.example.Queues.ArrayDeque;

import java.util.AbstractSequentialList;

import java.util.ListIterator;

/**
 * A space-efficient list (SEList) reduces this wasted space using a simple idea:
 * Rather than store individual elements in a DLList, we store a block (array) containing several items.
 * More precisely, an SEList is parameterized by a block size 'b'.
 * Each individual node in an SEList stores a block that can hold up to b+1 elements... Much like a Rootish array stack.
 * An SEList places very tight restrictions on the number of elements in a block:
 * Unless a block is the last block, then that block contains at least b-1 and at most b+1 elements.
 * This means that, if an SEList contains n elements, then it has at most n/(b-1)+1 = O(n/b) blocks.
 * The BoundedDeque(BDeque) for each block contains an array of length b+1 but, for every block except the last, at most a constant amount of space is wasted in this array.
 * The remaining memory used by a block is also constant. This means that the wasted space in an SEList is only O(b+n/b).
 *  By choosing a value of b within a constant factor of sqrt(n), we can make the space-overhead of an SEList approach the sqrt(n) lower bound
 * @param <T> the type of data stored in this list
 */

public class SpaceEfficientLinkedList<T> extends AbstractSequentialList<T> {

    Factory<T> f;

    /**
     * Number of nodes in the list
     */
    int n;

    /**
     * The size of blocks in the list
     */
    int b;

    protected Node dummy;


    public SpaceEfficientLinkedList(int b, Class<T> t){
        this.b = b;
        f = new Factory<T>(t);
        dummy = new Node();
        dummy.next = dummy;
        dummy.prev = dummy;
        n = 0;

    }
    /**
     * SEList is a doubly-linked list of blocks
     */
    class Node{
        BoundedDeque d;
        Node prev, next;

    }

    /**
     * So we can do Deque operations on each block.
     * The BDeque differs from the ArrayDeque in one small way:
     * When a new BDeque is created, the size of the backing array 'a' is fixed at b+1 and never grows or shrinks.
     * The important property of a BDeque is that it allows for the addition or removal of elements at either the front or back in constant time.
     * This will be useful as elements are shifted from one block to another.
     */
    protected class BoundedDeque extends ArrayDeque<T> {

        public BoundedDeque() {
            super(SpaceEfficientLinkedList.this.f.type());
            a = f.newArray(b+1);
        }
        protected void resize(){}
    }

    /**
     * The first challenge we face with an SEList is finding the list item with a given index i.
     * @Note The location of an element consists of two parts:
     *  The node u that contains the block that contains the element with index i;
     *  and the index j of the element within its block.
     */
    protected class Location{
        Node u;
        int j;

        public Location(Node u, int j){
            this.u = u;
            this.j = j;
        }
    }

    /**
     * To find the block that contains a particular element, we proceed the same way as we do in a DLList.
     * We either start at the front of the list and traverse in the forward direction, or at the back of the list and traverse backwards until we reach the node we want.
     * The only difference is that, each time we move from one node to the next, we skip over a whole block of elements.
     * @Note Except for at most one block, each block contains at least b-1 elements,
     *  so each step in our search gets us b-1 elements closer to the element we are looking for.
     *  If we are searching forward, this means that we reach the node we want after O(1 + i/b) steps.
     *  If we search backwards, then we reach the node we want after O(1 + (n-i)/b) steps.
     *  The algorithm takes the smaller of these two quantities depending on the value of i, so the time to locate the item with index i is O(1 + min{1, n-i}/b).
     * @param i the index of the desired Location
     * @return
     */
    protected Location getLocation(int i){
        if(i < n/2){
            Node u = dummy.next;
            while(i >= u.d.size()){
                i -= u.d.size();
                u = u.next;
            }
            return new Location(u, i);
        } else {
            Node u = dummy;
            int idx = n;
            while(i < idx){
                u = u.prev;
                idx -= u.d.size();
            }
            return new Location(u, i-idx);
        }
    }

    /**
     * get a particular index in the correct block
     * @Note dominated by the time it takes to locate the item, so also runs in O(1 + min{1, n-i}/b) time.
     * @param i index of the element to return
     * @return
     */
    public T get(int i){
        if (i < 0 || i > n - 1) throw new IndexOutOfBoundsException();
        Location l = getLocation(i);
        return l.u.d.get(l.j);
    }

    /**
     * set a particular index in the correct block
     * @Note dominated by the time it takes to locate the item, so also runs in O(1 + min{1, n-i}/b) time.
     * @param i index of the element to replace
     * @param x element to be stored at the specified position
     * @return
     */
    public T set(int i, T x){
        if (i < 0 || i > n - 1) throw new IndexOutOfBoundsException();
        Location l = getLocation(i);
        T y = l.u.d.get(l.j);
        l.u.d.set(l.j, x);
        return y;
    }

    /**
     * Adding elements to an SEList is a little more complicated.
     * Before considering the general case, we consider the easier operation, add(x), in which x is added to the end of the list.
     * If the last block is full (or does not exist because there are no blocks yet), then we first allocate a new block and append it to the list of blocks.
     * Now that we are sure that the last block exists and is not full, we append x to the last block.
     *
     * @param x element whose presence in this collection is to be ensured
     * @return
     */
    @Override
    public boolean add(T x) {
        Node last = dummy.prev;
        if(last == dummy || last.d.size() == b+1) last = addBefore(dummy);
        last.d.add(x);
        n++;
        return true;
    }


    /**
     * Things get more complicated when we add to the interior of the list.
     * We first locate i to get the node u whose block contains the ith list item.
     *  The problem is that we want to insert x into u's block,
     *  but we have to be prepared for the case where u's block already contains b+1 elements,
     *  so that it is full and there is no room for x.
     * Let u0,u1,u2,... denote u, u.next, u.next.next and so on.
     * We explore u0,u1,u2,... looking for a node that can provide space for x.
     * Three cases can occur during our space exploration:
     *  1.We quickly (in r + 1 <= b steps) find a node ur whose block is not full.
     *      In this case, we perform r shifts of an element from one block into the next, so that the free space in ur becomes a free space in u0.
     *      We can then insert x into u0's block.
     *  2.We quickly (in r + 1 <= b steps) run off the end of the list of blocks.
     *      In this case, we add a new empty block to the end of the list of blocks and proceed as in the first case.
     *  3.After b steps we do not find any block that is not full.
     *       In this case, u0,...,ub-1 is a sequence of b blocks that each contain b+1 elements.
     *       We insert a new block ub at the end of this sequence and spread the original b(b+1) elements so that each block of u0,...,ub contains exactly b elements.
     *       Now u0's block contains only b elements, so it has room for us to insert x.
     * @Note Running time depends on which of the 3 cases occur:
     *       Cases 1 & 2 involve examining and shifting elements through at most b blocks and take O(b) time.
     *       Case 3 involves calling the spread(u) method, which moves b(b+1) elements and takes O(b^2) time.
     *       If we ignore the cost of Case 3 (amortization) then total running time to locate i and perform the insertion of x is O(b + min{i,n-1}/b)
     * @param i index at which the specified element is to be inserted
     * @param x element to be inserted
     */
    public void add(int i, T x){
        if (i < 0 || i > n) throw new IndexOutOfBoundsException();
        if(i == n){
            add(x);
            return;
        }
        Location l = getLocation(i);
        Node u = l.u;
        int r = 0;
        while(r < b && u != dummy && u.d.size() == b + 1){
            u = u.next;
            r++;
        }
        if(r == b){ // b blocks each with b+1 elements
            spread(l.u);
            u = l.u;
        }
        if(u == dummy){ //ran off the end, add new node
            u = addBefore(u);
        }
        while(u != l.u){ //work backwards, shifting elements
            u.d.add(0, u.prev.d.remove(u.prev.d.size()-1));
            u = u.prev;
        }
        u.d.add(l.j, x);
        n++;
    }

    /**
     * Remove the node w from the list
     * @param w the node to remove
     */
    protected void remove(Node w){
        w.prev.next = w.next;
        w.next.prev = w.prev;
    }

    /**
     * Removing an element from an SEList is similar to adding an element.
     * We first locate the node u that contains the element with index i.
     * Now, we have to be prepared for the case where we cannot remove an element from u without causing u's block to become smaller than b-1.
     * Let u0,u1,u2,... denote u, u.next, u.next.next and so on.
     * We examine u0,u1,u2,... looking for a node from which we can borrow an element to make the size of u0's block at least b-1.
     * There are three cases to consider:
     *  1.We quickly (in r + 1 <= b steps) find a node ur whose block contains more than b-1 elements.
     *      In this case, we perform r shifts of an element from one block into the previous one, so that the extra element in ur becomes an extra element in u0.
     *      We can then remove the appropriate element from u0's block.
     *  2.We quickly (in r + 1 <= b steps) run off the end of the list of blocks.
     *      In this case, ur is the last block and there is no need for ur's block to contain at least b-1 elements.
     *      Therefore, we proceed to borrow an element from ur to make an extra element in u0.
     *      If this causes ur's block to become empty, we remove it.
     *  3.After b steps we do not find any block containing more than b-1 elements.
     *      In this case, u0,...,ub-1 is a sequence of b blocks that each contain b-1 elements.
     *      We gather these b(b-1) elements into u0,...,ub-2 so that each of these b-1 blocks contains exactly b elements, and we remove ub-1 which is now empty.
     *      Now u0's block contains b elements, and we can then remove the appropriate element from it.
     * @Note Like the add(i,x) operation, the running time is O(b + min{i,n-1}/b) if we ignore the gather(u) method that occurs in case 3.
     *
     * @param i the index of the element to be removed
     * @return
     */
    public T remove(int i){
        if (i < 0 || i > n - 1) throw new IndexOutOfBoundsException();
        Location l = getLocation(i);
        T y = l.u.d.get(i);
        Node u = l.u;
        int r = 0;
        while (r < b && u != dummy && u.d.size() == b-1){
            u = u.next;
            r++;
        }
        if(r == b){ // b blocks each with b-1 elements
            gather(l.u);
        }
        u = l.u;
        u.d.remove(l.j);
        while (u.d.size() < b-1 && u.next != dummy){
            u.d.add(u.next.d.remove(0));
            u = u.next;
        }
        if(u.d.isEmpty()) remove(u);
        n--;
        return y;
    }

    private void gather(Node u) {
        Node w = u;
        for(int j = 0; j < b-1; j++){
            while(w.d.size() < b){
                w.d.add(w.next.d.remove(0));
            }
            w = w.next;
        }
        remove(w);
    }

    private void spread(Node u) {
        Node w = u;
        for(int j = 0; j < b; j++){
            w = w.next;
        }
        w = addBefore(w);
        while(w != u){
            while(w.d.size() < b){
                w.d.add(w.next.d.remove(w.prev.d.size()-1));
            }
            w = w.prev;
        }
    }

    /**
     * add a new node before the node w
     * @param w the node to insert the new node before
     * @return the newly created and inserted node
     */
    private Node addBefore(Node w) {
        Node u = new Node();
        u.d = new BoundedDeque();
        u.prev = w.prev;
        u.next = w;
        u.next.prev = u;
        u.prev.next = u;
        return u;
    }


    @Override
    public int size() {
        return n;
    }

    public void clear(){
        dummy.next = dummy;
        dummy.prev = dummy;
        n = 0;
    }

    @Override
    public String toString() {
        Node u = dummy.next;
        String s = "";
        while(u != dummy){
            s += u.d.toString();
            u = u.next;
        }
        return s;
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return null;
    }

    //@TODO Implement Iterator
//    class Iterator implements ListIterator<T>{
//
//        /**
//         * The list we are iterating over
//         */
//        SpaceEfficientLinkedList<T> l;
//
//        /**
//         * The node whose value is returned by next()
//         */
//		Node p;
//
//		/**
//		 * The last node whose value was returned by next() or previous()
//		 */
//		Node last;
//
//		/**
//		 * The index of p
//		 */
//		int i;
//
//        Iterator(SpaceEfficientLinkedList<T> il, int ii){
//            l = il;
//            i = ii;
//            p = l.getNode(i);
//        }
//
//        @Override
//        public boolean hasNext() {
//            return p != dummy;
//        }
//
//        @Override
//        public T next() {
//
//
//        }
//
//        @Override
//        public boolean hasPrevious() {
//            return false;
//        }
//
//        @Override
//        public T previous() {
//            return null;
//        }
//
//        @Override
//        public int nextIndex() {
//            return 0;
//        }
//
//        @Override
//        public int previousIndex() {
//            return 0;
//        }
//
//        @Override
//        public void remove() {
//
//        }
//
//        @Override
//        public void set(T t) {
//
//        }
//
//        @Override
//        public void add(T t) {
//
//        }
//    }

    public static void main(String[] args){
        int n = 51;
        int b = 4;
        SpaceEfficientLinkedList<Integer> l = new SpaceEfficientLinkedList<Integer>(b, Integer.class);

        for(int i = 0; i < n; i++){
            l.add(i*10);
        }
        System.out.println(l);
    }

}
