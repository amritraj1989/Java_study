package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
 * Mapped to CLIENT_METHODS table
 */

@Entity
@Table(name = "CLIENT_METHODS")
public class ClientMethod extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CMET_SEQ")    
    @SequenceGenerator(name="CMET_SEQ", sequenceName="CMET_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "CMET_ID")
    private Long clientMethodId;

    @Size(max = 80)
    @Column(name = "METHOD_NAME")
    private String name;
        
    @Size(max = 80)
    @Column(name = "METHOD_DESCRIPTION")
    private String description;
    
    @OneToMany(mappedBy = "clientMethod")
    private List<ClientContactMethod> clientContactMethod;       
        
    public Long getClientMethodId() {
		return clientMethodId;
	}

	public void setClientMethodId(Long clientMethodId) {
		this.clientMethodId = clientMethodId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ClientContactMethod> getClientContactMethod() {
		return clientContactMethod;
	}

	public void setClientContactMethod(List<ClientContactMethod> clientContactMethod) {
		this.clientContactMethod = clientContactMethod;
	}
   
}
