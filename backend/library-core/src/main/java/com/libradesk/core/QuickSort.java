package com.libradesk.core;
import java.util.Comparator;
/** In-place median-of-three quicksort with insertion-sort cutoff. */
public final class QuickSort { private QuickSort(){} private static final int CUTOFF=16;
 /** Sorts in expected O(n log n), worst-case O(n²) time and O(log n) expected stack space. */
 public static <T> void sort(T[] a,Comparator<? super T> c){sort(a,0,a.length-1,c);}
 private static <T> void sort(T[] a,int lo,int hi,Comparator<? super T> c){if(hi-lo<CUTOFF){for(int i=lo+1;i<=hi;i++){T v=a[i];int j=i-1;while(j>=lo&&c.compare(a[j],v)>0){a[j+1]=a[j];j--;}a[j+1]=v;}return;}int mid=(lo+hi)>>>1;if(c.compare(a[mid],a[lo])<0)swap(a,mid,lo);if(c.compare(a[hi],a[lo])<0)swap(a,hi,lo);if(c.compare(a[hi],a[mid])<0)swap(a,hi,mid);swap(a,mid,hi-1);T pivot=a[hi-1];int i=lo,j=hi-1;while(true){while(c.compare(a[++i],pivot)<0){}while(c.compare(a[--j],pivot)>0){}if(i>=j)break;swap(a,i,j);}swap(a,i,hi-1);sort(a,lo,i-1,c);sort(a,i+1,hi,c);}
 private static void swap(Object[] a,int i,int j){Object t=a[i];a[i]=a[j];a[j]=t;}
}
