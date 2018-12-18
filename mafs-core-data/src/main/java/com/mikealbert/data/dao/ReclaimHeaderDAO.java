package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ReclaimHeaders;

/**
* DAO for ReclaimLines Entity
* 
* @author Roshan
*/

public interface ReclaimHeaderDAO extends JpaRepository<ReclaimHeaders, Long> {

	@Query("SELECT rh FROM ReclaimHeaders rh WHERE rh.docId= ?1")
	public ReclaimHeaders findByDocId(Long docId);
	
	@Query("SELECT rh FROM ReclaimHeaders rh WHERE rh.docId= ?1 and rh.celId  is null")
	public ReclaimHeaders findReclaimHeadersForInvoice(Long docId);

	@Query("SELECT rh FROM ReclaimHeaders rh WHERE rh.doc.docId = rh.docId AND rh.doc.genericExtId= ?1 AND rh.doc.docType = 'INVOICEAP'  AND rh.doc.sourceCode = 'POINV' AND rh.doc.docStatus = 'P'")
	public List<ReclaimHeaders> findByQmdId(Long qmdId);	
}
