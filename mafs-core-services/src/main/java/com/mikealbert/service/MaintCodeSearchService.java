package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.vo.ProviderMaintCodeSearchCriteriaVO;
import com.mikealbert.data.vo.ProviderMaintCodeSearchResultsVO;
import com.mikealbert.data.vo.ServiceProviderDiscountVO;

public interface MaintCodeSearchService {
	
	public ProviderMaintCodeSearchResultsVO searchProviderMaintCodes(ProviderMaintCodeSearchCriteriaVO providerMaintCodeSearchCriteriaVO, Pageable pageable, Sort sort);
	
	public List<ServiceProviderDiscountVO> getServiceProviderDiscountList(String supplierName, String maintCode, String maintCategoryCode);
	
}
