package com.mikealbert.vision.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.vo.OrderProgressSearchCriteriaVO;
import com.mikealbert.data.vo.OrderProgressSearchResultVO;
import com.mikealbert.exception.MalException;

public interface OrderProgressService {
	static final String SORT_BY_UNIT_NO = "unitNo";
	static final String SORT_BY_YEAR = "year";
	static final String SORT_BY_MAKE = "make";
	static final String SORT_BY_MODEL = "model";
	static final String SORT_BY_TRIM = "trim";
	static final String SORT_BY_CLIENT = "accountName";
	static final String SORT_BY_CSS = "css";
	static final String SORT_BY_ETA = "currentETADate";
	
	public List<OrderProgressSearchResultVO> findUnits(OrderProgressSearchCriteriaVO searchCriteria, PageRequest page, Sort sort);
	public int findUnitsCount(OrderProgressSearchCriteriaVO searchCriteria);
	public String resolveSortByName(String columnName);
	public List<String> findDistinctYears(OrderProgressSearchCriteriaVO searchCriteria);
	public List<String> findDistinctMakes(OrderProgressSearchCriteriaVO searchCriteria);
	public String getOptionalAccessories(Long docId);
	public void saveETAandNotes(SupplierProgressHistory supplierProgressHistory, Long fmsId, String loggedInUserName, String adjustmentNote) throws MalException;
	
	public Long getManufacturerLeadTime(Long fmsId);
	public Long getPDILeadTime(Long fmsId);
	
	public List<String> getPossibleStandardAccessoriesByDocId(Long docId);
}
