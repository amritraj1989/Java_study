package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.ClientAgreement;
import com.mikealbert.data.entity.ClientContractServiceElement;
import com.mikealbert.data.entity.ClientServiceElement;
import com.mikealbert.data.entity.ClientServiceElementParameter;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountGradeGroup;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MulQuoteEle;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.data.entity.ContractAgreement;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.ClientServiceElementParameterVO;
import com.mikealbert.data.vo.ServiceElementsVO;

public interface ServiceElementService {
	
	public static final List<String> MAINT_MGMT_ELEMENTS = Arrays.asList("CVMAINT","MAINT_EX_R","MAINT_MGMT","MM","NAP","NATL_ACCT","PREV_MAINT");
	public static final List<String> RISK_MGMT_ELEMENTS = Arrays.asList("RISK_ACCDT", "RISK_RENT", "RISK_REPR", "RISK_SUB");	
	public static final List<String> ER_ROADSIDE_ELEMENT_TYPES = Arrays.asList("BREAKDOWN");
	
	public List<ClientAgreement> getClientAgreementsByClient(Long cId, String accountType, String accountCode);
	public List<ClientServiceElement> getClientServiceElementsByClient(Long cId, String accountType, String accountCode);
	public List<ClientServiceElement> getClientServiceElementsHistoryByClient(Long cId, String accountType, String accountCode, Long clientServiceElementTypeId);
	public List<ClientServiceElement> getGradeGroupServiceElementsHistoryByClient(Long cId, String accountType, String accountCode, Long clientServiceElementTypeId);
	public ClientServiceElement getClientServiceElementByClientAndElement(Long cId, String accountType, String accountCode, Long leaseElementId);
	public List<ClientContractServiceElement> getClientContractServiceElementsByClient(Long cId, String accountType, String accountCode);
	public List<ContractAgreement> getAllContractAgreements();
	public void saveUpdateClientServiceElements(List<ClientServiceElement> clientServiceElementList, String parameterType) throws MalBusinessException;
	public ExternalAccount getRootParentAccount(Long cId, String accountType, String accountCode);
	public void saveOrUpdateClientAgreement(ClientAgreement clientAgreement, ContractAgreement contractAgreement);
	public void saveOrUpdateClientContractServiceElement(ClientContractServiceElement clientContractServiceElement, LeaseElement serviceElement);
	public List<ClientServiceElementParameterVO> getServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId);
	public BigDecimal getParameterDefaultValuesSum(Long leaseElementId, Long clientServiceElementId);
	public BigDecimal getParameterClientValuesSum(Long clientServiceElementId);
	public void saveOrUpdateClientServiceElementParameters(List<ClientServiceElementParameterVO> clientServiceElementParameterList, Boolean productOverride);
	public List<LeaseElement> findAllFilterByFinanceTypeAndElementList(String elementNameParam, List<Long> listOfExcludedElements, Pageable pageable);
	public int findAllFilterByFinanceTypeAndElementListCount(String elementNameParam, List<Long> listOfExcludedElements);
	public long findByAgreementNumberCount(String agreementNumber, Long clientAgreementId);
	public ClientAgreement getClientAgreementByAgreementNumber(String agreementNumber);
	public LeaseElement getLeaseElementByName(LeaseElement leaseElement);
	public long getClientAgreementByContractAgreementAndClientCount(String contractAgreement, Long cId, String accountType, String accountCode);
	public long getContractElementByClientElementNameAndClientCount(String elementName, Long cId, String accountType, String accountCode);
	public ClientServiceElementParameter getClientServiceElementParameter(Long clientServiceElementId, Long formulaParameterId);
	public String convertBillingOptionToReadableString(String billingOption);
	public List<ClientServiceElementParameter> findByClientServiceElementId(Long clientServiceElementId);
	public List<ServiceElementsVO> getGradeGroupServiceElements(Long cId, String accountType, String accountCode);
	public void saveOrUpdateGradeGroupServiceElementParameters(List<ClientServiceElementParameterVO> clientServiceElementParameterVOList, Boolean productOverride);
	public List<ClientServiceElementParameterVO> getGradeGroupServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId);
	public List<ClientServiceElement> getGradeGroupServiceElementsByClientAndGradeGroup(Long cId, String accountType, String accountCode, Long gradeGroupId);
	public List<ClientServiceElement> getGradeGroupServiceElementsByClientAndGradeGroupWRemoved(Long cId, String accountType, String accountCode, Long gradeGroupId);
	public ClientServiceElement getGradeGroupServiceElementByGradeGroupAndElement(Long cId, String accountType, String accountCode, Long gradeGroupId, Long leaseElementId);
	public ExternalAccountGradeGroup getEagByExternalAccountCodeAndType(Long cId, String accountType, String accountCode, String gradeGroup);
	public ClientContractServiceElement getClientContractServiceElement(Long clientContractServiceElementId);
	public ClientServiceElement getClientServiceElement(Long clientServiceElementId);
	public ClientServiceElement getClientProductServiceElementByClientAndElement(Long cId, String accountType, String accountCode,Long leaseElementId, String productCode);
	public void removeProductClientServiceElement(ClientServiceElement clientElement, String product, String parameterType) throws MalBusinessException;
	public List<ClientServiceElement> getProductServiceElementsByAccountIncludeRemoved(Long cId, String accountType, String accountCode);
	public List<ClientServiceElement> getProductServiceElementsByAccount(Long cId, String accountType, String accountCode);	
	public List<ClientServiceElement> getClientServiceElementsByClientAndElementIncludeRemoved(Long cId, String accountType, String accountCode,Long leaseElementId);
	public void removeGradeGroupClientServiceElement(ClientServiceElement gradeGroupClientElement) throws MalBusinessException ;
	public ClientServiceElement constructClientServiceElement(ClientContractServiceElement contractedElement, String selectedBillingOption, CorporateEntity corp, String accountCode, String selectedProduct);
	public ClientServiceElement constructClientServiceElement(ClientContractServiceElement contractedElement, String selectedBillingOption, CorporateEntity corp, String accountCode, String selectedProduct, String gradeGroup);
	public void deleteProductRemovalServiceElement(ClientServiceElement clientElement, String parameterType) throws MalBusinessException;
	public List<ClientServiceElementParameterVO> getProductServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId);
	public List<ClientServiceElement> getGradeGroupProductServiceElementsByAccountAndGradeGroup(Long cId, String accountType, String accountCode, Long gradeGroupId);
	public ClientServiceElement getGradeGroupProductServiceElementByGradeGroupAndElement(Long cId, String accountType, String accountCode, Long gradeGroupId, Long leaseElementId, String productCode);
	public List<ClientServiceElementParameterVO> getGradeGroupProductServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId);
	public ClientServiceElement getGradeGroupProductServiceElementByGradeGroupAndElementWRemoved(Long cId, String accountType, String accountCode, Long gradeGroupId, Long leaseElementId, String productCode);
	public ClientServiceElement getClientProductServiceElementByGradeGroupAndElementWRemoved(Long cId, String accountType, String accountCode, Long leaseElementId, String productCode);
	public List<ServiceElementsVO> determineElementsWithChanges(List<ServiceElementsVO> assignedElements, List<ServiceElementsVO> allAvailableElements) throws MalBusinessException;
	public List<ServiceElementsVO> determineElementsWithChanges(QuotationModel qm) throws MalBusinessException;
	public boolean elementsHaveChanges(List<ServiceElementsVO> serviceElements) throws MalBusinessException;
		
	public boolean isServiceElement(QuotationElement quoteElement);
	public BigDecimal getServiceElementCost(QuotationModel quotationModel);
	public BigDecimal getServiceElementMonthlyCost(QuotationModel quotationModel);
	public List<QuotationElement> getServiceElementDetails(QuotationModel quotationModel) throws MalBusinessException;
	public Date getBillingDate(QuotationElement qe);
	public BigDecimal getRentalPeriods(QuotationElement qe, QuotationModel quotationModel, Boolean isExistingEle);

	public boolean hasServiceElementsChanged(Long qmdId, Long cid, String acctType, String acctCode, String gradeGroup, boolean ignoreQuoteLvlDifferences) throws MalBusinessException;
	
	public List<ServiceElementsVO> getAvailableServiceElements(QuotationModel quoteModel) throws MalBusinessException;
	public List<ServiceElementsVO> getAvailableServiceElements(QuotationModel quoteModel, ExternalAccount extAccount, String gradeGroup, QuotationProfile quoteProfile) throws MalBusinessException;
	public List<ServiceElementsVO> getAvailableServiceElements(QuotationModel quoteModel, ExternalAccount extAccount, String gradeGroup, QuotationProfile quoteProfile, boolean ignoreQuoteLvlDifferences) throws MalBusinessException;

	public List<ServiceElementsVO> getAssignedServiceElements(QuotationModel quoteModel) throws MalBusinessException;
	
	public List<MulQuoteEle> getMulQuoteElemsForServiceElements(QuotationModel quotationModel, List<ServiceElementsVO> changesList);
	
	public void saveOrUpdateServiceElements(QuotationModel quotationModel, List<ServiceElementsVO> changesList) throws MalBusinessException;
	public List<ServiceElementsVO> findElementsWithChanges (List<ServiceElementsVO> assignedElements,List<ServiceElementsVO> allAvailableElements) throws MalBusinessException;

	public boolean isGradeGroupOverrides(Long cId, String accountType, String accountCode) throws MalBusinessException;
	public void removeServiceElementsForFormalExtension(QuotationModel newQuotationModel) throws MalBusinessException;
	public void saveServiceElementsForFormalExtension(QuotationModel newQuotationModel, QuotationModel originalQuotationModel) throws MalBusinessException;
	public ServiceElementsVO populateServiceElementFields(ServiceElementsVO serviceElement, QuotationModel quotationModel, ExternalAccount extAccount) throws MalBusinessException;
	public ServiceElementsVO populateServiceElementFields(ServiceElementsVO serviceElement, QuotationModel quotationModel, ExternalAccount extAccount, boolean ignoreQuoteLvlDifferences) throws MalBusinessException;

	public ServiceElementsVO copyServiceElementsVOWithNewAmounts(ServiceElementsVO serviceElement, QuotationModel quoteModel) throws MalBusinessException;
	
	public List<Product> findActiveClientProductList();
	
	public boolean hasMaintenance(QuotationModel quoteModel) throws MalBusinessException;	
	public boolean hasMaintenance(String accountCode);
	public boolean hasRisk(String accountCode);
	public boolean hasEmergencyRoadSide(String accountCode);
	
}
