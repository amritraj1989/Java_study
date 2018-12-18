package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.vo.OrderProgressSearchCriteriaVO;
import com.mikealbert.data.vo.OrderProgressSearchResultVO;

public interface OrderProgressDAOCustom {
	public List<OrderProgressSearchResultVO> findUnits(OrderProgressSearchCriteriaVO criteria, PageRequest page, Sort sort);
	public int findUnitsCount(OrderProgressSearchCriteriaVO searchCriteria);
	
	public Long getManufacturerLeadTime(Long fmsId);
	public Long getPDILeadTime(Long fmsId);
	public Long getUnitLeadTimeByDocId(Long docId);		
	public Long getPurchaseOrderLeadTimeByDocId(Long docId);	
	public List<String> getPossibleStandardAccessoriesByDocId(Long docId);

}
