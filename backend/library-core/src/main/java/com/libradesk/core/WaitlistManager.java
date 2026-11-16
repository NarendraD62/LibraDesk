package com.libradesk.core;

import java.time.Instant;
import java.util.Comparator;

/** Per-book reservation queue ordered by membership priority, request time, then sequence. */
public final class WaitlistManager {
    public record Request(long memberId,String membership,Instant requestedAt,long sequence){}
    private final IndexedMinHeap<Long,Request> queue=new IndexedMinHeap<>(Comparator.comparingInt((Request r)->priority(r.membership())).thenComparing(Request::requestedAt).thenComparingLong(Request::sequence));
    private long sequence;
    /** Adds or replaces a reservation in O(log n) time and O(1) amortized space. */
    public Request enqueue(long memberId,String membership,Instant time){Request r=new Request(memberId,membership,time,sequence++);queue.put(memberId,r);return r;}
    /** Cancels a reservation in O(log n) time and O(1) space. */ public boolean cancel(long memberId){return queue.remove(memberId)!=null;}
    /** Returns the next eligible member and assigns the hold expiry in O(log n) time and O(1) space. */ public ReadyHold nextEligible(Instant now,int holdDays){Request r=queue.poll();return r==null?null:new ReadyHold(r.memberId(),now.plusSeconds(holdDays*86400L));}
    /** Returns one-based queue position in O(n) time and O(1) extra space. */ public int positionOf(long memberId){Request target=queue.get(memberId);if(target==null)return 0;int[] ahead={0};queue.forEach(r->{if(compare(r,target)<0)ahead[0]++;});return ahead[0]+1;}
    /** Returns number waiting in O(1) time and space. */ public int size(){return queue.size();}
    private static int compare(Request a,Request b){return Comparator.comparingInt((Request r)->priority(r.membership())).thenComparing(Request::requestedAt).thenComparingLong(Request::sequence).compare(a,b);}
    private static int priority(String type){return switch(type.toUpperCase()){case "FACULTY"->0;case "STUDENT"->1;default->2;};}
    public record ReadyHold(long memberId,Instant readyUntil){}
}
