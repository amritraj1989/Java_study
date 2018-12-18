package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MaintenancePreferenceAccount;

public interface MaintenancePreferencesDAO extends MaintenancePreferencesDAOCustom, JpaRepository<LeaseElement, Long> {
	@Query("from MaintenancePreferenceAccount where externalAccount.externalAccountPK.cId = ?1 "
			+ " and externalAccount.externalAccountPK.accountCode = ?2 "
			+ " and externalAccount.externalAccountPK.accountType = ?3"
			+ " and activeToDate is null")
	MaintenancePreferenceAccount	findByAccount(Long cId, String accountCode, String accountType);
}
