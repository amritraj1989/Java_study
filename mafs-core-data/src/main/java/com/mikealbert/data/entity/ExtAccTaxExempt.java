package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="EXT_ACC_TAX_EXEMPT")
public class ExtAccTaxExempt extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EATE_SEQ")    
    @SequenceGenerator(name="EATE_SEQ", sequenceName="EATE_SEQ", allocationSize=1)
    @NotNull
	@Column(name="EATE_ID")
	private long eateId;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_FROM", nullable = false)
	private Date effectiveFrom;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_TO", nullable = false)
	private Date effectiveTo;

	@Column(name="TAX_EXEMPT_NO", nullable = false)
	private String taxExemptNo;

    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false)
    private ExternalAccount client;
    
    @JoinColumns({
    	@JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE"),
    	@JoinColumn(name = "REGION_CODE", referencedColumnName = "REGION_CODE")})
    @ManyToOne(optional = false)
    private RegionCode regionCode;
	
    public ExtAccTaxExempt() {
    }

	public long getEateId() {
		return this.eateId;
	}

	public void setEateId(long eateId) {
		this.eateId = eateId;
	}

	public Date getEffectiveFrom() {
		return this.effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveTo() {
		return this.effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public String getTaxExemptNo() {
		return this.taxExemptNo;
	}

	public void setTaxExemptNo(String taxExemptNo) {
		this.taxExemptNo = taxExemptNo;
	}

	public ExternalAccount getClient() {
		return client;
	}

	public void setClient(ExternalAccount client) {
		this.client = client;
	}

	public RegionCode getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(RegionCode regionCode) {
		this.regionCode = regionCode;
	}

}
