package com.mikealbert.data.dao;

import com.mikealbert.data.vo.QuotationElementAmountsVO;
import com.mikealbert.exception.MalBusinessException;

public interface QuotationElementDAOCustom {
	
	public String findRecalcNeededByLeaseElementId(Long leaseElementId);
	public QuotationElementAmountsVO callQuotationElementOriginalValues(Long qmdId, Long lelId) throws MalBusinessException;
	
	public String findFinanceElementExistByProfileId(Long qprId, Long cId);
}
