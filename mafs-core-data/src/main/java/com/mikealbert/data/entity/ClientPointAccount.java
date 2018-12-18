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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Mapped to CLIENT_POINT_ACCOUNTS table
 */

@Entity
@Table(name = "CLIENT_POINT_ACCOUNTS")
public class ClientPointAccount extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CPNTA_SEQ")    
    @SequenceGenerator(name="CPNTA_SEQ", sequenceName="CPNTA_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "CPNTA_ID")
    private Long clientPointAccountId;  

    @Column(name = "LAST_CLIENT_CONTACT_UPDATE")
    private Date lastClientContactUpdate;
    
    @JoinColumn(name = "CPNT_CPNT_ID", referencedColumnName = "CPNT_ID")
    @ManyToOne
    private ClientPoint clientPoint;
        
    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne
    private ExternalAccount externalAccount; 
    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="clientPointAccount")
	private List<ClientContact> clientContacts;
    
    public Long getClientPointAccountId() {
		return clientPointAccountId;
	}

	public void setClientPointAccountId(Long clientPointAccountId) {
		this.clientPointAccountId = clientPointAccountId;
	}

	public Date getLastClientContactUpdate() {
		return lastClientContactUpdate;
	}

	public void setLastClientContactUpdate(Date lastClientContactUpdate) {
		this.lastClientContactUpdate = lastClientContactUpdate;
	}

	public ClientPoint getClientPoint() {
		return clientPoint;
	}

	public void setClientPoint(ClientPoint clientPoint) {
		this.clientPoint = clientPoint;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public List<ClientContact> getClientContacts() {
		return clientContacts;
	}

	public void setClientContacts(List<ClientContact> clientContacts) {
		this.clientContacts = clientContacts;
	}


    
}
