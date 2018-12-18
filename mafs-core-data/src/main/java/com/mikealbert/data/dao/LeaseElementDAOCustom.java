package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import com.mikealbert.data.entity.LeaseElement;

public interface LeaseElementDAOCustom {

	List<LeaseElement> findAllFilterByFinanceTypeAndElementList(String elementNameParam, List<Long> listOfExcludedElements, Pageable pageable);
	public int findAllFilterByFinanceTypeAndElementListCount(String elementNameParam, List<Long> listOfExcludedElements);
	
	public List<LeaseElement> findAllMaintenanceLeaseElements();
	public List<LeaseElement> findInRateLeaseElements();	
	public List<LeaseElement> getLeaseElementByTypeAndProfile(String elementType, Long qprId);
}
