package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.CapitalElementRule;

/**
* DAO for CapitalElementRule Entity
* @author Singh
*/

public interface CapitalElementRuleDAO extends JpaRepository<CapitalElementRule, Long> {
	
	@Query("SELECT cer FROM CapitalElementRule cer WHERE cer.capitalElement.celId = ?1 and  cer.lelLelId = ?2")
	public CapitalElementRule findCERuleByCapitalElementAndLeaseElement(Long celId, Long lelId);
	
	@Query("SELECT cer FROM CapitalElementRule cer WHERE cer.capitalElement.celId = ?1 and  cer.makMakId = ?2")
	public CapitalElementRule findCERuleByCapitalElementAndMake(Long celId, Long makId);
	
	@Query("SELECT cer FROM CapitalElementRule cer WHERE cer.capitalElement.celId = ?1 and  cer.supCId = ?2 and  cer.supAccountType = ?3 and  cer.supAccountCode = ?4")
	public CapitalElementRule findCERuleByCapitalElementAndSupplier(Long celId, Long supCId, String supAccountType, String supAccountCode);
	
}
