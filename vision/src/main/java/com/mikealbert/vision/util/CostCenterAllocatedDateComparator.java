package com.mikealbert.vision.util;

import java.util.Comparator;

import com.mikealbert.data.entity.DriverCostCenter;


public class CostCenterAllocatedDateComparator implements Comparator<DriverCostCenter> {

    @Override
    public int compare(DriverCostCenter dcc1, DriverCostCenter dcc2) {
 
        if (dcc1.getDateAllocated().after(dcc2.getDateAllocated())){
            return 1;
        }else if (dcc1.getDateAllocated().before(dcc2.getDateAllocated())){
            return -1;
        }else{
            return 0;
        }
    }

}