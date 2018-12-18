package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.CapitalElementRule;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;


public  class CapitalElementDAOImpl extends GenericDAOImpl<CapitalElement, Long> implements CapitalElementDAOCustom {

	@Resource
	private CapitalElementDAO capitalElementDAO;
	
	private static final long serialVersionUID = 1L;	
	
	@Resource DataSource dataSource;

	@SuppressWarnings("unchecked")
	public List<CapitalElement> getCapitalElementsByProductCode(String productCode) {
		
		String stmt = "select CE.CEL_ID, CE.CODE, CE.DESCRIPTION, CE.CAP_ELEMENT_TYPE, CE.FIXED_ASSET, CE.FMV_INCLUDE, " +
				  	  "CE.LF_MARGIN_ONLY, CE.ON_INVOICE, CE.PURCHASE_ORDER, CE.QUOTATION_CALCULATION, CE.QUOTE_CAPITAL, " +
					  "CE.QUOTE_CONCEALED, CE.RECHARGEABLE, CE.RECLAIM_AGAINST, CE.RECLAIM_CALCULATION, CE.RECLAIMABLE, CE.VERSIONTS " +
				  	  "from product_elements pe, lease_ele_capital_ele lece, capital_elements ce " + 
				  	  "where PE.PRD_PRODUCT_CODE = ? " +
				  	  "and PE.LEL_LEL_ID = LECE.LEL_LEL_ID " +
				  	  "and LECE.CEL_CEL_ID = CE.CEL_ID";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, productCode);
		List<Object[]> results = (List<Object[]>) query.getResultList();
	
		List<CapitalElement> list = new ArrayList<CapitalElement>(); 

		if (results != null && results.size() > 0) {	
			
			for (Iterator<Object[]> iterator = results.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				CapitalElement capitalElement = new CapitalElement();
				capitalElement.setCelId(object[0] != null ? ((BigDecimal) object[0]).longValue() : null);
				capitalElement.setCode(object[1] != null ? ((String) object[1]) : null);
				capitalElement.setDescription(object[2] != null ? ((String) object[2]) : null);
				capitalElement.setCapElementType(object[3] != null ? ((String) object[3]) : null);
				capitalElement.setFixedAsset(object[4] != null ? ((String) object[4]) : null);
				capitalElement.setFmvInclude(object[5] != null ? ((String) object[5]) : null);
				capitalElement.setLfMarginOnly(object[6] != null ? ((String) object[6]) : null);
				capitalElement.setOnInvoice(object[7] != null ? ((String) object[7]) : null);
				capitalElement.setPurchaseOrder(object[8] != null ? ((String) object[8]) : null);
				capitalElement.setQuotationCalculation(object[9] != null ? ((String) object[9]) : null);
				capitalElement.setQuoteCapital(object[10] != null ? ((String) object[10]) : null);
				capitalElement.setQuoteConcealed(object[11] != null ? ((String) object[11]) : null);
				capitalElement.setRechargeable(object[12] != null ? ((String) object[12]) : null);
				capitalElement.setReclaimAgainst(object[13] != null ? ((String) object[13]) : null);
				capitalElement.setReclaimCalculation(object[14] != null ? ((String) object[14]) : null);
				capitalElement.setReclaimable(object[15] != null ? ((String) object[15]) : null);
				
				list.add(capitalElement);
			}
		}
		return list;
	}

	@Override
	public List<QuotationCapitalElement> getApplicableCapitalElementsWithValue(Long qprId, String standardEDINo, Long corporateId, String accountType, String accountCode, String orderType) {
		String sql;
		Query query;
		
		List<QuotationCapitalElement> qceList = new ArrayList<QuotationCapitalElement>();
		QuotationCapitalElement quotationCapitalElement = null;
		CapitalElement capitalElement = null;

		sql = "SELECT element_name,"
				+ " element_id,"
				+ " element_code,"
				+ " cer_cer_id,"
				+ " quote_elem_calc,"
				+ " element_value"
				+ " FROM TABLE(quotation_wrapper.get_cap_ele_by_profile_model(:qprId, :standardEDINo, :corporateId, :accountType, :accountCode, :orderType))";

		query = entityManager.createNativeQuery(sql);
		query.setParameter("qprId", qprId);
		query.setParameter("standardEDINo", standardEDINo);
		query.setParameter("corporateId", corporateId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		query.setParameter("orderType", orderType);

		CapitalElementRule cer;
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				quotationCapitalElement = new QuotationCapitalElement();
				
				capitalElement = new CapitalElement();
				capitalElement.setDescription((String)record[i]);
				capitalElement.setCelId(MALUtilities.isEmpty(record[i+=1]) ? null : ((BigDecimal)record[i]).longValue());
				capitalElement.setCode((String)record[i+=1]);
				
				if(!MALUtilities.isEmpty(record[i+=1])){
					cer = new CapitalElementRule();
					cer.setCerId(((BigDecimal)record[i]).longValue());
					quotationCapitalElement.setCapitalElementRule(cer);
				}else{
					quotationCapitalElement.setCapitalElementRule(null);
				}
				
				
				capitalElement.setQuotationCalculation((String)record[i+=1]);
				
				quotationCapitalElement.setCapitalElement(capitalElement);
				quotationCapitalElement.setValue((BigDecimal)record[i+=1]);
				
				
				qceList.add(quotationCapitalElement);
			}
		}
		return qceList;
	}

	@Override
	public BigDecimal getCapitalElementValue(Long celId, Long cerId, Long qprId, Long modelId, Long corporateId,
			String accountType, String accountCode, String quoteElemCalculation) throws MalException {
		
		
		Connection connection = null;
		CallableStatement callableStatement = null;
		
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			
			callableStatement = connection.prepareCall("{ ? = call vision.quotation_wrapper.get_cap_element_value(?,?,?,?,?,?,?,?)}");
			callableStatement.registerOutParameter(1, Types.NUMERIC);
			
			callableStatement.setLong(2, celId);
			
			if(cerId != null){
				callableStatement.setLong(3, cerId);
			}else{
				callableStatement.setNull(3, Types.NULL);
			}
			
			
			callableStatement.setLong(4, qprId);
			callableStatement.setLong(5, modelId);
			callableStatement.setLong(6, corporateId);
			
			callableStatement.setString(7, accountType);
			callableStatement.setString(8, accountCode);
			callableStatement.setString(9, quoteElemCalculation);
			
			callableStatement.execute();

			BigDecimal value = callableStatement.getBigDecimal(1);
			
			return value;
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}
		
	}

	@Override
	public List<QuotationCapitalElement> getApplicableCapitalElementList(Long qprId, String standardEDINo) {
		String sql;
		Query query;
		
		List<QuotationCapitalElement> qceList = new ArrayList<QuotationCapitalElement>();
		QuotationCapitalElement quotationCapitalElement = null;
		CapitalElement capitalElement = null;

		sql = "SELECT element_name, element_id, element_code, on_invoice, quote_concealed, cer_cer_id, quote_elem_calc "
				+ " FROM TABLE(quotation_wrapper.get_app_cap_ele_by_qpr_mdl(:qprId, :standardEDINo))";

		query = entityManager.createNativeQuery(sql);
		query.setParameter("qprId", qprId);
		query.setParameter("standardEDINo", standardEDINo);

		CapitalElementRule cer;
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				quotationCapitalElement = new QuotationCapitalElement();
				
				capitalElement = new CapitalElement();
				capitalElement.setDescription((String)record[i]);
				capitalElement.setCelId(MALUtilities.isEmpty(record[i+=1]) ? null : ((BigDecimal)record[i]).longValue());
				capitalElement.setCode((String)record[i+=1]);
				capitalElement.setOnInvoice((String)record[i+=1]);
				capitalElement.setQuoteCapital((String)record[i+=1]);
				
				if(!MALUtilities.isEmpty(record[i+=1])){
					cer = new CapitalElementRule();
					cer.setCerId(((BigDecimal)record[i]).longValue());
					quotationCapitalElement.setCapitalElementRule(cer);
				}else{
					quotationCapitalElement.setCapitalElementRule(null);
				}
				
				
				
				capitalElement.setQuotationCalculation((String)record[i+=1]);
				
				quotationCapitalElement.setCapitalElement(capitalElement);
				
				qceList.add(quotationCapitalElement);
			}
		}
		return qceList;
	}

	@Override
	public QuotationCapitalElement getCapitalElementWithValue(Long cerId, String standardEDINo, Long corporateId,
			String accountType, String accountCode, String orderType) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
