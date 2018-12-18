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
 * Mapped to CLIENT_RULES table
 */

@Entity
@Table(name = "CLIENT_RULES")
public class ClientRule extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CRUL_SEQ")    
    @SequenceGenerator(name="CRUL_SEQ", sequenceName="CRUL_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "CRUL_ID")
    private Long clientRuleId;

    @Size(max = 80)
    @Column(name = "RULE_NAME")
    private String ruleName;
        
    @Size(max = 80)
    @Column(name = "RULE_DESCRIPTION")
    private String description;

    @Column(name = "SORT_ORDER")
    private Long sortOrder;

    public Long getClientRuleId() {
		return clientRuleId;
	}

	public void setClientRuleId(Long clientRuleId) {
		this.clientRuleId = clientRuleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
   
    public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}

   
}
