package org.example;

import java.util.*;

//currently runs breadth-first searches
//@Todo: implement depth-first searches

public class BinarySearchTree {

    public static void main(String[] args) {
        Tree<Integer> tree = new Tree();
        tree.add(10);
        tree.add(2);
        tree.add(1);
        tree.add(3);
        tree.add(14);
        tree.add(4);
        tree.add(10);
        tree.add(8);
        tree.add(19);
        tree.add(32);
        tree.add(146);
        tree.add(5);

    }



    static class Node<T extends Comparable<T>> {
        private T value;
        private Node<T> Parent;
        private Node<T> left;
        private Node<T> right;



        public Node(T val){
            this.value = val;
        }

        public Node<T> getParent() {
            return Parent;
        }

        public void setParent(Node<T> parent) {
            Parent = parent;
        }

        public Node<T> getLeft() {
            return left;
        }

        public void setLeft(Node<T> left) {
            this.left = left;
        }

        public Node<T> getRight() {
            return right;
        }

        public void setRight(Node<T> right) {
            this.right = right;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T val){this.value = val;}


    }

    /**
     * operations in a BinarySearchTree each involve following a path from the root of the tree to some node in the tree.
     * Without knowing more about the shape of the tree it is difficult to say much about the length of this path, except that it is less than n, the number of nodes in the tree.
     * The problem with the BinarySearchTree structure is that it can become unbalanced; it can look like a long chain of n nodes, all but the last having exactly one child.
     * @param <T>
     */
    static class Tree<T extends Comparable<T>>{
        private Node<T> root;

        private int n;

        public Tree(){}

        public Tree(Node<T> r){
            this.root = r;
        }

        int depth(Node<T> u){
            int d = 0;
            while (u!= root){
                u = u.getParent();
                d++;
            }
            return d;
        }



        void traverse(){
            //If we arrive at a node u from u.parent, then the next thing to do is to visit u.left.
            //If we arrive at u from u.left, then the next thing to do is to visit u.right.
            //If we arrive at u from u.right, then we are done visiting u's subtree, so we return to u.parent
            //handling the cases where any of u.left, u.right, or u.parent is null
            Node<T> u = root, prev = null, next;
            while(u!=null){
                if(prev == u.getParent()){
                    //arrive at u from u.parent
                    if(u.getLeft() != null) next = u.getLeft();
                    else if (u.getRight() != null) next = u.getRight();
                    else next = u.getParent();
                } else if (prev == u.getLeft()) {
                    //arrive at u from u.left
                    if(u.getRight() != null) next = u.getRight();
                    else next = u.getParent();
                } else{
                    //arrive at u from u.right
                    next = u.getParent();
                }
                prev = u;
                u = next;
            }
        }

        void bfTraverse(){
            //nodes are visited level-by-level starting at the root and working our way down, visiting the nodes at each level from left to right
            //implemented using a queue, that initially contains only the root
            //each step, we extract the next node, u, from q, process u and add u.left and u.right (if they are non-null) to q
            Queue<Node<T>> q = new LinkedList<>();
            if(root != null) q.add(root);
            while(!q.isEmpty()){
                Node<T> u = q.remove();
                if(u.getLeft() != null) q.add(u.getLeft());
                if(u.getRight() != null) q.add(u.getRight());
            }
        }



        /**
         SEARCHES
         Case 1: x < u.x then the search proceeds to u.left
         Case 2: x > u.x then the search proceeds to u.right
         Case 3: x = u.x then we have found the node u containing
         **/

        T findEQ(T x){
            // start searching for x at the root
            // terminates when Case 3 occurs or when u = null, former --> found x, latter --> x does not exist in BST
            Node<T> u = root;
            while(u != null){
                int comp = x.compareTo(u.getValue());
                if(comp < 0){
                    u = u.getLeft();
                } else if (comp > 0) {
                    u = u.getRight();
                } else {
                   return (T) u.getValue();
                }
            }
            return null;
        }

        T find(T x){
            /*
             If we look at the last node, u, at which Case 1 occurred, we see that u.x is the smallest value in the tree > x.
             Similarly, the last node at which Case 2 occurred contains the largest value in the tree < x.
             Therefore, by keeping track of the last node, z, at which Case 1 occurs,
             we can implement a find(x) operation that returns the smallest value stored in the tree that is greater than or equal to x
             */
            Node<T> w = root, z = null;
            while(w!=null){
                int comp = x.compareTo(w.getValue());
                if(comp < 0){
                    z=w;
                    w = w.getLeft();
                } else if (comp > 0) {
                    w = w.getRight();
                } else return (T) w.getValue();
            }
            return z == null ? null : z.getValue();
        }

        boolean add(T x){
            Node<T> p = findLast(x);
            return addChild(p, new Node<T>(x));
        }

        private boolean addChild(Node<T> p, Node<T> u) {
            if(p == null){
                this.root = u; // insert into empty tree
            } else {
                int comp = u.getValue().compareTo(p.getValue());
                if(comp < 0){
                    p.setLeft(u);
                } else if (comp > 0){
                    p.setRight(u);
                } else{
                    return false; //u.x is already in tree
                }
                n++; //if we're tracking how many children the tree has why the need for size()?
                u.setParent(p);
            }
            return true;
        }

        private Node<T> findLast(T x) {
            Node<T> w = root, prev = null;
            while(w!=null){
                prev = w;
                int comp = x.compareTo(w.getValue());
                if(comp < 0){
                    w = w.left;
                } else if (comp > 0){
                    w = w.right;
                } else{
                    return w;
                }
            }
            return prev;
        }


        int size(){
            Node<T> u = root, prev = null, next;
            int n=0;
            while(u!= null){
                if(prev == u.getParent()){
                    n++;
                    if(u.getLeft() != null) next = u.getLeft();
                    else if (u.getRight() != null) next = u.getRight();
                    else next = u.getParent();
                } else if (prev == u.getLeft()) {
                    if(u.getRight() != null) next = u.getRight();
                    else next = u.getParent();
                } else{
                    next = u.getParent();
                }
                prev = u;
                u = next;
            }
            return n;
        }

        int height(){
            Node<T> u = root, prev = null, next;
            int d = -1;
            while(u!=null){
                if(prev == u.getParent()){
                    d++;
                    if(u.getLeft() != null) next = u.getLeft();
                    else if (u.getRight() != null) next = u.getRight();
                    else next = u.getParent();
                } else if (prev == u.getLeft()) {
                    if(u.getRight() != null) next = u.getRight();
                    else next = u.getParent();
                } else{
                    next = u.getParent();
                }
                prev = u;
                u = next;
            }
            return d;
        }

        // If u is a leaf, then we can just detach u from its parent
        // Even better: If u has only one child, then we can splice u from the tree by having u.parent adopt u's child
        void splice(Node<T> u){
            Node<T> s, p = null;
            if(u.getLeft() != null){
                s = u.getLeft();
            } else s= u.getRight();

            if(u == root){
                root = s;
            } else{
                p = u.getParent();
                if(p.getLeft() == u){
                    p.setLeft(s);
                } else{
                    p.setRight(s);
                }
            }
            if(s != null){
                s.setParent(p);
            }
            n--;
        }

        //When u has two children the simplest thing to do is to find a node, w, that has less than two children and such that w.x can replace u.x
        //To maintain the binary search tree property, the value w.x should be close to the value of u.x
        //choosing w such that w.x is the smallest value greater than u.x will work
        //Therefore w is the smallest value in the subtree rooted at u.right which can be easily removed because it has no left child
        void remove(Node<T> u){
            if(u.getLeft() == null || u.getRight() == null){
                splice(u);
            } else {
                Node<T> w =u.getRight();
                while(w.getLeft() != null){
                    w = w.getLeft();
                    u.setValue(w.getValue());
                    splice(w);
                }
            }
        }

        /***RECURSIVE**/
        //using recursion can be problematic. Max depth can cause stack overflow on deep trees!
        void traverseRecursively(Node<T> u){
            /**
             * Both size and height traverse the tree in this manner.
             * **/
            if(u == null) return;
            traverseRecursively(u.getLeft());
            traverseRecursively(u.getRight());
        }
        int sizeRecursively(Node<T> u){
            //to compute the size of (number of nodes in) a binary tree rooted at node u
            //we recursively compute the sizes of the two subtrees rooted at the children of u, sum these sizes, and add one
            if(u == null) return 0;
            return 1 + sizeRecursively(u.getLeft()) + sizeRecursively(u.getRight());
        }

        int heightRecursively(Node<T> u){
            //To compute the height of a node u we can compute the height of u's two subtrees, take the maximum, and add one:
            if(u == null) return -1;
            return 1 + Math.max(heightRecursively(u.getLeft()), heightRecursively(u.getRight()));
        }
        /***RECURSIVE END**/



    }


}
