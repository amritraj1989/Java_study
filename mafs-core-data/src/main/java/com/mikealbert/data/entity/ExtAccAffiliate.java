package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "EXT_ACC_AFFIL")
public class ExtAccAffiliate implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8103353517014825046L;

	@Id
	@Column(name="EAAF_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EAAF_ID_SEQ")
    @SequenceGenerator(name = "EAAF_ID_SEQ", sequenceName = "EAAF_ID_SEQ", allocationSize = 1)
    private Long extAccAffilId;

    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ExternalAccount externalAccount;
    
	@JoinColumn(name = "EAA_EAA_ID", referencedColumnName = "EAA_ID")
    @OneToOne(fetch = FetchType.EAGER)
    private ExtAccAddress externalAccountAddress;
    
    @Size(max = 80)
    @Column(name = "AFFILIATE_NAME")
    private String affiliateName;
	
    @Size(max = 25)
    @Column(name = "TAX_REG_NO")
    private String taxRegNo;
    
	@JoinColumn(name = "EAAFT_EAAFT_ID", referencedColumnName = "EAAFT_ID")
    @OneToOne(fetch = FetchType.EAGER)
    private ExtAccAffiliateType externalAccountAffiliateType;
	
	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public Long getExtAccAffilId() {
		return extAccAffilId;
	}

	public void setExtAccAffilId(Long extAccAffilId) {
		this.extAccAffilId = extAccAffilId;
	}

	public ExtAccAddress getExternalAccountAddress() {
		return externalAccountAddress;
	}

	public void setExternalAccountAddress(ExtAccAddress externalAccountAddress) {
		this.externalAccountAddress = externalAccountAddress;
	}

	public String getAffiliateName() {
		return affiliateName;
	}

	public void setAffiliateName(String affiliateName) {
		this.affiliateName = affiliateName;
	}

	public String getTaxRegNo() {
		return taxRegNo;
	}

	public void setTaxRegNo(String taxRegNo) {
		this.taxRegNo = taxRegNo;
	}

	public ExtAccAffiliateType getExternalAccountAffiliateType() {
		return externalAccountAffiliateType;
	}

	public void setExternalAccountAffiliateType(
			ExtAccAffiliateType externalAccountAffiliateType) {
		this.externalAccountAffiliateType = externalAccountAffiliateType;
	}
}
