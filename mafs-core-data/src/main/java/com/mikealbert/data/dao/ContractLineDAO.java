package com.mikealbert.data.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ContractLine;

/**
* DAO for ExternalAccont Entity
* @author sibley
*/
public interface ContractLineDAO extends JpaRepository<ContractLine, Long>{ /* Bug 16599(HPS-1973) added condition to get MAX REV_NO*/
	@Query("SELECT cl FROM ContractLine cl WHERE cl.fleetMaster.fmsId = ?1 AND cl.startDate IS NOT NULL AND cl.startDate > ?2 AND cl.inServDate IS NOT NULL AND cl.inServDate <= ?2 AND cl.revNo = (SELECT MAX(cl1.revNo) FROM ContractLine cl1 WHERE cl1.fleetMaster.fmsId = ?1 )")
	public ContractLine findByFmsIdAndDate(Long fmsId, Date date);
	
	@Query("SELECT cl FROM ContractLine cl WHERE cl.fleetMaster.fmsId = ?1")
	public List <ContractLine> findByFmsId(Long fmsId);
	
	@Query("SELECT cl FROM ContractLine cl WHERE cl.fleetMaster.fmsId = ?1 order by cl.revNo DESC")
	public List <ContractLine> findByFmsIdOrderByRev(Long fmsId);	
	
	/* Bug 16599(HPS-1973) added condition to get MAX REV_NO */
	@Query("SELECT cl FROM ContractLine cl WHERE cl.fleetMaster.fmsId = ?1 AND cl.inServDate <=?2 AND cl.revNo = (SELECT MAX(cl1.revNo) FROM ContractLine cl1 WHERE cl1.fleetMaster.fmsId = ?1 )")
	public ContractLine findByFmdIdAndDateWithInService(Long fmsId, Date date);	
	
	@Query("SELECT cl FROM ContractLine cl WHERE cl.quotationModel.qmdId = ?1 ")
	public List<ContractLine> findByQmdId(Long qmdId );

	@Query("SELECT cl FROM ContractLine cl LEFT JOIN FETCH cl.quotationModel WHERE cl.clnId = ?1 ")
	public ContractLine findByClnIdWithQuotationModel(Long clnId);

	//Added for Bug 16598
	@Query("SELECT cl FROM ContractLine cl WHERE cl.fleetMaster.fmsId = ?1 AND cl.inServDate IS NOT NULL AND cl.startDate IS NULL")
	public ContractLine findByFmsIdOnly(Long fmsId);
	
	@Query("SELECT cl FROM ContractLine cl LEFT JOIN FETCH cl.driver dr WHERE cl.clnId = ?1 ")
	public ContractLine findByClnIdWithDriver(Long clnId);
}

