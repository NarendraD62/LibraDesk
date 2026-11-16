package com.libradesk.core;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BusinessAlgorithmsTest {
 @Test void fineUsesGraceHolidaysAndPriceCap(){FineCalculator f=new FineCalculator();LocalDate due=LocalDate.of(2025,1,1);assertEquals(new BigDecimal("0.00"),f.calculate(due,due.plusDays(1),1,new BigDecimal("2"),new BigDecimal("100"),Set.of()));assertEquals(new BigDecimal("2.00"),f.calculate(due,due.plusDays(3),1,new BigDecimal("2"),new BigDecimal("100"),Set.of(due.plusDays(2))));assertEquals(new BigDecimal("5.00"),f.calculate(due,due.plusDays(20),1,new BigDecimal("2"),new BigDecimal("5"),Set.of()));}
 @Test void loanPolicyEnforcesLimitsAndRenewalRules(){var rules=java.util.Map.of(LoanPolicy.Membership.STUDENT,new LoanPolicy.Rule(5,14,2,new BigDecimal("2")));LoanPolicy p=new LoanPolicy(rules,new BigDecimal("100"));assertEquals(LocalDate.of(2025,1,15),p.dueDate(LoanPolicy.Membership.STUDENT,LocalDate.of(2025,1,1)));assertEquals(2,p.renew(LoanPolicy.Membership.STUDENT,1,false,false));assertThrows(IllegalStateException.class,()->p.renew(LoanPolicy.Membership.STUDENT,2,false,false));assertThrows(IllegalStateException.class,()->p.validateCheckout(new LoanPolicy.MemberState(LoanPolicy.Membership.STUDENT,1,true,BigDecimal.ZERO,true)));}
 @Test void waitlistHonorsMembershipThenFifo(){WaitlistManager w=new WaitlistManager();Instant t=Instant.parse("2025-01-01T00:00:00Z");w.enqueue(1,"PUBLIC",t);w.enqueue(2,"STUDENT",t.plusSeconds(1));w.enqueue(3,"FACULTY",t.plusSeconds(10));assertEquals(1,w.positionOf(3));assertEquals(2,w.positionOf(2));assertEquals(3,w.positionOf(1));assertEquals(3,w.nextEligible(t,3).memberId());assertTrue(w.cancel(1));}
 @Test void invertedIndexReturnsSortedAndIntersectedIds(){InvertedWordIndex i=new InvertedWordIndex();i.add(9,"Algorithms and data structures");i.add(2,"Data structures design");i.add(5,"Algorithms for design");assertArrayEquals(new int[]{9},i.searchAnd("algorithms structures"));assertArrayEquals(new int[]{2,5},i.searchAnd("design"));i.remove(9,"Algorithms and data structures");assertArrayEquals(new int[0],i.searchAnd("algorithms structures"));}
}
