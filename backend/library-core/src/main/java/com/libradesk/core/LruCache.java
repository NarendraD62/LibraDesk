package com.libradesk.core;
/** Least-recently-used cache backed by a custom hash table and doubly linked list. */
public final class LruCache<K,V>{private final int capacity;private final HashTable<K,Node<K,V>> index=new HashTable<>();private Node<K,V> head,tail;
 /** Creates a cache of fixed capacity in O(1) time and space. */ public LruCache(int capacity){this.capacity=Math.max(0,capacity);}
 /** Gets and promotes an entry in expected O(1) time and O(1) space. */ public V get(K key){Node<K,V> n=index.get(key);if(n==null)return null;promote(n);return n.value;}
 /** Inserts or replaces in expected O(1) time and O(1) amortized space. */ public void put(K key,V value){if(capacity==0)return;Node<K,V> n=index.get(key);if(n!=null){n.value=value;promote(n);return;}n=new Node<>(key,value);index.put(key,n);append(n);if(index.size()>capacity){Node<K,V> old=head;unlink(old);index.remove(old.key);}}
 /** Removes a cache entry in expected O(1) time and O(1) space. */ public V remove(K key){Node<K,V> n=index.remove(key);if(n==null)return null;unlink(n);return n.value;}
 /** Returns number of entries in O(1) time and space. */ public int size(){return index.size();}
 private void promote(Node<K,V> n){if(n==tail)return;unlink(n);append(n);}private void append(Node<K,V> n){n.prev=tail;n.next=null;if(tail==null)head=n;else tail.next=n;tail=n;}private void unlink(Node<K,V> n){if(n.prev==null)head=n.next;else n.prev.next=n.next;if(n.next==null)tail=n.prev;else n.next.prev=n.prev;}
 private static final class Node<K,V>{K key;V value;Node<K,V>prev,next;Node(K k,V v){key=k;value=v;}}
}
