package com.mikealbert.vision.util;

import java.util.Comparator;

import com.mikealbert.data.entity.DriverAllocation;


public class AllocationStartDateComparator implements Comparator<DriverAllocation> {

    @Override
    public int compare(DriverAllocation da1, DriverAllocation da2) {
 
        if (da1.getAllocationDate().after(da2.getAllocationDate())){
            return 1;
        }else if (da1.getAllocationDate().before(da2.getAllocationDate())){
            return -1;
        }else{
            return 0;
        }
    }

}