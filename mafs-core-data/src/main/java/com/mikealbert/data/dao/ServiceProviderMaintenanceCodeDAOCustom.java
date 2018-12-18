package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.vo.ProviderMaintCodeSearchCriteriaVO;
import com.mikealbert.data.vo.ProviderMaintCodeSearchResultsVO;
import com.mikealbert.data.vo.ServiceProviderDiscountVO;

public interface ServiceProviderMaintenanceCodeDAOCustom{
	ProviderMaintCodeSearchResultsVO searchProviderMaintCodes(ProviderMaintCodeSearchCriteriaVO providerMaintCodeSearchCriteriaVO, Pageable pageable, Sort sort);
	
	boolean isProviderMaintCodeAdded(String providerMaintCode, long parentProviderId, boolean excludeUnapproved);
	
	public List<ServiceProviderDiscountVO> getServiceProviderDiscountList(String SupplierName, String maintCode, String maintCodeCategory);
}
