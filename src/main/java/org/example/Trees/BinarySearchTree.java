package org.example.Trees;

import org.example.Utils.DefaultComparator;
import org.example.Sets.SSet;

import java.util.Comparator;
import java.util.Iterator;

public class BinarySearchTree<Node extends BinarySearchTree.BSTNode<Node, T>, T> extends BinaryTree<Node> implements SSet<T> {

    protected Comparator<T> c;

    protected int n;

    public static class BSTNode<Node extends BSTNode<Node, T>, T> extends BinaryTree.BTNode<Node>{
        T x;

        public BSTNode(){};
        public BSTNode(T value){
            x = value;
        }
    }

    public BinarySearchTree(Node sampleNode, Node nil, Comparator<T> c){
        super(sampleNode, nil);
        this.c = c;
    }

    public BinarySearchTree(Node sampleNode, Comparator<T> c){
        super(sampleNode);
        this.c = c;
    }

    public BinarySearchTree(Node sampleNode){
        this(sampleNode, new DefaultComparator<T>());
    }

    /**
     * Search for a value in the tree
     * @param x value to find
     * @return the last node in the search path for x
     */
    protected Node findLast(T x){
        Node w = r, prev = nil;
        while(w != nil){
            prev = w;
            int comp = c.compare(x, w.x);
            if(comp < 0){
                w = w.left;
            } else if(comp > 0){
                w = w.right;
            } else{
                return w;
            }
        }
        return prev;
    }

    /**
     * Search for a value in the tree
     * @param x the value to find
     * @return the last node in the search path for x, less than or equal to x.
     */
    protected Node findGENode(T x){
        Node w = r, z = nil;
        while(w != nil){
            int comp = c.compare(x, w.x);
            if(comp < 0){
                z = w;
                w = w.left;
            } else if(comp > 0){
                w = w.right;
            } else {
                return w;
            }
        }
        return z;
    }

    /**
     * Search for a value in a tree
     * @param x value to find
     * @return last node in the search path for x, greater or equal to x.
     */
    protected Node findLTNode(T x){
        Node u = r, z = nil;
        while(u != nil){
            int comp = c.compare(x, u.x);
            if(comp < 0){
                u = u.left;
            } else if (comp > 0){
                z = u;
                u = u.right;
            } else{
                return u;
            }
        }
        return z;
    }

    /**
     * Search for value in tree
     * @param x value to find
     * @return last value equal to x or null.
     */
    protected T findEQ(T x){
        Node u = r;
        while(u != nil){
            int comp = c.compare(x, u.x);
            if(comp < 0){
                u = u.left;
            } else if(comp > 0){
                u = u.right;
            } else{
                return u.x;
            }
        }
        return null; //value not found
    }


    /**
     * create new node containing desired value
     * @param x value to add
     * @return new node with value x
     */
    protected Node newNode(T x){
        Node u = super.newNode();
        u.x = x;
        return u;
    }

    /**
     * add node u as child of p
     * @param p new parent
     * @param u new child
     * @return true on successful add, false on pre-existing value
     */
    protected boolean addChild(Node p, Node u){
        if(p == nil){
            r = u; //inserting into empty tree
        } else{
            int comp = c.compare(u.x, p.x);
            if(comp < 0){
                p.left = u;
            } else if(comp > 0) {
                p.right = u;
            } else {
                return false; //u.x is already in tree
            }
            u.parent = p;
        }
        n++;
        return true;
    }

    /**
     * add a new value via node insertion
     * @param u node to insert
     * @return true on insertion, false on pre-existing value
     */
    public boolean add(Node u){
        Node p = findLast(u.x);
        return addChild(p, u);
    }

    /**
     * Remove the node u
     * @Warning assumption that u has at least one child!
     * @param u node to remove
     */
    protected void splice(Node u){
        Node s, p;
        if(u.left != nil){
            s = u.left;
        } else{
            s = u.right;
        }
        if(u == r){
            r = s;
            p = nil;
        } else{
            p = u.parent;
            if(p.left == u){
                p.left = s;
            } else {
                p.right = s;
            }
        }
        if(s != nil){
            s.parent = p;
        }
        n--;
    }

    /**
     * remove node u from the tree
     * @param u node to remove
     */
    protected void remove(Node u){
        if(u.left == nil || u.right == nil){
            splice(u);
        } else {
            Node w = u.right;
            while(w.left != nil){
                w = w.left;
            }
            u.x = w.x;
            splice(w);
        }
    }

    /**
     * left rotation at u
     * @param u node we are rotating at
     */
    protected void rotateLeft(Node u){
        Node w = u.right;
        w.parent = u.parent;
        if(w.parent != nil){
            if(w.parent.left == u){
                w.parent.left = w;
            } else{
                w.parent.right = w;
            }
        }
        u.right = w.left;
        if(u.right != null){
            u.right.parent = u;
        }
        u.parent = w;
        w.left = u;
        if(u == r){
            r = w;
            r.parent = nil;
        }
    }

    /**
     * Right rotation at u
     * @param u node to rotate at
     */
    protected void rotateRight(Node u){
        Node w = u.left;
        w.parent = u.parent;
        if(w.parent != nil){
            if(w.parent.left == u){
                w.parent.left = w;
            } else{
                w.parent.right = w;
            }
        }
        u.left = w.right;
        if(u.left != nil){
            u.left.parent = u;
        }
        u.parent = w;
        w.right = u;
        if(u == r){
            r = w;
            r.parent = nil;
        }
    }

    @Override
    public void clear() {
        super.clear();
        n = 0;
    }

    @Override
    public Comparator<? super T> comparator() {
        return c;
    }

    @Override
    public T find(T x) {
        Node w = r, z = nil;
        while(w != nil){
            int comp = c.compare(x, w.x);
            if(comp < 0 ){
                z = w;
                w = w.left;
            } else if(comp > 0){
                w = w.right;
            } else{
                return w.x;
            }
        }
        return z == nil ? null : z.x;
    }

    @Override
    public T findGE(T u) {
       if(u == null){ // find the minimum value
           Node w = r;
           if(w == nil) return null;
           while(w.left != nil){
               w = w.left;
           }
           return w.x;
       }
       Node w = findGENode(u);
       return w == nil ? null : w.x;
    }

    @Override
    public T findLT(T x) {
        if(x == null) { // find max value
            Node w = r;
            if(w == nil) return null;
            while (w.right != nil){
                w = w.right;
            } return w.x;
        }
        Node w = findLTNode(x);
        return w == nil ? null : w.x;
    }

    /**
     * Add a new value to tree
     * @param x new value
     * @return true on added, false on pre-existing value
     */
    @Override
    public boolean add(T x) {
        Node p = findLast(x);
        return addChild(p, newNode(x));
    }

    /**
     * remove a node with value x
     * @param x value of node to remove
     * @return true on removal, otherwise false
     */
    @Override
    public boolean remove(T x) {
        Node u = findLast(x);
        if(u != nil && c.compare(x, u.x) == 0){
            remove(u);
            return true;
        }
        return false;
    }

    public Iterator<T> iterator(Node u){
        class BTI implements Iterator<T>{
            protected Node w, prev;

            public BTI(Node iw){w = iw;}
            public boolean hasNext(){
                return w!=nil;
            }
            public T next(){
                T x = w.x;
                prev = w;
                w = nextNode(w);
                return x;
            }
            public void remove(){
                BinarySearchTree.this.remove(prev);
            }
        }
        return new BTI(u);
    }

    @Override
    public Iterator<T> iterator(T x) {
        return iterator(findGENode(x));
    }

    @Override
    public Iterator<T> iterator() {
        return iterator(firstNode());
    }

    @Override
    public String toString() {
        String s = "[";
        Iterator<T> it = iterator();
        while (it.hasNext()){
            s+= it.next().toString() + (it.hasNext() ? "," : "");
        }
        s+="]";
        return s;
    }

    public static void main(String[] args) {
        BSTNode tmp = new BSTNode(0);
        BinarySearchTree BST = new BinarySearchTree(tmp);
        System.out.println(BST.n);
        System.out.println(BST.r);
        BST.add(new BSTNode(7));
        BST.add(new BSTNode(6));
        BST.add(new BSTNode(18));
        BST.add(new BSTNode(42));
        BST.add(new BSTNode(2));
        BST.add(new BSTNode(19));
        BST.add(new BSTNode(21));
        BST.add(new BSTNode(7));
        System.out.println(BST.toString());
    }
}


