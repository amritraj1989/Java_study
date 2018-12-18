package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the MAKE_COUNTRY_SUPPLIERS database table.
 * 
 */
@Entity
@Table(name="MAKE_COUNTRY_SUPPLIERS")
public class MakeCountrySuppliers extends BaseEntity  implements Serializable {

	private static final long serialVersionUID = -8847319062418998320L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MCS_SEQ")
    @SequenceGenerator(name = "MCS_SEQ", sequenceName = "MCS_SEQ", allocationSize = 1)
    @Column(name = "MCS_ID")
    private Long mcsId;
    
	@Column(name="MAKE_CODE")
	private String makeCode;
	
	@Column(name="SUP_SUP_ID")
	private Long supId;

	@Column(name="COUNTRY_CODE")
	private String countryCode;
	
	@Column(name="BAILMENT_DEALER_CODE")
	private String bailmentDealerCode;
	
	@Column(name="DEFAULT_YN")
	private String defaultYesNo;

	public Long getMcsId() {
		return mcsId;
	}

	public void setMcsId(Long mcsId) {
		this.mcsId = mcsId;
	}

	public String getMakeCode() {
		return makeCode;
	}

	public void setMakeCode(String makeCode) {
		this.makeCode = makeCode;
	}

	public Long getSupId() {
		return supId;
	}

	public void setSupId(Long supId) {
		this.supId = supId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getBailmentDealerCode() {
		return bailmentDealerCode;
	}

	public void setBailmentDealerCode(String bailmentDealerCode) {
		this.bailmentDealerCode = bailmentDealerCode;
	}

	public String getDefaultYesNo() {
		return defaultYesNo;
	}

	public void setDefaultYesNo(String defaultYesNo) {
		this.defaultYesNo = defaultYesNo;
	}
	

}