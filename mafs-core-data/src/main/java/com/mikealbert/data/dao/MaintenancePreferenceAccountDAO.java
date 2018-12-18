package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MaintenancePreferenceAccount;

public interface MaintenancePreferenceAccountDAO extends JpaRepository<MaintenancePreferenceAccount, Long>{

	@Query("from MaintenancePreferenceAccount mpa where mpa.externalAccount.externalAccountPK.cId = ?1 and mpa.externalAccount.externalAccountPK.accountType =?2 and mpa.externalAccount.externalAccountPK.accountCode =?3 and mpa.activeFromDate <= CURRENT_DATE and (mpa.activeToDate IS NULL OR mpa.activeToDate >= CURRENT_DATE)")
	public MaintenancePreferenceAccount getMaintenancePreferenceAccountData(long cid, String accountType, String accountCode);
	
}
