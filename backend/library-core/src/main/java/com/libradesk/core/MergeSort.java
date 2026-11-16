package com.libradesk.core;
import java.util.Comparator;
/** Stable merge sort implementation. */
public final class MergeSort {
 private MergeSort(){}
 /** Sorts an array stably in O(n log n) time and O(n) auxiliary space. */
 public static <T> void sort(T[] a, Comparator<? super T> c){if(a.length<2)return;Object[] tmp=new Object[a.length];sort(a,tmp,0,a.length,c);}
 @SuppressWarnings("unchecked") private static <T> void sort(T[] a,Object[] t,int lo,int hi,Comparator<? super T> c){if(hi-lo<2)return;int m=(lo+hi)>>>1;sort(a,t,lo,m,c);sort(a,t,m,hi,c);int i=lo,j=m,k=lo;while(i<m&&j<hi)t[k++]=c.compare(a[i],a[j])<=0?a[i++]:a[j++];while(i<m)t[k++]=a[i++];while(j<hi)t[k++]=a[j++];for(k=lo;k<hi;k++)a[k]=(T)t[k];}
}
