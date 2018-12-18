package com.mikealbert.vision.util;

import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import com.mikealbert.data.entity.DriverAddressHistory;
import com.mikealbert.data.entity.DriverAllocation;


public class AddressStartDateComparator implements Comparator<DriverAddressHistory> {

    @Override
    public int compare(DriverAddressHistory dah1, DriverAddressHistory dah2) {
 
    	final Date BASELINE_DATE = new GregorianCalendar(1900, 0, 1).getTime();

    	Date date1 = dah1.getStartDate() == null ? BASELINE_DATE : dah1.getStartDate();
    	Date date2 = dah2.getStartDate() == null ? BASELINE_DATE : dah2.getStartDate();
    	
        if (date1.after(date2)){
            return 1;
        }else if (date1.before(date2)){
            return -1;
        }else{
            return 0;
        }
    }

}