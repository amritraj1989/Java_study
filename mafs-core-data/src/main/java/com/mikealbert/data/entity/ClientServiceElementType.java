package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Mapped to CLIENT_SERV_ELEMENT_TYPES table
 */

@Entity
@Table(name = "CLIENT_SERV_ELEMENT_TYPES")
public class ClientServiceElementType extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -4441959234088673128L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CSET_SEQ")    
    @SequenceGenerator(name="CSET_SEQ", sequenceName="CSET_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
	@Column(name = "CSET_ID")
    private Long clientServiceElementTypeId;

    @NotNull
    @Column(name = "SERVICE_ELEMENT_TYPE_CODE")
    private String serviceElementTypeCode;
    
    @NotNull
    @Column(name = "DESCRIPTION")
    private String description;

	public ClientServiceElementType() {
	}
    
    public Long getClientServiceElementTypeId() {
		return clientServiceElementTypeId;
	}

	public void setClientServiceElementTypeId(Long clientServiceElementTypeId) {
		this.clientServiceElementTypeId = clientServiceElementTypeId;
	}

	public String getServiceElementTypeCode() {
		return serviceElementTypeCode;
	}

	public void setServiceElementTypeCode(String serviceElementTypeCode) {
		this.serviceElementTypeCode = serviceElementTypeCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}

