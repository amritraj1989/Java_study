package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.WebsiteUserAssociation;


/**
 * DAO for WebsiteUserAssociations Entity
 * 
 */
public interface WebsiteUserAssociationDAO extends JpaRepository<WebsiteUserAssociation, Long> {
	
	@Query("FROM WebsiteUserAssociation wua WHERE wua.associationId = ?1 and wua.userType = ?2")
	public WebsiteUserAssociation findAssociationByIdAndType(Long id, String userType);

}