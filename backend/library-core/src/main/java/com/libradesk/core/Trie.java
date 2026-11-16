package com.libradesk.core;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Case-insensitive Unicode-normalized prefix trie with popularity-ranked suggestions. */
public final class Trie {
    private final Node root=new Node();
    /** Inserts or updates a word in O(m) time and O(m) new space, where m is normalized word length. */
    public void insert(String word,int popularity){String n=normalize(word);Node p=root;for(int i=0;i<n.length();i++)p=p.children.computeIfAbsent(n.charAt(i),x->new Node());p.word=n;p.popularity=popularity;}
    /** Removes a word in O(m) time and O(m) recursion space. */
    public boolean remove(String word){String n=normalize(word);Node p=root;for(int i=0;i<n.length();i++){p=p.children.get(n.charAt(i));if(p==null)return false;}if(p.word==null)return false;p.word=null;p.popularity=0;return true;}
    /** Tests a normalized prefix in O(m) time and O(1) extra space. */
    public boolean startsWith(String prefix){return find(normalize(prefix))!=null;}
    /** Returns at most k words ranked by popularity in O(s log s) time and O(s) space for s words below the prefix. */
    public List<String> topK(String prefix,int k){List<Node> heap=new ArrayList<>();Node p=find(normalize(prefix));if(p!=null)collect(p,heap,Math.max(k,0));for(int i=1;i<heap.size();i++){Node v=heap.get(i);int j=i-1;while(j>=0&&rank(heap.get(j),v)>0){heap.set(j+1,heap.get(j));j--;}heap.set(j+1,v);}List<String> out=new ArrayList<>();for(Node n:heap)out.add(n.word);return out;}
    private Node find(String n){Node p=root;for(int i=0;i<n.length();i++){p=p.children.get(n.charAt(i));if(p==null)return null;}return p;}
    private void collect(Node p,List<Node> heap,int k){if(k>0&&p.word!=null){if(heap.size()<k){heap.add(p);siftUp(heap,heap.size()-1);}else if(rank(p,heap.get(0))<0){heap.set(0,p);siftDown(heap,0);}}p.children.forEachValue(c->collect(c,heap,k));}
    private static int rank(Node a,Node b){int d=Integer.compare(b.popularity,a.popularity);return d!=0?d:a.word.compareTo(b.word);}
    private static void siftUp(List<Node> h,int i){while(i>0){int p=(i-1)/2;if(rank(h.get(i),h.get(p))>=0)break;Node x=h.get(i);h.set(i,h.get(p));h.set(p,x);i=p;}}
    private static void siftDown(List<Node> h,int i){while(true){int l=i*2+1,r=l+1,m=i;if(l<h.size()&&rank(h.get(l),h.get(m))<0)m=l;if(r<h.size()&&rank(h.get(r),h.get(m))<0)m=r;if(m==i)return;Node x=h.get(i);h.set(i,h.get(m));h.set(m,x);i=m;}}
    private static String normalize(String s){return Normalizer.normalize(s==null?"":s,Normalizer.Form.NFKC).toLowerCase(Locale.ROOT).trim();}
    private static final class Node{final HashTable<Character,Node> children=new HashTable<>();String word;int popularity;}
}
