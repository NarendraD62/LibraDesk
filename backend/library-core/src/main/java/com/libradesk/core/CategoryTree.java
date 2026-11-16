package com.libradesk.core;

import java.util.ArrayList;
import java.util.List;

/** Dewey-style category hierarchy with DFS aggregate counts. */
public final class CategoryTree {
    private final HashTable<String,Node> nodes=new HashTable<>();
    /** Creates a root category in O(1) expected time and space. */
    public CategoryTree(String rootId,String name){Node root=new Node(rootId,name,null);nodes.put(rootId,root);}
    /** Adds a child category in expected O(1) time and space. */
    public void add(String parentId,String id,String name){if(nodes.containsKey(id))throw new IllegalArgumentException("Duplicate category");Node p=require(parentId),n=new Node(id,name,p);p.children.add(n);nodes.put(id,n);}
    /** Moves a subtree in expected O(1) time and space, rejecting cycles. */
    public void move(String id,String newParentId){Node n=require(id),p=require(newParentId);if(n.parent==null)throw new IllegalArgumentException("Cannot move root");for(Node a=p;a!=null;a=a.parent)if(a==n)throw new IllegalArgumentException("Category cycle");n.parent.children.remove(n);n.parent=p;p.children.add(n);}
    /** Returns root-to-node names in O(h) time and space. */
    public List<String> pathToRoot(String id){Node n=require(id);List<String> path=new ArrayList<>();while(n!=null){path.add(n.name);n=n.parent;}for(int i=0,j=path.size()-1;i<j;i++,j--){String v=path.get(i);path.set(i,path.get(j));path.set(j,v);}return path;}
    /** Returns descendants excluding the supplied node in O(s) time and space. */
    public List<String> descendants(String id){List<String> out=new ArrayList<>();descend(require(id),out);return out;}
    /** Adds a count to a category in expected O(1) time and space. */
    public void addCount(String id,long amount){require(id).count+=amount;}
    /** Returns subtree aggregate in O(s) time and O(h) stack space. */
    public long aggregateCount(String id){return count(require(id));}
    private Node require(String id){Node n=nodes.get(id);if(n==null)throw new IllegalArgumentException("Unknown category: "+id);return n;}
    private void descend(Node n,List<String> out){for(Node c:n.children){out.add(c.name);descend(c,out);}}
    private long count(Node n){long sum=n.count;for(Node c:n.children)sum+=count(c);return sum;}
    private static final class Node{final String id,name;Node parent;final List<Node> children=new ArrayList<>();long count;Node(String id,String name,Node parent){this.id=id;this.name=name;this.parent=parent;}}
}
