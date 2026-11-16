package com.libradesk.api.catalog;

import com.libradesk.core.CatalogIndex;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CatalogController {
    private final CatalogIndex index;
    public CatalogController(CatalogIndex index){this.index=index;}
    @GetMapping("/books/search") public SearchPage search(@RequestParam(defaultValue="") String q,@RequestParam(required=false) String category,@RequestParam(required=false) Boolean available,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="24") int size,@RequestParam(defaultValue="false") boolean explain){List<CatalogIndex.Book> selected;int total;String structures;long elapsed;if(q.isBlank()){long start=System.nanoTime();List<CatalogIndex.Book> filtered=index.all().stream().filter(b->category==null||category.isBlank()||b.category().equalsIgnoreCase(category)).filter(b->available==null||(b.available()>0)==available).toList();total=filtered.size();selected=filtered.stream().skip((long)Math.max(0,page)*Math.max(0,size)).limit(Math.max(0,size)).toList();elapsed=(System.nanoTime()-start)/1000;structures="AVL in-order catalog pagination";}else{CatalogIndex.QueryResult result=index.search(q,category,available,page,size);selected=result.books();total=result.totalHits();structures=result.explain();elapsed=result.elapsedMicros();}List<BookView> books=selected.stream().map(CatalogController::view).toList();return new SearchPage(books,total,q,Math.max(0,page),Math.max(0,size),explain?structures:null,explain?elapsed:null);}
    @GetMapping("/books/suggest") public List<String> suggest(@RequestParam(defaultValue="") String prefix,@RequestParam(defaultValue="title") String type){return index.suggest(prefix,"author".equalsIgnoreCase(type),8);}
    @GetMapping("/books/{id}") public BookView get(@PathVariable long id){return index.all().stream().filter(b->b.id()==id).findFirst().map(CatalogController::view).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));}
    @GetMapping("/books/isbn/{isbn}") public BookView isbn(@PathVariable String isbn){CatalogIndex.Book b=index.findByIsbn(isbn);if(b==null)throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);return view(b);}
    @GetMapping("/lab/search-compare") public Map<String,Object> compare(@RequestParam(defaultValue="") String q){long start=System.nanoTime();var core=index.search(q,null,null,0,1000);long coreMicros=(System.nanoTime()-start)/1000;start=System.nanoTime();List<CatalogIndex.Book> avl=index.all().stream().filter(b->b.title().toLowerCase().startsWith(q.toLowerCase())).toList();long avlMicros=(System.nanoTime()-start)/1000;start=System.nanoTime();List<CatalogIndex.Book> linear=index.all().stream().filter(b->(b.title()+" "+b.authors()+" "+b.category()).toLowerCase().contains(q.toLowerCase())).toList();long linearMicros=(System.nanoTime()-start)/1000;return Map.of("query",q,"results",List.of(Map.of("method","Inverted word index AND","count",core.books().size(),"micros",coreMicros),Map.of("method","AVL title prefix scan","count",avl.size(),"micros",avlMicros),Map.of("method","Linear substring scan","count",linear.size(),"micros",linearMicros)),"note","SQL ILIKE comparison is pending database-backed catalog integration.");}
    @GetMapping("/lab/structure-stats") public Map<String,Object> stats(){return Map.of("catalogBooks",index.size(),"titleIndex","Trie","authorIndex","Trie","wordIndex","InvertedWordIndex","orderedIndex","AVL tree");}
    private static BookView view(CatalogIndex.Book b){return new BookView(b.id(),b.title(),b.authors(),b.isbn(),b.category(),b.language(),b.year(),b.available(),b.popularity(),"Synthetic catalog example.",b.shelfLocation());}
    public record SearchPage(List<BookView> items,int total,String query,int page,int size,String structures,Long elapsedMicros){}
}
