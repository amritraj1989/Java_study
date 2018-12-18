package com.mikealbert.data.comparator;

import java.util.Comparator;

import com.mikealbert.data.entity.UpfitterProgress;
import com.mikealbert.data.vo.VendorInfoVO;
import com.mikealbert.util.MALUtilities;
/**
 * Sorts the list of UpfitterProgress on sequence no in ASC order.
 * 
 * @see LogBookEntry
 * @author sibley
 *
 */
public class VendorInfoVOComparator implements Comparator<VendorInfoVO> {

	@Override
	public int compare(VendorInfoVO obj1, VendorInfoVO obj2) { 
		int retVal;
		
		if(obj1.getSequenceNo().compareTo(obj2.getSequenceNo()) == 0) {
			if(MALUtilities.isEmpty(obj1.getLinked())) {
				retVal = 0; //This scenario supports the scenario when stock quote upfits are on a single PO. This is only for backwards compatibility
				            //Otherwise, a po is created for each upfitter resulting in each object linked property being set.
			} else if(!obj1.getLinked()) {
				retVal = -1;
			} else if(obj1.getLinked() && obj2.getLinked()) {
				retVal = obj1.getName().compareTo(obj2.getName());
			} else {
				retVal = 1;				
			}
		} else {
			retVal = obj1.getSequenceNo().compareTo(obj2.getSequenceNo()); 
		}
		
		return retVal;
	}

}
