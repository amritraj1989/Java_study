package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Mapped to CLIENT_CONTACTS table
 */

@Entity
@Table(name = "CLIENT_CONTACTS")
public class ClientContact extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CCON_SEQ")    
    @SequenceGenerator(name="CCON_SEQ", sequenceName="CCON_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "CCON_ID")
    private Long clientContactId;
    
    @Size(max = 1)
    @Column(name = "DRV_IND")
    private String drvInd;     

    @Size(max = 1)
    @Column(name = "DEFAULT_IND")
    private String defaultInd;
                
    @Column(name = "EFFECTIVE_FROM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveFrom;

    @Column(name = "EFFECTIVE_TO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveTo;
    
    @JoinColumn(name = "CPNTA_CPNTA_ID", referencedColumnName = "CPNTA_ID")
    @ManyToOne
    private ClientPointAccount clientPointAccount;

    @JoinColumn(name = "CNT_CNT_ID", referencedColumnName = "CNT_ID")
    @ManyToOne(optional=true)
    private Contact contact;
            
    @JoinColumns({
        @JoinColumn(name = "COST_CENTRE_CODE", referencedColumnName = "COST_CENTRE_CODE"),
        @JoinColumn(name = "CC_C_ID", referencedColumnName = "EA_C_ID"),
        @JoinColumn(name = "CC_ACCOUNT_TYPE", referencedColumnName = "EA_ACCOUNT_TYPE"),
        @JoinColumn(name = "CC_ACCOUNT_CODE", referencedColumnName = "EA_ACCOUNT_CODE")})
    @ManyToOne(optional=true)
    private CostCentreCode costCentreCode;
    
    @OneToMany(mappedBy = "clientContact", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ClientContactMethod> clientContactMethod;     

    public Long getClientContactId() {
		return clientContactId;
	}

	public void setClientContactId(Long clientContactId) {
		this.clientContactId = clientContactId;
	}

	public String getDrvInd() {
		return drvInd;
	}

	public void setDrvInd(String drvInd) {
		this.drvInd = drvInd;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public CostCentreCode getCostCentreCode() {
		return costCentreCode;
	}

	public void setCostCentreCode(CostCentreCode costCentreCode) {
		this.costCentreCode = costCentreCode;
	}

	public List<ClientContactMethod> getClientContactMethod() {
		return clientContactMethod;
	}

	public void setClientContactMethod(List<ClientContactMethod> clientContactMethod) {
		this.clientContactMethod = clientContactMethod;
	}

	public String getDefaultInd() {
		return defaultInd;
	}

	public void setDefaultInd(String defaultInd) {
		this.defaultInd = defaultInd;
	}

	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public ClientPointAccount getClientPointAccount() {
		return clientPointAccount;
	}

	public void setClientPointAccount(ClientPointAccount clientPointAccount) {
		this.clientPointAccount = clientPointAccount;
	}

}
