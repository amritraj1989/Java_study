package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.VehicleConfigGrouping;
import com.mikealbert.data.entity.VehicleConfigUpfitQuote;
import com.mikealbert.data.entity.VehicleConfiguration;
import com.mikealbert.data.vo.VehicleConfigurationSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleConfigurationSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;

public interface VehicleConfigurationService {
	public List<VehicleConfigurationSearchResultVO> findBySearchCriteria(VehicleConfigurationSearchCriteriaVO vehicleConfigurationSearchCriteriaVO, Pageable pageable, Sort sort) throws MalBusinessException;
	public int findBySearchCriteriaCount(VehicleConfigurationSearchCriteriaVO vehicleConfigurationSearchCriteriaVO) throws MalBusinessException;
	public String resolveSortByName(String columnName);
	public List<String> findDistinctYears(VehicleConfigurationSearchCriteriaVO searchCriteria);
	public List<String> findDistinctMakes(VehicleConfigurationSearchCriteriaVO searchCriteria);
	
	public VehicleConfiguration getVehicleConfiguration(Long vcfId);
	public VehicleConfiguration getVehicleConfigurationWithGroupingAndModel(Long vcfId);
	public List<VehicleConfigUpfitQuote> getVehicleConfigUpfitQuoteByUpfitterQuote(Long vcfId) ;
	public VehicleConfigGrouping getVehicleConfigGroupingWithUpfitQuotes(VehicleConfigGrouping vcg);
	
	public byte[]  getUploadedDocument(Long uploadId) throws MalException ;
	public VehicleConfiguration saveUpdateConfiguration(VehicleConfiguration vehicleConfiguration) throws MalException;
	
	public List<ExternalAccount> findVehicleConfigAccountByNameOrCode(Long cId, String accountType, String searchValue, Pageable page);
	public List<String> getVehicleConfigMfgCodes(String mfgCode, Pageable page);
	public List<String> getVehicleConfigTrims(String trim, Pageable page);
	public List<VehicleConfiguration> getActiveVehicleConfigurationsByAccount(ExternalAccountPK externalAccountPK);
}
