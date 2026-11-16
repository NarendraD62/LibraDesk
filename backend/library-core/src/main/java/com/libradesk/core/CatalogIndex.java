package com.libradesk.core;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** In-memory catalog facade combining hash lookup, tries, postings and AVL ordering. */
public final class CatalogIndex {
    public record Book(long id,String title,String authors,String isbn,String category,String language,int year,int popularity,int available,String shelfLocation){}
    public record QueryResult(List<Book> books,int totalHits,String explain,long elapsedMicros){}
    private final HashTable<Long,Book> byId=new HashTable<>();private final HashTable<String,Book> byIsbn=new HashTable<>();private final Trie titles=new Trie(),authors=new Trie();private final InvertedWordIndex words=new InvertedWordIndex();private final AvlTree<TitleKey> ordered=new AvlTree<>();
    /** Adds or replaces a book in expected O(w + log n) time and O(w) auxiliary space. */
    public void add(Book book){Book old=byId.get(book.id());if(old!=null)remove(old.id());byId.put(book.id(),book);byIsbn.put(book.isbn(),book);titles.insert(book.title(),book.popularity());for(String author:book.authors().split(","))authors.insert(author.trim(),book.popularity());words.add((int)book.id(),book.title()+" "+book.authors()+" "+book.category());ordered.insert(new TitleKey(normalize(book.title()),book.id()));}
    /** Updates a book using remove-and-add in expected O(w + log n) time and O(w) auxiliary space. */ public void update(Book book){add(book);}
    /** Removes a book in expected O(w + log n) time and O(1) auxiliary space. */ public void remove(long id){Book old=byId.remove(id);if(old==null)return;byIsbn.remove(old.isbn());titles.remove(old.title());for(String a:old.authors().split(","))authors.remove(a.trim());words.remove((int)id,old.title()+" "+old.authors()+" "+old.category());ordered.delete(new TitleKey(normalize(old.title()),id));}
    /** Finds an ISBN in expected O(1), worst-case O(n) time and O(1) space. */ public Book findByIsbn(String isbn){return byIsbn.get(isbn);}
    /** Suggests title or author prefixes in O(s log s) time and O(s) space. */ public List<String> suggest(String prefix,boolean byAuthor,int limit){return (byAuthor?authors:titles).topK(prefix,limit);}
    /** Searches terms, applies category and availability filters, sorts and pages in O(p log p + n) time and O(n) space. */
    public QueryResult search(String query,String category,Boolean available,int page,int size){long start=System.nanoTime();int[] ids=words.searchAnd(query);List<Book> found=new ArrayList<>();for(int id:ids){Book b=byId.get((long)id);if(b!=null&&(category==null||category.isBlank()||b.category().equalsIgnoreCase(category))&&(available==null||(b.available()>0)==available))found.add(b);}Book[] sorted=found.toArray(Book[]::new);MergeSort.sort(sorted,(a,b)->normalize(a.title()).compareTo(normalize(b.title())));int total=sorted.length,from=Math.min(total,Math.max(0,page)*Math.max(0,size)),to=Math.min(total,from+Math.max(0,size));List<Book> result=new ArrayList<>();for(int i=from;i<to;i++)result.add(sorted[i]);return new QueryResult(result,total,"Inverted word index + linear posting intersection; stable merge sort",(System.nanoTime()-start)/1000);}
    /** Returns indexed book count in O(1) time and space. */ public int size(){return byId.size();}
    /** Returns books in title order in O(n) time and O(n) result space. */ public List<Book> all(){List<Book> out=new ArrayList<>();for(TitleKey k:ordered.page(0,ordered.size())){Book b=byId.get(k.id());if(b!=null)out.add(b);}return out;}
    private static String normalize(String s){return Normalizer.normalize(s,Normalizer.Form.NFKC).toLowerCase(Locale.ROOT).trim();}
    private record TitleKey(String title,long id) implements Comparable<TitleKey>{@Override public int compareTo(TitleKey other){int c=title.compareTo(other.title);return c!=0?c:Long.compare(id,other.id);}}
}
