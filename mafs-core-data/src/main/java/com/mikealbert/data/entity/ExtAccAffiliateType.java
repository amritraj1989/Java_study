package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * The persistent class for the EXT_ACC_AFFIL_TYPE database table.
 * 
 */
@Entity
@Table(name="EXT_ACC_AFFIL_TYPE")
public class ExtAccAffiliateType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 333825313120081997L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EAAFT_ID_SEQ")    
    @SequenceGenerator(name="EAAFT_ID_SEQ", sequenceName="EAAFT_ID_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
	@Column(name="EAAFT_ID")
	private Long eaaftId;

	@Column(name="AFFIL_TYPE")
	private String affiliateType;
	
	@Column(name="AFFIL_TYPE_DESC")
	private String affiliateTypeDesc;

	public Long getEaaftId() {
		return eaaftId;
	}

	public void setEaaftId(Long eaaftId) {
		this.eaaftId = eaaftId;
	}

	public String getAffiliateType() {
		return affiliateType;
	}

	public void setAffiliateType(String affiliateType) {
		this.affiliateType = affiliateType;
	}

	public String getAffiliateTypeDesc() {
		return affiliateTypeDesc;
	}

	public void setAffiliateTypeDesc(String affiliateTypeDesc) {
		this.affiliateTypeDesc = affiliateTypeDesc;
	}
}