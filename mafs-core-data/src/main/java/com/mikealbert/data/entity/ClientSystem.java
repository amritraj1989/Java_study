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
 * Mapped to CLIENT_SYSTEMS table
 */

@Entity
@Table(name = "CLIENT_SYSTEMS")
public class ClientSystem extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CSYS_SEQ")    
    @SequenceGenerator(name="CSYS_SEQ", sequenceName="CSYS_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "CSYS_ID")
    private Long clientSystemId;

    @Size(max = 80)
    @Column(name = "SYSTEM_NAME")
    private String name;
        
    @Size(max = 80)
    @Column(name = "SYSTEM_DESCRIPTION")
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clientSystem")    
    private List<ClientPoint> clientPoint;    
        
    public Long getClientSystemId() {
		return clientSystemId;
	}

	public void setClientSystemId(Long clientSystemId) {
		this.clientSystemId = clientSystemId;
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

	public List<ClientPoint> getClientPoint() {
		return clientPoint;
	}

	public void setClientPoint(List<ClientPoint> clientPoint) {
		this.clientPoint = clientPoint;
	}
   
}
