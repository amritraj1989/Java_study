package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
* Mapped to CONTRACTStable.
* @author sibley
*/
@Entity
@Table(name = "CONTRACTS")
public class Contract extends BaseEntity implements Serializable  {
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CON_SEQ")    
    @SequenceGenerator(name="CON_SEQ", sequenceName="CON_SEQ", allocationSize=1)  
    @Basic(optional = false)
    @NotNull
    @Column(name = "CON_ID")
    private Long conId;  
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "C_ID")
    private Long cId;      
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "CONTRACT_NO")
    private String contractNo;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "REV_NO")
    private Long revNo;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "REV_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date revDate; 
    
    @Size(max = 25)
    @Column(name = "THIRD_PARTY_CONTRACT_NO")
    private String thirdPartyContractNo;
    
    @Size(max = 1)
    @Column(name = "LEASE_FINALISATION_IND")
    private String leaseFinalisationInd;
    
    @Size(max = 1)
    @Column(name = "SETTLEMENT_STATUS")
    private String settlementStatus;
    
    @Size(max = 200)
    @Column(name = "ET_PROGRAM_ELIGIBILITY")
    private String etProgramEligibility;
    
    @Size(max = 200)
    @Column(name = "XSM_PROGRAM_ELIGIBILITY")
    private String xsmProgramEligibility;
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "FMV_OVERRIDE")
    private BigDecimal fmvOverride;
           
    @OneToMany(mappedBy = "contract", fetch = FetchType.EAGER)
    private List<ContractLine> contractLineList;      
    
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ExternalAccount externalAccount;
    
   
    public Contract() {}

    public Contract(Long conId) {
        this.conId = conId;
    }

    public Contract(Long conId, Long cId, String contractNo, String description, Long revNo, Date revDate) {
        this.conId = conId;
        this.setcId(cId);
        this.setContractNo(contractNo);
        this.setDescription(description);
        this.setRevNo(revNo);
        this.setRevDate(revDate);
    }
    
    public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getRevNo() {
		return revNo;
	}

	public void setRevNo(Long revNo) {
		this.revNo = revNo;
	}

	public Date getRevDate() {
		return revDate;
	}

	public void setRevDate(Date revDate) {
		this.revDate = revDate;
	}

	public String getThirdPartyContractNo() {
		return thirdPartyContractNo;
	}

	public void setThirdPartyContractNo(String thirdPartyContractNo) {
		this.thirdPartyContractNo = thirdPartyContractNo;
	}

	public String getLeaseFinalisationInd() {
		return leaseFinalisationInd;
	}

	public void setLeaseFinalisationInd(String leaseFinalisationInd) {
		this.leaseFinalisationInd = leaseFinalisationInd;
	}

	public String getSettlementStatus() {
		return settlementStatus;
	}

	public void setSettlementStatus(String settlementStatus) {
		this.settlementStatus = settlementStatus;
	}

	public String getEtProgramEligibility() {
		return etProgramEligibility;
	}

	public void setEtProgramEligibility(String etProgramEligibility) {
		this.etProgramEligibility = etProgramEligibility;
	}

	public String getXsmProgramEligibility() {
		return xsmProgramEligibility;
	}

	public void setXsmProgramEligibility(String xsmProgramEligibility) {
		this.xsmProgramEligibility = xsmProgramEligibility;
	}

	public BigDecimal getFmvOverride() {
		return fmvOverride;
	}

	public void setFmvOverride(BigDecimal fmvOverride) {
		this.fmvOverride = fmvOverride;
	}

	public List<ContractLine> getContractLineList() {
		return contractLineList;
	}

	public void setContractLineList(List<ContractLine> contractLineList) {
		this.contractLineList = contractLineList;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (conId != null ? conId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Contract)) {
            return false;
        }
        Contract other = (Contract) object;
        if ((this.conId == null && other.conId != null) || (this.conId != null && !this.conId.equals(other.conId))) {
            return false;
        }
        return true;
    }


	public Long getConId() {
		return conId;
	}

	public void setConId(Long conId) {
		this.conId = conId;
	}
    
      @Override
    public String toString() {
        return "com.mikealbert.vision.entity.Contract[ conId=" + conId + " ]";
    }
   
}
