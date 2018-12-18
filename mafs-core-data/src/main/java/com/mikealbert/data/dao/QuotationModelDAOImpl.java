package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.vo.ActiveQuoteVO;
import com.mikealbert.data.vo.CnbvVO;
import com.mikealbert.data.vo.DbProcParamsVO;
import com.mikealbert.data.vo.ProcessQueueResultVO;
import com.mikealbert.data.vo.QuotationSearchVO;
import com.mikealbert.data.vo.RevisionVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
* This class will be used perform database transactions via the EntityManager.
* @author sibley
*/
public class QuotationModelDAOImpl extends GenericDAOImpl<QuotationModel, Long> implements QuotationModelDAOCustom {
	@Resource DataSource dataSource;
	@Resource DriverAllocationDAO driverAllocationDAO;
	@Resource private QuotationModelDAO quotationModelDAO;	
	@Resource ContractLineDAO contractLineDAO;
	
	private static final long serialVersionUID = 1L;
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	protected JpaEntityInformation<QuotationModel, ?> entityInformation;

    @PostConstruct
    public void postConstruct() {
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(QuotationModel.class, entityManager);
    }
    
	/*
	 * Retrieves the original quotation model id associated with the passed in quotation model id.
	 * (non-Javadoc)
	 * @see com.mikealbert.vision.dao.QuotationModelDAOCustom#getOriginalQuoteModelId(java.lang.Long)
	 */
	public Long getOriginalQuoteModelId(Long qmdID){
		String stmt = "SELECT quotation_wrapper.getOriginalQuoteModelId(?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qmdID);
		BigDecimal quotationModelId = (BigDecimal) query.getSingleResult();
		return quotationModelId == null ? null : quotationModelId.longValue();
	}
	
	public String getLeaseType(Long qmdId) {
		
		String stmt = "SELECT prd.product_type FROM quotations quo,  quotation_models qmd, quotation_profiles qpr,   products prd, product_type_codes ptc WHERE qmd.qmd_id  = :qmdId "
				+ " AND qmd.quo_quo_id = quo.quo_id   AND quo.qpr_qpr_id = qpr.qpr_id  AND qpr.prd_product_code = prd.product_code  AND prd.product_type = ptc.product_type";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("qmdId", qmdId);

		return (String) query.getSingleResult();
	}
	
	public Double getProfitability(Long qmdId, String profitType, String profitSource){
		
		String stmt = "SELECT quotation1.get_profitability(?,?,?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qmdId);
		query.setParameter(2, profitType);
		query.setParameter(3, profitSource);
		BigDecimal profitability = (BigDecimal) query.getSingleResult();
		
		return profitability == null ? null : profitability.doubleValue();
	}
	
	public Double getMinimumProfit(Long qmdId, String profitType, String profitSource){
		
		String stmt = "SELECT profit_base + profit_adjustment FROM quotation_profitability WHERE qmd_qmd_id = :qmdId AND profit_type = :profitType AND profit_source = :profitSource";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("qmdId", qmdId);
		query.setParameter("profitType", profitType);
		query.setParameter("profitSource", profitSource);
		BigDecimal minimumProfit = (BigDecimal) query.getSingleResult();
		
		return minimumProfit == null ? null : minimumProfit.doubleValue();
	}
	
	public Double getFinanceParam(String parameterkey, Long qmdId, Long qprId, Date effectiveDate) {
		return this.getFinanceParam(parameterkey, qmdId, qprId, effectiveDate, false);
	}
	
	public Double getFinanceParam(String parameterkey, Long qmdId, Long qprId,
			Date effectiveDate, boolean skipQuoteLevelOverrides, Long cId, String accountType, String accountCode) {
		
		String stmt;

		if(MALUtilities.isNotEmptyString(accountCode)){
			stmt = "SELECT QUOTATION_WRAPPER.getFinanceParam(?,?,?,?,?,?,?,?) from dual";
		}else if(skipQuoteLevelOverrides){
			stmt = "SELECT QUOTATION_WRAPPER.getFinanceParam(?,?,?,?,?) from dual";
		}else if (!MALUtilities.isEmpty(effectiveDate)) {
			stmt = "SELECT QUOTATION_WRAPPER.getFinanceParam(?,?,?,?) from dual";
		} else {
			stmt = "SELECT QUOTATION_WRAPPER.getFinanceParam(?,?,?) from dual";
		}
		//TODO: this code below could be cleaned up quite a bit.
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, parameterkey);
		query.setParameter(2, qmdId);
		query.setParameter(3, qprId);
		if(skipQuoteLevelOverrides){
			if(!MALUtilities.isEmpty(effectiveDate)) {
				query.setParameter(4, effectiveDate);
			}else{
				query.setParameter(4, "");
			}
			query.setParameter(5, "Y");
			
			if(MALUtilities.isNotEmptyString(accountCode)){
				query.setParameter(6, cId);
				query.setParameter(7, accountType);
				query.setParameter(8, accountCode);
			}
		}else if (MALUtilities.isNotEmptyString(accountCode)) {
			if(!MALUtilities.isEmpty(effectiveDate)) {
				query.setParameter(4, effectiveDate);
			}else{
				query.setParameter(4, "");
			}
			if(skipQuoteLevelOverrides){
				query.setParameter(5, "Y");
			}else{
				query.setParameter(5, "N");
			}
			
			query.setParameter(6, cId);
			query.setParameter(7, accountType);
			query.setParameter(8, accountCode);

		}else if (!MALUtilities.isEmpty(effectiveDate)) {
			query.setParameter(4, effectiveDate);
		}
		BigDecimal financeValue = (BigDecimal) query.getSingleResult();
		return financeValue == null ? null : financeValue.doubleValue();
	}
	
	public Double getFinanceParam(String parameterkey, Long qmdId, Long qprId,
			Date effectiveDate, boolean skipQuoteLevelOverrides) {
		
		return this.getFinanceParam(parameterkey, qmdId, qprId, effectiveDate, skipQuoteLevelOverrides, null, null, null);
	}
	
	
	
	public BigDecimal getInterestRate(Long qmdId, Long qprId,Date effectiveDate,Long contractPeriod){
		
		String stmt = "SELECT quotation_wrapper.getInterestRate(?,?,?,?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qmdId);
		query.setParameter(2, qprId);
		query.setParameter(3, effectiveDate);
		query.setParameter(4, contractPeriod);
		
		BigDecimal interestRate = (BigDecimal) query.getSingleResult();
		
		return interestRate ;
		
	}
	
	public BigDecimal getGlobalBaseInterestRate(Date effectiveDate, Long contractPeriod, String interestType){
		
		String stmt = "SELECT quotation_wrapper.getGlobalBaseRate(?,?,?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, effectiveDate);
		query.setParameter(2, contractPeriod);
		query.setParameter(3, interestType);
		
		BigDecimal globalBaseInterestRate = (BigDecimal) query.getSingleResult();
		
		return globalBaseInterestRate ;
		
	}	


	/*
	 * This method will return interest rate for supplied interest type and other parameters	
	 */
	
	public BigDecimal getInterestRateByType(Date effectiveDate, Long contractPeriod, String interestType, String reviewFrequency, String fixedOrFloat){
	    
	        
		
		String stmt = "SELECT quotation_wrapper.getInterestRateByType(?,?,?,?,?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, effectiveDate);
		query.setParameter(2, contractPeriod);
		query.setParameter(3, interestType);
		query.setParameter(4, reviewFrequency);
		query.setParameter(5, fixedOrFloat);
		
		return (BigDecimal) query.getSingleResult() ;
		
	}
	public String getInterestType(Long qmdId , Long lelId, Date revisionDate){   //added lel id to the method for HD-215
	    	String stmt = "SELECT quotation1.get_interest_type(?,?,?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qmdId);
		query.setParameter(2, lelId); // added lelId for HD-215
		query.setParameter(3, revisionDate);
		
		return (String) query.getSingleResult() ;
	}
	
		
	public BigDecimal fetchMaintValue(Long mtbId, Long period,Long distance) throws MalBusinessException {
	    
	        String stmt = "SELECT quotation_wrapper.fetchMaintValue(?,?,?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, mtbId);
		query.setParameter(2, period);
		query.setParameter(3, distance);
		
		BigDecimal maintValue = (BigDecimal) query.getSingleResult();
		
		return maintValue ;
		
	    	}
	


	
	public BigDecimal getBaseRate(long qmdId) throws MalBusinessException {
	    
    		String stmt = "SELECT quotation_wrapper.get_base_rate(?) FROM dual";
        	Query query = entityManager.createNativeQuery(stmt);
        	query.setParameter(1, qmdId);
        	
        	
        	BigDecimal maintValue = (BigDecimal) query.getSingleResult();
        	
        	return maintValue ;
	
	}
	

	public BigDecimal getHurdleRate(Long qmdId, Long term) throws MalBusinessException {
		BigDecimal hurdleRate = null;
			
		if (MALUtilities.isEmpty(term)) {
			String stmt = "SELECT quotation_wrapper.get_hurdle_rate(?) FROM dual";
        	Query query = entityManager.createNativeQuery(stmt);
        	query.setParameter(1, qmdId);
        	
        	hurdleRate = (BigDecimal) query.getSingleResult();
        				
		} else {
			Connection connection = null;
			CallableStatement callableStatement = null;
				
			try {
				connection = DataSourceUtils.getConnection(dataSource);
				callableStatement = connection.prepareCall("{ call quotation_wrapper.get_hurdle_rate_by_term(?, ?, ?) }");
				
				callableStatement.setLong(1, qmdId);
				callableStatement.setLong(2, term);
				callableStatement.registerOutParameter(3, Types.NUMERIC);
				callableStatement.execute();
				
				hurdleRate = callableStatement.getBigDecimal(3);
				
			} catch (Exception ex) {
				throw new MalBusinessException(ex.getMessage());
			}			
		}
		
		return hurdleRate ;
		
   	    
	}
	
	public BigDecimal getRevisionInterestAdjustment(Long cId, Long clnId, BigDecimal monthUsed, String productType, int contractPeriod, BigDecimal marketValue, String effectiveDate, String isOEQuoteRevision) throws MalBusinessException {
		
		String stmt = "SELECT quotation_wrapper.get_interest_adjustment(?,?,?,?,?,?,?,?) FROM dual";		
    	Query query = entityManager.createNativeQuery(stmt);
    	query.setParameter(1, cId);
    	query.setParameter(2, clnId);
    	query.setParameter(3, monthUsed);
    	query.setParameter(4, productType);
    	query.setParameter(5, contractPeriod);
    	query.setParameter(6, marketValue);
    	query.setParameter(7, effectiveDate); 
    	query.setParameter(8, isOEQuoteRevision);
    	
    	BigDecimal intAdjValue = (BigDecimal) query.getSingleResult();
    	
    	return intAdjValue ;

}	
	
	@SuppressWarnings("unchecked")
	public List<Long> getBaseCapitalElementList(long qmdId) throws MalBusinessException {
	    
			String stmt = "select CE.CEL_ID " +
						  "from quotation_models qm, quotations q, quotation_profiles qp, " + 
						  "product_elements pe, lease_ele_capital_ele lece, capital_elements ce " +
						  "where qm.qmd_id = ? and qm.quo_quo_id = q.quo_id and Q.QPR_QPR_ID = QP.QPR_ID " +
						  "and QP.PRD_PRODUCT_CODE = PE.PRD_PRODUCT_CODE and PE.LEL_LEL_ID = LECE.LEL_LEL_ID " +
						  "and LECE.CEL_CEL_ID = CE.CEL_ID";
			Query query = entityManager.createNativeQuery(stmt);
			query.setParameter(1, qmdId);
			
			List<Object> results = query.getResultList();
			
			List<Long> list = new ArrayList<Long>(); 
			for(Object obj : results) {
				list.add(Long.valueOf(obj.toString()));
			}
			
        	return list;
	}
	
	public String getVQRefNo(Long qmdId, String currRefNo)  {
		String vqRefNo = null;
		String stmt = "SELECT quotation_wrapper.get_vq_ref_no(?,?) FROM dual";
    	Query query = entityManager.createNativeQuery(stmt);
    	query.setParameter(1, qmdId);
    	query.setParameter(2, currRefNo);
    	vqRefNo = (String)query.getSingleResult();
		return vqRefNo;
	}
	
	public BigDecimal getMafsAuthorizationLimit(Long corpId, String accountType, String accountCode, String unitNo) throws MalBusinessException {
	    
        String stmt = "SELECT quotation_wrapper.getMafsAuthorizationLimit(?,?,?,?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, corpId);
		query.setParameter(2, accountType);
		query.setParameter(3, accountCode);
		query.setParameter(4, unitNo);
		
		BigDecimal mafsAuthLimit = (BigDecimal) query.getSingleResult();
		
		return mafsAuthLimit;
    }	

	public BigDecimal getDriverAuthorizationLimit(Long corpId, String accountType, String accountCode, String unitNo) throws MalBusinessException {
	    
        String stmt = "SELECT quotation_wrapper.getDriverAuthorizationLimit(?,?,?,?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, corpId);
		query.setParameter(2, accountType);
		query.setParameter(3, accountCode);
		query.setParameter(4, unitNo);
		
		BigDecimal driverAuthLimit = (BigDecimal) query.getSingleResult();
		
		return driverAuthLimit;
    }
	public Long	getDealerAcc(Long oldQmdId, Long newQmdId, Long newQdaId){
		  String stmt = "SELECT quotation_wrapper.getDealerAcc(?,?,?) FROM dual";
		  Query query = entityManager.createNativeQuery(stmt);
		  query.setParameter(1, oldQmdId);
		  query.setParameter(2, newQmdId);
		  query.setParameter(3, newQdaId);
		  BigDecimal oldQdaId = (BigDecimal) query.getSingleResult();
		  return oldQdaId == null ? null : oldQdaId.longValue();
	}
	
	public BigDecimal	getCustInvPrice(Long qmdId){
		 String stmt = "SELECT quotation_wrapper.getCustInvPrice(?) FROM dual";
		  Query query = entityManager.createNativeQuery(stmt);
		  query.setParameter(1, qmdId);
		  BigDecimal custInvPrice = (BigDecimal) query.getSingleResult();
		  return custInvPrice;
	}
	
	@Transactional
	public void saveWithLock(QuotationModel model) {
	
		if (entityInformation.isNew(model)) {
			entityManager.persist(model);

		} else {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("javax.persistence.lock.timeout", 3000);
			entityManager.find(QuotationModel.class, model.getQmdId(),LockModeType.PESSIMISTIC_WRITE,properties);
			entityManager.merge(model);
		}
	
	}

	@SuppressWarnings("unchecked")
	public  List<ProcessQueueResultVO> getUnitsToFinalizeQueueResults(String unitNo, String vin6,Pageable page, Sort sort) {
		List<ProcessQueueResultVO> processQueueResultList = new ArrayList<ProcessQueueResultVO>();
		Query query = generateUnitsToFinalizeQueueResultsQuery(unitNo, vin6, sort);
		if(page != null){
			query.setFirstResult(page.getPageNumber() * page.getPageSize());
			query.setMaxResults(page.getPageSize());
		}
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		if (resultList != null){
			for (Object[] record : resultList){
				int i = 0;
				ProcessQueueResultVO processQueueResultVO = new ProcessQueueResultVO();
				processQueueResultVO.setFmsId(((BigDecimal)record[i]).longValue());
				processQueueResultVO.setUnitNumber((String)record[i+=1]);
				processQueueResultVO.setVin((String)record[i+=1]);
				processQueueResultVO.setQmdId(((BigDecimal)record[i+=1]).longValue());
				processQueueResultVO.setAccountCode((String)record[i+=1]);
				processQueueResultVO.setAccountName((String)record[i+=1]);
				processQueueResultVO.setUnitDescription((String)record[i+=1]);
				processQueueResultVO.setLastUpdate((Date) record[i+=1]);
				processQueueResultList.add(processQueueResultVO);
			}
		}
		return processQueueResultList;
	}

	private Query generateUnitsToFinalizeQueueResultsQuery(String unitNo, String vin6, Sort sort) {
		Query query = null;
		StringBuilder sqlStmt = new StringBuilder("");

		sqlStmt.append("SELECT fm.fms_id, fm.unit_no, fm.vin, qm.qmd_id, ea.account_code, ea.account_name, m.model_desc, qm.last_amended_date");		

		sqlStmt.append(" FROM DOC PO,DOCL POL,QUOTATION_MODELS QM,CONTRACT_LINES CL,CONTRACTS CON,MODELS M,FLEET_MASTERS FM," +
				   " DOC INVC,DOC_LINKS DLNK,EXTERNAL_ACCOUNTS EA ");		
		
		sqlStmt.append(" WHERE PO.DOC_TYPE = 'PORDER' AND PO.DOC_STATUS = 'R' AND PO.SOURCE_CODE IN ('FLQUOTE', 'FLORDER')" +
				" AND POL.DOC_ID = PO.DOC_ID AND POL.USER_DEF4 = 'MODEL' AND PO.DOC_ID = DLNK.PARENT_DOC_ID AND" +
				" DLNK.CHILD_DOC_ID = INVC.DOC_ID AND INVC.DOC_TYPE = 'INVOICEAP' AND INVC.RECEIVED_DATE IS NOT NULL" +
				" AND QM.QMD_ID = CL.QMD_QMD_ID AND CL.REV_DATE = (SELECT MAX (CL1.REV_DATE) FROM CONTRACT_LINES CL1" +
				" WHERE CL1.QMD_QMD_ID = QM.QMD_ID) AND FM.FMS_ID = CL.FMS_FMS_ID AND CL.CON_CON_ID = CON.CON_ID" +
				" AND CON.LEASE_FINALISATION_IND <> 'Y' AND PO.REV_NO = (SELECT MAX (REV_NO) FROM DOC WHERE DOC_ID = PO.DOC_ID) " +
				" AND PO.GENERIC_EXT_ID = QM.QMD_ID AND FM.MDL_MDL_ID = M.MDL_ID AND CON.EA_C_ID = EA.C_ID " +
				" AND CON.EA_ACCOUNT_TYPE = EA.ACCOUNT_TYPE AND CON.EA_ACCOUNT_CODE = EA.ACCOUNT_CODE");

		
		
		if ((unitNo != null) && !(unitNo.trim().equals(""))) {
			sqlStmt.append(" AND FM.UNIT_NO = ? ");
		}
		if ((vin6 != null) && !(vin6.trim().equals(""))) {
			sqlStmt.append(" AND upper(substr(trim(vin), " + (-1 * vin6.length()) + ")) LIKE upper(?)");
			//sqlStmt.append(" AND SUBSTR(FM.VIN, 12) = ? ");
		}
		if(sort != null){
			Order order = sort.iterator().next();
			String sortOrder = "DESC";
			if(order.isAscending()){
				sortOrder = "ASC";
			}
			
			if(DataConstants.SEARCH_SORT_FIELD_ACCOUNT.equalsIgnoreCase(order.getProperty())){
				sqlStmt.append(" ORDER BY ea.account_name "+sortOrder);	
			}else if(DataConstants.SEARCH_SORT_FIELD_UNIT_NO.equalsIgnoreCase(order.getProperty())){
				sqlStmt.append(" ORDER BY fm.unit_no "+sortOrder);	
			}else if(DataConstants.SEARCH_SORT_FIELD_LAST_UPDATE.equalsIgnoreCase(order.getProperty())){
				sqlStmt.append(" ORDER BY qm.last_amended_date "+sortOrder);	
			}else{
				sqlStmt.append(" ORDER BY fm.unit_no ").append(sortOrder);	
			}
		}
		
		query = entityManager.createNativeQuery(sqlStmt.toString());

		int count = 0;
		if ((unitNo != null) && !(unitNo.trim().equals(""))) {
			
			query.setParameter(count+=1, unitNo);
		}
		if ((vin6 != null) && !(vin6.trim().equals(""))) {
			query.setParameter(count+=1, vin6);			
		}
		
		return query;
	}
	
	private Query	generateQueryToGetReleasedOnlyPorders(String unitNo, String vinNo,boolean iscountQuery){
		Query query = null;
		Long  cdFeeCelId = getCourtesyDeliveryElementId();
		String grdSupplierCode = getGrdSupplierCode(1L);
		
		StringBuilder sqlStmt = new StringBuilder("");
		if(!iscountQuery){
			sqlStmt.append(" select FM.FMS_ID,FM.UNIT_NO,FM.VIN,D1.GENERIC_EXT_ID QMD_ID,M.MODEL_DESC,D1.DOC_NO PORDER,D1.DOC_ID PO_DOC_ID,D1.TOTAL_DOC_PRICE PO_AMOUNT, D1.ACCOUNT_CODE, EA.ACCOUNT_NAME,D1.SOURCE_CODE,D1.SUB_ACC_CODE ");// added D1.SUB_ACC_CODE in query for Bug 16467 
			sqlStmt.append(" FROM DOC D1,DIST DS, EXTERNAL_ACCOUNTS EA,FLEET_MASTERS FM,Models M");
		}else{
			sqlStmt.append(" select count(D1.DOC_NO)");
			sqlStmt.append(" FROM DOC D1,DIST DS,FLEET_MASTERS FM");
		}

		sqlStmt.append(" where D1.C_ID = 1 ");
		sqlStmt.append(" and D1.DOC_TYPE = 'PORDER' ");
		sqlStmt.append(" and D1.DOC_STATUS = 'R' ");
		sqlStmt.append(" and D1.DOC_NO like 'PON%' ");
		//SET PARAMETERS
		if(!MALUtilities.isEmpty(unitNo)){
			
			sqlStmt.append(" AND FM.UNIT_NO = ? ");
		}
		if(!MALUtilities.isEmpty(vinNo)){
			vinNo = "%"+vinNo;
			sqlStmt.append(" AND UPPER(FM.VIN) like UPPER('"+  vinNo+"')");
			//sqlStmt.append(" AND upper(substr(trim(vin), " + (-1 * vinNo.length()) + ")) LIKE upper("+vinNo +")");
		}
		if(!iscountQuery){
			sqlStmt.append(" AND FM.MDL_MDL_ID = M.MDL_ID ");
			sqlStmt.append(" and EA.C_ID = D1.EA_C_ID ");
			sqlStmt.append(" and EA.ACCOUNT_CODE = D1.ACCOUNT_CODE ");
			sqlStmt.append(" and EA.ACCOUNT_TYPE = D1.ACCOUNT_TYPE ");
		}else{
			
		}
		
		
		sqlStmt.append(" and D1.SOURCE_CODE in ('FLQUOTE','FLORDER','FLGRD')");
		sqlStmt.append(" and op_code <> 'CONV'");
		sqlStmt.append(" and (");
		sqlStmt.append(" D1.DOC_ID not in");
		sqlStmt.append(" (select PARENT_DOC_ID from DOC_LINKS LD where PARENT_DOC_ID = D1.DOC_ID");
		sqlStmt.append(" and CHILD_DOC_ID in(select D3.DOC_ID from DOC D3 where D3.DOC_ID = LD.CHILD_DOC_ID");
		sqlStmt.append(" and D3.C_ID = D1.C_ID  and D3.DOC_TYPE = 'INVOICEAP'  and D3.EA_C_ID = D1.EA_C_ID  and D3.ACCOUNT_TYPE = D1.ACCOUNT_TYPE");
		sqlStmt.append(" and D3.DOC_STATUS = 'P')) ");
		sqlStmt.append(" or exists(select 'x' from DOCL where DOC_ID = D1.DOC_ID and NVL(QTY_INVOICE, 0) < NVL(QTY_CHANGE, 0))");
		//sqlStmt.append(" or exists( select 'x'from DOC_LINKS DLKSPO, DOC DINV where DLKSPO.PARENT_DOC_ID = D1.DOC_ID and DLKSPO.CHILD_DOC_ID = DINV.DOC_ID and DINV.DOC_STATUS = 'O')");
		sqlStmt.append(" or D1.DOC_ID in (select D.DOC_ID from DOC_LINKS DLKS, DOC D, DOCL DL where DLKS.PARENT_DOC_ID = D1.DOC_ID and DLKS.CHILD_DOC_ID = D.DOC_ID ");
		sqlStmt.append(" and D.DOC_STATUS = D1.DOC_STATUS and D.ORDER_TYPE = 'T' and D.DOC_ID = DL.DOC_ID and DL.QTY_OUTSTANDING > 0 and DL.USER_DEF4 = 'CAPITAL' and DL.GENERIC_EXT_ID = ? ");
		sqlStmt.append(" and D.C_ID = D1.C_ID and D.ACCOUNT_TYPE = D1.ACCOUNT_TYPE and D.ACCOUNT_CODE =? and D.SOURCE_CODE = D1.SOURCE_CODE) ");			
		sqlStmt.append(")");
		sqlStmt.append(" AND d1.doc_id = ds.doc_id ");
		sqlStmt.append(" AND ds.dis_id = (SELECT dis_id FROM dist WHERE doc_id =  d1.doc_id AND ROWNUM = 1 )");
		sqlStmt.append(" and TO_CHAR(FM.FMS_ID) = DS.CDB_CODE_1 ");
		sqlStmt.append(" order by FM.UNIT_NO ASC ");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		int index = 1;
		if (!MALUtilities.isEmpty(unitNo)) {
			query.setParameter(index++, unitNo);
		}
		query.setParameter(index++, cdFeeCelId);
		query.setParameter(index, grdSupplierCode);
		
		return query;
	}
	public Integer	getCountOfReleasedOnlyPoResults(String unitNo, String vin6){
		Query query = generateQueryToGetReleasedOnlyPorders(unitNo, vin6,true);
		
		 BigDecimal count= (BigDecimal) query.getSingleResult();
		if(count != null){
			return count.intValue();
		}
		return 0;
	}
	public  List<ProcessQueueResultVO> getReleaseOnlyPoResults(String unitNo, String vin6,Pageable page, Sort sort) {
		List<ProcessQueueResultVO> processQueueResultList = new ArrayList<ProcessQueueResultVO>();
		Query query = generateQueryToGetReleasedOnlyPorders(unitNo, vin6,false);
		if(page != null){
			query.setFirstResult(page.getPageNumber() * page.getPageSize());
			query.setMaxResults(page.getPageSize());
		}
		
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		if (resultList != null) {
			for (Object[] record : resultList) {
				int i = 0;
				ProcessQueueResultVO processQueueResultVO = new ProcessQueueResultVO();
				Object obj = record[i];
				if (obj != null) {
					if (obj instanceof BigDecimal) {
						processQueueResultVO.setFmsId(((BigDecimal) record[i]).longValue());
					} else {
						processQueueResultVO.setFmsId((Long) record[i]);
					}
				}
				obj = record[i += 1];
				if (obj != null) {
					processQueueResultVO.setUnitNumber((String) obj);
				}

				obj = record[i += 1];
				if (obj != null) {
					processQueueResultVO.setVin((String) obj);
				}

				obj = record[i += 1];
				if (obj != null) {
					if (obj instanceof BigDecimal) {
						processQueueResultVO.setQmdId(((BigDecimal) obj).longValue());
					} else {
						processQueueResultVO.setQmdId((Long) obj);
					}
				}
				obj = record[i += 1];
				if (obj != null) {
					processQueueResultVO.setUnitDescription((String) obj);
				}
				obj = record[i += 1];
				if (obj != null) {
					processQueueResultVO.setPoNumber((String) obj);
				}

				obj = record[i += 1];
				if (obj != null) {
					if (obj instanceof BigDecimal) {
						processQueueResultVO.setId(((BigDecimal) obj).toPlainString());
					} else {
						processQueueResultVO.setId(((Long) obj).toString());
					}
				}
				obj = record[i += 1];
				if (obj != null) {
					if (obj instanceof BigDecimal) {
						processQueueResultVO.setPoAmount((BigDecimal) obj);
					}
				}

				obj = record[i += 1];
				if (obj != null) {
					processQueueResultVO.setVendorCode((String) obj);
				}

				obj = record[i += 1];
				if (obj != null) {
					processQueueResultVO.setVendorName((String) obj);
				}
				
				obj = record[i += 1];
				if (obj != null) {
					processQueueResultVO.setPoSourceCode((String) obj); // added for Bug 16467
				}
				
				obj = record[i += 1];
				if (obj != null) {
					processQueueResultVO.setSubAccountCode((String) obj); // added for Bug 16467
				}
				processQueueResultList.add(processQueueResultVO);
			}
		}
		return processQueueResultList;
	}

	public Long	getCourtesyDeliveryElementId(){
		  String stmt = "SELECT quotation_wrapper.getCourtesyDeliveryElementId() FROM dual";
		  Query query = entityManager.createNativeQuery(stmt);
		  BigDecimal celId= (BigDecimal) query.getSingleResult();
		  return celId == null ? null : celId.longValue();
	}
	
	public String	getGrdSupplierCode(Long cId){
		  String stmt = "SELECT quotation_wrapper.getGrdSupplierCode(?) FROM dual";
		  Query query = entityManager.createNativeQuery(stmt);
		  query.setParameter(1, cId);
		  String accountCode= (String) query.getSingleResult();
		  return accountCode;
	}
	
	public Long createRevisedQuote(Long acceptedQmdId, String opCode) {
		String stmt = "SELECT QUOTATION_WRAPPER.CREATE_REVISE_QUOTE(?, ?) FROM DUAL";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, acceptedQmdId);
		query.setParameter(2, opCode);
		String finalizeQmdStr = (String) query.getSingleResult();
		
		if(finalizeQmdStr != null && finalizeQmdStr.trim().length() > 0 )
			return Long.parseLong(finalizeQmdStr);
		else
			return null;
	}
	
	/**
	 * Retrieve the qmdId from the fmsId; It calls a TAL function
	 * @param fmsId
	 * @return
	 * @throws MalBusinessException
	 */
	public Long getQmdIdFromFmsId(long fmsId, Date date) {
	    
    	String stmt = "SELECT TAL.get_quotation_model(?,?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, fmsId);
		query.setParameter(2, date);
	
	
		BigDecimal qmdId = (BigDecimal) query.getSingleResult();
	
		return qmdId == null ? null : qmdId.longValue() ;

	}
	
	/*
	 * This method return the qmd which gets created from accepted qmd for quote finalization.
	 */
	public Long getRevisedQmd(long acceptedQmd){
	
		String stmt = "SELECT r_qmd.qmd_id FROM quotation_models a_qmd,  quotation_models r_qmd WHERE a_qmd.qmd_id = r_qmd.orig_qmd_id"
				+ " AND a_qmd.quote_status = '3'  AND r_qmd.quote_status = '4' AND a_qmd.qmd_id = ?";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, acceptedQmd);
		String finalizeQmdStr = null;
		try {
			 finalizeQmdStr = String.valueOf(query.getSingleResult());
		} catch (Exception e) {}
		
		
		if(finalizeQmdStr != null && finalizeQmdStr.trim().length() > 0 )
			return Long.parseLong(finalizeQmdStr);
		else
			return null;
	

	}
	
	public String getFleetStatus(Long fmsId) {
		String queryString = "SELECT QUOTATION_WRAPPER.GET_FLEET_STATUS(?) from dual";
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter(1, fmsId);
		String status = null;
		try {
			status = String.valueOf(query.getSingleResult());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (status != null && status.trim().length() > 0){
			return status;
		}
		else{
			return null;
		}
			
	}
	public QuotationModel	getDetachedQuotationModel(QuotationModel quotationModel){
		entityManager.detach(quotationModel);
		return quotationModel;
		
	}	
	
	
	public boolean deleteQuotationModel(Long qmdId)throws MalBusinessException {
		String stmt = "SELECT QUOTATION_WRAPPER.DELETE_REVISION(?) FROM DUAL";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qmdId);
		String resultString = (String) query.getSingleResult();
		if(MALUtilities.isEmpty(resultString) ){
			return true;
		}else{
			throw new MalBusinessException(resultString);
		}
		
	}

	public BigDecimal getResidualAmount(Long qmdId, Long qdaId) {
		String stmt = "SELECT quotation_wrapper.fetch_residual_amount(?,?,?) FROM dual";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, "Y");
		query.setParameter(2, qmdId);
		query.setParameter(3, qdaId);

		BigDecimal residualAmt = (BigDecimal) query.getSingleResult();
		return residualAmt;
	}	

	public BigDecimal getResidualAmount(Long rtbId, Long period, Long distance) throws MalBusinessException {
		
		Connection connection = null;
		CallableStatement callableStatement = null;
			
		BigDecimal residualAmt = BigDecimal.ZERO;
		
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ call willow2k.quotation.fetch_residual_value(?,?,?,?) }");
			
			callableStatement.setLong(1, rtbId);
			callableStatement.setLong(2, period);
			callableStatement.setLong(3, distance);
			callableStatement.registerOutParameter(4, Types.NUMERIC);
			callableStatement.execute();
			
			residualAmt = callableStatement.getBigDecimal(4);
			
		} catch (Exception ex) {
			throw new MalBusinessException(ex.getMessage());
		}			

		return residualAmt;
	}	

	
	public String getProductType(Long qmdId) {
		String stmt = "SELECT quotation_wrapper.get_product_type(?) FROM dual";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qmdId);

		String productType = (String) query.getSingleResult();
		return productType;
	}
	
	public Long getQutationModelForTransportByFmsId(Long fmsId) {
		Long qmdId = 0L;
		String stmt = "select max(qmd_id) qmdId "
				+ "from quotation_models "
				+ "where replacement_for_fms_id = ?1 "
				+ "and quote_status not in (1, 2, 7, 8, 14, 15)";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, fmsId);

		BigDecimal qmdIdResult = (BigDecimal) query.getSingleResult();
		if(qmdIdResult != null){
			qmdId = ((BigDecimal) qmdIdResult).longValue();
		}
		
		return qmdId;
	}
	
	public Long getQuoteAccessoryLeadTimeByQdaId(Long qdaId) {
		Long leadTime = 0L;
		String stmt = " select quote_services.get_accessory_lead_time(?1) from dual";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qdaId);

		BigDecimal leadTimeResult = (BigDecimal) query.getSingleResult();
		if(leadTimeResult != null){
			leadTime = ((BigDecimal) leadTimeResult).longValue();
		}
		
		return leadTime;
	}
	
	public String getClientRequestTypeValue(Long qmdId) {
		String stmt = "SELECT fl_general.get_client_request_type_desc(?) FROM dual";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qmdId);

		String clientRequestType = (String) query.getSingleResult();
		return clientRequestType;
	}
	
	public boolean stockValidityCheck(Long qmdId, Long fmsId)  throws MalBusinessException {
		String stmt = "Select quote_services.stock_validity_check(?, ?) from dual";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qmdId);
		query.setParameter(2, fmsId);

		String resultString = (String) query.getSingleResult();
		if(MALUtilities.isEmpty(resultString) ){
			return true;
		}else{
			throw new MalBusinessException(resultString);
		}
	}
	
	public void stockFinalAccept(Long qmdId, Long fmsId, Date contractStartDate, Long odoMeterReading, Date odoReadingDate, String odoReadingType, String employeeNo) throws MalException {
		Connection connection = null;
		CallableStatement callableStatement = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ ? = call quote_services.stock_final_accept(?,?,?,?,?,?,?) }");
			callableStatement.registerOutParameter(1, Types.VARCHAR);
			callableStatement.setLong(2, qmdId);
			callableStatement.setLong(3, fmsId);
			callableStatement.setTimestamp(4, new java.sql.Timestamp(contractStartDate.getTime()));
			callableStatement.setLong(5, odoMeterReading);
			callableStatement.setTimestamp(6, new java.sql.Timestamp(odoReadingDate.getTime()));
			callableStatement.setString(7, odoReadingType);
			callableStatement.setString(8, employeeNo);
			
			callableStatement.execute();
			String resultString = callableStatement.getString(1);
			
			if (!MALUtilities.isEmpty(resultString)) {
				throw new MalException(resultString);
			}
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}
	}
	
	public DbProcParamsVO acceptQuote(Long qmdId, String employeeNo) throws MalException {
		Connection connection = null;
		CallableStatement callableStatement = null;
		DbProcParamsVO parameterVO = new DbProcParamsVO();
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ ? = call quote_services.accept_quote(?,?,?) }");
			callableStatement.registerOutParameter(1, Types.VARCHAR);
			callableStatement.setLong(2, qmdId);
			callableStatement.setString(3, employeeNo);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			
			callableStatement.execute();
			parameterVO.setMessage(callableStatement.getString(1));
			parameterVO.setSuccessTrueFalse(callableStatement.getString(4));
			
			return parameterVO;
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		} 
	}
	
	public String getPlateTypeCodeDescription(String plateTypeCode){
		
		String stmt = "SELECT INITCAP(description) FROM plate_type_codes ptc WHERE ptc.plate_type_code = :plateTypeCode";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("plateTypeCode", plateTypeCode);

		return (String) query.getSingleResult();
	}

	public int searchQuotationsCount(Long corpId, String accountType, String accountCodeOrName, List<String> productTypeCodes, String unitNo, String vin6, Long projectedMonths) {
		Query query = generateQueryToGetOnContractUnits(corpId, accountType, accountCodeOrName, productTypeCodes, unitNo, vin6, projectedMonths, null);
		List<String> resultList = (List<String>)query.getResultList();

		if (resultList.size() > 0) {
			return resultList.size();
		} else {
			return 0;
		}	
	}
	
	@Override
	public List<QuotationSearchVO> searchQuotations(Long corpId, String accountType, String accountCodeOrName, List<String> productTypeCodes, String unitNo, String vin6, Long projectedMonths, Pageable page, Sort sort) throws MalBusinessException {
		
		List<QuotationSearchVO> quotationSearchVOList = new ArrayList<QuotationSearchVO>();
		Query query = generateQueryToGetOnContractUnits(corpId, accountType, accountCodeOrName, productTypeCodes, unitNo, vin6, projectedMonths, sort);
		if(page != null){
			query.setFirstResult(page.getPageNumber() * page.getPageSize());
			query.setMaxResults(page.getPageSize());
		}		
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		if (resultList != null) {
			for (Object[] record : resultList) {
				int i = 0;
				QuotationSearchVO quotationSearchVO = new QuotationSearchVO();
				Object obj = record[i];
				if (obj != null) {
					quotationSearchVO.setQuoId(((BigDecimal) obj).longValue());
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setQmdId(((BigDecimal) obj).longValue());
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setQuoteRefNo((String) obj);
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setContractPeriod(((BigDecimal) obj).longValue());
				}				
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setProjectedMonths(((BigDecimal) obj).longValue());
				}								
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setcId(((BigDecimal) obj).longValue());
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setAccountType((String) obj);
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setAccountCode((String) obj);
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setAccountName((String) obj);
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setAccountShortName((String) obj);
				}				
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setModelDescription((String) obj);
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setGradeGroupCode((String) obj);
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setGradeGroupDescription((String) obj);
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setVinNumber((String) obj);
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setUnitNo((String) obj);
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setFmsId(((BigDecimal) obj).longValue());
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setDriverId(((BigDecimal) obj).longValue());
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setDriverName((String) obj);
				}
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setFleetRefNo((String) obj);
				}			
				obj = record[i += 1];
				if (obj != null) {
					quotationSearchVO.setContractStartDate((Date) obj);
				}				
				quotationSearchVOList.add(quotationSearchVO);
				
			}		
		}
		
		return quotationSearchVOList;		
 }
	
	private Query generateQueryToGetOnContractUnits(Long corpId, String accountType, String accountCodeOrName, List<String> productTypeCodes, String unitNo, String vin6, Long projMonths, Sort sort) {
		Query query = null;
		StringBuilder sqlStmt = new StringBuilder("");
		sqlStmt.append("SELECT quo_id, qmd_id, quo_id||'/'||quote_no||'/'||revision_no quote_ref_no, qmd.contract_period, qmd.projected_months, ea.c_id, ea.account_type, ea.account_code, ea.account_name, ea.short_name, md.model_desc,"
					  + " quo.driver_grade_group, dggc.description, vin, fms.unit_no, fms.fms_id, da.drv_drv_id, drv.driver_surname ||', '||drv.driver_forename driver_name, fms.vehicle_cost_centre fleet_ref_no, cln.start_date"
					  + " FROM quotations quo, quotation_models qmd, fleet_masters fms, models md, external_accounts ea, driver_grade_group_codes dggc, driver_allocations da, drivers drv, contract_lines cln"
					  + " WHERE quo.quo_id = qmd.quo_quo_id"
					  + " AND qmd.quote_status = '6'"					  
					  + " AND qmd.unit_no = fms.unit_no "
					  + " AND fms.mdl_mdl_id = md.mdl_id "
					  + " AND quo.driver_grade_group = dggc.driver_grade_group (+) "
					  + " AND fms.fms_id = da.fms_fms_id "
					  + " AND fms.fms_id = cln.fms_fms_id "
					  + " AND qmd.qmd_id = cln.qmd_qmd_id "
					  + " AND cln.actual_end_date is null "
					  + " AND sysdate between da.from_date AND NVL(da.to_date, sysdate) "
					  + " AND da.drv_drv_id = drv.drv_id "
					  + " AND quo.c_id = ea.c_id "
					  + " AND quo.account_type = ea.account_type "
					  + " AND quo.account_code = ea.account_code ");
		
		if ((unitNo != null) && !(unitNo.trim().equals(""))) {
			sqlStmt.append(" AND fms.unit_no LIKE :piUnitNo ");
		}
		if ((productTypeCodes != null) && (productTypeCodes.size() > 0)) {
			sqlStmt.append(" AND quotation.get_product_type_lov(qmd.qmd_id, quo.qpr_qpr_id) IN (:piProductTypeCode)");
		}
		if (projMonths != null) {
			sqlStmt.append(" AND NVL(qmd.projected_months, 0) = :piProjMonths ");
		}
		if ((vin6 != null) && !(vin6.trim().equals(""))) {
			sqlStmt.append(" AND upper(substr(trim(vin), " + (-1 * vin6.length()) + ")) LIKE upper(:piVinNo)");
		}	
		if (!MALUtilities.isEmpty(accountCodeOrName)) {
			if (MALUtilities.isNumber(accountCodeOrName)) {
				sqlStmt.append(" AND quo.c_id = :piCid  AND quo.account_type = :piAccountType AND quo.account_code = :piAccountCodeOrName ");
			}else if (!MALUtilities.isNumber(accountCodeOrName)) {
				sqlStmt.append(" AND ea.c_id = :piCid  AND ea.account_type = :piAccountType AND ea.account_name = :piAccountCodeOrName ");
			} 
		}
		
		//Defaults order by unless otherwise specified by the passed in sort object
		if(!MALUtilities.isEmpty(sort)){
			sqlStmt.append(
					"     ORDER BY ");
			for ( Iterator<Order> orderIterator = sort.iterator(); orderIterator.hasNext(); ) {
				Order order = orderIterator.next();
				
				if(DataConstants.VEHICLE_SEARCH_SORT_FIELD_UNIT_NO.equals(order.getProperty())){	
					sqlStmt.append(" fms.unit_no " + order.getDirection());
				}
				if(DataConstants.VEHICLE_SEARCH_SORT_FIELD_ACCOUNT_NAME.equals(order.getProperty())){	
					sqlStmt.append(" ea.account_name " + order.getDirection());
				}				
				if(orderIterator.hasNext()){
					sqlStmt.append(", ");
				}
			}			
		} else {
			sqlStmt.append("  ORDER BY fms.unit_no ASC, ea.account_code ASC");
		}        
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		
		if ((unitNo != null) && !(unitNo.trim().equals(""))) {
			query.setParameter("piUnitNo",'%' + unitNo + '%');
		}
		if ((vin6 != null) && !(vin6.trim().equals(""))) {
			query.setParameter("piVinNo", vin6);
		}	
		if ((productTypeCodes != null) && (productTypeCodes.size() > 0)) {
			query.setParameter("piProductTypeCode", productTypeCodes);
		}
		if (projMonths != null) {
			query.setParameter("piProjMonths", projMonths);
		}		
		if (!MALUtilities.isEmpty(accountCodeOrName)) {
			query.setParameter("piCid", corpId);
			query.setParameter("piAccountType", accountType);
			query.setParameter("piAccountCodeOrName", accountCodeOrName);
		}		

		return query;
	}

	@Override
	public boolean hasPendingInformalAmendment(Long qmdId) {
		Query query = null;
		StringBuilder sqlStmt = new StringBuilder("");
		sqlStmt.append("SELECT COUNT(1) FROM ( "
						+ " SELECT mql.lel_id "
						+ "  FROM mul_quote_ele mql, lease_elements lel "
						+ " WHERE mql.lel_id = lel.lel_id "
						+ "   AND lel.element_type != 'FINANCE' "
						+ "   AND mql.quo_id = (SELECT quo_quo_id "
						+ "                       FROM quotation_models "
						+ "                      WHERE qmd_id = :piQmdId) "
						+ " MINUS "
						+ " (SELECT qel.lel_lel_id "
						+ "   FROM quotation_elements qel, lease_elements lel "
						+ " WHERE qel.lel_lel_id = lel.lel_id "
						+ "   AND lel.element_type != 'FINANCE' "
						+ "   AND qel.qmd_qmd_id = :piQmdId "
						+ " UNION    "
						+ " SELECT ia.lel_lel_id "
						+ "  FROM informal_amendments ia "
						+ " WHERE qmd_qmd_id = :piQmdId "
						+ "   AND effective_from = (SELECT MAX (effective_from) "
						+ "                           FROM informal_amendments ia1 "
						+ "                          WHERE ia1.qmd_qmd_id = ia.qmd_qmd_id)))  ");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("piQmdId", qmdId);

		BigDecimal result = (BigDecimal)query.getSingleResult();
		
		if (!MALUtilities.isEmpty(result)) {
			if (result.compareTo(BigDecimal.ZERO) == 0) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		} 
	}

	public List<ActiveQuoteVO> getActiveQuoteVOs(Long fmsId) {
		
		List<ActiveQuoteVO> results = null;
		Query query = null;	
		StringBuilder sqlStmt;
		
		sqlStmt = new StringBuilder("");
		sqlStmt.append("select qm.qmd_id, qm.quo_quo_id, qm.quote_no, qm.revision_no, pb.last_name, pb.first_name, revision_exp_date, qsc.description "
				+ " from quotation_models qm, quotation_status_codes qsc, personnel_base pb "
				+ " where  fms_fms_id = :fmsId and " 
				+ " qm.quote_status not in ('7','8') and "
				+ " qm.quote_status = qsc.quotation_status and "
				+ " qm.revision_user = pb.employee_no " );			

		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		query.setParameter("fmsId", fmsId);
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<ActiveQuoteVO>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				ActiveQuoteVO result = new ActiveQuoteVO();
				result.setQmdId(((BigDecimal)record[i]).longValue());				
				String quote = ((BigDecimal)record[i+=1]).toPlainString();
				String no = ((BigDecimal)record[i+=1]).toPlainString();
				String rev = ((BigDecimal)record[i+=1]).toPlainString();
				result.setQuoteDesc(quote+"/"+no+"/"+rev);
				String lastName = ((String)record[i+=1]);
				String firstName = ((String)record[i+=1]);
				result.setOriginator(lastName + ", " + firstName);
				result.setQuoteDate(((Date)record[i+=1]));
				result.setStatus(((String)record[i+=1]));
				results.add(result);
			}
		}		
		
		return results;				

		
	}

	@Override
	public String getExcessMileage(Long qmdId) {
	    
    	String stmt = "SELECT quotation_wrapper.get_excess_mile_band(?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qmdId);
	
		String excessMileBand = (String) query.getSingleResult();
	
		return excessMileBand ;

	}
	
	@Override
	public boolean hasPriorAcceptedQuoteModel(long qmdId) {
		if(!MALUtilities.isEmpty(this.getPriorAcceptedQmd(qmdId))){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Long getPriorAcceptedQmd(long qmdId) {
		String stmt = "SELECT qm1.qmd_id FROM QUOTATION_MODELS qm1 INNER JOIN QUOTATIONS quo ON quo.quo_id = qm1.quo_quo_id"
				+ " WHERE quo_id IN (SELECT quo_quo_id FROM QUOTATION_MODELS qm2 WHERE qm2.qmd_id = ?)"
				+ " AND qm1.QUOTE_STATUS = 3";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qmdId);
		String acceptedQmdStr = null;
		try {
			acceptedQmdStr = String.valueOf(query.getSingleResult());
		} catch (Exception e) {}
		
		if(acceptedQmdStr != null && acceptedQmdStr.trim().length() > 0 ){
			return Long.parseLong(acceptedQmdStr);
		}else{
			return null;
		}
	}

	@Override
	public Long createContractRevisionQuote (Long oldQmdId, String contractRevType, String employeeNo) throws MalException {
		Connection connection = null;
		CallableStatement callableStatement = null;
		
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ ? = call QUOTATION_WRAPPER.copy_quote_for_contract_rev(?,?,?) }");
			callableStatement.registerOutParameter(1, Types.NUMERIC);
			callableStatement.setLong(2, oldQmdId);				
			callableStatement.setString(3, contractRevType);		
			callableStatement.setString(4, employeeNo);
			
			callableStatement.execute();					
			
			return Long.valueOf(callableStatement.getInt(1));
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		} 
	}

	@Override
	public List<RevisionVO> getListByQmdAndStatus(Long qmdId, List<String> quoteStatusCodes) {
		List<RevisionVO> results = null;
		Query query = null;	
		StringBuilder sqlStmt;
		
		sqlStmt = new StringBuilder("");
		sqlStmt.append("select qm.qmd_id, qm.quo_quo_id, qm.quote_no, qm.revision_no, qm.contract_period, qm.contract_distance, " 
				+ "qm.revision_date, qm.printed_date, qm.residual_value, qm.depreciation_factor, qsc.description,  "
				+ "(select sum(residual_value) from quotation_elements qe where qe.qmd_qmd_id=qm.qmd_id and (qe.qma_qma_id is not null or qe.qda_qda_id is not null)) "
				+ " from quotation_models qm, quotation_status_codes qsc "
				+ " where  qm.quo_quo_id = "
				+ " (select qm2.quo_quo_id from quotation_models qm2 where qm2.qmd_id = :qmdId) and " 
				+ " qm.quote_status in (:quoteStatusCodes) and "
				+ " qm.quote_status = qsc.quotation_status " 
				+ " order by qm.revision_no desc ");	
				

		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		query.setParameter("qmdId", qmdId);
		query.setParameter("quoteStatusCodes", quoteStatusCodes);
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<RevisionVO>();
			
			for(Object[] record : resultList){
				int i = 0;
				RevisionVO result = new RevisionVO();
				result.setQmdId(((BigDecimal)record[i]).longValue());				
				result.setQuoId(((BigDecimal)record[i+=1]).longValue());				
				result.setQuoteNo(((BigDecimal)record[i+=1]).longValue());				
				result.setRevisionNo(((BigDecimal)record[i+=1]).longValue());				
				result.setContractPeriod(((BigDecimal)record[i+=1]).longValue());				
				result.setContractDistance(((BigDecimal)record[i+=1]).longValue());				
				result.setRevisionDate(((Date)record[i+=1]));
				result.setPrintedDate(((Date)record[i+=1]));
				BigDecimal mainResidual = ((BigDecimal) record[i+=1]);
				result.setDepreciationFactor((BigDecimal) record[i+=1]);
				result.setStatusDescription(((String)record[i+=1]));
				BigDecimal equipmentResidual = ((BigDecimal) record[i+=1]);
				if(mainResidual == null) {
					mainResidual = BigDecimal.ZERO;
				}
				if(equipmentResidual == null) {
					equipmentResidual = BigDecimal.ZERO;
				}
				result.setFinalNBV(mainResidual.add(equipmentResidual));
				results.add(result);
			}
		}		
		
		return results;				
	}
	
	@Override
	public String generateWillowReportUrl(String moduleName, String paramString)
			throws MalBusinessException {
		Connection connection = null;
		CallableStatement callableStatement = null;
		
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			
			callableStatement = connection.prepareCall("{ ? = call vision.UTILITY_WRAPPER.generate_report_url(?,?,?)}");
			callableStatement.registerOutParameter(1, Types.VARCHAR);
			callableStatement.setString(2, moduleName);
			callableStatement.setString(3, paramString);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			
			callableStatement.execute();

			String errorText = callableStatement.getString(4);
			if (!MALUtilities.isEmpty(errorText)) {
				throw new MalException(errorText);
			}
			String reportUrl = callableStatement.getString(1);
			
			return reportUrl;
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}
	}

	@Override
	public BigDecimal getProfileBaseRateByTerm(long qmdId, long term)
			throws MalBusinessException {
	    
		Connection connection = null;
		CallableStatement callableStatement = null;
		
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			
			callableStatement = connection.prepareCall("{ ? = call vision.QUOTATION_WRAPPER.get_profile_base_rate(?,?,?)}");
			callableStatement.registerOutParameter(1, Types.DECIMAL);
			callableStatement.setLong(2, qmdId);
			callableStatement.setLong(3, term);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			
			callableStatement.execute();

			String errorText = callableStatement.getString(4);
			if (!MALUtilities.isEmpty(errorText)) {
				throw new MalException(errorText);
			}
			BigDecimal baseRate = callableStatement.getBigDecimal(1);
			
			return baseRate;
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}

	}	
	
	/*
	 * This function used to display steps for OE ,  can be simplified by joining contract lines and quote_model_property_value having quote_type 'R' (get all revised quote).
	 * And add qmd before first revision.
	 *
	 */
	public List<Long> getPreviousQMDForSteps(long qmdId) {	    
			
			Set<Long> qmdSet = new LinkedHashSet<>();
			
			String stmt = " With qry_period AS ( "+
								 "  SELECT DISTINCT qes.from_period  ,cln.con_con_id  , temp_table.rev_no "+
								 "    FROM quotation_element_steps qes, quotation_elements qel, contract_lines cln  , " +
								 " 			(SELECT  max(con_con_id) con_con_id  , max(rev_no) rev_no FROM contract_lines where qmd_qmd_id = :qmdId) temp_table "+// we have may same qmd in contract_lines ET case
								 "    WHERE qes.qel_qel_id = qel.qel_id "+
								 "      AND qel.qmd_qmd_id = cln.qmd_qmd_id "+
								 "      AND temp_table.con_con_id = cln.con_con_id ) "+
								 "   SELECT  max(cln.qmd_qmd_id) AS MAX_QMD_ID  "+
								 "     FROM contract_lines cln, quotation_elements qel, quotation_element_steps qes, qry_period "+
								 "    WHERE cln.qmd_qmd_id = qel.qmd_qmd_id "+
								 "      AND qes.from_period = qry_period.from_period "+
								 "      AND qes.qel_qel_id = qel.qel_id "+
								 "      AND cln.con_con_id = qry_period.con_con_id "+							
								 "      AND cln.rev_no <= qry_period.rev_no "+
								 "      GROUP BY qry_period.from_period "+
								 "      ORDER BY  max(cln.rev_no)  DESC ";
    		
			Query query = entityManager.createNativeQuery(stmt);
			query.setParameter("qmdId", qmdId);
			
			for (Object obj : query.getResultList()) {
				qmdSet.add(((BigDecimal)obj).longValue());
			}
			
        	return new ArrayList<Long>(qmdSet);
	}
	
		
	@Override
	public String getReportNameByModuleAndProductCode(String moduleName, String productCode) throws MalBusinessException {
    	
		String stmt = "SELECT quotation_wrapper.get_report_name (?, ?) FROM DUAL";
		Query query = entityManager.createNativeQuery(stmt);
		
		query.setParameter(1, moduleName);
		query.setParameter(2, productCode);
		
		return (String) query.getSingleResult() ;
}

	@Override
	public BigDecimal getInterestRateByQuotationProfile(Long qprId, Date effectiveDate, Long term) throws MalBusinessException{
		Connection connection = null;
		CallableStatement callableStatement = null;
		
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			
			callableStatement = connection.prepareCall("{ ? = call vision.quotation_wrapper.getInterestRate(?,?,?,?)}");
			callableStatement.registerOutParameter(1, Types.NUMERIC);
			callableStatement.setNull(2, Types.NULL);
			callableStatement.setLong(3, qprId);
			callableStatement.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));
			callableStatement.setLong(5, term);
			
			callableStatement.execute();

			BigDecimal indexRate = callableStatement.getBigDecimal(1);
			
			return indexRate;
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}
	}
	
	@Override
	public Long generateCfgId() {
		String stmt;
		Long cfgId;
		Query query;
		
		stmt = "SELECT serialized_config_seq.nextval FROM DUAL";
		
		query = entityManager.createNativeQuery(stmt.toString());
		cfgId = ((BigDecimal) query.getSingleResult()).longValue();		
		
		return cfgId;
	}




	public BigDecimal getApplicableCapitalContribution(Long qmdId){
		String stmt = "SELECT quotation_wrapper.get_applied_capital_contrb(?) FROM dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, qmdId);
		
		return (BigDecimal) query.getSingleResult();
	}
	
	public Long getOriginalQmdIdOnCurrentContract(Long qmdId){
		String stmt = "SELECT qmd_qmd_id " +
						"FROM (SELECT qmd_qmd_id " +
								"FROM contract_lines cln " +
								"WHERE cln.qmd_qmd_id = ANY (SELECT qm1.qmd_id " +
                                								"FROM quotation_models qm1 " +
                                								"WHERE qm1.quo_quo_id = ANY (SELECT qm2.quo_quo_id " +
                                																"FROM quotation_models qm2 " +
                                																"WHERE qm2.qmd_id = :qmdId)) " +
								"ORDER BY rev_no ASC) " +
						"WHERE ROWNUM = 1";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("qmdId", qmdId);
		
		Long originalQmdId = null;
		try {
			originalQmdId = ((BigDecimal) query.getSingleResult()).longValue();
		}
		catch (NoResultException nre) {
			originalQmdId = null;
		}
		
		return originalQmdId;		
	}


	@Override
	public String compareProfiles(Long oldQprId, Long newQprId) throws MalBusinessException {
		String stmt = "SELECT QUOTATION_WRAPPER.compareProfiles(?, ?) FROM DUAL";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, oldQprId);
		query.setParameter(2, newQprId);
		String resultString = (String) query.getSingleResult();
		
		return resultString;
	}

	@Override
	public List<CnbvVO> getCnbv(Long lastestQmdId, Long anyOldQmdId){
		
		Date conEndDate = contractLineDAO.findByQmdId(lastestQmdId).get(0).getEndDate();
		
		List<CnbvVO> results = null;
		String stmt	= null;
		if (MALUtilities.isEmpty(anyOldQmdId)) {
			stmt = "SELECT period, trans_date, qmd_id, cap_cost, residual_value, cnbv FROM TABLE (FL_GENERAL.get_cnbv_data (:lastestQmdId)) WHERE (trans_date <= :upToDate OR :upToDate IS NULL)";
		} else {
			stmt = "SELECT period, trans_date, qmd_id, cap_cost, residual_value, cnbv FROM TABLE (FL_GENERAL.get_cnbv_data (:lastestQmdId)) WHERE qmd_id = :anyOldQmdId AND (trans_date <= :upToDate OR :upToDate IS NULL)";
		}
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("lastestQmdId", lastestQmdId);
		
		if (!MALUtilities.isEmpty(anyOldQmdId)) {
			query.setParameter("anyOldQmdId", anyOldQmdId);
		}
		query.setParameter("upToDate", conEndDate);
		
		@SuppressWarnings("unchecked")	
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		
		if(resultList != null){
			results = new ArrayList<CnbvVO>();		
			for(Object[] record : resultList){
				int i = 0;
				CnbvVO result = new CnbvVO();
				
				result.setPeriod(((BigDecimal)record[i]).longValue());
				result.setTransDate(((Date)record[i+=1]));
				result.setQmdId(((BigDecimal)record[i+=1]).longValue());
				result.setCustCapCost(((BigDecimal)record[i+=1]));
				result.setResidualValue(((BigDecimal)record[i+=1]));
				result.setCnbv(((BigDecimal)record[i+=1]));

				results.add(result);
			}
		}		
		
		return results;
	}
}
