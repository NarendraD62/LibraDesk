package com.libradesk.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/** Pure fine calculation with injected policy values. */
public final class FineCalculator {
    /** Calculates due-date fine after grace, excluding configured closed dates, capped by price; O(d) time and O(1) space for d overdue days. */
    public BigDecimal calculate(LocalDate due,LocalDate returned,int graceDays,BigDecimal dailyRate,BigDecimal price,Set<LocalDate> closedDays){long daysOverdue=Math.max(0,ChronoUnit.DAYS.between(due,returned));long chargeable=0;for(long i=Math.max(0,graceDays)+1;i<=daysOverdue;i++)if(!closedDays.contains(due.plusDays(i)))chargeable++;BigDecimal fine=dailyRate.multiply(BigDecimal.valueOf(chargeable)).min(price);return fine.max(BigDecimal.ZERO).setScale(2,RoundingMode.HALF_UP);}
}
