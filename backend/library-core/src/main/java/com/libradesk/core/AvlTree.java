package com.libradesk.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Self-balancing AVL set for ordered catalog keys. */
public final class AvlTree<T extends Comparable<? super T>> {
 private Node<T> root; private int size;
 /** Creates an empty tree in O(1) time and space. */ public AvlTree(){}
 /** Returns size in O(1) time and O(1) space. */ public int size(){return size;}
 /** Inserts a key in O(log n) time and O(log n) stack space. */ public void insert(T key){if(!contains(key))size++;root=insert(root,key);}
 /** Deletes a key in O(log n) time and O(log n) stack space. */ public boolean delete(T key){if(!contains(key))return false;root=delete(root,key);size--;return true;}
 /** Tests membership in O(log n) time and O(1) space. */ public boolean contains(T key){Node<T> n=root;while(n!=null){int c=key.compareTo(n.key);if(c==0)return true;n=c<0?n.left:n.right;}return false;}
 /** Traverses keys in order in O(n) time and O(log n) recursion space. */ public void forEach(Consumer<T> action){walk(root,action);}
 /** Returns an in-order page in O(n) time and O(take) result space. */ public List<T> page(int skip,int take){List<T> all=new ArrayList<>();walk(root,all::add);int a=Math.min(Math.max(0,skip),all.size()),b=Math.min(all.size(),a+Math.max(0,take));return new ArrayList<>(all.subList(a,b));}
 /** Returns keys in [fromInclusive,toExclusive) in O(log n + r) time and O(r + log n) space. */ public List<T> range(T fromInclusive,T toExclusive){List<T> out=new ArrayList<>();range(root,fromInclusive,toExclusive,out);return out;}
 private static <T extends Comparable<? super T>> void walk(Node<T> n,Consumer<T> a){if(n!=null){walk(n.left,a);a.accept(n.key);walk(n.right,a);}}
 private void range(Node<T> n,T lo,T hi,List<T> out){if(n==null)return;int l=lo.compareTo(n.key),h=hi.compareTo(n.key);if(l<0)range(n.left,lo,hi,out);if(l<=0&&h>0)out.add(n.key);if(h>0)range(n.right,lo,hi,out);}
 private Node<T> insert(Node<T> n,T k){if(n==null)return new Node<>(k);int c=k.compareTo(n.key);if(c<0)n.left=insert(n.left,k);else if(c>0)n.right=insert(n.right,k);else return n;return balance(n);}
 private Node<T> delete(Node<T> n,T k){int c=k.compareTo(n.key);if(c<0)n.left=delete(n.left,k);else if(c>0)n.right=delete(n.right,k);else{if(n.left==null)return n.right;if(n.right==null)return n.left;Node<T> s=n.right;while(s.left!=null)s=s.left;n.key=s.key;n.right=delete(n.right,s.key);}return balance(n);}
 private Node<T> balance(Node<T> n){n.height=1+Math.max(height(n.left),height(n.right));int b=height(n.left)-height(n.right);if(b>1){if(height(n.left.left)<height(n.left.right))n.left=rotateLeft(n.left);return rotateRight(n);}if(b< -1){if(height(n.right.right)<height(n.right.left))n.right=rotateRight(n.right);return rotateLeft(n);}return n;}
 private int height(Node<T> n){return n==null?0:n.height;}
 private Node<T> rotateRight(Node<T> y){Node<T>x=y.left;y.left=x.right;x.right=y;height(y);height(x);return x;}
 private Node<T> rotateLeft(Node<T> x){Node<T>y=x.right;x.right=y.left;y.left=x;height(x);height(y);return y;}
 private static final class Node<T>{T key;Node<T>left,right;int height=1;Node(T k){key=k;}}
}
