package com.libradesk.core;
/** Resizing array-backed LIFO stack. */
public final class Stack<T>{private Object[] a=new Object[8];private int size;
 /** Pushes in amortized O(1) time and O(n) space when resized. */ public void push(T v){if(size==a.length){Object[] b=new Object[a.length*2];System.arraycopy(a,0,b,0,size);a=b;}a[size++]=v;}
 /** Pops in O(1) time and O(1) space. */ @SuppressWarnings("unchecked") public T pop(){if(size==0)return null;T v=(T)a[--size];a[size]=null;return v;}
 /** Returns number of items in O(1) time and O(1) space. */ public int size(){return size;}}
