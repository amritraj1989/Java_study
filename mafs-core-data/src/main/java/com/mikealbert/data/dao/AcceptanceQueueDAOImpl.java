package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.mikealbert.data.entity.AcceptanceQueueV;

public class AcceptanceQueueDAOImpl extends GenericDAOImpl<AcceptanceQueueV, Long> implements AcceptanceQueueDAOCustom {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8249709217532451721L;

	@Override
	public List<AcceptanceQueueV> getAcceptanceQueueList() {
		List<AcceptanceQueueV> acceptanceQueueList = new ArrayList<AcceptanceQueueV>();
		Query query = null;
		
		String sqlStmt = "SELECT aqv.*"
							+ " FROM ACCEPTANCE_QUEUE_V aqv"
							+ " ORDER BY"
							+ " DECODE(DECODE(tolerance_yn, 'N', 1, 2), 1, REQUEST_FOR_ACCEPTANCE_DATE, NULL),"
							+ " DECODE(PRODUCT_CODE, 'MAX', 1, DECODE(ORDER_TYPE, 'Locate', -1, 0)),"
							+ " DECODE (QUOTE_STATUS,  4, '1',  12, '1',  '2'),"
							+ " DECODE (REQUEST_FOR_ACCEPTANCE_TYPE, 'Vehicle Lease Order', '5', '6')";
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null && objectList.size() > 0) {
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				
				Object[] object = iterator.next();
				int i = 0;
				AcceptanceQueueV acceptanceQueueV = new AcceptanceQueueV();
				
				acceptanceQueueV.setOrderType(object[i] != null ? (String) object[i] : null);
				acceptanceQueueV.setRequestForAcceptDate(object[i+=1] != null ? (Date) object[i] : null);
				acceptanceQueueV.setRequestForAcceptBy(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setQuoteAcceptanceTypeCode(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setRequestForAcceptType(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setQmdId(object[i+=1] != null ? ((BigDecimal) object[i]).longValue() : null);
				acceptanceQueueV.setQuoId(object[i+=1] != null ? ((BigDecimal) object[i]).longValue() : null);
				acceptanceQueueV.setQuoteNumber(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setUnitNo(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setQuoteStatus(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setPrintedDate(object[i+=1] != null ? (Date) object[i] : null);
				acceptanceQueueV.setRevisionExpDate(object[i+=1] != null ? (Date) object[i] : null);
				acceptanceQueueV.setClientAccount(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setClientAccountName(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setClientAccountCid(object[i+=1] != null ? String.valueOf(((BigDecimal) object[i]).longValue()) : null);
				acceptanceQueueV.setClientAccountType(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setDriverId(object[i+=1] != null ? ((BigDecimal) object[i]).longValue() : null);
				acceptanceQueueV.setDriverFirstName(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setDriverLastName(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setContractPeriod(object[i+=1] != null ? ((BigDecimal) object[i]).longValue() : null);
				acceptanceQueueV.setContractDistance(object[i+=1] != null ? ((BigDecimal) object[i]).longValue() : null);
				acceptanceQueueV.setModelDescription(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setQuoteStartOdo(object[i+=1] != null ? ((BigDecimal) object[i]).longValue() : null);
				acceptanceQueueV.setTmpVinNo(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setExteriorColor(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setInteriorColor(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setReturningUnitFmsId(object[i+=1] != null ? ((BigDecimal) object[i]).longValue() : null);
				acceptanceQueueV.setReturningUnitNumber(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setClientRequestDate(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setProductCode(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setToleranceYN(object[i+=1] != null ? (String) object[i] : null);
				acceptanceQueueV.setRevisionQmdId(object[i+=4] != null ? ((BigDecimal) object[i]).longValue() : null);
				acceptanceQueueList.add(acceptanceQueueV);
			}
		}
		return acceptanceQueueList;
	}
	
	
}
