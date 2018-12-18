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
 * Mapped to CLIENT_FINANCE_TYPES table
 */

@Entity
@Table(name = "CLIENT_FINANCE_TYPES")
public class ClientFinanceType extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1530470629170383070L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CFT_SEQ")    
    @SequenceGenerator(name="CFT_SEQ", sequenceName="CFT_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
	@Column(name = "CFT_ID")
    private Long clientFinanceTypeId;

    @NotNull
    @Column(name = "FINANCE_TYPE_CODE")
    private String financeTypeCode;
    
    @NotNull
    @Column(name = "DESCRIPTION")
    private String description;

	public ClientFinanceType() {
	}
    
    public Long getClientFinanceTypeId() {
		return clientFinanceTypeId;
	}

	public void setClientFinanceTypeId(Long clientFinanceTypeId) {
		this.clientFinanceTypeId = clientFinanceTypeId;
	}

	public String getFinanceTypeCode() {
		return financeTypeCode;
	}

	public void setFinanceTypeCode(String financeTypeCode) {
		this.financeTypeCode = financeTypeCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
