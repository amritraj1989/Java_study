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

import com.mikealbert.data.beanvalidation.MADriverAddresses;
import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Mapped to CLIENT_POINTS table
 */

@Entity
@Table(name = "CLIENT_POINTS")
public class ClientPoint extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CPNT_SEQ")    
    @SequenceGenerator(name="CPNT_SEQ", sequenceName="CPNT_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "CPNT_ID")
    private Long clientPointId;

    @Size(max = 80)
    @Column(name = "POINT_NAME")
    private String name;
        
    @Size(max = 80)
    @Column(name = "POINT_DESCRIPTION")
    private String description;
    
    @JoinColumn(name = "CSYS_CSYS_ID", referencedColumnName = "CSYS_ID")
    @ManyToOne
    private ClientSystem clientSystem;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clientPoint", orphanRemoval=true)    
    private List<ClientPointAccount> clientPointAccounts;
    
    @OneToMany(mappedBy = "clientPoint")
    private List<ClientPointRule> clientPointRules;      
        
    public Long getClientPointId() {
		return clientPointId;
	}

	public void setClientPointId(Long clientPointId) {
		this.clientPointId = clientPointId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ClientSystem getClientSystem() {
		return clientSystem;
	}

	public void setClientSystem(ClientSystem clientSystem) {
		this.clientSystem = clientSystem;
	}

	public List<ClientPointAccount> getClientPointAccounts() {
		return clientPointAccounts;
	}

	public void setClientPointAccounts(List<ClientPointAccount> clientPointAccounts) {
		this.clientPointAccounts = clientPointAccounts;
	}

	public List<ClientPointRule> getClientPointRules() {
		return clientPointRules;
	}

	public void setClientPointRules(List<ClientPointRule> clientPointRules) {
		this.clientPointRules = clientPointRules;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
   
}
