package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.MakeDAO;
import com.mikealbert.data.dao.ModelMarkYearDAO;
import com.mikealbert.data.dao.OnbaseUploadedDocsDAO;
import com.mikealbert.data.dao.UpfitterQuoteDAO;
import com.mikealbert.data.dao.VehicleConfigGroupingDAO;
import com.mikealbert.data.dao.VehicleConfigModelDAO;
import com.mikealbert.data.dao.VehicleConfigUpfitterQuoteDAO;
import com.mikealbert.data.dao.VehicleConfigurationDAO;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.OnbaseUploadedDocs;
import com.mikealbert.data.entity.UpfitterQuote;
import com.mikealbert.data.entity.VehicleConfigGrouping;
import com.mikealbert.data.entity.VehicleConfigUpfitQuote;
import com.mikealbert.data.entity.VehicleConfiguration;
import com.mikealbert.data.vo.VehicleConfigurationSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleConfigurationSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.OnbaseArchivalService;
import com.mikealbert.service.OnbaseRetrievalService;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.service.enumeration.OnbaseIndexEnum;
import com.mikealbert.service.vo.OnbaseKeywordVO;
import com.mikealbert.util.MALUtilities;

@Service("vehicleConfigurationService")
public class VehicleConfigurationServiceImpl implements VehicleConfigurationService {

	@Resource VehicleConfigurationDAO vehicleConfigurationDAO;
	@Resource ModelMarkYearDAO modelMarkYearDAO;
	@Resource MakeDAO makeDAO;
	@Resource UpfitterQuoteDAO upfitterQuoteDAO;
	@Resource VehicleConfigUpfitterQuoteDAO vehicleConfigUpfitterQuoteDAO;
	@Resource VehicleConfigGroupingDAO vehicleConfigGroupingDAO;
	@Resource VehicleConfigModelDAO vehicleConfigModelDAO;
	@Resource OnbaseUploadedDocsDAO onbaseUploadedDocsDAO;
	@Resource OnbaseArchivalService onbaseArchivalService;
	@Resource OnbaseRetrievalService onbaseRetrievalService;
	
	private static final Map<String, String> SORT_BY_MAPPING = new HashMap<String, String>();

	static {
		SORT_BY_MAPPING.put("client", DataConstants.VEHICLE_CONFIG_SEARCH_SORT_CLIENT);
		SORT_BY_MAPPING.put("configDescription", DataConstants.VEHICLE_CONFIG_SEARCH_SORT_DESC);
	}

	@Override
	public List<VehicleConfigurationSearchResultVO> findBySearchCriteria(VehicleConfigurationSearchCriteriaVO vehicleConfigurationSearchCriteriaVO, Pageable pageable, Sort sort) throws MalBusinessException {
		return vehicleConfigurationDAO.searchVehicleConfiguration(vehicleConfigurationSearchCriteriaVO, pageable, sort);
	}

	@Override
	public int findBySearchCriteriaCount(VehicleConfigurationSearchCriteriaVO vehicleConfigurationSearchCriteriaVO) throws MalBusinessException {
		return vehicleConfigurationDAO.searchVehicleConfigurationCount(vehicleConfigurationSearchCriteriaVO);
	}

	public String resolveSortByName(String columnName) {
		return SORT_BY_MAPPING.get(columnName);
	}

	public List<String> findDistinctYears(VehicleConfigurationSearchCriteriaVO searchCriteria) {
		List<String> distinctYears = vehicleConfigModelDAO.findDistinctYearByCriteria();
		return distinctYears;
	}

	public List<String> findDistinctMakes(VehicleConfigurationSearchCriteriaVO searchCriteria) {
		List<String> distinctMakes = vehicleConfigModelDAO.findDistinctMakeByCriteria();
		return distinctMakes;
	}

	@Transactional
	public VehicleConfiguration getVehicleConfiguration(Long vcfId) {
		return vehicleConfigurationDAO.findById(vcfId).orElse(null);

	}

	@Transactional
	public VehicleConfiguration getVehicleConfigurationWithGroupingAndModel(Long vcfId) {
		VehicleConfiguration vehicleConfiguration;
		
		vehicleConfiguration = vehicleConfigurationDAO.findById(vcfId).orElse(null);
		vehicleConfiguration.getVehicleConfigModels().size();
		
		for(VehicleConfigGrouping configGrouping : vehicleConfiguration.getVehicleConfigGroupings()) {
			configGrouping.getVehicleConfigUpfitQuotes().size();
		}
		
		return vehicleConfiguration;
	}
	
	@Transactional
	public List<VehicleConfigUpfitQuote> getVehicleConfigUpfitQuoteByUpfitterQuote(Long vcfId) {
		return vehicleConfigUpfitterQuoteDAO.getVehicleConfigUpfitQuoteByUpfitterQuoteId(vcfId);

	}
	
	@Transactional(readOnly = true)
	public byte[]  getUploadedDocument(Long uploadId) throws MalException {
		
		List<OnbaseKeywordVO> keyWordVOList = new ArrayList<OnbaseKeywordVO>();
		keyWordVOList.add(new OnbaseKeywordVO (OnbaseIndexEnum.UPLOAD_ID.getName(), String.valueOf(uploadId)));
		return onbaseRetrievalService.getDoc(OnbaseDocTypeEnum.TYPE_VENDOR_QUOTE ,keyWordVOList);
		
	}

	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public VehicleConfiguration saveUpdateConfiguration(VehicleConfiguration vehicleConfiguration) throws MalException {
			
			List<OnbaseUploadedDocs> onbaseListForUpload  = new ArrayList<OnbaseUploadedDocs>(); 			
			 for (VehicleConfigUpfitQuote vehicleConfigUpfitQuote : vehicleConfiguration.getVehicleConfigGroupings().get(0).getVehicleConfigUpfitQuotes()) {					
					 UpfitterQuote upfitterQuote  =  vehicleConfigUpfitQuote.getUpfitterQuote();
					 if(upfitterQuote.getUfqId() == null){	//add new vendor quote on edit mode					 
						 UpfitterQuote upfitterQuoteSaved = upfitterQuoteDAO.save(upfitterQuote);		
						 vehicleConfigUpfitQuote.setUpfitterQuote(upfitterQuoteSaved);
						 for (OnbaseUploadedDocs onbaseUploadedDoc :upfitterQuote.getOnbaseUploadedDocs()){
							 onbaseUploadedDoc.setObdId(onbaseUploadedDocsDAO.getNextPK());
							 onbaseUploadedDoc.setObjectId(String.valueOf(upfitterQuoteSaved.getUfqId()));
							 onbaseUploadedDoc.setIndexKey(getIndexKey(onbaseUploadedDoc));

							 onbaseListForUpload.add(onbaseUploadedDoc);
						 }

					 }else{
						 // Upload the new document added in an existing vendor quote.
						  UpfitterQuote upfitterQuoteDB = upfitterQuoteDAO.findById(upfitterQuote.getUfqId()).orElse(null);
						  upfitterQuoteDB.setQuoteDate(upfitterQuote.getQuoteDate()); 
						  upfitterQuoteDB.setExpirationDate(upfitterQuote.getExpirationDate());		
						  upfitterQuoteDB.setDescription(upfitterQuote.getDescription());
						  vehicleConfigUpfitQuote.setUpfitterQuote(upfitterQuoteDB);
						  upfitterQuoteDB  = upfitterQuoteDAO.save(upfitterQuoteDB);
						  
						  if(!MALUtilities.isEmpty(upfitterQuote.getOnbaseUploadedDocs()) && upfitterQuote.getOnbaseUploadedDocs().size() > 0 ){
							  for(OnbaseUploadedDocs onbaseUploadedDocs : upfitterQuote.getOnbaseUploadedDocs()) {
								  if(onbaseUploadedDocs.getObdId() == null){
									  onbaseUploadedDocs.setObdId(onbaseUploadedDocsDAO.getNextPK());
									  onbaseUploadedDocs.setIndexKey(getIndexKey(onbaseUploadedDocs));
									  onbaseUploadedDocs.setObjectId(String.valueOf(upfitterQuote.getUfqId()));
										
									  onbaseListForUpload.add(onbaseUploadedDocs);
								  }
							  }
						  }
					 }
				 }
		if (!onbaseListForUpload.isEmpty()) {
			onbaseUploadedDocsDAO.saveAll(onbaseListForUpload);
		}
			 
		vehicleConfiguration = vehicleConfigurationDAO.save(vehicleConfiguration);
		
		if(onbaseListForUpload.size() > 0){			
			 onbaseArchivalService.archiveDocumentInOnBase(onbaseListForUpload);
		}
		
		return vehicleConfiguration;
	}

	private String getIndexKey(OnbaseUploadedDocs onbaseUploadedDocs) {
		
		String indexKey = OnbaseDocTypeEnum.TYPE_VENDOR_QUOTE.getValue()+ "|"
				+ onbaseUploadedDocs.getObdId()+ "|"
				+ onbaseUploadedDocs.getFileType()+ "|"
				+ onbaseUploadedDocs.getUpfitterQuote().getExternalAccount().getAccountName() + "|"
				+ onbaseUploadedDocs.getUpfitterQuote().getExternalAccount().getExternalAccountPK().getAccountCode() + "|"
				+ onbaseUploadedDocs.getUpfitterQuote().getQuoteNumber() + "|"
				+ MALUtilities.getNullSafeDatetoString(new Date());
		
		return indexKey;
	}
	
	public List<ExternalAccount> findVehicleConfigAccountByNameOrCode(Long cId, String accountType, String searchValue, Pageable page) {
		/*   "(\\%)?    1.Optional Wildcard
		 *   +\\d*      2.Required Digit
		 *   +(\\d*)?   3.Optional Digit
		 *   In order to search by code, there must be a digit. Wildcards can occur anywhere 
		 *   between digits. Wildcards can occur before the digit string and after. Client codes are 8 digits long*/
		if(searchValue.matches("(\\%)?+\\d*+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?")) {
			return findVehicleConfigAccountsByCode(cId, accountType, searchValue, page);
		} else {
			return findVehicleConfigAccounts(cId, accountType, searchValue, page);
		}
	}
	
	public List<ExternalAccount> findVehicleConfigAccountsByCode(Long cId, String accountType, String accountCode, Pageable page) {
		try {
			accountCode = "%" + accountCode.toLowerCase() + "%";
			
			if (accountType.equals(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER)) {
				return vehicleConfigurationDAO.getVehicleConfigurationClientsByCode(cId, accountType, accountCode, page);
			} else if(accountType.equals(ExternalAccountDAO.ACCOUNT_TYPE_SUPPLIER)) {
				return vehicleConfigurationDAO.getVehicleConfigurationVendorsByCode(cId, accountType, accountCode, page);
			} else {
				return null;
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Code" }, ex);			
		}
	}
	
	public List<ExternalAccount> findVehicleConfigAccounts(Long cId, String accountType, String accountName, Pageable page) {
		try {
			accountName = "%" + accountName.toLowerCase() + "%";
			
			if (accountType.equals(ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER)) {
				return vehicleConfigurationDAO.getVehicleConfigurationClientsByName(cId, accountType, accountName, page);
			} else if(accountType.equals(ExternalAccountDAO.ACCOUNT_TYPE_SUPPLIER)) {
				return vehicleConfigurationDAO.getVehicleConfigurationVendorsByName(cId, accountType, accountName, page);
			} else {
				return null;
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Accounts by Account Name" }, ex);			
		}
	}
	
	public List<String> getVehicleConfigMfgCodes(String mfgCode, Pageable page) {
		try {
			mfgCode = "%" + mfgCode.toLowerCase() + "%";
			return vehicleConfigurationDAO.getVehicleConfigurationMfgCodes(mfgCode, page);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Manufacturers" }, ex);			
		}
	}
	
	public List<String> getVehicleConfigTrims(String trim, Pageable page) {
		try {
			trim = "%" + trim.toLowerCase() + "%";
			return vehicleConfigurationDAO.getVehicleConfigurationTrims(trim, page);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding Trims" }, ex);			
		}
	}

	public List<VehicleConfiguration> getActiveVehicleConfigurationsByAccount(ExternalAccountPK externalAccountPK) {
		return vehicleConfigurationDAO.getActiveVehicleConfigurationsByAccount(externalAccountPK);
	}

	@Override
	@Transactional
	public VehicleConfigGrouping getVehicleConfigGroupingWithUpfitQuotes(VehicleConfigGrouping vcg) {
		VehicleConfigGrouping vehCfgGrouping;
		vehCfgGrouping = vehicleConfigGroupingDAO.findById(vcg.getVcgId()).orElse(null);
		if(vehCfgGrouping != null) {
			vehCfgGrouping.getVehicleConfigUpfitQuotes().size();			
		}
		return vehCfgGrouping;
	}

}
