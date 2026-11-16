package com.libradesk.core;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/** Configurable pure loan and renewal rules. */
public final class LoanPolicy {
    public enum Membership { STUDENT, FACULTY, PUBLIC }
    public record Rule(int limit,int loanDays,int maxRenewals,BigDecimal finePerDay){}
    public record MemberState(Membership type,int activeLoans,boolean overdue,BigDecimal unpaidFines,boolean active){}
    private final Map<Membership,Rule> rules;
    private final BigDecimal fineBlockThreshold;
    /** Creates rules in O(1) time and O(1) additional space. */
    public LoanPolicy(Map<Membership,Rule> rules,BigDecimal fineBlockThreshold){this.rules=Map.copyOf(rules);this.fineBlockThreshold=fineBlockThreshold;}
    /** Checks eligibility in O(1) time and space. */
    public void validateCheckout(MemberState m){Rule r=rules.get(m.type());if(!m.active())throw new IllegalStateException("Membership is not active");if(m.overdue())throw new IllegalStateException("Return overdue items before borrowing");if(m.unpaidFines().compareTo(fineBlockThreshold)>0)throw new IllegalStateException("Unpaid fines exceed the borrowing limit");if(m.activeLoans()>=r.limit())throw new IllegalStateException("Loan limit reached");}
    /** Calculates due date in O(1) time and space. */ public LocalDate dueDate(Membership type,LocalDate borrowed){return borrowed.plusDays(rules.get(type).loanDays());}
    /** Validates and returns next renewal count in O(1) time and space. */ public int renew(Membership type,int renewals,boolean overdue,boolean waiting){if(overdue)throw new IllegalStateException("Overdue loans cannot be renewed");if(waiting)throw new IllegalStateException("A reservation is waiting");if(renewals>=rules.get(type).maxRenewals())throw new IllegalStateException("Renewal limit reached");return renewals+1;}
    /** Returns membership rule in O(1) time and space. */ public Rule rule(Membership type){return rules.get(type);}
}
