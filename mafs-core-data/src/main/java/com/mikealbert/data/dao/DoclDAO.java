package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.DoclPK;

/**
* DAO for Docl Entity
* @author Singh
*/

public interface DoclDAO extends JpaRepository<Docl, DoclPK> {
	
	@Query("SELECT dl FROM Docl dl WHERE  dl.userDef2 = ?1 and dl.userDef1 = ?2 and dl.lineType = ?3  and dl.sourceCode = ?4 ")
	public List<Docl> findInvoiceLineForUnitByUserDef1AndLineTypeAndSourceCode(String fmsId, String userDef1 , String lineType, String sourceCode);
	
	@Query("SELECT sum(dl.totalPrice) FROM Docl dl WHERE dl.genericExtId = ?1 and dl.sourceCode = 'FLMAINT' AND dl.lineType IN ('INVOICEAR','DEBITAR','CREDITAR') AND dl.doc.docId = dl.doc AND dl.doc.docStatus = 'P'")
	public BigDecimal findTotalAmountForMaintReqTask(Long mrtId);
	
	@Query("SELECT sum(NVL(dl.totalPrice,0)) + SUM(ROUND(NVL(qtyInvoice,0) * NVL(unitTax,0),2)) FROM Docl dl WHERE dl.docNo = ?1 AND dl.lineType IN ('INVOICEAR','CREDITAR') AND dl.doc.docStatus = 'O'")
	public BigDecimal findTotalAmountForUnPostedInvoiceNo(String invoiceNo);	
	
	@Query("SELECT dl FROM Docl dl WHERE  dl.genericExtId = ?1 and dl.userDef1 = ?2 and dl.lineType = ?3  and dl.sourceCode = ?4 ")
	public List<Docl> findInvoiceLineForQuoteCapitalByUserDef1AndLineTypeAndSourceCode(Long quotationCapitalElementId, String userDef1 , String lineType, String sourceCode);
	
	
	@Query("SELECT dl FROM Docl dl WHERE  dl.userDef2 = ?1 and dl.lineType = ?2  and dl.sourceCode = ?3  and dl.lineDescription like ?4 order by dl.docDate desc")
	public	List<Docl>	findInvoiceLineForQuoteElementByUserDef2AndLineTypeAndSourceAndDesc(String userDef2,String lineType, String sourceCode ,String lineDesc);
	
	@Query("select sum(dl.totalPrice) FROM  Docl dl ,  MaintenanceRequestTask mrt   "
			  	+"  WHERE dl.genericExtId = mrt.mrtId"
			  	+"  AND  mrt.fmsFmsId = ?1"
			  	+"  AND mrt.dacDacId = ?2"
			  	+ " AND dl.lineType = 'INVOICEAP'"
			  	+"  AND dl.id.docId   in (?3 )")	
	public BigDecimal findInvoiceForAmendedEquipmenet(Long fmsId ,Long dacId, List<Long>  docIds);	
	
	@Query(" select dl from Docl dl where id.docId = ?1")
	public	List<Docl>	findByDocId(Long docId);
	
	@Query("SELECT dl FROM Docl dl WHERE dl.doc.docId = ?1 and dl.userDef4 = ?2")
	public List<Docl> findDoclByDocIdAndUserDef4(Long docId, String userDef4);
}
