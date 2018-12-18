package com.mikealbert.data.entity;

import java.io.Serializable;
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

@Entity
@Table(name="EXT_ACC_ADDRESSES")
public class ExtAccAddress extends Address implements Serializable {
	private static final long serialVersionUID = -4836589793398329110L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EAA_ID_SEQ")    
    @SequenceGenerator(name="EAA_ID_SEQ", sequenceName="EAA_ID_SEQ", allocationSize=1)
    @NotNull
	@Column(name="EAA_ID")
	private Long eaaId;
	
	@Size(max = 1)
    @Column(name = "DEFAULT_IND")
    private String defaultInd;
	
	 @Size(max = 80)
    @Column(name = "ADDRESS_LINE_3")
    private String addressLine3;
	
    @Size(max = 80)
    @Column(name = "ADDRESS_LINE_4")
    private String addressLine4;
	    
	@JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false)
    private ExternalAccount externalAccount;
    
    
    public ExtAccAddress() {
    }


	public Long getEaaId() {
		return eaaId;
	}


	public void setEaaId(Long eaaId) {
		this.eaaId = eaaId;
	}

	public String getDefaultInd() {
		return defaultInd;
	}


	public void setDefaultInd(String defaultInd) {
		this.defaultInd = defaultInd;
	}


	public String getAddressLine3() {
		return addressLine3;
	}


	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}


	public String getAddressLine4() {
		return addressLine4;
	}


	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}


	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}


}
