package com.github.derpynewbie.report.util.comparator;

import com.github.derpynewbie.report.util.ReportData;

import java.util.Comparator;

public class ReportDataDateComparator implements Comparator<ReportData> {
    @Override
    public int compare(ReportData t1, ReportData t2) {
        return t1.getTime().equals(t2.getTime()) ? 0 : t1.getTime() > t2.getTime() ? -1 : 1;
    }
}
