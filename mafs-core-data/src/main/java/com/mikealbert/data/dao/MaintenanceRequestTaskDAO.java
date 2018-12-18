package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MaintenanceRequestTask;

/**
* DAO for MaintenanceRequest Entity
* @author sibley
*/
public interface MaintenanceRequestTaskDAO extends MaintenanceRequestTaskDAOCustom, JpaRepository<MaintenanceRequestTask, Long>{
	
	@Query("select DISTINCT(mrt.maintCatCode) FROM MaintenanceRequestTask mrt WHERE mrt.maintenanceRequest.mrqId = ?1")
	public List<String> findDistinctCategoryCodes(Long mrqId);
	
	@Query("select mrt FROM MaintenanceRequestTask mrt WHERE mrt.maintenanceRequest.mrqId = ?1")
	public List<MaintenanceRequestTask> getMaintRequestTasksByMrqId(Long mrqId);
	
	@Query("select mrt FROM MaintenanceRequestTask mrt WHERE mrt.maintenanceRequest.mrqId in (?1 )")
	public List<MaintenanceRequestTask> getMaintRequestTasksByMrqIdList(List<Long> mrqList);
	
	// Query Added for Bug 16387
	@Query("select dl.totalPrice from Docl dl ,MaintenanceRequestTask mrt, MaintenanceRequest mr where mr.mrqId = mrt.maintenanceRequest.mrqId "+
			" and mrt.mrtId =dl.genericExtId and dl.lineType='INVOICEAP' and dl.lineStatus = 'P' and dl.sourceCode = 'FLMAINT' and mrt.mrtId =?1")
	public BigDecimal	getActualInvoiceAmount(Long mrtId);
		
	// Query Added for Bug 16387
    @Query("select sum(d.totalDocPrice - NVL(d.totalDocTax,0)) from Doc d, Docl dl ,MaintenanceRequestTask mrt where mrt.mrtId =dl.genericExtId "+
            "and d.docNo = dl.docNo and dl.lineType in ('INVOICEAR', 'DEBITAR')and dl.lineStatus = 'P' and dl.sourceCode = 'FLMAINT' AND rownum =1 and mrt.maintenanceRequest.mrqId =?1")
    public BigDecimal    getActualCustAmount(Long mrqId);
}
