package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.Size;

/**
 * Mapped to CLIENT_FINANCES table
 */

@Entity
@Table(name = "CLIENT_FINANCES")
public class ClientFinance extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1530470629170383070L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CF_SEQ")    
    @SequenceGenerator(name="CF_SEQ", sequenceName="CF_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
	@Column(name = "CF_ID")
	private Long clientFinanceId;
	
	@Size(min = 1, max = 25)
    @NotNull
    @Column(name = "STATUS")
    private String status;
    
    @NotNull
    @Column(name = "LAST_UPDATED")
    private Date lastUpdated;
    
    @Column(name = "NVALUE")
    private BigDecimal nvalue;
    
    @Column(name = "COST_CENTRE_CODE")
    private String costCentreCode;
    
    @Size(max = 40)
    @Column(name = "CVALUE")
    private String cvalue;
    
    @ManyToOne
	@JoinColumn(name="PARAMETER_ID")
	private FinanceParameter financeParameter;
    
    @JoinColumn(name = "CFT_CFT_ID", referencedColumnName = "CFT_ID")
    @ManyToOne(optional = true)
    private ClientFinanceType clientFinanceType;
    
    @JoinColumn(name = "EAG_EAG_ID", referencedColumnName = "EAG_ID")
    @ManyToOne(optional = true)
    private ExternalAccountGradeGroup externalAccountGradeGroup;
    
    /*@JoinColumns({
    	@JoinColumn(name = "COST_CENTRE_CODE", referencedColumnName = "COST_CENTRE_CODE"),
    	@JoinColumn(name = "EA_C_ID", referencedColumnName = "EA_C_ID", insertable = false, updatable = false),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "EA_ACCOUNT_TYPE", insertable = false, updatable = false),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "EA_ACCOUNT_CODE", insertable = false, updatable = false)})
    @ManyToOne(optional = true)
    private CostCentreCode costCentreCode;*/
    
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = true)
    private ExternalAccount externalAccount;
    
    public ClientFinance(){
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public BigDecimal getNvalue() {
		return nvalue;
	}

	public void setNvalue(BigDecimal nvalue) {
		this.nvalue = nvalue;
	}

	public String getCvalue() {
		return cvalue;
	}

	public void setCvalue(String cvalue) {
		this.cvalue = cvalue;
	}

	public FinanceParameter getFinanceParameter() {
		return financeParameter;
	}

	public void setFinanceParameter(FinanceParameter financeParameter) {
		this.financeParameter = financeParameter;
	}

	public Long getClientFinanceId() {
		return clientFinanceId;
	}

	public void setClientFinanceId(Long clientFinanceId) {
		this.clientFinanceId = clientFinanceId;
	}

	public ClientFinanceType getClientFinanceType() {
		return clientFinanceType;
	}

	public void setClientFinanceType(ClientFinanceType clientFinanceType) {
		this.clientFinanceType = clientFinanceType;
	}

	public ExternalAccountGradeGroup getExternalAccountGradeGroup() {
		return externalAccountGradeGroup;
	}

	public void setExternalAccountGradeGroup(
			ExternalAccountGradeGroup externalAccountGradeGroup) {
		this.externalAccountGradeGroup = externalAccountGradeGroup;
	}

	public String getCostCentreCode() {
		return costCentreCode;
	}

	public void setCostCentreCode(String costCentreCode) {
		this.costCentreCode = costCentreCode;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

}
