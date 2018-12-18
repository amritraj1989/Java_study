package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Doc;
/**
* DAO for Doc Entity
* @author Singh
*/

public interface DocDAO extends JpaRepository<Doc, Long>, DocDAOCustom {
	

	@Query("SELECT d FROM Doc d WHERE d.genericExtId = ?1 and d.docType = ?2  and d.sourceCode = ?3")
	public List<Doc> findInvoiceForQuoteByDocTypeAndSourceCode(Long extId ,String docType ,String  sourceCode );
	
	@Query("SELECT d FROM Doc d WHERE d.genericExtId = ?1 and d.docType = ?2  and d.sourceCode = ?3")
	public List<Doc> findInvoiceByExtIdAndDocTypeAndSourceCode(Long extId ,String docType ,String  sourceCode );	
	
	@Query("SELECT d FROM Doc d WHERE d.genericExtId in ( ?1 ) and d.docType = ?2  and d.sourceCode = ?3")
	public List<Doc> findInvoiceByExtIdsAndDocTypeAndSourceCode(List<Long>  extIds ,String docType ,String  sourceCode );
	
	@Query("SELECT d FROM Doc d WHERE  d.docStatus = ?1  and d.docType = ?2 and d.sourceCode = ?3 and NVL (d.orderType, 'x') = 'T' and d.docId in (?4 )")
	public List<Doc> findInvoiceByDocIdAndDocStatusAndDocTypeAndSourceCode(String docStatus ,String docType, String sourceCode, List<Long> docIds );	
		
	@Query("SELECT d FROM Doc d WHERE d.docId = ?1  and d.docStatus = ?2  and d.docType = ?3 and d.sourceCode = ?4 and NVL (d.orderType, 'x') = 'T'")
	public List<Doc> findInvoiceByDocIdAndDocStatusAndDocTypeAndSourceCode(Long docId ,String docStatus ,String docType, String sourceCode);	
	
	@Query("select d FROM Doc d ,MaintenanceRequestTask mrt , AccessoryMaintJobActivation amja  where mrt.maintenanceRequest.mrqId =  d.genericExtId  and d.sourceCode = 'FLMAINT'  and d.docType = 'PORDER' and d.docStatus = 'R'"
			+ "and mrt.fmsFmsId = amja.id.fmsFmsId  AND mrt.maintenanceRequest.mrqId = amja.maintenanceRequest.mrqId  AND mrt.fmsFmsId = ?1")
	public List<Doc> getReleasedMaintenancePODocByFmsId(Long fmsId);
	
	
	
	@Query("SELECT d FROM Doc d WHERE d.genericExtId = ?1")
	public List<Doc> findInvoiceByExtId(Long extId);	
	
	
	@Query("SELECT d FROM Doc d WHERE d.genericExtId = ?1 and d.docStatus = ?2  and d.docType = ?3 and d.sourceCode = ?4 and NVL (d.orderType, 'x') != 'T' ")
	public List<Doc> findInvoiceByExtIdAndDocStatusAndDocTypeAndSourceCode(Long extId , String docStatus , String docType, String sourceCode);	
	
	
	@Query("SELECT d FROM Doc d WHERE d.genericExtId = ?1  AND doc_status = 'R' AND  doc_type = 'PORDER'  AND  NVL (order_type, 'x') != 'T' AND source_code = 'FLQUOTE'")	
	public Doc findReleasedMainPO(Long qmdId);		
	
	@Query("SELECT d FROM Doc d WHERE d.genericExtId = ?1 and d.docType = 'PORDER' and d.sourceCode = 'FLMAINT'")
	public List<Doc> findPurchaseOrderForMaintReq(Long mrqId);
	
	@Query("SELECT d FROM Doc d WHERE d.genericExtId = ?1 AND d.docType = 'PORDER' AND d.sourceCode = 'FLMAINT' AND d.docStatus = 'R'")
	public List<Doc> findReleasedPurchaseOrderForMaintReq(Long mrqId);	
	
	@Query("SELECT d FROM Doc d WHERE d.genericExtId = ?1 AND d.docType = 'PORDER' AND d.sourceCode = 'FLMAINT' AND d.docStatus = 'R'")
	public Doc findReleasedPurchaseOrderDocForMaintReq(Long mrqId);

	@Query("SELECT d FROM Doc d WHERE doc_type = 'PORDER' AND " +
			"source_code IN ('FLQUOTE', 'FLORDER') AND " +
			"NVL (order_type, 'M') = 'M' AND " +
			"generic_ext_id = ?1 AND " +
			"doc_status = ?2")	
	public Doc findMainPurchaseOrderByQmdIdAndStatus(Long qmdId, String docStatus);	
	
	@Query("SELECT d FROM Doc d WHERE d.docNo = ?1")
	public	List<Doc>	findByDocNo(String docNo);
	

	@Query("SELECT d FROM Doc d WHERE d.genericExtId = ?1 and d.docType = ?2  and d.sourceCode = ?3")
	public List<Doc> findDocForGenericExtIdByDocTypeAndSourceCode(Long extId ,String docType ,String  sourceCode );
	
	@Query("SELECT dl.childDoc FROM DocLink dl WHERE dl.childDoc.docType = 'PORDER' AND dl.childDoc.docStatus != 'C' AND NVL (dl.childDoc.orderType, 'x') = 'T' AND dl.id.parentDocId = ?1")	
	public List<Doc> findThirdPartyPurchaseOrderByMainDocId(Long docId);
	
	@Query("SELECT d FROM Doc d WHERE d.docNo = ?1  and d.docStatus = ?2")
	public Doc findByDocNoAndDocStatus(String docNo ,String docStatus);	
	
	@Query("SELECT d FROM Doc d WHERE d.docNo = ?1 and d.accountCode = ?2  and d.docType in (?3 ) and d.docStatus in ?4")
	public  List<Doc> findDocByDocNoAndClientAndTypesAndStatus(String docNo, String accountCode, List<String>  docTypes,  List<String>  status);	
	
	@Query("SELECT d FROM Doc d LEFT JOIN FETCH d.docPropertyValues dpv WHERE d.docId = ?1")	
	public Doc findDocWithPropertiesByDocId(Long docId);

	@Query("SELECT d FROM Doc d WHERE d.genericExtId = ?1 and d.docType = ?2  and d.sourceCode = ?3 and NVL (order_type, 'x') != 'T'")
	public List<Doc> findMultipleQuoteMainPo(Long qmdId, String docType, String sourceCode);
}
