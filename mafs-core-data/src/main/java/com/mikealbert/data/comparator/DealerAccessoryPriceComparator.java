package com.mikealbert.data.comparator;

import java.util.Comparator;

import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.util.MALUtilities;
/**
 * Sorts the list of DealerAccessoryPrice on dplId in descending order.
 * 
 * @see DealerAccessoryPrice
 * @author sibley
 *
 */
public class DealerAccessoryPriceComparator implements Comparator<DealerAccessoryPrice> {

	@Override
	public int compare(DealerAccessoryPrice o1, DealerAccessoryPrice o2) {
		if(MALUtilities.isEmpty(o1)){
			return -1;
		} else if (MALUtilities.isEmpty(o2)) {
			return 1;
		} else {
			return o1.getDplId().compareTo(o2.getDplId());			
		}					
	}

}
