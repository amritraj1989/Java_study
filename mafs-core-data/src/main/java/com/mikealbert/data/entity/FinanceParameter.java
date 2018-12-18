package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the FINANCE_PARAMETERS database table.
 * 
 */
@Entity
@Table(name = "FINANCE_PARAMETERS")
public class FinanceParameter implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PARAMETER_ID")
    private String parameterId;

    @Column(name = "CVALUE")
    private String cvalue;

    @Column(name = "DESCRIPTION")
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "EFFECTIVE_FROM")
    private Date effectiveFrom;

    @Column(name = "NVALUE")
    private BigDecimal nvalue;

    @Column(name = "PARAMETER_KEY")
    private String parameterKey;

    @Column(name = "STATUS")
    private String status;

    @JoinColumn(name = "FPC_FPC_ID", referencedColumnName = "FPC_ID")
    @ManyToOne(optional = true)
    private FinanceParameterCategory financeParamCategory;
    
    
    // bi-directional many-to-one association to WorkClassAuthority
    @OneToMany(mappedBy = "financeParameter")
    private List<WorkClassAuthority> workClassAuthorities;

    public FinanceParameter() {
    }

    public String getParameterId() {
	return this.parameterId;
    }

    public void setParameterId(String parameterId) {
	this.parameterId = parameterId;
    }

    public String getCvalue() {
	return this.cvalue;
    }

    public void setCvalue(String cvalue) {
	this.cvalue = cvalue;
    }

    public String getDescription() {
	return this.description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public Date getEffectiveFrom() {
	return this.effectiveFrom;
    }

    public void setEffectiveFrom(Date effectiveFrom) {
	this.effectiveFrom = effectiveFrom;
    }

    public BigDecimal getNvalue() {
	return this.nvalue;
    }

    public void setNvalue(BigDecimal nvalue) {
	this.nvalue = nvalue;
    }

    public String getParameterKey() {
	return this.parameterKey;
    }

    public void setParameterKey(String parameterKey) {
	this.parameterKey = parameterKey;
    }

    public String getStatus() {
	return this.status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public List<WorkClassAuthority> getWorkClassAuthorities() {
	return this.workClassAuthorities;
    }

    public void setWorkClassAuthorities(List<WorkClassAuthority> workClassAuthorities) {
	this.workClassAuthorities = workClassAuthorities;
    }

	public FinanceParameterCategory getFinanceParamCategory() {
		return financeParamCategory;
	}

	public void setFinanceParamCategory(
			FinanceParameterCategory financeParamCategory) {
		this.financeParamCategory = financeParamCategory;
	}

}