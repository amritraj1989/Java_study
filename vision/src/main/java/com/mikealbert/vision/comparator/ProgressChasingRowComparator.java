package com.mikealbert.vision.comparator;

import java.util.Comparator;

import com.mikealbert.data.vo.ProgressChasingVO;
import com.mikealbert.util.ObjectUtils;

public class ProgressChasingRowComparator implements Comparator<ProgressChasingVO> {
    private String sortField;
    private String sortOrder;
     
    public ProgressChasingRowComparator(String sortField, String sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
 
    public int compare(ProgressChasingVO o1, ProgressChasingVO o2) {
    	try {
    		
    		Object value1 = ObjectUtils.getProperty(o1, sortField);
            Object value2 = ObjectUtils.getProperty(o2, sortField);

            int value = ((Comparable)value1).compareTo(value2);
           
            return sortOrder.equalsIgnoreCase("ASC") ? value : -1 * value;
    	} catch(Exception e) {
            throw new RuntimeException();
    	}
    }

}
