package com.mikealbert.data.comparator;

import java.util.Comparator;

import com.mikealbert.data.entity.UpfitterProgress;
import com.mikealbert.util.MALUtilities;
/**
 * Sorts the list of UpfitterProgress on sequence no in ASC order.
 * 
 * @see UpfitterProgress
 * @author sibley
 *
 */
public class UpfitterProgressComparator implements Comparator<UpfitterProgress> {

	@Override
	public int compare(UpfitterProgress ufp1, UpfitterProgress ufp2) { 
		int retVal;
		
		if(ufp1.getSequenceNo().compareTo(ufp2.getSequenceNo()) == 0) {
			if(MALUtilities.isEmpty(ufp1.getParentUpfitterProgress())) {
				retVal = -1;
			} else if(!MALUtilities.isEmpty(ufp1.getParentUpfitterProgress()) && !MALUtilities.isEmpty(ufp2.getParentUpfitterProgress())) {
				retVal = ufp1.getUpfitter().getAccountName().compareTo(ufp2.getUpfitter().getAccountName());
			} else {
				retVal = 1;				
			}
		} else {
			retVal = ufp1.getSequenceNo().compareTo(ufp2.getSequenceNo()); 
		}
		
		return retVal;
	}

}
