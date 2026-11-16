package com.libradesk.core;
import java.util.Comparator;
/** Binary min-heap with O(log n) keyed updates and removals. */
public final class IndexedMinHeap<K,V>{private Object[] keys=new Object[16],values=new Object[16];private int size;private final HashTable<K,Integer> positions=new HashTable<>();private final Comparator<? super V> order;
 /** Creates an empty heap in O(1) time and space. */ public IndexedMinHeap(Comparator<? super V> order){this.order=order;}
 /** Returns size in O(1) time and space. */ public int size(){return size;}
 /** Inserts or replaces a keyed item in O(log n) time and O(n) resize space. */ public void put(K key,V value){Integer i=positions.get(key);if(i==null){ensure();i=size++;keys[i]=key;values[i]=value;positions.put(key,i);up(i);}else{V old=valueAt(i);values[i]=value;if(order.compare(value,old)<0)up(i);else down(i);}}
 /** Returns the minimum item in O(1) time and space. */ public V peek(){return size==0?null:valueAt(0);}
 /** Looks up a keyed item in expected O(1) time and space. */ public V get(K key){Integer i=positions.get(key);return i==null?null:valueAt(i);}
 /** Visits heap items in O(n) time and O(1) extra space. */ public void forEach(java.util.function.Consumer<? super V> action){for(int i=0;i<size;i++)action.accept(valueAt(i));}
 /** Removes and returns the minimum item in O(log n) time and O(1) extra space. */ public V poll(){return size==0?null:remove(keyAt(0));}
 /** Removes an item by key in O(log n) time and O(1) extra space. */ public V remove(K key){Integer at=positions.remove(key);if(at==null)return null;V out=valueAt(at);int last=--size;if(at!=last){keys[at]=keys[last];values[at]=values[last];positions.put(keyAt(at),at);if(at>0&&order.compare(valueAt(at),valueAt((at-1)/2))<0)up(at);else down(at);}keys[last]=null;values[last]=null;return out;}
 private void ensure(){if(size==keys.length){Object[] k=new Object[size*2],v=new Object[size*2];System.arraycopy(keys,0,k,0,size);System.arraycopy(values,0,v,0,size);keys=k;values=v;}}
 @SuppressWarnings("unchecked") private K keyAt(int i){return (K)keys[i];}@SuppressWarnings("unchecked") private V valueAt(int i){return (V)values[i];}
 private void up(int i){while(i>0){int p=(i-1)/2;if(order.compare(valueAt(i),valueAt(p))>=0)break;swap(i,p);i=p;}}
 private void down(int i){while(true){int l=2*i+1,r=l+1,m=i;if(l<size&&order.compare(valueAt(l),valueAt(m))<0)m=l;if(r<size&&order.compare(valueAt(r),valueAt(m))<0)m=r;if(m==i)return;swap(i,m);i=m;}}
 private void swap(int a,int b){Object k=keys[a],v=values[a];keys[a]=keys[b];values[a]=values[b];keys[b]=k;values[b]=v;positions.put(keyAt(a),a);positions.put(keyAt(b),b);}
}
