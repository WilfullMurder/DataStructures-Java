package org.example.Trees;

import org.example.Utils.DefaultComparator;
import org.example.Sets.SSet;

import java.util.Comparator;
import java.util.Random;

public class Treap<T> extends BinarySearchTree<Treap.Node<T>, T> implements SSet<T> {


    Random rand;

    public Treap(Comparator<T> c){
        super(new Node<T>(), c);
        rand = new Random();
    }

    public Treap(){this(new DefaultComparator<T>());}

    protected static class Node<T> extends BinarySearchTree.BSTNode<Node<T>, T>{
        int p;
    }

    protected void bubbleUp(Node<T> u){
        while(u.parent != nil && u.parent.p > u.p){
            if(u.parent.right == u){
                rotateLeft(u.parent);
            } else{
                rotateRight(u.parent);
            }
        }
        if(u.parent == nil){
            r = u;
        }
    }

    protected void trickleDown(Node<T> u){
        while(u.left != nil || u.right != nil){
            if(u.left == nil){
                rotateLeft(u);
            } else if (u.right == nil){
                rotateRight(u);
            } else if (u.left.p < u.right.p) {
                rotateRight(u);
            } else{
                rotateLeft(u);
            }
            if(r == u){
                r = u.parent;
            }
        }
    }

    @Override
    public boolean remove(T x) {
        Node<T> u = findLast(x);
        if(u != nil && c.compare(u.x, x) == 0){
            trickleDown(u);
            splice(u);
            return true;
        }
        return false;
    }

    @Override
    public boolean add(T x) {
        Node<T> u = newNode();
        u.x = x;
        u.p = rand.nextInt();

        if(super.add(x)){
            bubbleUp(u);
            return true;
        }
        return false;
    }
}