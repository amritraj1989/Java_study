package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuotationModel;

/**
* DAO for QuotationModel Entity
* @author sibley
*/
public interface QuotationModelDAO extends JpaRepository<QuotationModel, Long>, QuotationModelDAOCustom{ 
	@Query("SELECT qm FROM QuotationModel qm WHERE qm.contractLine.clnId = ?1")
	public List<QuotationModel> findByClnId(Long clnId);
	
	@Query("SELECT qm FROM QuotationModel qm WHERE qm.unitNo = ?1")
	public List<QuotationModel> findByUnitNo(String unitNo);
	
	@Query("SELECT qm FROM QuotationModel qm WHERE qm.unitNo = ?1 and qm.qmdId <> ?2 order by qm.revisionDate asc ")
	public List<QuotationModel> findPrevQuotationsByUnitNo(String unitNo,Long qmdId);
	
	@Query("from QuotationModel where revisionQmdId = ?1")
	public QuotationModel findRevisionQmdId(Long qmdId);
	
	@Query("from QuotationModel where unitNo = ?1 and amendmentEffectiveDate is not null")
	public	List<QuotationModel>	findAmendedQuotes(String unitNumber);
	
	@Query("from QuotationModel where qmdId = ?1 and contractLine.clnId= ?2")
	public	QuotationModel	findQuotesByQuotationAndContract(Long qmdId, Long clnId);
	
	@Query(" select qm from QuotationModel qm join qm.contractLineList cln where  qm.qmdId < ?1 and cln.contract.conId= ?2 and cln.earlyTermQuoteId is null order by cln.contract.contractNo,cln.startDate, cln.revNo desc")
	public	List<QuotationModel>	findPreviousQuotesByQuotation(Long qmdId, Long conId);
	
	@Query(" select qm from QuotationModel qm join qm.contractLineList cln where  qm.qmdId > ?1 and cln.contract.conId= ?2  and cln.earlyTermQuoteId is null order by qm.qmdId asc")
	public	List<QuotationModel>	findNewerQuotesByQuotation(Long qmdId,Long conId);
	
	@Query("select qm from QuotationModel qm join qm.contractLineList cln where qm.unitNo = ?1 and cln.contract.conId= ?2 and cln.earlyTermQuoteId is null order by qm.qmdId asc ")
	public	List<QuotationModel>	findQuotesByUnitNoAndContract(String unitNumber, Long conId);
	
	@Query("SELECT qm FROM QuotationModel qm WHERE qm.quotation.quoId = ?1 order by qm.qmdId asc ")
	public List<QuotationModel> findByQuoteId(Long  quoId);
	
	@Query("SELECT qm FROM QuotationModel qm WHERE qm.quotation.quoId = ?1 and qm.quoteStatus = ?2")
	public QuotationModel findByQuoteIdAndStatus(Long  quoId,Integer quoteStatus);
	
	@Modifying
	@Query("update QuotationModel qm set qm.projectedMonths = ?3 where qm.quotation.quoId = ?1 and qm.qmdId >= ?2")
	int updateProjectedMonths(Long quoId, Long qmdId, Long projectedMonth);

	@Query("SELECT qm.qmdId FROM QuotationModel qm WHERE qm.quotation.quoId = ?1 and qm.quoteNo = ?2 and qm.revisionNo = ?3")
	public Long findQmdIdByQuoteNumber(Long  quoId, Long  quoteNo, Long revisionNo);
	
	@Query("SELECT qm.model.modelId FROM QuotationModel qm WHERE qm.qmdId = ?1")
	public Long findModelIdByQmdId(Long  qmdId);
	
}

