package com.libradesk.core;

import java.util.Objects;

/** Separate-chaining hash table with amortized constant-time access. */
public final class HashTable<K,V> {
    private static final float MAX_LOAD = .75f;
    private Entry<K,V>[] buckets;
    private int size;
    /** Creates an empty table in O(1) time and O(1) space. */
    @SuppressWarnings("unchecked")
    public HashTable() { buckets = (Entry<K,V>[]) new Entry[16]; }
    /** Returns the number of key/value pairs in O(1) time and O(1) space. */
    public int size() { return size; }
    /** Returns whether the table is empty in O(1) time and O(1) space. */
    public boolean isEmpty() { return size == 0; }
    /** Returns the current load factor in O(1) time and O(1) space. */
    public double loadFactor() { return (double)size / buckets.length; }
    /** Looks up a key in expected O(1), worst-case O(n) time and O(1) space. */
    public V get(K key) { for (Entry<K,V> e=buckets[index(key,buckets.length)];e!=null;e=e.next) if(Objects.equals(key,e.key)) return e.value; return null; }
    /** Returns the value for key or null in expected O(1), worst-case O(n) time and O(1) space. */
    public V computeIfAbsent(K key, java.util.function.Function<? super K,? extends V> factory) { V v=get(key); if(v==null){v=factory.apply(key);put(key,v);} return v; }
    /** Visits all values in O(capacity + n) time and O(1) extra space. */
    public void forEachValue(java.util.function.Consumer<? super V> action) { for(Entry<K,V> head:buckets)for(Entry<K,V> e=head;e!=null;e=e.next)action.accept(e.value); }
    /** Tests key membership in expected O(1), worst-case O(n) time and O(1) space. */
    public boolean containsKey(K key) { for (Entry<K,V> e=buckets[index(key,buckets.length)];e!=null;e=e.next) if(Objects.equals(key,e.key)) return true; return false; }
    /** Adds or replaces a value in amortized O(1), worst-case O(n) time and O(n) resize space. */
    public V put(K key,V value) { int i=index(key,buckets.length); for(Entry<K,V> e=buckets[i];e!=null;e=e.next) if(Objects.equals(key,e.key)){V old=e.value;e.value=value;return old;} if((size+1f)/buckets.length>MAX_LOAD) resize(); i=index(key,buckets.length); buckets[i]=new Entry<>(key,value,buckets[i]);size++;return null; }
    /** Removes a key in expected O(1), worst-case O(n) time and O(1) space. */
    public V remove(K key) { int i=index(key,buckets.length); Entry<K,V> prev=null,e=buckets[i];while(e!=null){if(Objects.equals(key,e.key)){if(prev==null)buckets[i]=e.next;else prev.next=e.next;size--;return e.value;}prev=e;e=e.next;}return null; }
    private static int index(Object key,int capacity){int h=key==null?0:key.hashCode();h^=h>>>16;return h&(capacity-1);}
    @SuppressWarnings("unchecked") private void resize(){Entry<K,V>[] old=buckets;buckets=(Entry<K,V>[])new Entry[old.length*2];for(Entry<K,V> head:old)for(Entry<K,V> e=head;e!=null;){Entry<K,V> next=e.next;int i=index(e.key,buckets.length);e.next=buckets[i];buckets[i]=e;e=next;}}
    private static final class Entry<K,V>{final K key;V value;Entry<K,V> next;Entry(K k,V v,Entry<K,V> n){key=k;value=v;next=n;}}
}
