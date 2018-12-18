package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.CostCentreCodePK;

/**
* DAO 
* @author Lizak
*/

public interface CostCenterDAO extends CostCenterDAOCustom, JpaRepository<CostCentreCode, CostCentreCodePK> {
	
    @Query("select c from CostCentreCode c where c.costCentreCodePK.eaAccountCode = ?1 and c.costCentreCodePK.eaAccountType = ?2 and c.costCentreCodePK.eaCId = ?3 order by c.costCentreCodePK.costCentreCode")
	public List<CostCentreCode> findByAccount(String accountCode, String accountType, long cId);
    
    @Query("select c from CostCentreCode c where c.costCentreCodePK.eaAccountCode = ?1 and c.costCentreCodePK.eaAccountType = ?2 and c.costCentreCodePK.eaCId = ?3 ")
	public List<CostCentreCode> findByAccount(String accountCode, String accountType, long cId, Pageable pageable);

    @Query("select count(c.costCentreCodePK.costCentreCode) from CostCentreCode c where c.costCentreCodePK.eaAccountCode = ?1 and c.costCentreCodePK.eaAccountType = ?2 and c.costCentreCodePK.eaCId = ?3 order by c.costCentreCodePK.costCentreCode")
	public Long findByAccountCount(String accountCode, String accountType, long cId);   
    
    @Query("SELECT c FROM CostCentreCode c WHERE c.costCentreCodePK.eaAccountCode = ?1 AND c.costCentreCodePK.eaAccountType = ?2 AND c.costCentreCodePK.eaCId = ?3 AND c.costCentreCodePK.costCentreCode = ?4")
    public CostCentreCode findByAccountCostCenterCode(String accountCode, String accountType, long cId, String costCenterCode);    
	
}
