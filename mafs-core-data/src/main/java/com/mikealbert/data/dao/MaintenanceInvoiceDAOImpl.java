package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.util.CollectionUtils;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DoclPK;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.vo.MaintenanceInvoiceCreditVO;
import com.mikealbert.data.vo.InvoiceDateAndNumberVO;

public class MaintenanceInvoiceDAOImpl extends GenericDAOImpl<Doc, Long> implements MaintenanceInvoiceDAOCustom {

	@Resource
	private MaintenanceInvoiceDAO maintenanceInvoiceDAO;
	/**
	 * Find invoice data based on maintenance request id
	 */
	private static final long serialVersionUID = 1L;
	
	public InvoiceDateAndNumberVO getMaintenanceRequestMafsInvoiceNumber(Long mrqId)
	{
		List<InvoiceDateAndNumberVO> mafsInvoiceList = new ArrayList<InvoiceDateAndNumberVO>();
		Query query = generateMaintenanceRequestInvoiceDataQuery(mrqId, "mafs");
		
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		if (resultList != null){
			for (Object[] record : resultList){
				int i = 0;
				
				InvoiceDateAndNumberVO mafsInvoiceVO = new InvoiceDateAndNumberVO();
				mafsInvoiceVO.setDocNo((String)record[i]);
				mafsInvoiceVO.setDocDate((Date)record[i+=1]);
		
				mafsInvoiceList.add(mafsInvoiceVO);
			}
		}
		return CollectionUtils.isEmpty(mafsInvoiceList) ? null : mafsInvoiceList.get(0);

	}
	
	@SuppressWarnings("unchecked")
	public InvoiceDateAndNumberVO getMaintenanceRequestPayeeInvoiceData(Long mrqId){
		List<InvoiceDateAndNumberVO> payeeInvoiceList = new ArrayList<InvoiceDateAndNumberVO>();
		Query query = generateMaintenanceRequestPayeeInvoiceDataQuery(mrqId);
		
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		if (resultList != null){
			for (Object[] record : resultList){
				int i = 0;
				
				InvoiceDateAndNumberVO payeeInvoiceVO = new InvoiceDateAndNumberVO();
				payeeInvoiceVO.setDocNo((String)record[i]);
				payeeInvoiceVO.setDocDate((Date)record[i+=1]);
		
				payeeInvoiceList.add(payeeInvoiceVO);
			}
		}
		return CollectionUtils.isEmpty(payeeInvoiceList) ? null : payeeInvoiceList.get(0);
		
	}
	
	public boolean isMaintenanceRequestInvoiceCredit(Long mrqId)
	{
		boolean result = false;
		Query query = generateMaintenanceRequestInvoiceDataQuery(mrqId,"credit");

		try {
			if (query.getSingleResult().toString() != null) {
				result = true;
			}
		}
		catch (NoResultException nre){
			//not found
		}

		return result;
	}
	
	/**
	 * Retrieve the task lines of a Maintenance Invoice Credit
	 */
	@SuppressWarnings("unchecked")
	public List<MaintenanceInvoiceCreditVO> getMaintenanceCreditAPLines(MaintenanceRequest mrq){
		List<MaintenanceInvoiceCreditVO> maintenanceInvoiceCreditLineList	= new ArrayList<MaintenanceInvoiceCreditVO>();
		
		Query query = generateMaintenanceCreditAPLinesQuery(mrq.getMrqId());

		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;

				MaintenanceInvoiceCreditVO maintenanceInvoiceCreditVO = new MaintenanceInvoiceCreditVO();				
				maintenanceInvoiceCreditVO.setDocId(((BigDecimal)record[i]).longValue());
				maintenanceInvoiceCreditVO.setCreditNo((String)record[i+=1] != null ? ((String)record[i]) : null);
				maintenanceInvoiceCreditVO.setCreditDate((Date)record[i+=1]);
				maintenanceInvoiceCreditVO.setCreditDesc((String)record[i+=1] != null ? ((String)record[i]) : null);
				maintenanceInvoiceCreditVO.setMrtId(((BigDecimal)record[i+=1]) != null ? ((BigDecimal)record[i]).longValue() : 0L);
				maintenanceInvoiceCreditVO.setMaintCategory((String)record[i+=1] != null ? ((String)record[i]) : null);
				maintenanceInvoiceCreditVO.setServiceCode((String)record[i+=1] != null ? ((String)record[i]) : null);
				maintenanceInvoiceCreditVO.setServiceCodeDesc((String)record[i+=1] != null ? ((String)record[i]) : null);
				maintenanceInvoiceCreditVO.setMaintCode((String)record[i+=1] != null ? ((String)record[i]) : null);
				maintenanceInvoiceCreditVO.setMaintCodeDesc((String)record[i+=1] != null ? ((String)record[i]) : null);
				maintenanceInvoiceCreditVO.setRechargeInd((String)record[i+=1] != null ? ((String)record[i]) : null);
				maintenanceInvoiceCreditVO.setRechargeCode((String)record[i+=1] != null ? ((String)record[i]) : null);
				maintenanceInvoiceCreditVO.setDiscountInd((String)record[i+=1] != null ? ((String)record[i]) : null);
				maintenanceInvoiceCreditVO.setQuantity(((BigDecimal)record[i+=1]).intValue());
				maintenanceInvoiceCreditVO.setUnitPrice(((BigDecimal)record[i+=1]).setScale(2, BigDecimal.ROUND_HALF_UP));
				maintenanceInvoiceCreditVO.setTotalPrice(((BigDecimal)record[i+=1]).setScale(2, BigDecimal.ROUND_HALF_UP));

				maintenanceInvoiceCreditLineList.add(maintenanceInvoiceCreditVO);
			}

		}
							
		return maintenanceInvoiceCreditLineList;
	}
	
	/**
	 * Retrieve the Maintenance Invoice CreditAP Doc Ids
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getMaintenanceCreditAPDocIds(MaintenanceRequest mrq){
		List<Long> creditAPDocIds = new ArrayList<Long>();
		Query query = generateMaintenanceCreditAPQuery(mrq.getMrqId());
		
		List<BigDecimal>resultList = (List<BigDecimal>)query.getResultList();
		if(resultList != null){
			for(BigDecimal record : resultList){
				creditAPDocIds.add((record).longValue());
			}
		}
		return creditAPDocIds;
	}
	
	/**
	 * Retrieve the Maintenance Invoice CreditAR Markup
	 */
	@SuppressWarnings("unchecked")
	public List<DoclPK> getMaintenanceCreditARMarkupDoclPKs(MaintenanceRequest mrq){
		List<DoclPK> creditARMarkupList = new ArrayList<DoclPK>();
		Query query = generateMaintenanceCreditARMarkupDoclPKsQuery(mrq.getMrqId());
		
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				DoclPK markupDoclPK = new DoclPK();				
				markupDoclPK.setDocId(((BigDecimal)record[i]).longValue());
				markupDoclPK.setLineId(((BigDecimal)record[i+=1]).longValue());
				
				creditARMarkupList.add(markupDoclPK);
			}
		}
		return creditARMarkupList;
	}
	
	/**
	 * Retrieve the Maintenance Invoice CreditAR Tax
	 */
	@SuppressWarnings("unchecked")
	public List<DoclPK> getMaintenanceCreditARTaxDoclPKs(MaintenanceRequest mrq){
		List<DoclPK> creditARTaxList = new ArrayList<DoclPK>();
		Query query = generateMaintenanceCreditARTaxDoclPKsQuery(mrq.getMrqId());
		
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				DoclPK taxDoclPK = new DoclPK();				
				taxDoclPK.setDocId(((BigDecimal)record[i]).longValue());
				taxDoclPK.setLineId(((BigDecimal)record[i+=1]).longValue());
				
				creditARTaxList.add(taxDoclPK);
			}
		}
		return creditARTaxList;
	}
	
	/**
	 * Retrieve the Maintenance Invoice CreditAR Docl Lines without the Markup line
	 *  nor the Tax Line.
	 */
	@SuppressWarnings("unchecked")
	public List<DoclPK> getMaintenanceCreditARDoclPKsWithoutTaxAndMarkupLines(MaintenanceRequest mrq){
		List<DoclPK> creditARLineList = new ArrayList<DoclPK>();
		Query query = generateMaintenanceCreditARLinesWithoutTaxOrMarkupQuery(mrq.getMrqId());
		
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				DoclPK lineDoclPK = new DoclPK();				
				lineDoclPK.setDocId(((BigDecimal)record[i]).longValue());
				lineDoclPK.setLineId(((BigDecimal)record[i+=1]).longValue());
				
				creditARLineList.add(lineDoclPK);
			}
		}
		return creditARLineList;
	}
	
	
	
	private Query generateMaintenanceRequestInvoiceDataQuery(Long mrqId, String invoiceType) {
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		if (invoiceType.equals("mafs")) {
			sqlStmt.append (
						" SELECT d4.doc_no, d4.doc_date " +
						" FROM doc d4 " +
						" WHERE d4.doc_id = " +
						"     (SELECT " +		
						"       max(d3.doc_id)" + 
						"       FROM doc d3" + 
						"       WHERE d3.doc_id in " +
						"           (select dld.child_doc_id"); 
		}
		
		if (invoiceType.equals("credit")) {
			sqlStmt.append (			
						"SELECT " + 
						" 'Y'" + 
						" FROM doc_links dl" +
						" WHERE dl.parent_doc_id in " +
							"(select dl.child_doc_id");	
		}

			sqlStmt.append (								
								" from doc d" + 
				                " INNER JOIN doc_links dl ON d.doc_id = dl.parent_doc_id" +
				                " INNER JOIN doc d1 ON dl.child_doc_id = d1.doc_id");
			
		if (invoiceType.equals("mafs")) {
			sqlStmt.append (				                
				                " LEFT OUTER JOIN (SELECT dll2.parent_doc_id, dll2.child_doc_id" +
		                                            " FROM docl_links dll2" +
		                                            " WHERE dll2.parent_line_id = (SELECT dll.parent_line_id" +
	                                                                                " FROM docl_links dll, doc d2" +
	                                                                                " WHERE dll.child_doc_id = d2.doc_id" +
                                                                                    " AND dll2.parent_doc_id = dll.parent_doc_id" +
                                                                                    " AND d2.source_code = 'FLMAINT'" +
                                                                                    " AND d2.doc_type = 'INVOICEAR'" +
                                                                                    " AND d2.doc_status = 'P'" +
                                                                                    " AND ROWNUM=1)) dld ON d1.doc_id = dld.parent_doc_id");
		}
		
			sqlStmt.append (
				                " WHERE d.source_code = 'FLMAINT'" +
				                " AND d.doc_type = 'PORDER'" +
				                " AND d.doc_status = 'R'" +
				                " AND d1.source_code = 'FLMAINT'" +
				                " AND d1.doc_type = 'INVOICEAP'" +
				                " AND d1.doc_status = 'P'" +
				                " AND d.generic_ext_id = :mrqId)");
			
        if (invoiceType.equals("mafs")) {
        	sqlStmt.append (
		                " AND d3.doc_type = 'INVOICEAR'" +
		                " AND d3.source_code = 'FLMAINT'" + 
		                " AND d3.doc_status = 'P')");
        }
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		query.setParameter("mrqId", mrqId);

		return query;
	}
	
	
	private Query generateMaintenanceRequestPayeeInvoiceDataQuery(Long mrqId) {

		Query query = null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append (
		          "SELECT doc_no, doc_date" +
		          " FROM doc, maintenance_requests mr" +
		          " WHERE doc_type = 'INVOICEAP'" +
		          " AND source_code = 'FLMAINT'" +
		          " AND mr.mrq_id = :mrqId" +
				  " AND mr.maint_request_status = 'C'" +
				  " AND doc_status = 'P'" +
		          " AND generic_ext_id = mr.mrq_id" +
		          " AND doc_id = (SELECT max(doc_id)" +
					  			  " FROM doc" +
					  			  " WHERE doc_type = 'INVOICEAP'" +
					  			  " AND source_code = 'FLMAINT'" +
					  			  " AND mr.maint_request_status = 'C'" +
					  			  " AND doc_status = 'P'" +
		                 		  " AND generic_ext_id = mr.mrq_id)"); 			
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		query.setParameter("mrqId", mrqId);

		return query;
	}	
	
	private Query generateMaintenanceCreditAPLinesQuery(Long mrqId){
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append ("SELECT doc2.doc_id, doc2.doc_no, doc2.doc_date, doc2.doc_description, mrt.mrt_id, mrt.mcg_maint_cat_code, "
                   +"                sml.vendor_code, sml.description, mrt.maint_code, mrt.vendor_code_desc, "
                   +"                mrt.recharge_flag, mrt.recharge_code, mrt.discount_ind, docl2.qty_invoice," 
                   +"                docl2.unit_price, docl2.total_price "
                   +"     FROM docl docl2 "
                   +"              INNER JOIN doc doc2 " 
                   +"              ON docl2.doc_id = doc2.doc_id "
                   +"              LEFT OUTER JOIN maintenance_request_tasks mrt "
                   +"              ON docl2.generic_ext_id = mrt.mrt_id "
                   +"              LEFT OUTER JOIN supp_maint_link sml "
                   +"              ON sml.sml_id = mrt.sml_sml_id "
                   +"     WHERE doc2.doc_id IN(SELECT   dl.child_doc_id "
                   +"                                      FROM doc_links dl "
                   +"                                      WHERE dl.parent_doc_id in  "
                   +"                                         (select dl.child_doc_id "
                   +"                                          from doc d  "
                   +"                                              INNER JOIN doc_links dl ON d.doc_id = dl.parent_doc_id "
                   +"                                              INNER JOIN doc d1 ON dl.child_doc_id = d1.doc_id "
                   +"                                              WHERE d.source_code = 'FLMAINT' "
                   +"                                              AND d.doc_type = 'PORDER' "
                   +"                                              AND d.doc_status = 'R' "
                   +"                                              AND d1.source_code = 'FLMAINT' "
                   +"                                              AND d1.doc_type = 'INVOICEAP' "
                   +"                                              AND d1.doc_status = 'P' "
                   +"                                              AND d.generic_ext_id = :mrqId))   "			);	
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		query.setParameter("mrqId", mrqId);

		return query;
	}
	
	private Query generateMaintenanceCreditAPQuery(Long mrqId){
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append ("SELECT DISTINCT doc_id"
					+"	 FROM docl"
                    +"     WHERE generic_ext_id IS NOT NULL AND"
                    +"     doc_id IN (SELECT   dl.child_doc_id"
                    +"             FROM doc_links dl "
                    +"             WHERE dl.parent_doc_id in  "
                    +"                (select dl.child_doc_id "
                    +"                 from doc d  "
                    +"                     INNER JOIN doc_links dl ON d.doc_id = dl.parent_doc_id "
                    +"                     INNER JOIN doc d1 ON dl.child_doc_id = d1.doc_id "
                    +"                     WHERE d.source_code = 'FLMAINT' "
                    +"                     AND d.doc_type = 'PORDER' "
                    +"                     AND d.doc_status = 'R' "
                    +"                     AND d1.source_code = 'FLMAINT' "
                    +"                     AND d1.doc_type = 'INVOICEAP' "
                    +"                     AND d1.doc_status = 'P' "
                    +"                     AND d.generic_ext_id = :mrqId))"		);	
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		query.setParameter("mrqId", mrqId);

		return query;
	}
	
	private Query generateMaintenanceCreditARMarkupDoclPKsQuery(Long mrqId){
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append ("SELECT docl4.doc_id, docl4.line_id"
						+"  FROM docl docl4 "
						+"  WHERE docl4.user_def1 = 'UPLIFT' AND "
						+"             docl4.line_type = 'CREDITAR' AND " 
						+"             docl4.line_status = 'P' AND "
						+"             docl4.source_code = 'FLMAINT' AND "
						+"             docl4.doc_id IN(SELECT DISTINCT docl3.doc_id "
						+"                                      FROM docl docl3 "
						+"                                      WHERE docl3.line_type = 'CREDITAR' AND "
						+"                                                  docl3.line_status = 'P' AND "
						+"                                                  docl3.source_code = 'FLMAINT' AND "
						+"                                                  docl3.generic_ext_id IN(SELECT docl2.generic_ext_id "
						+"                                                                                      FROM docl docl2 "
						+"                                                                                     WHERE docl2.doc_id IN (SELECT   dl.child_doc_id "
						+"                                                                                                                          FROM doc_links dl "
						+"                                                                                                                         WHERE dl.parent_doc_id in  "
						+"                                                                                                                            (SELECT dl.child_doc_id "
						+"                                                                                                                             FROM doc d  "
						+"                                                                                                                                 INNER JOIN doc_links dl ON d.doc_id = dl.parent_doc_id "
						+"                                                                                                                                 INNER JOIN doc d1 ON dl.child_doc_id = d1.doc_id "
						+"                                                                                                                                 WHERE d.source_code = 'FLMAINT' "
						+"                                                                                                                                 AND d.doc_type = 'PORDER' "
						+"                                                                                                                                 AND d.doc_status = 'R' "
						+"                                                                                                                                 AND d1.source_code = 'FLMAINT' "
						+"                                                                                                                                 AND d1.doc_type = 'INVOICEAP' "
						+"                                                                                                                                 AND d1.doc_status = 'P' "
						+"                                                                                                                                 AND d.generic_ext_id = :mrqId))))"	);	
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		query.setParameter("mrqId", mrqId);

		return query;
	}
	
	private Query generateMaintenanceCreditARTaxDoclPKsQuery(Long mrqId){
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append (" SELECT docl4.doc_id, docl4.line_id"
                        +"  FROM docl docl4 "
                        +"  WHERE docl4.product_code = 'REBILL_TAX' AND "
                        +"             docl4.line_type = 'CREDITAR' AND  "
                        +"             docl4.line_status = 'P' AND "
                        +"             docl4.source_code = 'FLMAINT' AND "
                        +"             docl4.doc_id IN(SELECT DISTINCT docl3.doc_id "
                        +"                                      FROM docl docl3 "
                        +"                                      WHERE docl3.line_type = 'CREDITAR' AND "
                        +"                                                  docl3.line_status = 'P' AND "
                        +"                                                  docl3.source_code = 'FLMAINT' AND "
                        +"                                                  docl3.generic_ext_id IN(SELECT docl2.generic_ext_id "
                        +"                                                                                      FROM docl docl2 "
                        +"                                                                                     WHERE docl2.doc_id IN (SELECT   dl.child_doc_id "
                        +"                                                                                                                          FROM doc_links dl "
                        +"                                                                                                                         WHERE dl.parent_doc_id in  "
                        +"                                                                                                                            (SELECT dl.child_doc_id "
                        +"                                                                                                                             FROM doc d  "
                        +"                                                                                                                                 INNER JOIN doc_links dl ON d.doc_id = dl.parent_doc_id "
                        +"                                                                                                                                 INNER JOIN doc d1 ON dl.child_doc_id = d1.doc_id "
                        +"                                                                                                                                 WHERE d.source_code = 'FLMAINT' "
                        +"                                                                                                                                 AND d.doc_type = 'PORDER' "
                        +"                                                                                                                                 AND d.doc_status = 'R' "
                        +"                                                                                                                                 AND d1.source_code = 'FLMAINT' "
                        +"                                                                                                                                 AND d1.doc_type = 'INVOICEAP' "
                        +"                                                                                                                                 AND d1.doc_status = 'P' "
                        +"                                                                                                                                 AND d.generic_ext_id = :mrqId))))");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		query.setParameter("mrqId", mrqId);

		return query;
	}
	
	private Query generateMaintenanceCreditARLinesWithoutTaxOrMarkupQuery(Long mrqId){
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append ( " SELECT docl4.doc_id, docl4.line_id"
                         +" FROM docl docl4 "
                         +" WHERE docl4.line_type = 'CREDITAR' AND  "
                         +"            docl4.line_status = 'P' AND "
                         +"            docl4.source_code = 'FLMAINT' AND "
                         +"            docl4.generic_ext_id IS NOT NULL AND "
                         +"            docl4.doc_id IN(SELECT DISTINCT docl3.doc_id "
                         +"                                     FROM docl docl3 "
                         +"                                     WHERE docl3.line_type = 'CREDITAR' AND "
                         +"                                                 docl3.line_status = 'P' AND "
                         +"                                                 docl3.source_code = 'FLMAINT' AND "
                         +"                                                 docl3.generic_ext_id IN(SELECT docl2.generic_ext_id "
                         +"                                                                                     FROM docl docl2 "
                         +"                                                                                    WHERE docl2.doc_id IN (SELECT   dl.child_doc_id "
                         +"                                                                                                                         FROM doc_links dl "
                         +"                                                                                                                        WHERE dl.parent_doc_id in  "
                         +"                                                                                                                           (SELECT dl.child_doc_id "
                         +"                                                                                                                            FROM doc d  "
                         +"                                                                                                                                INNER JOIN doc_links dl ON d.doc_id = dl.parent_doc_id "
                         +"                                                                                                                                INNER JOIN doc d1 ON dl.child_doc_id = d1.doc_id "
                         +"                                                                                                                                WHERE d.source_code = 'FLMAINT' "
                         +"                                                                                                                                AND d.doc_type = 'PORDER' "
                         +"                                                                                                                                AND d.doc_status = 'R' "
                         +"                                                                                                                                AND d1.source_code = 'FLMAINT' "
                         +"                                                                                                                                AND d1.doc_type = 'INVOICEAP' "
                         +"                                                                                                                               AND d1.doc_status = 'P' "
                         +"                                                                                                                                AND d.generic_ext_id = :mrqId))))" );
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		query.setParameter("mrqId", mrqId);

		return query;
	}

}
