package com.mikealbert.vision.comparator;

import java.util.Comparator;
import com.mikealbert.data.vo.VehicleConfigModelVO;
import com.mikealbert.util.MALUtilities;

/**
 * 
 * @see VehicleConfigModelVO
 * @author ravresh
 * 
 */
public enum VehicleConfigModelVOComparator implements Comparator<VehicleConfigModelVO> {
	VCM_ID_SORT {
		public int compare(VehicleConfigModelVO vcm1, VehicleConfigModelVO vcm2) { 
			if(MALUtilities.isEmpty(vcm1.getVcmId()) || MALUtilities.isEmpty(vcm2.getVcmId())) {
				return MALUtilities.isEmpty(vcm1.getVcmId()) ? -1 : 1;
			} else {
				return vcm1.getVcmId().compareTo(vcm2.getVcmId()) * -1;
			} 
		}
	},
	VCM_STATUS_SORT {
		public int compare(VehicleConfigModelVO vcm1, VehicleConfigModelVO vcm2) {
			return vcm1.getStatus().compareTo(vcm2.getStatus());
		}
	};

	public static Comparator<VehicleConfigModelVO> getComparator(final VehicleConfigModelVOComparator... multipleOptions) {
		return new Comparator<VehicleConfigModelVO>() {
			public int compare(VehicleConfigModelVO vcm1, VehicleConfigModelVO vcm2) {
				for(VehicleConfigModelVOComparator option : multipleOptions) {
					int result = option.compare(vcm1, vcm2);
					if(result != 0) {
						return result;
					}
				}
				return 0;
			}
		};
	}
}
