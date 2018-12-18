package com.mikealbert.vision.comparator;

import java.util.Comparator;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.vo.UnitProgressSearchVO;

/**
 * Sorts the list of In Service Date Progress on 'Invoice Processed Date' then 'Client Requested Date' and then 'Dealer Received/Stock Accepted' 
 * in ascending order. Dates that are null will fall to the bottom of the list.
 * 
 * @see UnitProgressSearchVO
 * @author ravresh
 * 
 */
public enum InServiceProgressDateComparator implements Comparator<UnitProgressSearchVO> {
	INV_DATE_SORT {
		public int compare(UnitProgressSearchVO o1, UnitProgressSearchVO o2) {
			if(MALUtilities.isEmpty(o1.getInvoiceProcessedDate()) && MALUtilities.isEmpty(o2.getInvoiceProcessedDate())) {
				return 0;
			} else if(MALUtilities.isEmpty(o1.getInvoiceProcessedDate()) || MALUtilities.isEmpty(o2.getInvoiceProcessedDate())) {
				return MALUtilities.isEmpty(o1.getInvoiceProcessedDate()) ? 1 : -1;
			} else {
				return MALUtilities.clearTimeFromDate(o1.getInvoiceProcessedDate()).compareTo(MALUtilities.clearTimeFromDate(o2.getInvoiceProcessedDate()));
			}
		}
	},
	REQ_BY_DATE_SORT {
		public int compare(UnitProgressSearchVO o1, UnitProgressSearchVO o2) {
			if(MALUtilities.isEmpty(o1.getReqdBy()) && MALUtilities.isEmpty(o2.getReqdBy())) {
				return 0;
			} else if(MALUtilities.isEmpty(o1.getReqdBy()) || MALUtilities.isEmpty(o2.getReqdBy())) {
				return MALUtilities.isEmpty(o1.getReqdBy()) ? 1 : -1;
			} else {
				if(MALUtilities.isValidDate(o1.getReqdBy()) && MALUtilities.isValidDate(o2.getReqdBy())){
					return MALUtilities.formatDate(o1.getReqdBy()).compareTo(MALUtilities.formatDate(o2.getReqdBy()));
				}else if(MALUtilities.isValidDate(o1.getReqdBy()) && !MALUtilities.isValidDate(o2.getReqdBy())){
					return -1;
				}else if(!MALUtilities.isValidDate(o1.getReqdBy()) && MALUtilities.isValidDate(o2.getReqdBy())){
					return 1;
				}else if(! MALUtilities.isValidDate(o1.getReqdBy()) && !MALUtilities.isValidDate(o2.getReqdBy())){
					return o1.getReqdBy().compareTo(o2.getReqdBy());
				}else{
					return 0;
				}
				
			}
		}
	},
	DLR_REC_DATE_SORT {
		public int compare(UnitProgressSearchVO o1, UnitProgressSearchVO o2) {
			if(MALUtilities.isEmpty(o1.getDealerReceivedDate()) && MALUtilities.isEmpty(o2.getDealerReceivedDate())) {
				return 0;
			} else if(MALUtilities.isEmpty(o1.getDealerReceivedDate()) || MALUtilities.isEmpty(o2.getDealerReceivedDate())) {
				return MALUtilities.isEmpty(o1.getDealerReceivedDate()) ? 1 : -1;
			} else {
				return MALUtilities.clearTimeFromDate(o1.getDealerReceivedDate()).compareTo(MALUtilities.clearTimeFromDate(o2.getDealerReceivedDate()));
			}
		}
	};

	//TODO: This code looks like it may violate compare contract. Could become an issue in 1.7+ releases of Java. Need to look into....
	public static Comparator<UnitProgressSearchVO> getComparator(final InServiceProgressDateComparator... multipleOptions) {
		return new Comparator<UnitProgressSearchVO>() {
			public int compare(UnitProgressSearchVO o1, UnitProgressSearchVO o2) {
				for(InServiceProgressDateComparator option : multipleOptions) {
					int result = option.compare(o1, o2);
					if(result != 0) {
						return result;
					}
				}
				return 0;
			}
		};
	}
}
