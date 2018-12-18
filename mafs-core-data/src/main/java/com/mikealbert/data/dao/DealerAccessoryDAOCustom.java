package com.mikealbert.data.dao;

import java.util.List;


/**
* Custom DAO for DealerAccessory Entity
* @author ravresh
*/

public interface DealerAccessoryDAOCustom {
	
	public List<Object[]> getQuotationDealerAccessoryByQmdIdDocId(Long qmdId, Long docId);
	public List<Object[]> getStockQuotationDealerAccessoryByQmdIdDocIdAndAccountCode( Long qmdId, Long thpDocId, String accountCode);
}