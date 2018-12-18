package com.mikealbert.service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.ClientAgreementDAO;
import com.mikealbert.data.dao.ClientContractServiceElementDAO;
import com.mikealbert.data.dao.ClientServiceElementParameterDAO;
import com.mikealbert.data.dao.ClientServiceElementTypeDAO;
import com.mikealbert.data.dao.ClientServiceElementsDAO;
import com.mikealbert.data.dao.ContractAgreementDAO;
import com.mikealbert.data.dao.DriverGradeDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.ExternalAccountGradeGroupDAO;
import com.mikealbert.data.dao.FormulaParameterDAO;
import com.mikealbert.data.dao.InformalAmendmentDAO;
import com.mikealbert.data.dao.LeaseElementDAO;
import com.mikealbert.data.dao.MulQuoteEleDAO;
import com.mikealbert.data.dao.MulQuoteOptDAO;
import com.mikealbert.data.dao.ProductDAO;
import com.mikealbert.data.dao.QuotationElementDAO;
import com.mikealbert.data.dao.TaxCodeDAO;
import com.mikealbert.data.entity.ClientAgreement;
import com.mikealbert.data.entity.ClientContractServiceElement;
import com.mikealbert.data.entity.ClientServiceElement;
import com.mikealbert.data.entity.ClientServiceElementParameter;
import com.mikealbert.data.entity.ContractAgreement;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountGradeGroup;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FormulaParameter;
import com.mikealbert.data.entity.InformalAmendment;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MulQuoteEle;
import com.mikealbert.data.entity.MulQuoteOpt;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.entity.QuotationSchedule;
import com.mikealbert.data.enumeration.ClientServiceElementTypeCodes;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.ServiceElementOperations;
import com.mikealbert.data.util.QuotationScheduleTransDateComparator;
import com.mikealbert.data.vo.ClientServiceElementParameterVO;
import com.mikealbert.data.vo.ServiceElementsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;


@Service("serviceElementService")
public class ServiceElementServiceImpl implements ServiceElementService {
	
	@Resource ClientAgreementDAO clientAgreementDAO;
	@Resource ClientServiceElementsDAO clientServiceElementsDAO;
	@Resource ClientContractServiceElementDAO clientContractServiceElementDAO;
	@Resource ContractAgreementDAO contractAgreementDAO;
	@Resource CustomerAccountService customerAccountService;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource FormulaParameterDAO formulaParameterDAO;
	@Resource FinanceParameterService financeParameterService;
	@Resource ClientServiceElementParameterDAO clientServiceElementParameterDAO;
	@Resource LeaseElementDAO leaseElementDAO;
	@Resource ClientServiceElementTypeDAO clientServiceElementTypeDAO;
	@Resource ExternalAccountGradeGroupDAO externalAccountGradeGroupDAO;
	@Resource DriverGradeDAO driverGradeDAO;
	@Resource QuotationElementService quoteElementService;
	@Resource ProductDAO productDAO;
	@Resource MulQuoteEleDAO mulQuoteEleDao;
	@Resource MulQuoteOptDAO mulQuoteOptDao;
	@Resource TaxCodeDAO taxCodeDao;
	@Resource QuotationElementDAO quotationElementDao;
	@Resource FleetMasterService fleetMasterService;
	@Resource DriverAllocationService driverAllocationService;
	@Resource QuotationService quotationService;
	@Resource InformalAmendmentDAO informalAmendmentDAO;
	@Resource MaintenanceScheduleService maintenanceScheduleService;
	
	public List<ClientAgreement> getClientAgreementsByClient(Long cId, String accountType, String accountCode){
		List<ClientAgreement> clientAgreementList = new ArrayList<ClientAgreement>();
		try{
			clientAgreementList = clientAgreementDAO.findByExternalAccount(cId, accountType, accountCode);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding client agreements by client" }, ex);
		}
		return clientAgreementList;
	}
	
	public List<ClientContractServiceElement> getClientContractServiceElementsByClient(Long cId, String accountType, String accountCode){
		List<ClientContractServiceElement> clientContractServiceElementList = new ArrayList<ClientContractServiceElement>();
		try{
			clientContractServiceElementList = clientContractServiceElementDAO.findByExternalAccount(cId, accountType, accountCode);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding contracted service elements by client" }, ex);
		}
		return clientContractServiceElementList;
	}
	
	@Override
	public List<ClientServiceElement> getClientServiceElementsByClient(Long cId, String accountType, String accountCode) {
		List<ClientServiceElement> clientServiceElements = new ArrayList<ClientServiceElement>();
		try {
			clientServiceElements = clientServiceElementsDAO.getClientServiceElementsByAccount(cId, accountType, accountCode);
			/*for (ClientServiceElement cse : clientServiceElements){
				cse.setBillingOption(convertBillingOptionToReadableString(cse.getBillingOption()));
			}*/			
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding All Client Service Elements By Client" }, ex);
		}
		return clientServiceElements;
	}
	
	@Override
	public List<ClientServiceElement> getProductServiceElementsByAccount(Long cId, String accountType, String accountCode){
		List<ClientServiceElement> clientServiceElements = new ArrayList<ClientServiceElement>();
		try {
			clientServiceElements = clientServiceElementsDAO.getClientProductServiceElementsByAccount(cId, accountType, accountCode);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding All Client Service Elements By Client and Product" }, ex);
		}
		return clientServiceElements;	
	}
	
	@Override
	public List<ClientServiceElement> getGradeGroupProductServiceElementsByAccountAndGradeGroup(Long cId, String accountType, String accountCode, Long gradeGroupId){
	List<ClientServiceElement> clientServiceElements = new ArrayList<ClientServiceElement>();
	try {
		clientServiceElements = clientServiceElementsDAO.getGradeGroupProductServiceElementsByAccountAndGradeGroup(cId, accountType, accountCode, gradeGroupId);
	} catch(Exception ex) {
		throw new MalException("generic.error.occured.while", new String[] { "finding All Client Service Elements By Client and Product" }, ex);
	}
	return clientServiceElements;	
}	
	
	@Override
	public List<ClientServiceElement> getProductServiceElementsByAccountIncludeRemoved(Long cId, String accountType, String accountCode){
		List<ClientServiceElement> clientServiceElements = new ArrayList<ClientServiceElement>();
		try {
			clientServiceElements = clientServiceElementsDAO.getClientProductServiceElementsByAccountIncludeRemoved(cId, accountType, accountCode);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding all Client Service Element Product Overrides" }, ex);
		}
		return clientServiceElements;	
	}	
	
	@Override
	public List<ClientServiceElement> getGradeGroupServiceElementsByClientAndGradeGroup(Long cId, String accountType, String accountCode, Long gradeGroupId) {
		List<ClientServiceElement> gradeGroupServiceElements = new ArrayList<ClientServiceElement>();
		try{
			gradeGroupServiceElements = clientServiceElementsDAO.getClientServiceElementsByAccountAndGradeGroup(cId, accountType, accountCode, gradeGroupId);
			for (ClientServiceElement ggse : gradeGroupServiceElements){
				ggse.setBillingOption(convertBillingOptionToReadableString(ggse.getBillingOption()));
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Grade Group Service Elements By Client and Grade Group" }, ex);
		}
		return gradeGroupServiceElements;
	}
	
	@Override
	public List<ClientServiceElement> getGradeGroupServiceElementsByClientAndGradeGroupWRemoved(Long cId, String accountType, String accountCode, Long gradeGroupId) {
		List<ClientServiceElement> gradeGroupServiceElements = new ArrayList<ClientServiceElement>();
		try{
			gradeGroupServiceElements = clientServiceElementsDAO.getClientServiceElementsByAccountAndGradeGroupWRemoved(cId, accountType, accountCode, gradeGroupId);
			for (ClientServiceElement ggse : gradeGroupServiceElements){
				ggse.setBillingOption(convertBillingOptionToReadableString(ggse.getBillingOption()));
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Grade Group Service Elements By Client and Grade Group With Removed" }, ex);
		}
		return gradeGroupServiceElements;
	}	
	
	@Override
	public ClientServiceElement getGradeGroupServiceElementByGradeGroupAndElement(Long cId, String accountType, String accountCode, Long gradeGroupId, Long leaseElementId){	
		ClientServiceElement clientServiceElement = new ClientServiceElement();
		try {
			clientServiceElement = clientServiceElementsDAO.getGradeGroupServiceElementByGradeGroupAndElement(cId, accountType, accountCode, gradeGroupId, leaseElementId);
			//for the case of an element being removed from the details page with the REMOVE link
			if (clientServiceElement == null){
				clientServiceElement = clientServiceElementsDAO.getGradeGroupServiceElementByGradeGroupAndElementWRemoved(cId, accountType, accountCode, gradeGroupId, leaseElementId);
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Grade Group Service Element By Grade Group and Lease Element" }, ex);
		}
		return clientServiceElement;
	}

	@Override
	public ExternalAccountGradeGroup getEagByExternalAccountCodeAndType(Long cId, String accountType, String accountCode, String gradeGroup) {
		ExternalAccountGradeGroup externalAccountGradeGroup = new ExternalAccountGradeGroup();
		try {
			externalAccountGradeGroup = externalAccountGradeGroupDAO.getEagByExternalAccountCodeAndType(cId, accountType, accountCode, gradeGroup);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding External Account Grade Group" }, ex);
		}
		return externalAccountGradeGroup;
	}	
	
	@Override
	public List<ClientServiceElement> getClientServiceElementsHistoryByClient(Long cId, String accountType, String accountCode, Long clientServiceElementTypeId) {
		List<ClientServiceElement> clientServiceElementsHistory = new ArrayList<ClientServiceElement>();
		try {
			clientServiceElementsHistory = clientServiceElementsDAO.getClientServiceElementsHistoryByAccount(cId, accountType, accountCode, clientServiceElementTypeId);
			for (ClientServiceElement cse : clientServiceElementsHistory){
				cse.setBillingOption(convertBillingOptionToReadableString(cse.getBillingOption()));
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Client Service Element History By Client" }, ex);
		}
		return clientServiceElementsHistory;
	}	
	
	@Override
	public List<ClientServiceElement> getGradeGroupServiceElementsHistoryByClient(Long cId, String accountType, String accountCode, Long clientServiceElementTypeId) {
		List<ClientServiceElement> gradeGroupServiceElementsHistory = new ArrayList<ClientServiceElement>();
		try {
			gradeGroupServiceElementsHistory = clientServiceElementsDAO.getGradeGroupServiceElementsHistoryByAccount(cId, accountType, accountCode, clientServiceElementTypeId);
			for (ClientServiceElement ggse : gradeGroupServiceElementsHistory){
				ggse.setBillingOption(convertBillingOptionToReadableString(ggse.getBillingOption()));
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Grade Group Service Element History By Client" }, ex);
		}
		return gradeGroupServiceElementsHistory;
	}		

	@Override
	public ClientServiceElement getClientServiceElementByClientAndElement(Long cId, String accountType, String accountCode,Long leaseElementId) {
		ClientServiceElement clientServiceElement = new ClientServiceElement();
		try {
			clientServiceElement = clientServiceElementsDAO.getClientServiceElementByAccountAndElement(cId, accountType, accountCode, leaseElementId);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Client Service Element By Client and Element" }, ex);
		}
		return clientServiceElement;
	}	
	
	@Override
	public ClientServiceElement getClientProductServiceElementByClientAndElement(Long cId, String accountType, String accountCode,Long leaseElementId, String productCode) {
		ClientServiceElement clientServiceElement = new ClientServiceElement();
		try {
			clientServiceElement = clientServiceElementsDAO.getClientProductServiceElementByAccountAndElementAndProduct(cId, accountType, accountCode, leaseElementId, productCode);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Client Product Service Element By Client and Element" }, ex);
		}
		return clientServiceElement;
	}	
	
	@Override
	public ClientServiceElement getGradeGroupProductServiceElementByGradeGroupAndElement(Long cId, String accountType, String accountCode, Long gradeGroupId, Long leaseElementId, String productCode) {
		ClientServiceElement clientServiceElement = new ClientServiceElement();
		try {
			clientServiceElement = clientServiceElementsDAO.getGradeGroupProductServiceElementByGradeGroupAndElementAndProduct(cId, accountType, accountCode, gradeGroupId, leaseElementId, productCode);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Grade Group Product Service Element By Client and Element" }, ex);
		}
		return clientServiceElement;
	}	
	
	@Override
	public ClientServiceElement getGradeGroupProductServiceElementByGradeGroupAndElementWRemoved(Long cId, String accountType, String accountCode, Long gradeGroupId, Long leaseElementId, String productCode) {
		ClientServiceElement clientServiceElement = new ClientServiceElement();
		try {
			clientServiceElement = clientServiceElementsDAO.getGradeGroupProductServiceElementByGradeGroupElementAndProductWRemoved(cId, accountType, accountCode, gradeGroupId, leaseElementId, productCode);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Grade Group Product Service Element By Element and Product with Removed " }, ex);
		}
		return clientServiceElement;
	}	
	
	@Override
	public ClientServiceElement getClientProductServiceElementByGradeGroupAndElementWRemoved(Long cId, String accountType, String accountCode, Long leaseElementId, String productCode) {
		ClientServiceElement clientServiceElement = new ClientServiceElement();
		try {
			clientServiceElement = clientServiceElementsDAO.getClientProductServiceElementByElementAndProductWRemoved(cId, accountType, accountCode, leaseElementId, productCode);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Client Product Service Element By Element and Product with Removed " }, ex);
		}
		return clientServiceElement;
	}		
	
	@Override
	public List<ClientServiceElement> getClientServiceElementsByClientAndElementIncludeRemoved(Long cId, String accountType, String accountCode,Long leaseElementId) {
		List<ClientServiceElement> clientServiceElements = new ArrayList<ClientServiceElement>();
		try {
			clientServiceElements = clientServiceElementsDAO.getClientServiceElementsByAccountAndElementIncludeRemoved(cId, accountType, accountCode, leaseElementId);
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Client and Product Removed Service Elements" }, ex);
		}
		return clientServiceElements;
	}	

	@Override
	public List<ContractAgreement> getAllContractAgreements(){
		List<ContractAgreement> contractAgreementList = new ArrayList<ContractAgreement>();
		try{
			contractAgreementList = contractAgreementDAO.getAllContractAgreements();
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding all contract agreements" }, ex);
		}
		return contractAgreementList;
	}

	@Transactional(rollbackFor = MalBusinessException.class)
	public void saveUpdateClientServiceElements(List<ClientServiceElement> clientServiceElementList, String parameterType) throws MalBusinessException {
		// TODO: also look for removed elements
		if(!clientServiceElementList.isEmpty()){
			for (ClientServiceElement clientServiceElement : clientServiceElementList) {
				if (parameterType.equals(ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode())){
					clientServiceElement.setClientServiceElementType(clientServiceElementTypeDAO.findById(ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getId()).orElse(null));
				} else {
					clientServiceElement.setClientServiceElementType(clientServiceElementTypeDAO.findById(ClientServiceElementTypeCodes.ACCOUNT.getId()).orElse(null));
				}
				if (!MALUtilities.isEmpty(clientServiceElement.getEndDate())) {
					clientServiceElementParameterDAO.deleteAll(findByClientServiceElementId(clientServiceElement.getClientServiceElementId()));
				}
			}
			clientServiceElementsDAO.saveAll(clientServiceElementList);
		}		
	}
	
	@Override
	public ExternalAccount getRootParentAccount(Long cId, String accountType, String accountCode) {
		ExternalAccount currentAccount = externalAccountDAO.findById(new ExternalAccountPK(cId, accountType, accountCode)).orElse(null);
		while (!MALUtilities.isEmpty(currentAccount.getParentExternalAccount())) {
			currentAccount = externalAccountDAO.findById(currentAccount.getParentExternalAccount().getExternalAccountPK()).orElse(null);
		}
		
		return currentAccount;
	}

	
	@Transactional
	public void saveOrUpdateClientAgreement(ClientAgreement clientAgreement, ContractAgreement contractAgreement){
		clientAgreement.setContractAgreement(contractAgreementDAO.findById(contractAgreement.getAgreementCode()).orElse(null));
		clientAgreementDAO.saveAndFlush(clientAgreement);
	}
	
	@Transactional
	public void saveOrUpdateClientContractServiceElement(ClientContractServiceElement clientContractServiceElement, LeaseElement serviceElement){
		clientContractServiceElement.setLeaseElement(serviceElement);
		clientContractServiceElement.setClientAgreement(clientAgreementDAO.findById(clientContractServiceElement.getClientAgreement().getClientAgreementId()).orElse(null));
		clientContractServiceElementDAO.saveAndFlush(clientContractServiceElement);
	}
	
	@Override
	public List<ClientServiceElementParameterVO> getServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId) {
		return clientServiceElementParameterDAO.getServiceElementParametersByClientServiceElement(leaseElementId, clientServiceElementId);
	}
	
	@Override
	public List<ClientServiceElementParameterVO> getGradeGroupServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId) {
		return clientServiceElementParameterDAO.getGradeGroupServiceElementParametersByClientServiceElement(leaseElementId, clientServiceElementId);
	}
	
	@Override
	public List<ClientServiceElementParameterVO> getGradeGroupProductServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId) {
		return clientServiceElementParameterDAO.getGradeGroupProductServiceElementParametersByClientServiceElement(leaseElementId, clientServiceElementId);
	}	
	
	@Override
	public List<ClientServiceElementParameterVO> getProductServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId) {
		return clientServiceElementParameterDAO.getProductServiceElementParametersByClientServiceElement(leaseElementId, clientServiceElementId);
	}	

	@Override
	public BigDecimal getParameterClientValuesSum(Long clientServiceElementId) {
		return clientServiceElementParameterDAO.getParameterClientValuesSum(clientServiceElementId);
	}
	
	@Override
	public BigDecimal getParameterDefaultValuesSum(Long leaseElementId, Long clientServiceElementId) {
		return clientServiceElementParameterDAO.getParameterDefaultValuesSum(leaseElementId, clientServiceElementId);
	}

	@Transactional
	public void saveOrUpdateClientServiceElementParameters(List<ClientServiceElementParameterVO> clientServiceElementParameterVOList, Boolean productOverride) {
		Boolean deleteValues = false;
		int notNullValuesCount = 0;
		
		List <ClientServiceElementParameter> csepList = new ArrayList<ClientServiceElementParameter>();
		ClientServiceElementParameter csep = null;
		
		for (ClientServiceElementParameterVO csepVO : clientServiceElementParameterVOList) {
			if (MALUtilities.isEmpty(csepVO.getClientServiceElementParameterId())) {
				csep = new ClientServiceElementParameter();
				csep.setClientServiceElement(clientServiceElementsDAO.findById(csepVO.getClientServiceElementId()).orElse(null));
				csep.setFormulaParameter(formulaParameterDAO.findById(csepVO.getFormulaParameterId()).orElse(null));
			} else {
				csep = clientServiceElementParameterDAO.findById(csepVO.getClientServiceElementParameterId()).orElse(null);
			}
			
			if (productOverride){
				if (!MALUtilities.isEmpty(csepVO.getProductValue())) {
					csep.setValue(csepVO.getProductValue().setScale(5, BigDecimal.ROUND_HALF_UP));
					notNullValuesCount ++;
				} else {
					csep.setValue(null);
				}
			}
			else { //not a product override parameter value change
				if (!MALUtilities.isEmpty(csepVO.getClientValue())) {
					csep.setValue(csepVO.getClientValue().setScale(5, BigDecimal.ROUND_HALF_UP));
					notNullValuesCount ++;
				} else {
					csep.setValue(null);
				}
			}
			csepList.add(csep);
		}
		
		if (notNullValuesCount == 0) {
			deleteValues = true;
		}
		
		if (!deleteValues) {
			clientServiceElementParameterDAO.saveAll(csepList);
		} else {
			clientServiceElementParameterDAO.deleteAll(csepList);
		}
		
	}

	@Transactional
	public void saveOrUpdateGradeGroupServiceElementParameters(List<ClientServiceElementParameterVO> clientServiceElementParameterVOList, Boolean productOverride) {
		Boolean deleteValues = false;
		int notNullValuesCount = 0;
		
		List <ClientServiceElementParameter> csepList = new ArrayList<ClientServiceElementParameter>();
		ClientServiceElementParameter csep = null;
		
		for (ClientServiceElementParameterVO csepVO : clientServiceElementParameterVOList) {
			if (MALUtilities.isEmpty(csepVO.getClientServiceElementParameterId())) {
				csep = new ClientServiceElementParameter();
				csep.setClientServiceElement(clientServiceElementsDAO.findById(csepVO.getClientServiceElementId()).orElse(null));
				csep.setFormulaParameter(formulaParameterDAO.findById(csepVO.getFormulaParameterId()).orElse(null));
			} else {
				csep = clientServiceElementParameterDAO.findById(csepVO.getClientServiceElementParameterId()).orElse(null);
			}
			
			if (productOverride){
				if (!MALUtilities.isEmpty(csepVO.getProductValue())) {
					csep.setValue(csepVO.getProductValue().setScale(5, BigDecimal.ROUND_HALF_UP));
					notNullValuesCount ++;
				} else {
					csep.setValue(null);
				}
			}
			else { //not a product override parameter value change			
				if (!MALUtilities.isEmpty(csepVO.getGradeGroupValue())) {
					csep.setValue(csepVO.getGradeGroupValue().setScale(5, BigDecimal.ROUND_HALF_UP));
					notNullValuesCount ++;
				} else {
					csep.setValue(null);
				}
			}
			csepList.add(csep);
		}
		
		if (notNullValuesCount == 0) {
			deleteValues = true;
		}
		
		if (!deleteValues) {
			clientServiceElementParameterDAO.saveAll(csepList);
		} else {
			clientServiceElementParameterDAO.deleteAll(csepList);
		}
		
	}
	
	public List<LeaseElement> findAllFilterByFinanceTypeAndElementList(String elementNameParam, List<Long> listOfExcludedElements, Pageable pageable){
		List<LeaseElement> leaseElementList = new ArrayList<LeaseElement>();
		try{
			leaseElementList = leaseElementDAO.findAllFilterByFinanceTypeAndElementList(elementNameParam, listOfExcludedElements, pageable);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding lease elements" }, ex);
		}
		return leaseElementList;
	}
	public int findAllFilterByFinanceTypeAndElementListCount(String elementNameParam, List<Long> listOfExcludedElements){
		int count = 0;
		try{
			count = leaseElementDAO.findAllFilterByFinanceTypeAndElementListCount(elementNameParam, listOfExcludedElements);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding number of lease element records" }, ex);
		}
		return count;
	}
	
	public long findByAgreementNumberCount(String agreementNumber, Long clientAgreementId){
		long count = 0;
		try{
			if(clientAgreementId == null){
				clientAgreementId = -1L;
			}
			count = clientAgreementDAO.findByAgreementNumberCount(agreementNumber, clientAgreementId);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding number of client agreements with the agreement number of: " + agreementNumber }, ex);
		}
		return count;
	}
	
	public ClientAgreement getClientAgreementByAgreementNumber(String agreementNumber){
		ClientAgreement clientAgreement = new ClientAgreement();
		try{
			clientAgreement = clientAgreementDAO.findByAgreementNumber(agreementNumber);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding client agreement with the agreement number of: " + agreementNumber }, ex);
		}
		return clientAgreement;
	}
	
	public LeaseElement getLeaseElementByName(LeaseElement leaseElement){
		LeaseElement returnLeaseElement = new LeaseElement();
		try{
			returnLeaseElement = leaseElementDAO.getLeaseElementByName(leaseElement.getElementName());
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding lease element by name " + leaseElement.getElementName() }, ex);
		}
		return returnLeaseElement;
	}
	
	public long getClientAgreementByContractAgreementAndClientCount(String contractAgreement, Long cId, String accountType, String accountCode){
		long count = 0;
		try{
			count = clientAgreementDAO.findByContractAgreementAndClientCount(contractAgreement, cId, accountType, accountCode);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding number of client agreements with the contract agreement of " + contractAgreement }, ex);
		}
		return count;
	}
	
	public long getContractElementByClientElementNameAndClientCount(String elementName, Long cId, String accountType, String accountCode){
		long count = 0;
		try{
			count = clientContractServiceElementDAO.findByElementNameAndClientCount(elementName, cId, accountType, accountCode);
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding number of client contract service elements with the element name of " + elementName }, ex);
		}
		return count;
	}

	@Override
	public ClientServiceElementParameter getClientServiceElementParameter(Long clientServiceElementId, Long formulaParameterId) {
		return clientServiceElementParameterDAO.getClientServiceElementParameter(clientServiceElementId, formulaParameterId);
	}
	
	@Override
	public String convertBillingOptionToReadableString(String billingOption){
		if(billingOption.equalsIgnoreCase("MONTHLY")){
			return "Monthly";
		}else{
			return "Per Occurrence";
		}
	}

	@Override
	public List<ClientServiceElementParameter> findByClientServiceElementId(Long clientServiceElementId) {
		return clientServiceElementParameterDAO.findByClientServiceElementId(clientServiceElementId);
	}
	
	@Override
	public List<ServiceElementsVO> getGradeGroupServiceElements(Long cId, String accountType, String accountCode) {
		List<ServiceElementsVO> gradeGroupServiceElementList = new ArrayList<ServiceElementsVO>();
		List<ExternalAccountGradeGroup> gradeGroupList = externalAccountGradeGroupDAO.getGradeGroupByClient(cId, accountType, accountCode);
		
		ServiceElementsVO gradeGroupElements;
		for (ExternalAccountGradeGroup eaGradeGroup : gradeGroupList) {
			gradeGroupElements = new ServiceElementsVO();
			gradeGroupElements.setEagId(eaGradeGroup.getEagId());
			gradeGroupElements.setEaCId(cId);
			gradeGroupElements.setEaAccountType(accountType);
			gradeGroupElements.setEaAccountCode(accountCode);
			gradeGroupElements.setGradeGroupCode(eaGradeGroup.getDriverGradeGroup().getDriverGradeGroup());
			gradeGroupElements.setGradeDescription(eaGradeGroup.getDriverGradeGroup().getDescription());
			gradeGroupElements.setServiceElementList(dedupeRemovedClientElements(clientServiceElementsDAO.getClientServiceElementsByAccountAndGradeGroupWRemoved(cId, accountType, accountCode, eaGradeGroup.getEagId())));
			gradeGroupElements.setProductServiceElementList(clientServiceElementsDAO.getGradeGroupProductServiceElementsByGradeGroupIncludeRemoved(cId, accountType, accountCode, eaGradeGroup.getEagId()));
			gradeGroupServiceElementList.add(gradeGroupElements);
		}
		
		return gradeGroupServiceElementList;
	}
	
	/***
	 * This method is used to de-dupe a list of client service elements that have been flagged as "Removed" and re-added back on after that.
	 * @param clientsElements
	 * @return List<ClientServiceElement>
	 */
	private List<ClientServiceElement> dedupeRemovedClientElements(List<ClientServiceElement> clientsElements){
		Map<Long,ClientServiceElement> dedupingMap = new HashMap<Long,ClientServiceElement>();
		List<ClientServiceElement> dedupedList;
		for(ClientServiceElement clientElement : clientsElements){
			
			// if there is already an element for the lease element id
			if(dedupingMap.containsKey(clientElement.getClientContractServiceElement().getLeaseElement().getLelId())){
				// if the end date is not set in the clientElement we are looking at, replace the current
				if(MALUtilities.isEmpty(clientElement.getEndDate())){
					dedupingMap.put(clientElement.getClientContractServiceElement().getLeaseElement().getLelId(), clientElement);
				// if the one in the map has a non-empty end date and if the end date set is greater in clientElement that the one currently in the map 
				// (this is optional but makes sense; we may show additional detail on it someday)	
				}else if((!MALUtilities.isEmpty(dedupingMap.get(clientElement.getClientContractServiceElement().getLeaseElement().getLelId()).getEndDate()))  
						&&  clientElement.getEndDate().after(dedupingMap.get(clientElement.getClientContractServiceElement().getLeaseElement().getLelId()).getEndDate())){
					dedupingMap.put(clientElement.getClientContractServiceElement().getLeaseElement().getLelId(), clientElement);
				}
				// otherwise leave it
			}else{ // else put it in the map
				dedupingMap.put(clientElement.getClientContractServiceElement().getLeaseElement().getLelId(), clientElement);
			}
		}
		dedupedList = new ArrayList<ClientServiceElement>(dedupingMap.values());

		//order by element name ascending
		if(dedupedList != null && dedupedList.size() > 0){
			Collections.sort(dedupedList, new Comparator<ClientServiceElement>() {
				public int compare(ClientServiceElement cse1, ClientServiceElement cse2) {
					String name1 = cse1.getClientContractServiceElement().getLeaseElement().getElementName();
					String name2 = cse2.getClientContractServiceElement().getLeaseElement().getElementName();
					return name1.toLowerCase().compareTo(name2.toLowerCase());						
				}
			});			
		}		
		
		return dedupedList;
	}

	@Override
	public ClientContractServiceElement getClientContractServiceElement(
			Long clientContractServiceElementId) {
		try {
			ClientContractServiceElement contractServiceElement = clientContractServiceElementDAO.findById(clientContractServiceElementId).orElse(null);
			return contractServiceElement;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MalException("generic.error.occured.while", new String[] { "finding a contracted client service element by Id" }, ex);
		}
	}

	@Override
	public ClientServiceElement getClientServiceElement(
			Long clientServiceElementId) {
		try {
			
			ClientServiceElement clientServiceElement = clientServiceElementsDAO.findById(clientServiceElementId).orElse(null);
			return clientServiceElement;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MalException("generic.error.occured.while", new String[] { "finding a client service element by Id" }, ex);
		}
	}

	@Override
	public ClientServiceElement constructClientServiceElement(
			ClientContractServiceElement contractedElement,
			String selectedBillingOption, CorporateEntity corp,
			String accountCode, String selectedProduct) {

		ClientServiceElement clientServiceElement = this.constructClientServiceElement(
				contractedElement, selectedBillingOption, corp, accountCode, selectedProduct, null);
		
		return clientServiceElement;
	}

	@Override
	public ClientServiceElement constructClientServiceElement(
			ClientContractServiceElement contractedElement,
			String selectedBillingOption, CorporateEntity corp,
			String accountCode, String selectedProduct, String gradeGroup) {
		
		ClientServiceElement clientServiceElement = new ClientServiceElement();
		ExternalAccount clientAccount = customerAccountService.getCustomerAccount(accountCode,corp);
		clientServiceElement.setClientContractServiceElement(contractedElement);
		clientServiceElement.setExternalAccount(clientAccount);
		clientServiceElement.setStartDate(new Date());
		clientServiceElement.setEndDate(null);
		clientServiceElement.setRemovedFlag("N");
		clientServiceElement.setBillingOption(selectedBillingOption);
		
		if(MALUtilities.isNotEmptyString(selectedProduct)){
			clientServiceElement.setProduct(productDAO.findById(selectedProduct).orElse(null));
		}
		if(MALUtilities.isNotEmptyString(gradeGroup)){
			//TODO: the "C" is not needed
			clientServiceElement.setExternalAccountGradeGroup(this.getEagByExternalAccountCodeAndType(corp.getCorpId(), "C", accountCode, gradeGroup));
			//TODO: remove and test; this seems to be redundant
			clientServiceElement.setClientServiceElementType(clientServiceElementTypeDAO.findById(ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getId()).orElse(null));
		}
		
		return clientServiceElement;
	}

	@Transactional
	public void removeGradeGroupClientServiceElement(
			ClientServiceElement gradeGroupElement) throws MalBusinessException {
		List<ClientServiceElement> gradeGroupElements = new ArrayList<ClientServiceElement>();
		//set remove flag
		gradeGroupElement.setRemovedFlag("Y");		
		// set the end data
		gradeGroupElement.setEndDate(new Date());
		// update the client service elements
		gradeGroupElements.add(gradeGroupElement);
		this.saveUpdateClientServiceElements(gradeGroupElements, ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode());
		
	}
	
	/**
	 * Actually creates a service element record to be displayed in the Client Product Override table
	 * Used for both Client and Grade Group Product Exclusions
	 */	
	@Transactional
	public void removeProductClientServiceElement(ClientServiceElement clientElement, String product, String parameterType) throws MalBusinessException {
		List<ClientServiceElement> clientElements = new ArrayList<ClientServiceElement>();
		clientElement.setRemovedFlag("Y");		
		clientElement.setEndDate(new Date());
		clientElement.setProduct(productDAO.findById(product).orElse(null));
		clientElements.add(clientElement);
		if (parameterType.equals(ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode())){
			this.saveUpdateClientServiceElements(clientElements, ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode());
		} else {
			this.saveUpdateClientServiceElements(clientElements, ClientServiceElementTypeCodes.ACCOUNT.getCode());
		}
	}	
	
	//Used for both Client and Grade Group Product Exclusion removals
	@Transactional
	public void deleteProductRemovalServiceElement(ClientServiceElement clientElement, String parameterType) throws MalBusinessException {
		List<ClientServiceElement> clientElements = new ArrayList<ClientServiceElement>();
		//setting removal flag to N accomplishes what we need from a UI perspective but not a perfect solution.  Since we are not capturing Product Override history
		//it is not an issue.  If the business changes it's mind on this then simply setting the flag to N will not work because we could not differentiate between
		//Product Override historical records and Product Removal historical records.
		clientElement.setRemovedFlag("N");		
		clientElement.setEndDate(new Date());
		clientElements.add(clientElement);
		if (parameterType.equals(ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode())){
			this.saveUpdateClientServiceElements(clientElements, ClientServiceElementTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode());
		} else {
			this.saveUpdateClientServiceElements(clientElements, ClientServiceElementTypeCodes.ACCOUNT.getCode());
		}		
	}		
	/**
	 * Returns non finance lease element cost of quotation model
	 * 
	 * @param quotation
	 *            model returns non finance lease element cost
	 */
	@Override
	public BigDecimal getServiceElementCost(QuotationModel quotationModel) {
		BigDecimal nonFinanceCost = BigDecimal.valueOf(MalConstants.DEFAULT_CURRENCY_VALUE);

		if (quotationModel != null) {
			List<QuotationElement> quotationElementList = quotationModel.getQuotationElements();

			for (QuotationElement quotationElement : quotationElementList) {
				if (this.isServiceElement(quotationElement)) {
					BigDecimal bd = quotationElement.getRental();
					if (bd != null)
						nonFinanceCost = nonFinanceCost.add(bd);
				}
			}
		}

		return nonFinanceCost;
	}
	
	/**
	 * Returns non finance lease element monthly service value of quotation model
	 * 
	 * @param quotation
	 *            model returns non finance lease element monthly service cost
	 */
	@Override
	public BigDecimal getServiceElementMonthlyCost(QuotationModel quotationModel) {
		BigDecimal nonFinanceCost = BigDecimal.valueOf(MalConstants.DEFAULT_CURRENCY_VALUE);

		if (quotationModel != null) {
			List<QuotationElement> quotationElementList = quotationModel.getQuotationElements();

			for (QuotationElement quotationElement : quotationElementList) {
				if (this.isServiceElement(quotationElement)) {
					BigDecimal bdPeriod = BigDecimal.valueOf(quotationModel.getContractPeriod());
					if (quotationElement.getNoRentals() != null) {
						bdPeriod = quotationElement.getNoRentals();
					}
					BigDecimal rental = quotationElement.getRental();
					if (rental != null){
						rental = rental.divide(bdPeriod, CommonCalculations.MC);						
						nonFinanceCost = nonFinanceCost.add(CommonCalculations.getRoundedValue(rental , CommonCalculations.CURRENCY_DECIMALS));
					}
				}
			}
		}

		return nonFinanceCost;
	}

	/**                 
     * This method provides details for service element cost breakdown for a particular
	 * quotation model. It returns a list of all non-finance service elements.            
	 */
	@Override
	public List<QuotationElement> getServiceElementDetails(
			QuotationModel quotationModel) throws MalBusinessException {
		
		List<QuotationElement> filterQuotationElementList = new ArrayList<QuotationElement>();
		
		if (quotationModel != null) {
			List<QuotationElement> quotationElementList = quotationModel.getQuotationElements();

			for (QuotationElement quotationElement : quotationElementList) {
				if (this.isServiceElement(quotationElement)) {
					filterQuotationElementList.add(quotationElement);
			}
				}
		}
		return filterQuotationElementList;
	}

	@Override
	public Date getBillingDate(QuotationElement qe) {
		List<QuotationSchedule> qs = qe.getQuotationSchedules(); 
		if(qs != null && qs.size() > 0) {
			Collections.sort(qs, new QuotationScheduleTransDateComparator());
			return qs.get(0).getTransDate();
		} else {
			return null;
		}
	}

	@Override
	public BigDecimal getRentalPeriods(QuotationElement qe, QuotationModel quotationModel, Boolean isExistingEle) {
		BigDecimal rentalPeriods;
		if(qe.getNoRentals() != null && qe.getNoRentals() != BigDecimal.ZERO) {
			rentalPeriods = qe.getNoRentals();
		} else {
			if(isExistingEle){
				QuotationElement mainQuotationElement = null;
				for (QuotationElement quotationElement : quotationModel.getQuotationElements()) {
					if (quotationElement.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)
							&& quotationElement.getQuotationDealerAccessory() == null && quotationElement.getQuotationModelAccessory() == null
							&& MalConstants.FLAG_Y.equals(quotationElement.getIncludeYn())) {
						mainQuotationElement = quotationElement;

					}
				}
				rentalPeriods = mainQuotationElement.getNoRentals();
			} else {
				rentalPeriods = new BigDecimal(quotationModel.getContractPeriod());
			}
			
			
		}
		return rentalPeriods;
	}
	
	Comparator<ServiceElementsVO> svcElementVOComparator = new Comparator<ServiceElementsVO>() {
		public int compare(ServiceElementsVO r1, ServiceElementsVO r2) {
			int compareResult = 0;
			Date date1 = r1.getEffectiveBilling();
			Date date2 = r2.getEffectiveBilling();

			if (date1 == null && date2 == null) {
				compareResult = 0;
			} else if (date1 == null) {
				compareResult = -1;
			} else if (date2 == null) {
				compareResult = 1;
			} else {
				compareResult = date1.compareTo(date2);
			}
			if (compareResult != 0) {
				return compareResult;
			} else {
				String desc1 = r1.getDescription();
				String desc2 = r2.getDescription();
				if (desc1 != null && desc2 != null) {
					compareResult = desc1.compareToIgnoreCase(desc2);
				}
			}
			return compareResult;
		}
	};
	
	
	/**
	 * This method gets all available service elements from the Setup tables (part of Service Standards) for
	 * the QuotationModel, ExternalAccount, GradeGroup and optionally the QuotationProfile that has been passed it.<br>
	 * Used by:<br>
	 * 1) Accepting a quote under a related account, to see if this is allowed.
	 * 2) Performing a Novation of the Unit/Vehicle from One Account to another.
	 * 
	 * @param quoteModel used to pass the term/periods to the rental caclulation so a monthy price can be calculated
	 * @param extAccount ExternalAccount 
	 * @param gradeGroup String value of the grade group
	 * @param quoteProfile (Optional) used for product level overrides if null is passed then we will not check for these overrides
	 * 
	 * @return List<ServiceElementsVO> all service elements that are in setup based upon the arguments passed in.
	 * 
	 * @throws MalBusinessException
	 */
	@Override
	public List<ServiceElementsVO> getAvailableServiceElements(QuotationModel quoteModel, ExternalAccount extAccount, String gradeGroup, QuotationProfile quoteProfile, boolean ignoreQuoteLvlDifferences) throws MalBusinessException {

		List<ServiceElementsVO> serviceElements = null;
		Long qprId = null;
		
		if (!MALUtilities.isEmpty(quoteProfile)) {
			qprId = quoteProfile.getQprId();
		}

		serviceElements  = clientServiceElementsDAO.getAvailableServiceElementsByAccountGradeGroupProfile(extAccount, gradeGroup, qprId);

		// we need to make sure that the rental calculation uses the correct information otherwise we will be pulling in the wrong finance parameters.
		// and
		// if we want to ignore any quote level overrides in doing the comparison then we will 
		// need to pass in this flag ignoreQuoteLvlDifferences; it will make sure that the calculated
		// service element from the setup also includes in it's dollar amount any overrides if they were on that element in the quote
		// in this way the dollar amounts will match with what is on the quotation elements and the elements will be treated as the same.
		if(MALUtilities.isEmpty(extAccount)){
			serviceElements	= populateOtherFields(serviceElements, quoteModel, quoteModel.getQuotation().getExternalAccount(),ignoreQuoteLvlDifferences);
		}else{
			serviceElements	= populateOtherFields(serviceElements, quoteModel, extAccount,ignoreQuoteLvlDifferences);
		}

		return serviceElements;
	}
	
	
	/**
	 * This 
	 */
	
	/**
	 * This method is used to get all available service elements from the Setup tables (part of Service Standards) for
	 * the Quotation. 
	 * It is used for general comparison given a quotation model (i.e. amendments, recalculate and extensions) <br>
	 * Used by:<br>
	 * 1) Formal/InFormal Amendments to get the list of available elements to pass to determineElementsWithChanges; to show what can be added or removed.
	 * 2) Extensions to get the list of available elements to pass to determineElementsWithChanges; to show what can be added or removed.
	 * 3) Recalculate to get the list of Service Elements to calculate or recalculate a quote that has not need put on 
	 * contract yet (is not one of the other cases).
	 * 
	 * @param quoteModel QuotationModel
	 * 
	 * @return List<ServiceElementsVO> all service elements that are setup based upon the quotationModel passed in.
	 * 
	 * @throws MalBusinessException
	 */
	@Override
	public List<ServiceElementsVO> getAvailableServiceElements(
			QuotationModel quoteModel) throws MalBusinessException {
		
		List<ServiceElementsVO> serviceElements = null;

		serviceElements = this.getAvailableServiceElements(quoteModel, 
										quoteModel.getQuotation().getExternalAccount(), 
										quoteModel.getQuotation().getDriverGradeGroup(),
										quoteModel.getQuotation().getQuotationProfile(),
										false);
	
		return serviceElements;
	}
	
	@Override
	public List<ServiceElementsVO> getAvailableServiceElements(QuotationModel quoteModel, ExternalAccount extAccount, String gradeGroup, QuotationProfile quoteProfile) throws MalBusinessException {
		List<ServiceElementsVO> serviceElements = null;

		serviceElements = this.getAvailableServiceElements(quoteModel,extAccount,gradeGroup,quoteProfile,false);
	
		return serviceElements;
	}	

	@Override
	public List<ServiceElementsVO> getAssignedServiceElements(
			QuotationModel quoteModel) throws MalBusinessException {
		List<QuotationElement> serviceElements = getServiceElementDetails(quoteModel);
		
		List<ServiceElementsVO> rowList = new ArrayList<ServiceElementsVO>();
		
		ServiceElementsVO serviceElementVO;
		
		for(QuotationElement qe : serviceElements) {
			serviceElementVO = createServiceElemFromQuoteElem(qe,quoteModel);
			rowList.add(serviceElementVO);
		}
			
		
		// check for informal amendments
		List<InformalAmendment> informalAmendments = dedupeInformalAmendments(informalAmendmentDAO.findByQmdId(quoteModel.getQmdId()));

		// for each informal amendment record
		for(InformalAmendment amendElem : informalAmendments){
			// look within the service element to see if we find an one that has an informal amendment
			ServiceElementsVO servElem = findInformalAmendment(rowList,amendElem);
			// if we find one (it should be a remove)
			if(!MALUtilities.isEmpty(servElem)){
				// check it's status and remove it
				if(amendElem.getAddRemove().equalsIgnoreCase("R")){
					rowList.remove(servElem);
				}else{ // the element could be an update; check it's price for changes and update the price information - this will be detected as a change "C"
					// within determineElementsWithChanges
					if(amendElem.getBillingAmt().doubleValue() != servElem.getMonthlyCost().doubleValue()){
						servElem.setMonthlyCost(amendElem.getBillingAmt());
						servElem.setTotalCost(amendElem.getCapitalCost());
						servElem.setDisplayOnlyMonthlyCost(CommonCalculations.getRoundedValue(amendElem.getBillingAmt(), CommonCalculations.CURRENCY_DECIMALS));
					}
				}
			}else{ // if we don't find one (it should be an add)
				// check it's status and add it
				if(amendElem.getAddRemove().equalsIgnoreCase("A")){
					rowList.add(createServiceElemFromInformalAmendment(amendElem));
				}
			}
			//TODO: in the future we will have to address billing options (Monthly vs Per Occurrence)
		}

		Collections.sort(rowList, svcElementVOComparator);
		return rowList;
	}
	
	
	// this is an ordered list coming back from the DB; we need to get only the most 
	// recent record per a lel Id (lease element).
	private List<InformalAmendment> dedupeInformalAmendments(List<InformalAmendment> informalAmendments){
		HashMap<Long,InformalAmendment> dedupeMap = new HashMap<Long,InformalAmendment>();
		
		for(InformalAmendment amendElem : informalAmendments){
			// if the informal amendment record has no effective date (null) - skip it
			if(!MALUtilities.isEmpty(amendElem.getEffectiveFrom())){
				// if there is another element with the same lelId in the list then skip it
				if(!dedupeMap.containsKey(amendElem.getLelLelId())){
					dedupeMap.put(amendElem.getLelLelId(), amendElem);
				}
			}
			
		}
		
		return new ArrayList<InformalAmendment>(dedupeMap.values());
	}
	
	
	private ServiceElementsVO findInformalAmendment(List<ServiceElementsVO> serviceElements, InformalAmendment amendElem){
		ServiceElementsVO retVal = null;
		
		for(ServiceElementsVO servElem : serviceElements){
			if(servElem.getLelId().equals(amendElem.getLelLelId())){
				retVal = servElem;
				break;
			}
		}
		
		return retVal;
	}
	
	private ServiceElementsVO createServiceElemFromQuoteElem(QuotationElement qe, QuotationModel quoteModel){
		ServiceElementsVO serviceElementVO;
		
		serviceElementVO = new ServiceElementsVO();
		serviceElementVO.setLelId(qe.getLeaseElement().getLelId());
		serviceElementVO.setName(qe.getLeaseElement().getElementName());
		serviceElementVO.setDescription(qe.getLeaseElement().getDescription());
		if(qe.getRental() != null) {
			BigDecimal cost = qe.getRental().divide(getRentalPeriods(qe,quoteModel, true),CommonCalculations.MC);
			serviceElementVO.setMonthlyCost(cost);
		}		
		serviceElementVO.setMonthlyCost(serviceElementVO.getMonthlyCost() != null ? serviceElementVO.getMonthlyCost() : BigDecimal.ZERO);
		serviceElementVO.setDisplayOnlyMonthlyCost(CommonCalculations.getRoundedValue(serviceElementVO.getMonthlyCost(), CommonCalculations.CURRENCY_DECIMALS));		
		serviceElementVO.setTotalCost(qe.getRental() != null ? qe.getRental() : BigDecimal.ZERO);
		
		Date billingDate = getBillingDate(qe);

		if(billingDate != null) {
			serviceElementVO.setEffectiveBilling(billingDate);				
		} else {
			serviceElementVO.setEffectiveBilling(null);
		}
		serviceElementVO.setBillingOptions(qe.getBillingOptions());
		//TODO: I don't know that we need finance parameters for this; we will only if we fully refactor 
		// ServiceElementVO
		//setFinanceParameters(serviceElementVO, qe);

		return serviceElementVO;
	}
	
	private ServiceElementsVO createServiceElemFromInformalAmendment(InformalAmendment iae){
		ServiceElementsVO serviceElementVO;
		
		serviceElementVO = new ServiceElementsVO();
		serviceElementVO.setLelId(iae.getLelLelId());
		LeaseElement lel = leaseElementDAO.findById(iae.getLelLelId()).orElse(null);
		serviceElementVO.setName(lel.getElementName());
		serviceElementVO.setDescription(lel.getDescription());
		if(iae.getRental() != null) {
			serviceElementVO.setMonthlyCost(iae.getBillingAmt());			
		}		
		serviceElementVO.setMonthlyCost(serviceElementVO.getMonthlyCost() != null ? serviceElementVO.getMonthlyCost() : BigDecimal.ZERO);		
		serviceElementVO.setDisplayOnlyMonthlyCost(CommonCalculations.getRoundedValue(serviceElementVO.getMonthlyCost(), CommonCalculations.CURRENCY_DECIMALS));		
		serviceElementVO.setTotalCost(iae.getRental() != null ? iae.getRental() : BigDecimal.ZERO);
		
		serviceElementVO.setEffectiveBilling(iae.getEffectiveFrom());				

		//TODO: we have deferred billing options; so this will need to be revisited when we bring it back
		//serviceElementVO.setBillingOptions(qe.getBillingOptions());
		//TODO: I don't know that we need finance parameters for this; we will only if we fully refactor 
		// ServiceElementVO
		//setFinanceParameters(serviceElementVO, qe);

		return serviceElementVO;
	}
	
	
	/**
	 * @param List<ServiceElementsVO> assignedElements
	 * @param List<ServiceElementsVO> allAvailableElements
	 * 
	 * Uses 2 ServiceElementsVO Lists one populated from the quote and the second with the list of all service elements 
	 * that are available (to put on a new quote) then applies the business rules
	 * that are defined for applying changes to see if the available elements and element changes would be "movable"
	 * to the quote or not (determines if there are differences that could be applied between the 2 lists)
	 *  
	 * @return List<ServiceElementsVO> with operations set that indicate the action that can be taken
	 * @throws MalBusinessException 
	 */
	@Override
	public List<ServiceElementsVO> determineElementsWithChanges (
			List<ServiceElementsVO> assignedElements,
			List<ServiceElementsVO> allAvailableElements) throws MalBusinessException {
		
		List<ServiceElementsVO> elementWithChanges = new ArrayList<ServiceElementsVO>();

		//(list 2) if the element is available and on the quote
		for(ServiceElementsVO available: allAvailableElements){
			ServiceElementsVO found = elementOneInListTwo(available, assignedElements);
			
			//(list 1) if the element is available but not on the quote we can add it.
			if(MALUtilities.isEmpty(found)){
				available.setAvailableOperation(ServiceElementOperations.ADD);
				elementWithChanges.add(available);
			
			//(list 1) if this is an not an old element (where the billing option was not set)
			//(list 1) and, if the element is available and on the quote ("billing options" different) we can change it	
			}else if((!(null==found.getBillingOptions())) && (!found.getBillingOptions().equalsIgnoreCase(available.getBillingOptions())) ){
				available.setAvailableOperation(ServiceElementOperations.CHANGE);
				elementWithChanges.add(available);	
			//(list 1) if the element is available and on the quote ("totals" different) we can change it
			}else if(found.getMonthlyCost().doubleValue() != available.getMonthlyCost().doubleValue()){
				available.setAvailableOperation(ServiceElementOperations.CHANGE);
				elementWithChanges.add(available);
			}else{
				available.setAvailableOperation(ServiceElementOperations.NONE);
				elementWithChanges.add(available);
			}			
		}
		
		//find each element on the quote that is not available 
		for(ServiceElementsVO assigned: assignedElements){
			ServiceElementsVO found = elementOneInListTwo(assigned, allAvailableElements);
			if(MALUtilities.isEmpty(found)){
				// if it's on the quote but not in the available add it to the list 
				// marked so it can be removed
				assigned.setAvailableOperation(ServiceElementOperations.REMOVE);
				elementWithChanges.add(assigned);
			}
		}

		
		return elementWithChanges;
	}
	
	// used to find service element vo that are missing from the other list (quoted or available) 
	// to help determine changes that are available.
	private ServiceElementsVO elementOneInListTwo(ServiceElementsVO element,
			List<ServiceElementsVO> elementList){
		ServiceElementsVO found = null;
		
		for(ServiceElementsVO elementItem: elementList){
			if(elementItem.getLelId().equals(element.getLelId())){
				// return "ourself" (what was passed in)
				found = elementItem;
				break;
			}
		}
		
		return found;
	}
	
	private List<ServiceElementsVO> populateOtherFields(List<ServiceElementsVO> serviceElements, QuotationModel quotationModel, ExternalAccount extAccount, boolean ignoreQuoteLvlDifferences) throws MalBusinessException {
		List<ServiceElementsVO> list = new ArrayList<ServiceElementsVO>();
		
		for(ServiceElementsVO serviceElementVO : serviceElements) {
			list.add(populateServiceElementFields(serviceElementVO,quotationModel,extAccount,ignoreQuoteLvlDifferences));
		}

		Collections.sort(list, svcElementVOComparator);
		
		return list;
	}

	@Override
	public ServiceElementsVO populateServiceElementFields(ServiceElementsVO serviceElement, QuotationModel quotationModel, ExternalAccount extAccount) throws MalBusinessException {
	
		return this.populateServiceElementFields(serviceElement, quotationModel, extAccount, false);
	}

	@Override
	public ServiceElementsVO populateServiceElementFields(ServiceElementsVO serviceElement, QuotationModel quotationModel, ExternalAccount extAccount, boolean ignoreQuoteLvlDifferences) throws MalBusinessException {

		//TODO: pull in other values that are needed
		// one of which is to calculate the monthly cost of the service element 
		// so we can see if it's different
			
		QuotationElement qe = new QuotationElement();
		// noRentals
		qe.setLeaseElement(leaseElementDAO.findById(serviceElement.getLelId()).orElse(null));
		qe.setAcceptedInd("N");
		qe.setQuotationModel(quotationModel);
		
		// When we are adding paint to an existing quote that has quote level overrides
		// we want to make sure we "cancel out" the effects of those quote level overrides
		// within the comparison operation (i.e. quote level overrides should not be be 
		// 'counted' as a difference on an element by element basis). 
		//
		// Because the quote only stores the total and the number of periods we don't have 
		// the details needed to reduce the total by the appropriate amounts in  
		// a straightforward way; instead we need to do "opposite math".
		//
		// we added the ignoreQuoteLvlDifferences flag for use when calculating "Available" elements 
		// to allow for a mechanism to pull in quote level finance parameters
		// that were defined on that element for the quote we are comparing; in this way 
		// we "cancel out" the effects of adding the quote level finance parameters to the quotation elements
		// "Assigned" to the quote. 
		
		// if we are not ignoring quote level difference 
		// then we need to make sure we do skip reading finance parameter overrides 
		// i.e pass skipQuoteLvlFinanceParams (true) below.
		// from the quote so they will be not be added to the 
		// dollar amounts we are calculating for "Available" elements
		// and so we will treat the 2 elements "Assigned" and "Available" as 
		// different because "Assigned" was calculated and 
		// saved with quote level overrides and "Availble" did not have it's dollar amounts
		// adjusted via "opposite math" 
		if(!ignoreQuoteLvlDifferences){
			qe = quoteElementService.getCalculatedQuotationElement(qe, quotationModel,true,extAccount);
		}else{ // otherwise we are going to pull quote level finance parameter values into the calculation
			   // as "opposite math" and the 2 values will match.
			qe = quoteElementService.getCalculatedQuotationElement(qe, quotationModel,false,extAccount);
		}
		
		if(qe.getRental() != null) {
			BigDecimal cost = qe.getRental().divide(getRentalPeriods(qe,quotationModel, false),CommonCalculations.MC);
			serviceElement.setMonthlyCost(cost);
		}			
		serviceElement.setMonthlyCost(serviceElement.getMonthlyCost() != null ? serviceElement.getMonthlyCost() : BigDecimal.ZERO);
		serviceElement.setDisplayOnlyMonthlyCost(CommonCalculations.getRoundedValue(serviceElement.getMonthlyCost(), CommonCalculations.CURRENCY_DECIMALS));						
		serviceElement.setTotalCost(qe.getRental());
		Date billingDate = getBillingDate(qe);
		
		if(billingDate != null) {
			serviceElement.setEffectiveBilling(billingDate);				
		} else {
			serviceElement.setEffectiveBilling(null);
		}
			

		return serviceElement;
	}
	

	/**
	 * @param QuotationModel qm
	 * 
	 * Using a populated QuotationModel (with Quote Elements) as the argument
	 * this method fetches (from the DB) the service elements that are available (to put on a new quote) and uses the business rules
	 * that are defined for applying changes to see if the available elements and element changes would be "movable"
	 * to the quote or not (determines if there are differences that could be applied between the 2 lists)
	 *  
	 * @return List<ServiceElementsVO> with operations set that indicate the action that can be taken
	 * @throws MalBusinessException 
	 */
	@Override
	public List<ServiceElementsVO> determineElementsWithChanges(
			QuotationModel qm) throws MalBusinessException {
		
		List<ServiceElementsVO> assignedElements = this.getAssignedServiceElements(qm);
		List<ServiceElementsVO> allAvailableElements = this.getAvailableServiceElements(qm);
		
		return this.determineElementsWithChanges(assignedElements,allAvailableElements);
	}
	

	public List<ServiceElementsVO> findElementsWithChanges (List<ServiceElementsVO> assignedElements, List<ServiceElementsVO> allAvailableElements) throws MalBusinessException {
		List<ServiceElementsVO> elementWithChanges = new ArrayList<ServiceElementsVO>();
		Map<Long,ServiceElementsVO> dedupingMap = new HashMap<Long,ServiceElementsVO>();
		
		//loop thru assigned service elements getting ADD and CHANGE elements
		for (ServiceElementsVO assignedElement : assignedElements) {
			if(assignedElement.getAvailableOperation() != null){
				if(assignedElement.getAvailableOperation().getOperation().equals(ServiceElementOperations.ADD.getOperation()) || 
						(assignedElement.getAvailableOperation().getOperation().equals(ServiceElementOperations.CHANGE.getOperation()))) {
					
					elementWithChanges.add(assignedElement);
				}
			}
		}
		//loop thru available service elements getting REMOVE (and NULL) elements.  
		for (ServiceElementsVO availableElement : allAvailableElements) {
			if(availableElement.getAvailableOperation() == null){
				availableElement.setAvailableOperation(ServiceElementOperations.REMOVE);				
			}
			if(availableElement.getAvailableOperation().getOperation().equals(ServiceElementOperations.REMOVE.getOperation())) {
				elementWithChanges.add(availableElement);
			}
		}
		
		//in order to allow the removal of an element from the ASSIGNED list without automatically adding the same element from the AVAILABLE list if it exists,
		//we create a scenario where 2 of the same elements will be in the elementWithChanges list.  We only need the CHANGE element, so we have to remove the other
		//this may go away after the business defines its needs.
		for(ServiceElementsVO serviceElement : elementWithChanges){
			// if there is already an CHANGE element for the same lease element id
			if(dedupingMap.containsKey(serviceElement.getLelId())){
				// basically keep the one that has been added to the ASSIGNED list
				if(serviceElement.getAvailableOperation().getOperation().equals(ServiceElementOperations.CHANGE.getOperation())) {
					dedupingMap.put(serviceElement.getLelId(), serviceElement);
				}
			}else{ 
				dedupingMap.put(serviceElement.getLelId(), serviceElement);
			}
		}
		elementWithChanges = new ArrayList<ServiceElementsVO>(dedupingMap.values());		
	
		return elementWithChanges;
	}

	/**
	 * This method is used to update the mul_quote_ele related data in the ServiceElementAmendments process
	 */	
	@Transactional
	public void saveOrUpdateServiceElements(QuotationModel quotationModel, List<ServiceElementsVO> changesList) {
		try {
			List<MulQuoteEle> mulQuoteEleList = mulQuoteEleDao.findMulQuoteEleByQuotationId(quotationModel.getQuotation().getQuoId());			
			MulQuoteEle mqe = null;
			
			//We need to check for and remove Mul_Quote_Ele and Mul_Quote_Opt records for any service element 
			//that is no longer part of the user's selection
			List<ServiceElementsVO> assignedEleList = this.getAssignedServiceElements(quotationModel);
			for (MulQuoteEle mulQuoteEle : mulQuoteEleList) {
				String elementExists = "Y";
				if (!mulQuoteEle.getElementType().equals("FINANCE")) {
					for (ServiceElementsVO assignedEle : assignedEleList) {						
						if (mulQuoteEle.getLeaseElement().getLelId() == assignedEle.getLelId().longValue()) {
							elementExists = "Y";
							break;
						}else {
							elementExists = "N";
						}
					}	
					for (ServiceElementsVO changeServEle : changesList) {
						if (changeServEle.getLelId().longValue() == mulQuoteEle.getLeaseElement().getLelId()) {
							elementExists = "Y";
							break;
						}
					}
					if (elementExists.equalsIgnoreCase("N")) {
						if(mulQuoteEle.getMulQuoteOpts().size()> 0){
							mulQuoteOptDao.deleteAll(mulQuoteEle.getMulQuoteOpts());
						}
						mulQuoteEleDao.delete(mulQuoteEle);
					}
				}
			}
			
			for (ServiceElementsVO se : changesList) {
				
				if (se.getAvailableOperation().getOperation().equals(ServiceElementOperations.ADD.getOperation())) {					
					// first look to see if the element is already in the base tables 
					mqe = findMulQuoteEleInListByServiceElem(mulQuoteEleList,se,true);
					if(MALUtilities.isEmpty(mqe)){
						// if it is not then add it
						mqe = new MulQuoteEle();
						LeaseElement lel = leaseElementDAO.findById(se.getLelId()).orElse(null);
						mqe.setQuotation(quotationModel.getQuotation());
						mqe.setLeaseElement(lel);
						mqe.setMandatoryInd("Y");
						mqe.setSelectedInd("Y");
						mqe.setAdditElem("Y");
						mqe.setAllowMod("N");
						mqe.setReCalcNeeded("N");
						mqe.setTaxCode(taxCodeDao.findById(se.getTaxId()).orElse(null).getTaxCode());
						mqe.setElementType(lel.getElementType());
						
						mqe = mulQuoteEleDao.saveAndFlush(mqe);
						
						List<MulQuoteOpt> mqoList = new ArrayList<MulQuoteOpt>();
						List<FormulaParameter> fprList  =  formulaParameterDAO.findByLeaseElementId(se.getLelId());
						for (FormulaParameter formulaParameter : fprList) {
							MulQuoteOpt mqo = new MulQuoteOpt();
							mqo.setMulQuoteEle(mqe);
							mqo.setFprFprId(new BigDecimal(formulaParameter.getFprId()));
							mqo.setShowField("N");
							mqoList.add(mqo);
						}
						
						mulQuoteOptDao.saveAll(mqoList);						
					}else{
						//maybe set already added (unselected in the mul_quote_ele list) to mandatory/selected.
						mqe.setSelectedInd("Y");
						mqe.setMandatoryInd("Y");
						mqe = mulQuoteEleDao.saveAndFlush(mqe);
						
					}
				} else if (se.getAvailableOperation().getOperation().equals(ServiceElementOperations.CHANGE.getOperation())) { 
					for (MulQuoteEle mulQuoteEle : mulQuoteEleList) {
						if(mulQuoteEle.getLeaseElement().getLelId() == se.getLelId().longValue()) {
							List<QuotationElement> qelList = quotationElementDao.findByQmdIdAndLeaseEleId(quotationModel.getQmdId(), se.getLelId());
							for (QuotationElement quotationElement : qelList) {
								QuotationElement qel = quotationElement;
								qel.setAcceptedInd("N");
								quotationElementDao.saveAndFlush(qel);
							}
							mulQuoteEle.setSelectedInd("Y");
							mulQuoteEle.setReCalcNeeded("Y");
							mulQuoteEle = mulQuoteEleDao.saveAndFlush(mulQuoteEle);
						}
					}
				} else if(se.getAvailableOperation().getOperation().equals(ServiceElementOperations.REMOVE.getOperation())) {
					for (MulQuoteEle mulQuoteEle : mulQuoteEleList) {
						if(mulQuoteEle.getLeaseElement().getLelId() == se.getLelId().longValue()) {
							if(mulQuoteEle.getMulQuoteOpts().size()> 0){
								mulQuoteOptDao.deleteAll(mulQuoteEle.getMulQuoteOpts());
							}
							mulQuoteEleDao.delete(mulQuoteEle);
						}
					}
				}
			}			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "saving Service Elements Service for Quote: " + quotationModel.getQuotation().getQuoId() }, ex);
		}
	}
	
	private MulQuoteEle findMulQuoteEleInListByServiceElem(List<MulQuoteEle> mulQuoteEleList, ServiceElementsVO se, boolean skipSelectedInd){
		MulQuoteEle retVal = null;
		
		for (MulQuoteEle mulQuoteEle : mulQuoteEleList) {
			// selected mul quote ele records that have a lel id that match what is passed in.
			if(mulQuoteEle.getLeaseElement().getLelId() == se.getLelId().longValue()){
				// if we are not skipping selected and this item is selected
				if(skipSelectedInd == false && mulQuoteEle.getSelectedInd().equalsIgnoreCase("Y")){
					retVal = mulQuoteEle;
					break;
				}else{ // else we are skipping selected and we should return
					retVal = mulQuoteEle;
					break;	
				}
			}
		}	
		
		return retVal;
}
	

	/**
	 * This method is used to create a full list of MulQuoteEle object in memory to pass to the LeaseElementProducer to return the elements we need to process
	 * and is also used in the Save process to update the MulQuoteElements table
	 */
	@Override
	public List<MulQuoteEle> getMulQuoteElemsForServiceElements(
			QuotationModel quotationModel, List<ServiceElementsVO> changesList) {

		try {
			List<MulQuoteEle> mulQuoteEleList = mulQuoteEleDao.findMulQuoteEleByQuotationId(quotationModel.getQuotation().getQuoId());
			List<MulQuoteEle> mulQuoteEleChangedList= new ArrayList<MulQuoteEle>();
			MulQuoteEle mqe = null;
			
			for(MulQuoteEle mulQuoteEle : mulQuoteEleList){
				// if the mulQuoteEle is already in the service elements list; return it
				ServiceElementsVO servElem = findServiceElementByMulQuoteEleInList(mulQuoteEle,changesList);
				if(!MALUtilities.isEmpty(servElem)){
					// if it's a change then set the transient variable
					if(servElem.getAvailableOperation().getOperation().equals(ServiceElementOperations.CHANGE.getOperation())){
						//TODO: for now this does not come into play for anything but informal ammendments; ideally we should
						// indicate on a row by row level that recalc is needed (in more scenerios)
						// N is appropriate because we are calling this when we are getting ready to recalc (within that operation)
						mulQuoteEle.setReCalcNeeded("N");
						mqe = mulQuoteEle;
					}
					// if it's a remove then mark are removed
					if(servElem.getAvailableOperation().getOperation().equals(ServiceElementOperations.REMOVE.getOperation())) {
						//Mark it as not selected
						mqe = mulQuoteEle;
						mqe.setSelectedInd("N");
					}
					// else retain all (NONE/ADD) existing MulQuoteElems for recalculate
					if(servElem.getAvailableOperation().getOperation().equals(ServiceElementOperations.NONE.getOperation()) || servElem.getAvailableOperation().getOperation().equals(ServiceElementOperations.ADD.getOperation())){ 
						mqe = mulQuoteEle;
					}
				}else{ // else retain all existing for recalculate
					mqe = mulQuoteEle;
				}
				
				mulQuoteEleChangedList.add(mqe);
			}
			
			
			
			// loop thru all of the service elements to create and add all that are new (ADD)
			for (ServiceElementsVO se : changesList) {
				if (se.getAvailableOperation().getOperation().equals(ServiceElementOperations.ADD.getOperation())) {
					
					// first look to see if the element is already in the base tables 
					//(TODO: this is a little redundant)
					mqe = findMulQuoteEleInListByServiceElem(mulQuoteEleList,se,false);
					if(MALUtilities.isEmpty(mqe)){
						// if it is not then add it
						mqe = new MulQuoteEle();
						LeaseElement lel = leaseElementDAO.findById(se.getLelId()).orElse(null);
						mqe.setQuotation(quotationModel.getQuotation());
						mqe.setLeaseElement(lel);
						mqe.setMandatoryInd("Y");
						mqe.setSelectedInd("Y");
						mqe.setAdditElem("Y");
						mqe.setAllowMod("N");
						mqe.setTaxCode(taxCodeDao.findById(se.getTaxId()).orElse(null).getTaxCode());
						mqe.setElementType(lel.getElementType());

						List<MulQuoteOpt> mqoList = new ArrayList<MulQuoteOpt>();
						List<FormulaParameter> fprList  =  formulaParameterDAO.findByLeaseElementId(se.getLelId());
						for (FormulaParameter formulaParameter : fprList) {
							MulQuoteOpt mqo = new MulQuoteOpt();
							mqo.setMulQuoteEle(mqe);
							mqo.setFprFprId(new BigDecimal(formulaParameter.getFprId()));
							mqo.setShowField("N");
							mqoList.add(mqo);
						}
						mqe.setMulQuoteOpts(mqoList);
						
						mulQuoteEleChangedList.add(mqe);				
					}
					
				} 
			}
			
			return mulQuoteEleChangedList;
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "saving Service Elements Service for Quote: " + quotationModel.getQuotation().getQuoId() }, ex);
		}
	}
	
	private ServiceElementsVO findServiceElementByMulQuoteEleInList(MulQuoteEle mulQuoteEle, List<ServiceElementsVO> seList){
		ServiceElementsVO retVal = null;
		
		for (ServiceElementsVO se : seList) {
			if(se.getLelId().longValue() == mulQuoteEle.getLeaseElement().getLelId()) {
				retVal = se;
				break;
			}
		}
		
		return retVal;
	}

	/**
	 * @param List of ServiceElementsVO serviceElements
	 * 
	 * Using a List<ServiceElementsVO> serviceElements as the argument
	 * this method looks for any service element with an available operation other than NONE
	 * and returns true if one is found.
	 * 
	 * @return True if there are any changes that would/could be applied
	 * @throws MalBusinessException 
	 */

	@Override
	public boolean elementsHaveChanges(List<ServiceElementsVO> serviceElements)
			throws MalBusinessException {
		boolean hasChanges = false;
		if(serviceElements.size() > 0){
			// because we added a "NONE" operation we now need to do more than just check the length
			for(ServiceElementsVO serviceElement :serviceElements){
				if(!serviceElement.getAvailableOperation().equals(ServiceElementOperations.NONE)){
					hasChanges = true;
					break;
				}
			}
		}
		return hasChanges;
	}

	@Override
	public boolean isGradeGroupOverrides(Long cId, String accountType, String accountCode) 
				throws MalBusinessException {
		boolean hasOverride = false;
		
		int elementsCount = clientServiceElementsDAO.getElementOverrideCountOnGradeGroups(cId, accountType, accountCode);
		
		if (elementsCount > 0) {
			hasOverride = true;
		}
		
		return hasOverride;
		
	}
	
	@Override
	public boolean hasServiceElementsChanged(Long qmdId, Long cid,
			String acctType, String acctCode, String gradeGroup,
			boolean excludeQuoteLvlOverrides) throws MalBusinessException {
		
		QuotationModel model = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		List<ServiceElementsVO> assignedElements = this.getAssignedServiceElements(model);
		// TODO: maybe this is not the right approach, revisit
		List<ExternalAccount> acct = externalAccountDAO.findByAccountCodeAndAccountStatus(acctCode, "O", cid);
		List<ServiceElementsVO> allAvailableElements = this.getAvailableServiceElements(model, acct.get(0), gradeGroup,model.getQuotation().getQuotationProfile(), excludeQuoteLvlOverrides);
		return this.elementsHaveChanges(this.determineElementsWithChanges(assignedElements, allAvailableElements)); 

	}
	
	/**
	 * This method is used to remove service elements from the mul_quote_ele table and the mul_quote_opt table before rebuilding the mul_quote_ele elements when a formal extension is being performed
	 */	
	@Transactional
	public void removeServiceElementsForFormalExtension(QuotationModel newQuotationModel) {
		try {	
			List<MulQuoteEle> mulQuoteEleList = mulQuoteEleDao.findMulQuoteEleByQuotationId(newQuotationModel.getQuotation().getQuoId());

			for (MulQuoteEle mulQuoteEle : mulQuoteEleList) {
				if (!mulQuoteEle.getLeaseElement().getElementType().equals(MalConstants.FINANCE_ELEMENT)){
					mulQuoteOptDao.deleteAll(mulQuoteEle.getMulQuoteOpts());
					mulQuoteEleDao.delete(mulQuoteEle);
				}
			}			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "removing Service Elements for Formal Extension Quote: " + newQuotationModel.getQuotation().getQuoId() }, ex);
		}
	}
	
	/**
	 * This method is used to save service elements to the mul_quote_ele table from the original quote model when a formal extension is being performed
	 */	
	@Transactional
	public void saveServiceElementsForFormalExtension(QuotationModel newQuotationModel, QuotationModel originalQuotationModel) {
		try {
			List<MulQuoteEle> mqes = new ArrayList<MulQuoteEle>();
			
			MulQuoteEle mqe = null;
			List<QuotationElement> quoteElementList = originalQuotationModel.getQuotationElements();
			for (QuotationElement qe : quoteElementList){
				if (!qe.getLeaseElement().getElementType().equals("FINANCE")){
					mqe = createMulQuoteEleFromQuoteElement(qe,newQuotationModel);
					mqes.add(mqe);
				}
			}
			
			// check for informal amendments
			List<InformalAmendment> informalAmendments = dedupeInformalAmendments(informalAmendmentDAO.findByQmdId(originalQuotationModel.getQmdId()));

			// for each informal amendment record
			for(InformalAmendment amendElem : informalAmendments){
				// look within the service element to see if we find one that has an informal amendment
				MulQuoteEle mulQuoteEle = findInformalAmendmentMQE(mqes,amendElem);
				// if we find one (it should be a remove)
				if(!MALUtilities.isEmpty(mulQuoteEle)){
					// check it's status and remove it
					if(amendElem.getAddRemove().equalsIgnoreCase("R")){
						mqes.remove(mulQuoteEle);
					}
				}else{ // if we don't find one (it should be an add)
					// check it's status and add it
					if(amendElem.getAddRemove().equalsIgnoreCase("A")){
						mqes.add(createMulQuoteEleFromInformalAmendment(amendElem,newQuotationModel));
					}
				}
				//TODO: in the future we will have to address billing options (Monthly vs Per Occurrence)
			}
			
			for(MulQuoteEle mulQuoteEle : mqes){
				mulQuoteEleDao.saveAndFlush(mulQuoteEle);
			}
			
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "saving Service Elements for Formal Extension Quote: " + newQuotationModel.getQuotation().getQuoId() }, ex);
		}
	}

	private MulQuoteEle findInformalAmendmentMQE(List<MulQuoteEle> mulQuoteEles, InformalAmendment amendElem){
		MulQuoteEle retVal = null;
		
		for(MulQuoteEle mulQuoteEle : mulQuoteEles){
			if(mulQuoteEle.getLeaseElement().getLelId() == amendElem.getLelLelId().longValue()){
				retVal = mulQuoteEle;
				break;
			}
		}
		
		return retVal;
	}
	
	private MulQuoteEle createMulQuoteEleFromInformalAmendment(InformalAmendment iae, QuotationModel newQuotationModel){
		MulQuoteEle mqe;
		
		mqe = new MulQuoteEle();
		LeaseElement lel = leaseElementDAO.findById(iae.getLelLelId()).orElse(null);
		mqe.setQuotation(newQuotationModel.getQuotation());
		mqe.setLeaseElement(lel);
		mqe.setMandatoryInd("Y");
		mqe.setSelectedInd("Y");
		// This flag is only used for informal ammendment; we are just making sure in as many places 
		// as possible that we get it set to "N"
		mqe.setReCalcNeeded("N");
		mqe.setAdditElem("Y");
		mqe.setAllowMod("N"); // null in dev5
		mqe.setReCalcNeeded("N");
		mqe.setTaxCode(iae.getTaxCode());
		mqe.setElementType(lel.getElementType());
		
		return mqe;
	}
	
	private MulQuoteEle createMulQuoteEleFromQuoteElement(QuotationElement qe, QuotationModel newQuotationModel){
		MulQuoteEle mqe;
		
		mqe = new MulQuoteEle();
		LeaseElement lel = leaseElementDAO.findById(qe.getLeaseElement().getLelId()).orElse(null);
		mqe.setQuotation(newQuotationModel.getQuotation());
		mqe.setLeaseElement(lel);
		mqe.setMandatoryInd("Y");
		mqe.setSelectedInd("Y");
		// This flag is only used for informal ammendment; we are just making sure in as many places 
		// as possible that we get it set to "N"
		mqe.setReCalcNeeded("N");
		mqe.setAdditElem("Y");
		mqe.setAllowMod("N"); // null in dev5
		mqe.setReCalcNeeded("N");
		mqe.setTaxCode(qe.getTaxCode());
		mqe.setElementType(lel.getElementType());
		
		return mqe;
	}

	@Override
	public ServiceElementsVO copyServiceElementsVOWithNewAmounts(ServiceElementsVO serviceElement, QuotationModel quoteModel) throws MalBusinessException{
		ServiceElementsVO copiedServiceElement = new ServiceElementsVO();
		copiedServiceElement.setAvailableOperation(serviceElement.getAvailableOperation());
		copiedServiceElement.setAvailableOperationMarker(serviceElement.getAvailableOperationMarker());
		copiedServiceElement.setBillingOptions(serviceElement.getBillingOptions());
		copiedServiceElement.setDescription(serviceElement.getDescription());
		copiedServiceElement.setEaAccountCode(serviceElement.getEaAccountCode());
		copiedServiceElement.setEaAccountType(serviceElement.getEaAccountType());
		copiedServiceElement.setEaCId(serviceElement.getEaCId());
		copiedServiceElement.setEagId(serviceElement.getEagId());
		copiedServiceElement.setEffectiveBilling(serviceElement.getEffectiveBilling());
		copiedServiceElement.setFinanceParameters(serviceElement.getFinanceParameters());
		copiedServiceElement.setGradeDescription(serviceElement.getGradeDescription());
		copiedServiceElement.setGradeGroupCode(serviceElement.getGradeGroupCode());
		copiedServiceElement.setLelId(serviceElement.getLelId());
		copiedServiceElement.setMonthlyCost(serviceElement.getMonthlyCost());
		copiedServiceElement.setName(serviceElement.getName());
		copiedServiceElement.setProductServiceElementList(serviceElement.getProductServiceElementList());
		copiedServiceElement.setServiceElementList(serviceElement.getServiceElementList());
		copiedServiceElement.setTaxId(serviceElement.getTaxId());
		copiedServiceElement.setTotalCost(serviceElement.getTotalCost());
		
		copiedServiceElement = this.populateServiceElementFields(copiedServiceElement,quoteModel,quoteModel.getQuotation().getExternalAccount());
		
		return copiedServiceElement;
	}

	@Override
	public boolean isServiceElement(QuotationElement quoteElement) {
		LeaseElement leaseElement = quoteElement.getLeaseElement();
		if (leaseElement != null && !leaseElement.getElementType().equals(MalConstants.FINANCE_ELEMENT)) {
			return true;
		}else{
			return false;
		}
	}
	
	public List<Product> findActiveClientProductList(){
		List<Product> activeClientProductList = new ArrayList<Product>();
		try{
			activeClientProductList = productDAO.findActiveClientProductList();
		}catch(Exception ex){
			throw new MalException("generic.error.occured.while", new String[] { "finding active client product list" }, ex);
		}
		return activeClientProductList;	
	}

	@Override
	public boolean hasMaintenance(String accountCode) {
		boolean matchFound = false; 
		
		List<LeaseElement> maintenanceLeaseElements = maintenanceScheduleService.getAllMaintenanceLeaseElements();
		List<ClientServiceElement> serviceElements = this.getClientServiceElementsByClient(CorporateEntity.MAL.getCorpId(), "C", accountCode);
		for(ClientServiceElement eac : serviceElements){
			for(LeaseElement mle : maintenanceLeaseElements){
				if (mle.getLelId() == eac.getClientContractServiceElement().getLeaseElement().getLelId()){
					matchFound = true;
					break;
				}
			}
		}
		return matchFound;
	}

	@Override
	public boolean hasRisk(String accountCode) {
		boolean matchFound = false; 
		
		List<ClientServiceElement> serviceElements = this.getClientServiceElementsByClient(CorporateEntity.MAL.getCorpId(), "C", accountCode);
		for(ClientServiceElement eac : serviceElements){
			if(RISK_MGMT_ELEMENTS.contains(eac.getClientContractServiceElement().getLeaseElement().getElementName())) {
				matchFound = true;
				break;
			}
		}
		return matchFound;
	}

	@Override
	public boolean hasEmergencyRoadSide(String accountCode) {
		boolean matchFound = false; 
		
		List<ClientServiceElement> serviceElements = this.getClientServiceElementsByClient(CorporateEntity.MAL.getCorpId(), "C", accountCode);
		for(ClientServiceElement eac : serviceElements){
			if(ER_ROADSIDE_ELEMENT_TYPES.contains(eac.getClientContractServiceElement().getLeaseElement().getElementType())) {
				matchFound = true;
				break;
			}
		}
		return matchFound;
	}

	@Override
	public boolean hasMaintenance(QuotationModel quoteModel) throws MalBusinessException {
		boolean matchFound = false; 
		
		List<LeaseElement> maintenanceLeaseElements = maintenanceScheduleService.getAllMaintenanceLeaseElements();
		List<ServiceElementsVO> serviceElements = this.getAssignedServiceElements(quoteModel);
		for(ServiceElementsVO eac : serviceElements){
			for(LeaseElement mle : maintenanceLeaseElements){
				if (mle.getLelId() == eac.getLelId()){
					matchFound = true;
					break;
				}
			}
		}
		return matchFound;
	}
	
}