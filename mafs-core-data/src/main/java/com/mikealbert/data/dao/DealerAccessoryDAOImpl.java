package com.mikealbert.data.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;
import com.mikealbert.data.entity.DealerAccessory;

/**
 * DAO implementation for DealerAccessory Entity
 * 
 * @author ravresh
 */

public class DealerAccessoryDAOImpl extends GenericDAOImpl<DealerAccessory, Long> implements DealerAccessoryDAOCustom {
	
	@Resource 
	private DealerAccessoryDAO dealerAccessoryDAO;
	private static final long serialVersionUID = -7549588104019968473L;

	@SuppressWarnings("unchecked")
	public List<Object[]> getQuotationDealerAccessoryByQmdIdDocId(Long qmdId, Long docId) {

		String queryString = "Select qda.external_reference_no, dac.description "
				+ "from quotation_dealer_accessories qda, dealer_accessories da, dealer_accessory_codes dac "
				+ "where da.accessory_code = dac.accessory_code "
				+ "and qda.dac_dac_id = da.dac_id "
				+ "and qda.qmd_qmd_id = :qmdId "
				+ "and qda.dac_dac_id in(Select generic_ext_id from docl where doc_id = :docId)"
				+ "order by qda.external_reference_no, dac.description";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("qmdId", qmdId);
		query.setParameter("docId", docId);

		List<Object[]> resultList = (List<Object[]>) query.getResultList();

		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getStockQuotationDealerAccessoryByQmdIdDocIdAndAccountCode( Long qmdId, Long thpDocId, String accountCode) {
		String queryString = "Select qda.external_reference_no, dac.description "
				+ "from quotation_dealer_accessories qda, dealer_accessories da, dealer_accessory_codes dac "
				+ "where qda.dac_dac_id = da.dac_id "
				+ "and da.accessory_code = dac.accessory_code "
				+ "and qda.c_id = 1 "
				+ "and qda.account_type = 'S' "
				+ "and qda.account_code = :accountCode "
				+ "and qda.qmd_qmd_id = :qmdId "
				+ "and qda.dac_dac_id in(Select generic_ext_id from docl where doc_id = :thpDocId) "
				+ "order by qda.external_reference_no, dac.description";
		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("accountCode", accountCode);
		query.setParameter("qmdId", qmdId);
		query.setParameter("thpDocId", thpDocId);
		List<Object[]>resultList = (List<Object[]>) query.getResultList();
				
		return resultList;
	}
}