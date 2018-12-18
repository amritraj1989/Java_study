package com.mikealbert.service.comparator;

import java.util.Comparator;
import com.mikealbert.data.entity.QuotationElementStep;

public class QuotationElementStepComparator implements Comparator<QuotationElementStep> {

	@Override
	public int compare(QuotationElementStep r1, QuotationElementStep r2) {
		if (r1.getFromPeriod() == null && r2.getFromPeriod() == null) {
			return 0;
		} else if (r1.getFromPeriod() == null && r2.getFromPeriod() != null) {
			return -1;
		} else if (r1.getFromPeriod() != null && r2.getFromPeriod() == null) {
			return 1;
		} else {
			return (r1.getFromPeriod().compareTo(r2.getFromPeriod()));
		}
	}

}
