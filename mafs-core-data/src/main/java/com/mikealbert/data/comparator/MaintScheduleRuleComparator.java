package com.mikealbert.data.comparator;

import java.util.Comparator;

import com.mikealbert.data.entity.MaintScheduleRule;

/**Per mss-223 
 * This comparator is being used to replicate the results returned when the following 'order by' 
 *  is executed in a query selecting from maint_schedule_rules, master_schedules
 *  
 *  order by year desc, make_code asc, 
 *		(CASE 
 *		    WHEN make_code is not null and make_model_desc is null THEN fuel_type_group
 *		    ELSE model_type_desc END) asc, 
 *		model_type_desc asc, make_model_desc asc, fuel_type_group asc;  
 */  


public class MaintScheduleRuleComparator implements Comparator<MaintScheduleRule> {
	
	@Override
    public int compare(MaintScheduleRule o1, MaintScheduleRule o2) {

		int i = 0;
		if (Double.parseDouble(o1.getYear()) > Double.parseDouble(o2.getYear())){
			i = 1;
		} else if (Double.parseDouble(o1.getYear()) < Double.parseDouble(o2.getYear())){
			i = -1;
		} else {
			i = 0;
		}

        int j = nullSafeStringComparator(o1.getMakeCode(),o2.getMakeCode());
        int k = nullSafeStringComparator(o1.getModelTypeDesc(),o2.getModelTypeDesc());
        int l = nullSafeStringComparator(o1.getMakeModelDesc(),o2.getMakeModelDesc());
        int m = nullSafeStringComparator(o1.getFuelTypeGroup(),o2.getFuelTypeGroup());
        int retStatus=0;
       
        if ( i > 0 ) {
        	retStatus = -1;
        } else if ( i < 0 ) {
        	retStatus = 1;
        } else {
        	if ( j < 0 ) {
        		retStatus = -1;
            } else if ( j > 0 ) {
            	retStatus = 1;
            } else {
            	if ( k < 0 ) {
            		retStatus = -1;
                } else if( k > 0 ){
                	retStatus = 1;
                } else {
                	if ( l < 0 ) {
                		retStatus = -1;
                    } else if( l > 0 ){
                    	retStatus = 1;
                	} else {
                    	if ( m < 0 ) {
                    		retStatus = -1;
                    	} else if( m > 0 ){
                    		retStatus = 1;
                    	} else {
                    		retStatus = 0;
                    	}
                    }
        	 	}
            }
        }
        return retStatus;
	}
	
	public static int nullSafeStringComparator(final String one, final String two) {
	    if (one == null ^ two == null) {
	        return (one == null) ? 1 : -1;
	    }

	    if (one == null && two == null) {
	        return 0;
	    }

	    return one.compareToIgnoreCase(two);
	}	
}

