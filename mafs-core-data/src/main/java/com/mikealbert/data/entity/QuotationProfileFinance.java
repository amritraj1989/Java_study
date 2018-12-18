package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the QUOTATION_PROFILE_FINANCES database table.
 * 
 */
@Entity
@Table(name="QUOTATION_PROFILE_FINANCES")
public class QuotationProfileFinance implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private QuotationProfileFinancePK id;

	private String cvalue;

	private String description;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_FROM")
	private Date effectiveFrom;

	private BigDecimal nvalue;

	@Column(name="PARAMETER_KEY")
	private String parameterKey;

	private String status;

	//bi-directional many-to-one association to FinanceParameter
    @ManyToOne
	@JoinColumn(name="PARAMETER_ID", insertable=false, updatable=false)
	private FinanceParameter financeParameter;

    public QuotationProfileFinance() {
    }

	public QuotationProfileFinancePK getId() {
		return this.id;
	}

	public void setId(QuotationProfileFinancePK id) {
		this.id = id;
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

	public FinanceParameter getFinanceParameter() {
		return this.financeParameter;
	}

	public void setFinanceParameter(FinanceParameter financeParameter) {
		this.financeParameter = financeParameter;
	}
	
}