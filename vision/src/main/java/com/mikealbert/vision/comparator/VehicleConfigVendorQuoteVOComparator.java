package com.mikealbert.vision.comparator;

import java.util.Comparator;
import com.mikealbert.data.vo.VehicleConfigVendorQuoteVO;
import com.mikealbert.util.MALUtilities;

/**
 * 
 * @see VehicleConfigVendorQuoteVO
 * @author ravresh
 * 
 */
public enum VehicleConfigVendorQuoteVOComparator implements Comparator<VehicleConfigVendorQuoteVO> {
	VUQ_ID_SORT {
		public int compare(VehicleConfigVendorQuoteVO vuq1, VehicleConfigVendorQuoteVO vuq2) { 
			if(MALUtilities.isEmpty(vuq1.getVuqId()) || MALUtilities.isEmpty(vuq2.getVuqId())) {
				return MALUtilities.isEmpty(vuq1.getVuqId()) ? -1 : 1;
			} else {
				return vuq1.getVuqId().compareTo(vuq2.getVuqId()) * -1;
			}
			 
		}
	},
	VUQ_STATUS_SORT {
		public int compare(VehicleConfigVendorQuoteVO vuq1, VehicleConfigVendorQuoteVO vuq2) {
			return vuq1.getStatus().compareTo(vuq2.getStatus());
		}
	};

	public static Comparator<VehicleConfigVendorQuoteVO> getComparator(final VehicleConfigVendorQuoteVOComparator... multipleOptions) {
		return new Comparator<VehicleConfigVendorQuoteVO>() {
			public int compare(VehicleConfigVendorQuoteVO vuq1, VehicleConfigVendorQuoteVO vuq2) {
				for(VehicleConfigVendorQuoteVOComparator option : multipleOptions) {
					int result = option.compare(vuq1, vuq2);
					if(result != 0) {
						return result;
					}
				}
				return 0;
			}
		};
	}
}
