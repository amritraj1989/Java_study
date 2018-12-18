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
 * Mapped to CLIENT_POINT_RULES table
 */

@Entity
@Table(name = "CLIENT_POINT_RULES")
public class ClientPointRule extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CPRS_SEQ")    
    @SequenceGenerator(name="CPRS_SEQ", sequenceName="CPRS_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "CPRS_ID")
    private Long clientPointRuleId;

    @JoinColumn(name = "CPNT_CPNT_ID", referencedColumnName = "CPNT_ID")
    @ManyToOne
    private ClientPoint clientPoint;

    @JoinColumns({
        @JoinColumn(name = "CRUL_CRUL_ID", referencedColumnName = "CRUL_ID")})
    @ManyToOne
    private ClientRule clientRule;
 
    @Size(max = 80)
    @Column(name = "RULE_IND")
    private String ruleInd;

    public Long getClientPointRuleId() {
		return clientPointRuleId;
	}

	public void setClientPointRuleId(Long clientPointRuleId) {
		this.clientPointRuleId = clientPointRuleId;
	}

	public ClientPoint getClientPoint() {
		return clientPoint;
	}

	public void setClientPoint(ClientPoint clientPoint) {
		this.clientPoint = clientPoint;
	}

	public ClientRule getClientRule() {
		return clientRule;
	}

	public void setClientRule(ClientRule clientRule) {
		this.clientRule = clientRule;
	}

	public String getRuleInd() {
		return ruleInd;
	}

	public void setRuleInd(String ruleInd) {
		this.ruleInd = ruleInd;
	}

}
