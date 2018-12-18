package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the EXT_ACC_FIN_FAN database table.
 * 
 */
@Entity
@Table(name="EXT_ACC_FIN_FAN")
public class ExtAccFinFan implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
	@Column(name="EAFF_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EAFF_SEQ")
    @SequenceGenerator(name = "EAFF_SEQ", sequenceName = "EAFF_SEQ", allocationSize = 1)
    private Long eaffId;

	@Column(name="FINFAN_NUMBER")
	private String finFanNumber;
	
    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    private ExternalAccount externalAccount;
    
    @JoinColumn(name = "MAK_ID", referencedColumnName = "MAK_ID")
    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    private Make make;    

	public ExtAccFinFan() {}

	public Long getEaffId() {
		return eaffId;
	}

	public void setEaffId(Long eaffId) {
		this.eaffId = eaffId;
	}

	public String getFinFanNumber() {
		return finFanNumber;
	}

	public void setFinFanNumber(String finFanNumber) {
		this.finFanNumber = finFanNumber;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public Make getMake() {
		return make;
	}

	public void setMake(Make make) {
		this.make = make;
	}
	
}