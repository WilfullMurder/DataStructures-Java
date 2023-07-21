package org.example;

import java.util.*;

public class BinaryTree<Node extends BinaryTree.BTNode<Node>> {


   public static class BTNode<Node extends BTNode<Node>> {
       public Node parent;
       public Node left;
       public Node right;
   }

       /**
        * used for mini-factory
        */
       protected Node sampleNode;

       /**
        * root of the tree
        */
       protected Node r;

       /**
        * Tree's null node
        */
       protected Node nil;


       /**
        * Create a new instance
        * @param sampleNode - a sample of a node used to create a new node
        * @param nil - a node used in place of null
        */
      public BinaryTree(Node sampleNode, Node nil){
          this.sampleNode = sampleNode;
          this.nil = nil;
          r = nil;
      }

       /**
        * Create a new instance
        * @param sampleNode - a sample of a node used to create a new node
        */
      public BinaryTree(Node sampleNode){
          this.sampleNode = sampleNode;
      }

    /**
     * Try to allocate a new node for use in the tree
     * @return new node or null (on exception)
     */
   protected Node newNode(){
       try{
          Node u = (Node)sampleNode.getClass().newInstance();
          u.parent = u.left = u.right = nil;
          return u;
      } catch(Exception e){
          return null;
      }
   }

    /**
     * Compute the depth (distance to the root) of u
     * @param u the node at the desired depth
     * @return the distance between r and u
     */
   public int depth(Node u){
       int d = 0;
       while(u != r){
           u = u.parent;
           d++;
       }
       return d;
   }



    /**
     * compute the number of nodes in the tree without recursion
     * @return
     */
   public int size(){
       Node u = r, prev = nil, next;
       int n = 0;
       while(u != nil){
           if(prev == u.parent){
               n++;
               if(u.left != nil) next = u.left; //search left
               else if(u.right != nil) next = u.right; //search right
               else next = u.parent; //back to parent node
           } else if(prev == u.left){
               if(u.right != nil) next = u.right; //search right
               else next = u.parent; //back to parent node
           } else {
               next = u.parent; //back to parent node
           }
           prev = u;
           u = next;
       }
       return n;
   }

    /**
     * Non-recursive traversal
     */
   public void traverse(){
       Node u = r, prev = nil, next;
       while(u != nil){
           if(prev == u.parent){
               if(u.left != nil) next = u.left;
               else if (u.right != nil) next = u.right;
               else next = u.parent;
           } else if (prev == u.left) {
               if(u.right != null) next = u.right;
               else next = u.parent;
           } else{
               next = u.parent;
           }
           prev = u;
           u = next;
       }
   }

    /**
     * Breadth-First traversal
     */
   public void bfTraverse(){
       Queue<Node> q = new LinkedList<Node>();
       if( r != nil) q.add(r);
       while(!q.isEmpty()){
           Node u = q.remove();
           if(u.left != null) q.add(u.left);
           if(u.right != null) q.add(u.right);
       }
   }

    /**
     * Find the first node in an in-order traversal
     * @return the first node in an in-order traversal
     */
   public Node firstNode(){
       Node w = r;
       if(w == nil) return nil;
       while(w.left != nil){
           w = w.left;
       }
       return w;
   }

    /**
     * Find the node that follows w in an in-order traversal
     * @param w
     * @return
     */
   public Node nextNode(Node w){
       if(w.right != nil){
           w = w.right;
           while(w.left != nil){
               w = w.left;
           }
       } else {
           while(w.parent != nil && w.parent.left != w){
               w = w.parent;
           }
           w = w.parent;
       }
       return w;
   }

   public boolean isEmpty(){return r == nil;}

   public void clear(){r = nil;}


   /**RECURSIVE FUNC**/

    /**
     * @Warning RECURSIVE FUNCTION!
     * @param u root of the subtree
     * @return the size of the subtree rooted at u
     */
    protected int size(Node u){
        if(u == nil) return 0;
        return 1 + size(u.left) + size(u.right);
    }

    /**
     * @Warning RECURSIVE FUNCTION!
     * @param u root of the subtree
     * @return the height of the subtree rooted at u
     */
    protected int height(Node u){
        if(u==nil) return -1;
        return 1 + Math.max(height(u.left), height(u.right));
    }

    public int height(){
        return height(r);
    }

    /**
     * @Warning RECURSIVE FUNCTION!
     * @param u root of the subtree
     */
    public void traverse(Node u ){
        if(u == nil) return;
        traverse(u.left);
        traverse(u.right);
    }




}
