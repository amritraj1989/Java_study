package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to CONTACT_NUMBERS table
 * @author sibley
 */
@Entity
@Table(name = "CONTACT_NUMBERS")
public class PhoneNumber extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CNR_SEQ")    
	@SequenceGenerator(name="CNR_SEQ", sequenceName="CNR_SEQ", allocationSize=1)   
	@NotNull
	@Column(name = "CNR_ID")
	private Long cnrId;

	@JoinColumn(name = "CNC_CODE", referencedColumnName = "NUMBER_TYPE")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private PhoneNumberType type;

	@Size(max = 10)
	@Column(name = "EXTERNAL_DIAL_CODE")
	private String externalDialCode;

	@Size(max = 100)
	@Column(name = "CONTACT_COUNTRY_CODE")
	private String countryCode;

	@Size(max = 100)
	@Column(name = "CONTACT_AREA_CODE")
	private String areaCode;

	@Size(max = 100)
	@Column(name = "CONTACT_NUMBER")
	private String number;

	@Size(max = 40)
	@Column(name = "CONTACT_EXTENSION_NUMBER")
	private String extensionNumber;

	@MANotNull(label = "Preferred Indicator")   
	@Size(max = 1)
	@Column(name = "PREFERRED_IND")
	private String preferredInd;

	@JoinColumn(name = "DRV_DRV_ID", referencedColumnName = "DRV_ID")
	@ManyToOne(optional = true)
	private Driver driver;  

	public PhoneNumber() {}

	public PhoneNumber(Long cnrId, PhoneNumberType type, String preferredInd) {
		this.cnrId = cnrId;
		this.type = type;
		this.preferredInd = preferredInd;
	}

	public Long getCnrId() {
		return cnrId;
	}

	public void setCnrId(Long cnrId) {
		this.cnrId = cnrId;
	}

	public PhoneNumberType getType() {
		return type;
	}

	public void setType(PhoneNumberType type) {
		this.type = type;
	}

	public String getExternalDialCode() {
		return externalDialCode;
	}

	public void setExternalDialCode(String externalDialCode) {
		this.externalDialCode = externalDialCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getExtensionNumber() {
		return extensionNumber;
	}

	public void setExtensionNumber(String extensionNumber) {
		this.extensionNumber = extensionNumber;
	}

	public String getPreferredInd() {
		return preferredInd;
	}

	public void setPreferredInd(String preferredInd) {
		this.preferredInd = preferredInd;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.cnrId != null ? this.cnrId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof PhoneNumber)) {
			return false;
		}
		PhoneNumber other = (PhoneNumber) object;
		if ((this.cnrId == null && other.cnrId != null) || (this.cnrId != null && !this.cnrId.equals(other.cnrId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.mikealbert.entity.PhoneNumber[ cnrId=" + cnrId + " ]";
	}



}
