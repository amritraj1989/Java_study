package com.mikealbert.service;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mikealbert.data.dao.ServiceProviderMaintenanceCodeDAO;
import com.mikealbert.data.vo.ProviderMaintCodeSearchCriteriaVO;
import com.mikealbert.data.vo.ProviderMaintCodeSearchResultsVO;
import com.mikealbert.data.vo.ServiceProviderDiscountVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

@Service("maintCodeSearchService")
public class MaintCodeSearchServiceImpl implements MaintCodeSearchService {

	@Resource ServiceProviderMaintenanceCodeDAO serviceProviderMaintenanceCodeDAO;
	
	@Override
	public ProviderMaintCodeSearchResultsVO searchProviderMaintCodes(
			ProviderMaintCodeSearchCriteriaVO providerMaintCodeSearchCriteriaVO,
			Pageable pageable, Sort sort) {
		
		try {
			
			if(MALUtilities.isNotEmptyString(providerMaintCodeSearchCriteriaVO.getServiceProvider())){
				if(!providerMaintCodeSearchCriteriaVO.getServiceProvider().endsWith("%") && !providerMaintCodeSearchCriteriaVO.getServiceProvider().matches("\\%")){
					String criteria = providerMaintCodeSearchCriteriaVO.getServiceProvider() + "%";
					providerMaintCodeSearchCriteriaVO.setServiceProvider(criteria);
				}
			}
			
			return serviceProviderMaintenanceCodeDAO.searchProviderMaintCodes(providerMaintCodeSearchCriteriaVO, pageable, sort);
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Maintenance Codes by Code or Description" }, ex);			
		}
		
	}
	
	public List<ServiceProviderDiscountVO> getServiceProviderDiscountList(String supplierName, String maintCode, String maintCategoryCode) {
		return serviceProviderMaintenanceCodeDAO.getServiceProviderDiscountList(supplierName, maintCode, maintCategoryCode);
	}

}
