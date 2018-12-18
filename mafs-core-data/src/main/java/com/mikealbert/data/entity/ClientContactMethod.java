package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Mapped to CLIENT_CONTACT_METHODS table
 */

@Entity
@Table(name = "CLIENT_CONTACT_METHODS")
public class ClientContactMethod extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CCCM_SEQ")    
    @SequenceGenerator(name="CCCM_SEQ", sequenceName="CCCM_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "CCCM_ID")
    private Long clientContactMethodId;

    @JoinColumn(name = "CCON_CCON_ID", referencedColumnName = "CCON_ID")
    @ManyToOne
    private ClientContact clientContact;

    @JoinColumn(name = "CMET_CMET_ID", referencedColumnName = "CMET_ID")
    @ManyToOne
    private ClientMethod clientMethod;
        
    public Long getClientContactMethodId() {
		return clientContactMethodId;
	}

	public void setClientContactMethodId(Long clientContactMethodId) {
		this.clientContactMethodId = clientContactMethodId;
	}

	public ClientContact getClientContact() {
		return clientContact;
	}

	public void setClientContact(ClientContact clientContact) {
		this.clientContact = clientContact;
	}

	public ClientMethod getClientMethod() {
		return clientMethod;
	}

	public void setClientMethod(ClientMethod clientMethod) {
		this.clientMethod = clientMethod;
	}

}
