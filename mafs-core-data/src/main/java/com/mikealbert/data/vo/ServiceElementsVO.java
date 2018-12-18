package com.mikealbert.data.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.ClientServiceElement;
import com.mikealbert.data.enumeration.ServiceElementOperations;

public class ServiceElementsVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long eagId;
	private String gradeGroupCode;
	private String gradeDescription;
	private List<ClientServiceElement> serviceElementList;
	private List<ClientServiceElement> productServiceElementList;
	private Long eaCId;
	private String eaAccountType;
	private String eaAccountCode;
	
	private String name;
	private String description;
	private ServiceElementOperations availableOperation;
	private Long lelId;
	private String availableOperationMarker;
	
	private Long taxId;
	private String billingOptions;
	private Date effectiveBilling;
	private BigDecimal monthlyCost;
	private BigDecimal displayOnlyMonthlyCost;
	private BigDecimal totalCost;
	private List<FinanceParameterVO> financeParameters;
	
	private String taxCode;
	private BigDecimal taxRate;

	public ServiceElementsVO(){}

	public Long getEagId() {
		return eagId;
	}

	public void setEagId(Long eagId) {
		this.eagId = eagId;
	}

	public String getGradeGroupCode() {
		return gradeGroupCode;
	}

	public void setGradeGroupCode(String gradeGroupCode) {
		this.gradeGroupCode = gradeGroupCode;
	}

	public String getGradeDescription() {
		return gradeDescription;
	}

	public void setGradeDescription(String gradeDescription) {
		this.gradeDescription = gradeDescription;
	}

	public List<ClientServiceElement> getServiceElementList() {
		return serviceElementList;
	}

	public void setServiceElementList(List<ClientServiceElement> serviceElementList) {
		this.serviceElementList = serviceElementList;
	}

	public Long getEaCId() {
		return eaCId;
	}

	public void setEaCId(Long eaCId) {
		this.eaCId = eaCId;
	}

	public String getEaAccountType() {
		return eaAccountType;
	}

	public void setEaAccountType(String eaAccountType) {
		this.eaAccountType = eaAccountType;
	}

	public String getEaAccountCode() {
		return eaAccountCode;
	}

	public void setEaAccountCode(String eaAccountCode) {
		this.eaAccountCode = eaAccountCode;
	}

	// new properties from ServiceElementVO in Vision
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getEffectiveBilling() {
		return effectiveBilling;
	}

	public void setEffectiveBilling(Date effectiveBilling) {
		this.effectiveBilling = effectiveBilling;
	}

	public BigDecimal getMonthlyCost() {
		return monthlyCost;
	}

	public void setMonthlyCost(BigDecimal monthlyCost) {
		this.monthlyCost = monthlyCost;
	}

	public BigDecimal getDisplayOnlyMonthlyCost() {
		return displayOnlyMonthlyCost;
	}

	public void setDisplayOnlyMonthlyCost(BigDecimal displayOnlyMonthlyCost) {
		this.displayOnlyMonthlyCost = displayOnlyMonthlyCost;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	//TODO: should we populate finance parameters?
	public List<FinanceParameterVO> getFinanceParameters() {
		return financeParameters;
	}

	public void setFinanceParameters(List<FinanceParameterVO> financeParameters) {
		this.financeParameters = financeParameters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getLelId() {
		return lelId;
	}

	public void setLelId(Long lelId) {
		this.lelId = lelId;
	}

	public String getBillingOptions() {
		return billingOptions;
	}

	public void setBillingOptions(String billingOptions) {
		this.billingOptions = billingOptions;
	}

	public Long getTaxId() {
		return taxId;
	}

	public void setTaxId(Long taxId) {
		this.taxId = taxId;
	}

	public ServiceElementOperations getAvailableOperation() {
		return availableOperation;
	}

	public void setAvailableOperation(ServiceElementOperations availableOperation) {
		this.availableOperation = availableOperation;
	}

	public String getAvailableOperationMarker() {
		return availableOperationMarker;
	}

	public void setAvailableOperationMarker(String availableOperationMarker) {
		this.availableOperationMarker = availableOperationMarker;
	}

	public List<ClientServiceElement> getProductServiceElementList() {
		return productServiceElementList;
	}

	public void setProductServiceElementList(
			List<ClientServiceElement> productServiceElementList) {
		this.productServiceElementList = productServiceElementList;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	
}
