package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.LeaseElement;

/**
* DAO for LeaseElement Entity
* @author Singh
*/

public interface LeaseElementDAO extends JpaRepository<LeaseElement, Long>, LeaseElementDAOCustom {
	
	@Query("SELECT le FROM LeaseElement le WHERE le.elementName = ?1")	
	public List<LeaseElement> findByLeaseElementName(String elementName);	
	
	@Query("FROM LeaseElement le WHERE lower(le.elementName) = lower(?1)")
	public LeaseElement getLeaseElementByName(String elementName);
}
