package com.libradesk.core;

import java.text.Normalizer;
import java.util.Locale;

/** Word to sorted book ID postings index with AND-query merge intersection. */
public final class InvertedWordIndex {
    private final HashTable<String,Posting> postings=new HashTable<>();
    /** Adds unique book IDs for each word in O(w log p) time, where w is token count and p posting size. */
    public void add(int bookId,String text){for(String w:words(text)){if(w.isBlank())continue;Posting p=postings.computeIfAbsent(w,x->new Posting());p.add(bookId);}}
    /** Removes a book ID from supplied text words in O(w p) time and O(1) space. */
    public void remove(int bookId,String text){for(String w:words(text)){Posting p=postings.get(w);if(p!=null)p.remove(bookId);}}
    /** Intersects all query words shortest posting first in O(sum p) time and O(r) result space. */
    public int[] searchAnd(String query){String[] ws=words(query);if(ws.length==0)return new int[0];Posting first=null;for(String w:ws){Posting p=postings.get(w);if(p==null)return new int[0];if(first==null||p.size<first.size)first=p;}int[] result=first.toArray();for(String w:ws){Posting p=postings.get(w);if(p==first)continue;result=intersect(result,p);}return result;}
    private int[] intersect(int[] a,Posting b){int[] tmp=new int[Math.min(a.length,b.size)];int i=0,j=0,k=0;while(i<a.length&&j<b.size){if(a[i]==b.ids[j]){tmp[k++]=a[i++];j++;}else if(a[i]<b.ids[j])i++;else j++;}return java.util.Arrays.copyOf(tmp,k);}
    private static String[] words(String s){return Normalizer.normalize(s==null?"":s,Normalizer.Form.NFKC).toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+");}
    private static final class Posting{int[] ids=new int[8];int size;void add(int id){int i=java.util.Arrays.binarySearch(ids,0,size,id);if(i>=0)return;i=-i-1;if(size==ids.length)ids=java.util.Arrays.copyOf(ids,size*2);System.arraycopy(ids,i,ids,i+1,size-i);ids[i]=id;size++;}void remove(int id){int i=java.util.Arrays.binarySearch(ids,0,size,id);if(i<0)return;System.arraycopy(ids,i+1,ids,i,size-i-1);size--;}int[] toArray(){return java.util.Arrays.copyOf(ids,size);}}
}
