package com.libradesk.core;
import java.util.Comparator;
/** Binary search helpers for sorted arrays. */
public final class BinarySearch { private BinarySearch(){}
 /** Finds an equal item in O(log n) time and O(1) space, or -1. */
 public static <T> int exact(T[] a,T key,Comparator<? super T> c){int l=0,r=a.length-1;while(l<=r){int m=(l+r)>>>1,d=c.compare(a[m],key);if(d==0)return m;if(d<0)l=m+1;else r=m-1;}return -1;}
 /** Finds the first item not less than key in O(log n) time and O(1) space. */
 public static <T> int lowerBound(T[] a,T key,Comparator<? super T> c){int l=0,r=a.length;while(l<r){int m=(l+r)>>>1;if(c.compare(a[m],key)<0)l=m+1;else r=m;}return l;}
}
