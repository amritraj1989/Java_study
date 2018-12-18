package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.vo.ArCreationVO;
import com.mikealbert.data.vo.VehicleOrderStatusSearchCriteriaVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

public class DocDAOImpl extends GenericDAOImpl<Doc, Long> implements DocDAOCustom {
	
	@Resource
	private DocDAO docDAO;
	
	private static final long serialVersionUID = 1L;
	
	@Resource DataSource dataSource;
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
	
	@SuppressWarnings("unchecked")
	public List<Doc> findInvoiceHeaderForPoNo(String poNumber) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select d2 from Doc d1,DocLink dl,Doc d2  where ");
		queryBuilder.append(" d1.cId = 1 ");
		queryBuilder.append(" AND d1.docType = 'PORDER' ");
		queryBuilder.append(" AND d1.docNo = ?1 ");
		queryBuilder.append("  AND dl.id.parentDocId = d1.docId ");
		queryBuilder.append("   AND d2.docId = dl.id.childDocId ");
		queryBuilder.append("   AND d2.docType = 'INVOICEAP' ");
		queryBuilder.append("   AND d2.sourceCode = 'POINV' ");
		queryBuilder.append("   AND d2.docStatus =  'O' ");
		Query query = entityManager.createQuery(queryBuilder.toString());
		query.setParameter(1, poNumber);
		return query.getResultList();
	}

	public Doc findByDocNoAndDocTypeAndSourceCodeAndStatusForInvoiceEntry(String docNo, String docType, List<String> sourceCode,
			String Status) {
		try {
			String queryString = "select d from Doc d where d.docNo = :docNo and d.docType = :docType and d.sourceCode IN (:sourceCode) and d.docStatus= :status";
			Query query = entityManager.createQuery(queryString);
			query.setParameter("docNo", docNo);
			query.setParameter("docType", docType);
			query.setParameter("sourceCode", sourceCode);
			query.setParameter("status", Status);
			return (Doc) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}

	public boolean postInvoice(Long docId, String opCode)throws MalBusinessException {
		String stmt = "SELECT QUOTATION_WRAPPER.post_invoice(?, ?) FROM DUAL";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, docId);
		query.setParameter(2, opCode);
		String resultString = (String) query.getSingleResult();
		if(MALUtilities.isEmpty(resultString) ){
			return true;
		}else{
			throw new MalBusinessException(resultString);
		}
		
	}

	public Long getMissingInvoiceLineCount(Long invoiceDocId, Long poDocId) {
		try {
			String stmt = "SELECT COUNT(1) FROM docl WHERE doc_id IN(SELECT child_doc_id FROM doc_links WHERE parent_doc_id IN(SELECT parent_doc_id"
					+ " FROM doc_links WHERE child_doc_id = :invoiceDocId) AND parent_doc_id IN(SELECT doc_id FROM doc WHERE doc_id = parent_doc_id"
					+ " AND doc_type = 'PORDER')) AND NVL(user_def4, 'x') <> 'MODEL' AND line_type IN ('INVOICEAP', 'CREDITAP')"
					+ " MINUS " + " SELECT COUNT(1) FROM docl WHERE doc_id = :poDocId AND NVL(user_def4, 'x') <> 'MODEL'";

			Query query = entityManager.createNativeQuery(stmt);
			query.setParameter("invoiceDocId", invoiceDocId);
			query.setParameter("poDocId", poDocId);
			BigDecimal count =(BigDecimal) query.getSingleResult();
			Long returnedCount = count != null ? count.longValue() : 0L;
			return returnedCount ;
		} catch (NoResultException nre) {
			return 0L;
		}

	}

	public Long getModelInvoiceLineCount(Long invoiceDocId) {
		try {
			String stmt = "SELECT COUNT(1) FROM docl WHERE doc_id IN(SELECT child_doc_id FROM doc_links WHERE parent_doc_id IN(SELECT parent_doc_id"
					+ " FROM doc_links WHERE child_doc_id = :invoiceDocId) AND parent_doc_id IN(SELECT doc_id FROM doc WHERE doc_id = parent_doc_id"
					+ " AND doc_type = 'PORDER')) AND NVL(user_def4, 'x') = 'MODEL' AND line_type = 'INVOICEAP'";

			Query query = entityManager.createNativeQuery(stmt);
			query.setParameter("invoiceDocId", invoiceDocId);
			BigDecimal count =(BigDecimal) query.getSingleResult();
			Long returnedCount = count != null ? count.longValue() : 0L;
			return returnedCount ;
		} catch (NoResultException nre) {
			return 0L;
		}

	}
	
	public Doc searchMainPurchaseOrder(VehicleOrderStatusSearchCriteriaVO searchCriteria) throws MalBusinessException {

		StringBuffer queryString = new StringBuffer("");
		queryString.append("SELECT d.doc_id from doc d "
				+ "WHERE d.c_id = 1 "
				+ "AND d.doc_type = 'PORDER' "
				+ "AND d.source_code in('FLQUOTE','FLORDER') "
				+ "AND d.doc_status = 'R' "
				+ "AND NVL(order_type,'M') != 'T' ");

		if(!MALUtilities.isEmpty(searchCriteria.getUnitNo())) {
			queryString.append("AND d.doc_id in (select doc_id from dist where cdb_code_1 IN (select to_char(fms_id) from fleet_masters "
					+ "where unit_no = :unitNo) "
					+ "and cdb_code_1 is not null) ");
		}

		if(!MALUtilities.isEmpty(searchCriteria.getPurchaseOrderNumber())) {
			queryString.append("AND upper(d.doc_no) = :docNo ");
		}

		if(!MALUtilities.isEmpty(searchCriteria.getFactoryNo())) {
			queryString.append("AND upper(d.external_ref_no) = :factoryNo ");
		}

		try {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			if(!MALUtilities.isEmpty(searchCriteria.getUnitNo())) {
				parameterMap.put("unitNo", searchCriteria.getUnitNo());
			}
			if(!MALUtilities.isEmpty(searchCriteria.getPurchaseOrderNumber())) {
				parameterMap.put("docNo", searchCriteria.getPurchaseOrderNumber().toUpperCase());
			}
			if(!MALUtilities.isEmpty(searchCriteria.getFactoryNo())) {
				parameterMap.put("factoryNo", searchCriteria.getFactoryNo().toUpperCase());
			}

			Query query = entityManager.createNativeQuery(queryString.toString());

			for(String paramName : parameterMap.keySet()) {
				query.setParameter(paramName, parameterMap.get(paramName));
			}

			BigDecimal docId = (BigDecimal) query.getSingleResult();

			if(docId != null) {
				String stmt = "select d from Doc d where d.docId = :docId";
				try {
					query = entityManager.createQuery(stmt);
					query.setParameter("docId", docId.longValue());

					return (Doc) query.getSingleResult();

				} catch (NoResultException nre) {
					return null;
				}
			}
		} catch (NoResultException nre) {
			return null;
		} catch (NonUniqueResultException nure) {
			throw new MalBusinessException("Multiple main purchase orders found");
		}
		return null;
	}
	
	public Doc getMainPODocOfStockUnit(Long fmsId) {
		
		Doc doc =  null;
		try {			
			String stmt = "Select d1.doc_id from doc d1 where d1.c_id = 1 and d1.doc_type = 'PORDER'  "
					+ " and d1.source_code in('FLQUOTE','FLORDER') and d1.doc_status = 'R' AND NVL(order_type,'M') != 'T'  "
					+ "  and d1.doc_id in (select doc_id from dist where cdb_code_1 = :fmsId and cdb_code_1 is not null)";

			Query query = entityManager.createNativeQuery(stmt);
			query.setParameter("fmsId", fmsId);
			 BigDecimal docId =(BigDecimal) query.getSingleResult();
			  if(docId != null){
					stmt = "select d from Doc d where d.docId = :docId";
					try{
						query = entityManager.createQuery(stmt);
						query.setParameter("docId", docId.longValue());						
						doc  =  (Doc)query.getSingleResult();
						
					} catch (NoResultException nre) {}
				}
		} catch (NoResultException nre) {}
		
		return doc;

	}
	
	public String getOptionalAccessories(Long docId) throws MalException {
		String resultString = "";
		Connection connection = null;
		CallableStatement callableStatement = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ ? = call willow2k.fl_po.get_optional_accessories(?) }");
			callableStatement.registerOutParameter(1, Types.VARCHAR);
			callableStatement.setLong(2, docId);
			
			callableStatement.execute();
			resultString = callableStatement.getString(1);
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}
		
		return resultString;
	}


	public Long createInvoiceAR(ArCreationVO arVO) throws MalException {

		Connection connection = null;
		CallableStatement callableStatement = null;
		Long result;

		connection = DataSourceUtils.getConnection(dataSource);

		try {
			connection = DataSourceUtils.getConnection(dataSource);
			
			callableStatement = connection.prepareCall("{ ? = call VISION.INVOICE_SERVICE.create_invoice_ar_wrapper(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }");
			callableStatement.registerOutParameter(1, Types.VARCHAR);

			
			callableStatement.setLong(2, arVO.getcId());
			callableStatement.setString(3, arVO.getSourceCode());
			callableStatement.setString(4, arVO.getTransType());
			callableStatement.setString(5, arVO.getCostDbCode());
			callableStatement.setString(6, arVO.getUserName());
			callableStatement.setLong(7, arVO.getExternalAccount().getExternalAccountPK().getCId());
			callableStatement.setString(8, arVO.getExternalAccount().getExternalAccountPK().getAccountType());
			callableStatement.setString(9, arVO.getExternalAccount().getExternalAccountPK().getAccountCode());
			callableStatement.setDate(10, new java.sql.Date(arVO.getDocDate().getTime()));

			if(arVO.getFleetMaster() != null){
				callableStatement.setLong(11, arVO.getFleetMaster().getFmsId());
			}else{
				callableStatement.setNull(11,  Types.INTEGER);
			}
			if(arVO.getDriver() != null){
				callableStatement.setLong(12, arVO.getDriver().getDrvId());
			}else{
				callableStatement.setNull(12,  Types.INTEGER);
			}
			
			callableStatement.setBigDecimal(13, arVO.getAmount());
			callableStatement.setString(14, arVO.getCategoryType());
			callableStatement.setString(15, arVO.getAnalysisCategory());
			callableStatement.setString(16, arVO.getAnalysisCode());
			callableStatement.setString(17, arVO.getLineDescription());
			callableStatement.setString(18, arVO.getChargeCode());
			callableStatement.setString(19, arVO.getGlCode());
			
			callableStatement.execute();
			result = callableStatement.getLong(1);
			
			if (MALUtilities.isEmpty(result)) {
				throw new MalException("Did not create INVOICEAR");
			}
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}

		return result;
		
	}
	
	public Long getUnitPurchaseOrderDocIdFromQmdId(Long qmdId, String statuses) {
		Long result;
		String stmt;
		Query query;		
		
		stmt = "SELECT d.doc_id "
				+" FROM quotation_models qmd, fleet_masters fms, dist di, docl dl,doc d  "
				+" WHERE qmd.qmd_id = :qmdId "
				+"     AND (qmd.fms_fms_id = fms.fms_id or qmd.unit_no = fms.unit_no) "
				+"     AND di.amount > 0 "
				+"     AND di.cdb_code_1 = to_char(fms.fms_id) "
				+"     AND dl.doc_id = di.docl_doc_id "
				+"     AND dl.line_id = di.docl_line_id "
				+"     AND dl.user_def4 = 'MODEL' "
				+"     AND d.doc_id = dl.doc_id "
				+"     AND d.doc_type = 'PORDER' "
				+"     AND d.doc_status IN (:statuses) "
				+"     AND d.source_code IN ('FLQUOTE', 'FLGRD', 'FLORDER') "
				+"     AND nvl(d.order_type, 'X') != 'T' ";
		
		query = entityManager.createNativeQuery(stmt);	
		query.setParameter("qmdId", qmdId);
		query.setParameter("statuses", statuses);		
		
		try {
			result = ((BigDecimal) query.getSingleResult()).longValue();
		}
		catch (NoResultException nre) {
			result = null;
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getUpfitPurchaseOrderDocIdsFromQmdIdAndAccount(Long qmdId, List<String> statuses, Long cId, String accountCode, String accountType, boolean includeMainPO) throws Exception {
		List<Long> docIds = new ArrayList<Long>();
		List<BigDecimal> resultList;
		//String stmt;
		StringBuilder stmt;
		Query query;		
		
		stmt = new StringBuilder();
		stmt.append("SELECT * ");
		stmt.append("    FROM ( (SELECT d.doc_id ");
		stmt.append("                FROM doc d, quotation_models qmd ");
		stmt.append("                WHERE qmd.qmd_id = :qmdId ");
		stmt.append("                    AND d.generic_ext_id = qmd.qmd_id ");
		stmt.append("                    AND d.doc_type = 'PORDER' ");
		
		if(includeMainPO) {
			stmt.append("                AND nvl(d.order_type, 'M') IN ('T', 'M') ");			
		} else {
			stmt.append("                AND nvl(d.order_type, 'M') IN ('T') ");			
		}

		stmt.append("                    AND d.doc_status IN (:statuses) ");
		stmt.append("                    AND d.source_code IN ('FLQUOTE', 'FLGRD', 'FLORDER') ");
		stmt.append("                    AND d.c_id = :cId ");
		stmt.append("                    AND d.account_code = :accountCode ");
		stmt.append("                    AND d.account_type = :accountType ");
		stmt.append("                    AND EXISTS (SELECT 1 ");
		stmt.append("                                    FROM docl dl ");
		stmt.append("                                    WHERE dl.doc_id = d.doc_id ");
		stmt.append("                                        AND dl.user_def4 = 'DEALER')  ) )");
		
		query = entityManager.createNativeQuery(stmt.toString());	
		query.setParameter("qmdId", qmdId);
		query.setParameter("statuses", statuses);				
		query.setParameter("cId", cId);
		query.setParameter("accountCode", accountCode);
		query.setParameter("accountType", accountType);
		
		try {
			resultList = query.getResultList();
			for(BigDecimal record : resultList){
				docIds.add(record.longValue());
			}			
		}
		catch (NoResultException nre) {
			resultList = null;
		} catch(Exception e) {
			logger.error(e, "qmdId=" + qmdId + " accountCode=" + accountCode + " cId=" + cId + " accountType="+ accountType + " statuses=" + statuses.toString());
			throw new Exception(e);
		}
		
		return docIds;		
	}
	
	/**
	 * Includes unit and upfit PO for Factory and Stock quotes.
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getUnitUpfitPurchaseOrderDocIdsFromQmdId(Long qmdId, List<String> statuses){
		List<Long> docIds = new ArrayList<Long>();
		String stmt;
		Query query;	
		List<BigDecimal> resultList;		
		
		stmt = "SELECT d.doc_id "
				+" FROM doc d "
				+" WHERE d.doc_type = 'PORDER' " 
				+"     AND d.source_code IN ('FLQUOTE', 'FLGRD') "
				+"     AND d.doc_status IN (:statuses) "
				+"     AND NVL(d.order_type, 'M') IN ('M', 'T') "
				+"     AND d.generic_ext_id = :qmdId ";
		
		query = entityManager.createNativeQuery(stmt);	
		query.setParameter("qmdId", qmdId);
		query.setParameter("statuses", statuses);				
		
		try {
			resultList = query.getResultList();
			for(BigDecimal record : resultList){
				docIds.add(record.longValue());
			}
		} catch (NoResultException nre) {}
		
		return docIds;			
	}

	@Override
	public BigDecimal getCdFeeUnitCost(Long docId) {
		Query query = null;
		BigDecimal cost = null;
		StringBuilder sqlStmt = new StringBuilder("select dl.unit_cost "
				+ " from docl dl, capital_elements cel "
				+ " where dl.generic_ext_id = cel.cel_id "
				+ " and dl.user_def4 = 'CAPITAL' "
				+ " and cel.code = ut.get_willow_config(null, 'COURT_DELIV_CAP_ELE_TYPE') "
				+ " and dl.generic_ext_id = cel.cel_id "
				+ " and dl.doc_id = :docId ");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("docId", docId);
		
		List results = query.getResultList();
        if (results.isEmpty()) 	
        	return null;
        else if (results.size() == 1) 
        	cost = (BigDecimal)results.get(0);
        
        return cost;
	}	

	public void rechargeCapitalContribution(long cId, long contractLineId, long quotationModelId, Date docDate, String userName) throws MalException {

		Connection connection = null;
		CallableStatement callableStatement = null;
		
		connection = DataSourceUtils.getConnection(dataSource);

		try {
			connection = DataSourceUtils.getConnection(dataSource);
			
			callableStatement = connection.prepareCall("{ ? = call VISION.INVOICE_SERVICE.recharge_contribution(?,?,?,?,?) }");
			callableStatement.registerOutParameter(1, Types.VARCHAR);
			
			callableStatement.setLong(2, cId);
			callableStatement.setLong(3, contractLineId);
			callableStatement.setLong(4, quotationModelId);
			callableStatement.setDate(5, new java.sql.Date(docDate.getTime()));
			callableStatement.setString(6, userName);
			
			callableStatement.execute();
			String result = callableStatement.getString(1);
			
			if (!MALUtilities.isEmpty(result)) {
				throw new MalException(result);
			}
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}
		
	}

}
