package com.mikealbert.data.comparator;

import java.util.Comparator;

import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.VehicleOrderingFee;
import com.mikealbert.util.MALUtilities;
/**
 * Sorts the list of VehicleOrderingFeeComparator on effectiveDate in descending order.
 * 
 * @see DealerAccessoryPrice
 * @author sibley
 *
 */
public class VehicleOrderingFeeComparator implements Comparator<VehicleOrderingFee> {

	@Override
	public int compare(VehicleOrderingFee o1, VehicleOrderingFee o2) {
		return o1.getEffectiveDate().compareTo(o2.getEffectiveDate());			
	}

}
