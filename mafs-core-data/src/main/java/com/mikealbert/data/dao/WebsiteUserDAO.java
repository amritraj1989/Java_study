package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.WebsiteUser;

import java.util.List;

/**
 * DAO for WebsiteUser Entity
 * 
 */
public interface WebsiteUserDAO extends JpaRepository<WebsiteUser, Long> {

	@Query("FROM WebsiteUser wu WHERE wu.externalAccount.externalAccountPK = ?1 and wu.websiteUserType = ?2 and wu.loginEnabled = 'Y' order by wu.username")
	public List<WebsiteUser> findEnabledUsersByAccountAndType(ExternalAccountPK externalAccountPK, String websiteUserType);

}