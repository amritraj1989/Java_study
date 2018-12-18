package com.mikealbert.data.util;

import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import com.mikealbert.data.entity.QuotationSchedule;


public class QuotationScheduleTransDateComparator implements Comparator<QuotationSchedule> {

    @Override
    public int compare(QuotationSchedule one, QuotationSchedule two) {
 
    	final Date BASELINE_DATE = new GregorianCalendar(1900, 0, 1).getTime();

    	Date date1 = one.getTransDate() == null ? BASELINE_DATE : one.getTransDate();
    	Date date2 = two.getTransDate() == null ? BASELINE_DATE : two.getTransDate();
    	
        if (date1.after(date2)){
            return 1;
        }else if (date1.before(date2)){
            return -1;
        }else{
            return 0;
        }
    }

}