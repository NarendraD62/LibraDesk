package com.libradesk.core;

import java.util.Arrays;
import java.util.Random;

/** Standalone reproducible timing comparison against standard array sorting and linear lookup. */
public final class SortBenchmark {
    private SortBenchmark(){}
    /** Runs deterministic 200,000 item sorts and reports elapsed milliseconds; O(n log n) time per sort and O(n) benchmark memory. */
    public static void main(String[] args){int n=args.length==0?200_000:Integer.parseInt(args[0]);Integer[] input=new Integer[n];Random r=new Random(2026);for(int i=0;i<n;i++)input[i]=r.nextInt(n*4);measure("MergeSort",input,0);measure("QuickSort",input,1);measure("Arrays.sort",input,2);Integer[] sorted=input.clone();Arrays.sort(sorted);int target=sorted[n/2];long start=System.nanoTime();int found=-1;for(int i=0;i<sorted.length;i++)if(sorted[i].equals(target)){found=i;break;}long linear=System.nanoTime()-start;start=System.nanoTime();int binary=Arrays.binarySearch(sorted,target);long binaryTime=System.nanoTime()-start;if(found<0||binary<0)throw new IllegalStateException("lookup mismatch");System.out.printf("Linear scan: %.3f ms; Arrays.binarySearch: %.3f ms%n",linear/1e6,binaryTime/1e6);}
    private static void measure(String label,Integer[] source,int kind){Integer[] a=source.clone();long start=System.nanoTime();if(kind==0)MergeSort.sort(a,Integer::compare);else if(kind==1)QuickSort.sort(a,Integer::compare);else Arrays.sort(a);long elapsed=System.nanoTime()-start;if(!isSorted(a))throw new IllegalStateException(label+" produced unsorted values");System.out.printf("%s (%d): %.3f ms%n",label,a.length,elapsed/1e6);}
    private static boolean isSorted(Integer[] a){for(int i=1;i<a.length;i++)if(a[i-1]>a[i])return false;return true;}
}
