package com.mikealbert.vision.comparator;

import java.util.Comparator;

import com.mikealbert.data.entity.PurchaseOrderReleaseQueueV;
import com.mikealbert.util.MALUtilities;

/**
 * Sorts the list of Purchase order release on 'PO Status' then 'Make' and then 'Unit Number' 
 * in ascending order. 
 * 
 * @author Amritraj
 * 
 */
public enum PurchaseOrderReleaseQueueComparator implements Comparator<PurchaseOrderReleaseQueueV> {
	
	PO_STATUS_SORT {
		public int compare(PurchaseOrderReleaseQueueV o1, PurchaseOrderReleaseQueueV o2) {
			
			if(MALUtilities.isEmpty(o1.getPoStatus())) {
				return -1;
			}else if (o1.getPoStatus().equalsIgnoreCase("open")){
				return -1;
			}else if(o1.getPoStatus().equalsIgnoreCase(o2.getPoStatus())){
				return 0;
			}else{
				return 1;
			}
		}
	},
	MAKE_SORT {
		public int compare(PurchaseOrderReleaseQueueV o1, PurchaseOrderReleaseQueueV o2) {
			return o1.getMake().trim().compareTo(o2.getMake().trim());
		}
	},
	UNIT_NO_SORT {
		public int compare(PurchaseOrderReleaseQueueV o1, PurchaseOrderReleaseQueueV o2) {
			return o1.getUnitNo().compareTo(o2.getUnitNo());
		}
	};

	public static Comparator<PurchaseOrderReleaseQueueV> getComparator(final PurchaseOrderReleaseQueueComparator... multipleOptions) {
		return new Comparator<PurchaseOrderReleaseQueueV>() {
			public int compare(PurchaseOrderReleaseQueueV o1, PurchaseOrderReleaseQueueV o2) {
				for(PurchaseOrderReleaseQueueComparator option : multipleOptions) {
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
