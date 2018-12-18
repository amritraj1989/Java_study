package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.VrbDiscount;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
* DAO implementation for VrbDiscountDAOCustom 
* @author sibley
*/

public class VrbDiscountDAOImpl extends GenericDAOImpl<VrbDiscount, Long> implements VrbDiscountDAOCustom {
	private static final long serialVersionUID = 8301792480838843611L;

	@Override
	public Long getVRBDiscountId(Long cId, String accountCode, String accountType, Long mdlId, String vrbType, String productCode, Long term) {
	    Long vrbdId;
	    Query query;
	    String stmt;
	    Object result;
	    
	    if(MALUtilities.isEmpty(term)) {
	    	stmt = "SELECT vrb_discount_wrapper.getVRBDiscountId(?,?,?,?,?,?) FROM dual";	    	
	    } else {
	    	stmt = "SELECT vrb_discount_wrapper.getVRBDiscountId(?,?,?,?,?,?,?) FROM dual";
	    }

		query = entityManager.createNativeQuery(stmt);
		
		query.setParameter(1, cId);
		query.setParameter(2, accountCode);
		query.setParameter(3, accountType);
		query.setParameter(4, mdlId);
		query.setParameter(5, vrbType);
		query.setParameter(6, productCode);
		
		if(!MALUtilities.isEmpty(term)) {
			query.setParameter(7, term);			
		}
		
		result =  query.getSingleResult();
		vrbdId = MALUtilities.isEmpty(result) ? null : ((BigDecimal) result).longValue();
	
		return vrbdId;
	}
		
}
