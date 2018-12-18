package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Mapped to CLIENT_SERVICE_ELEMENTS table
 */

@Entity
@Table(name = "CLIENT_SERVICE_ELEMENTS")
public class ClientServiceElement extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CSE_SEQ")    
    @SequenceGenerator(name="CSE_SEQ", sequenceName="CSE_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
	@Column(name = "CSE_ID")
	private Long clientServiceElementId;
	
	@Column(name = "START_DATE")
    private Date startDate;
	
	@Column(name = "END_DATE")
    private Date endDate;
	
	@Column(name = "BILLING_OPTION")
	private String billingOption;
	
	@Column(name = "REMOVED_FLAG")
	private String removedFlag;
	
    @ManyToOne
	@JoinColumn(name="PRD_PRODUCT_CODE", referencedColumnName = "PRODUCT_CODE")
	private Product product;	
	
    @JoinColumn(name = "CSET_CSET_ID", referencedColumnName = "CSET_ID")
    @ManyToOne(optional = true)
    private ClientServiceElementType clientServiceElementType;
    
    @JoinColumn(name = "EAG_EAG_ID", referencedColumnName = "EAG_ID")
    @ManyToOne(optional = true)
    private ExternalAccountGradeGroup externalAccountGradeGroup;	
    
    @ManyToOne
	@JoinColumn(name="CCSE_CCSE_ID", referencedColumnName = "CCSE_ID")
	private ClientContractServiceElement clientContractServiceElement;
    
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = true)
    private ExternalAccount externalAccount;
    
    public ClientServiceElement(){
    }

	public Long getClientServiceElementId() {
		return clientServiceElementId;
	}

	public void setClientServiceElementId(Long clientServiceElementId) {
		this.clientServiceElementId = clientServiceElementId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ClientContractServiceElement getClientContractServiceElement() {
		return clientContractServiceElement;
	}

	public void setClientContractServiceElement(
			ClientContractServiceElement clientContractServiceElement) {
		this.clientContractServiceElement = clientContractServiceElement;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public String getBillingOption() {
		return billingOption;
	}

	public void setBillingOption(String billingOption) {
		this.billingOption = billingOption;
	}

	public ClientServiceElementType getClientServiceElementType() {
		return clientServiceElementType;
	}

	public void setClientServiceElementType(
			ClientServiceElementType clientServiceElementType) {
		this.clientServiceElementType = clientServiceElementType;
	}

	public ExternalAccountGradeGroup getExternalAccountGradeGroup() {
		return externalAccountGradeGroup;
	}

	public void setExternalAccountGradeGroup(
			ExternalAccountGradeGroup externalAccountGradeGroup) {
		this.externalAccountGradeGroup = externalAccountGradeGroup;
	}

	public String getRemovedFlag() {
		return removedFlag;
	}

	public void setRemovedFlag(String removedFlag) {
		this.removedFlag = removedFlag;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
