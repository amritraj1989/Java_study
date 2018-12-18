package com.mikealbert.data.dao;


/**
* Custom DAO for VRBDiscount 
* @author sibley
*/

public interface VrbDiscountDAOCustom {
	public Long getVRBDiscountId(Long cId, String accountCode, String accountType, Long mdlId, String vrbType, String productCode, Long term);

}
