package com.libradesk.core;
/** Resizing circular-buffer FIFO queue. */
public final class CircularQueue<T>{private Object[] a=new Object[8];private int head,size;
 /** Adds an item in amortized O(1) time and O(n) space when resized. */ public void enqueue(T value){if(size==a.length)grow();a[(head+size)%a.length]=value;size++;}
 /** Removes the oldest item in O(1) time and O(1) space. */ @SuppressWarnings("unchecked") public T dequeue(){if(size==0)return null;T v=(T)a[head];a[head]=null;head=(head+1)%a.length;size--;return v;}
 /** Returns queue length in O(1) time and O(1) space. */ public int size(){return size;}
 private void grow(){Object[] b=new Object[a.length*2];for(int i=0;i<size;i++)b[i]=a[(head+i)%a.length];a=b;head=0;}}
