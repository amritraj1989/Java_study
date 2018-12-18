package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ANALYSIS_CODES")
public class AnalysisCode implements Serializable {
    private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AnalysisCodePK id;

    @Column(name = "ADDRESS_TYPE")
    private String addressType;
            
    @Column(name = "COST_DB_CATEGORY")
    private Long costDbCategory;
   
    @Column(name = "DEBIT_CREDIT_MEMO_IND")
    private String debitCreditMemoInd;    

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "INTEREST_REVENUE_IND")
    private String interestRevenueInd;
    
    @Column(name = "LOCATION_TYPE")
    private String locationType;
    
    @Column(name = "TAX_ID")
    private Long taxInd;
    
	public AnalysisCodePK getId() {
		return id;
	}

	public void setId(AnalysisCodePK id) {
		this.id = id;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public Long getCostDbCategory() {
		return costDbCategory;
	}

	public void setCostDbCategory(Long costDbCategory) {
		this.costDbCategory = costDbCategory;
	}

	public String getDebitCreditMemoInd() {
		return debitCreditMemoInd;
	}

	public void setDebitCreditMemoInd(String debitCreditMemoInd) {
		this.debitCreditMemoInd = debitCreditMemoInd;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInterestRevenueInd() {
		return interestRevenueInd;
	}

	public void setInterestRevenueInd(String interestRevenueInd) {
		this.interestRevenueInd = interestRevenueInd;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public Long getTaxInd() {
		return taxInd;
	}

	public void setTaxInd(Long taxInd) {
		this.taxInd = taxInd;
	}    


}
