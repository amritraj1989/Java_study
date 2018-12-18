package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Mapped to CLIENT_SERV_ELEMENT_PARAMS table
 */

@Entity
@Table(name = "CLIENT_SERV_ELEMENT_PARAMS")
public class ClientServiceElementParameter extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CSEP_SEQ")    
    @SequenceGenerator(name="CSEP_SEQ", sequenceName="CSEP_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
	@Column(name = "CSEP_ID")
	private Long clientServiceElementParameterId;
	
    @Column(name = "VALUE")
    private BigDecimal value;
	
	@ManyToOne
	@JoinColumn(name="CSE_CSE_ID", referencedColumnName = "CSE_ID")
	private ClientServiceElement clientServiceElement;
    
    @ManyToOne
	@JoinColumn(name="FPR_FPR_ID", referencedColumnName = "FPR_ID")
	private FormulaParameter formulaParameter;
    
    public ClientServiceElementParameter(){
    }

	public Long getClientServiceElementParameterId() {
		return clientServiceElementParameterId;
	}

	public void setClientServiceElementParameterId(
			Long clientServiceElementParameterId) {
		this.clientServiceElementParameterId = clientServiceElementParameterId;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public ClientServiceElement getClientServiceElement() {
		return clientServiceElement;
	}

	public void setClientServiceElement(ClientServiceElement clientServiceElement) {
		this.clientServiceElement = clientServiceElement;
	}

	public FormulaParameter getFormulaParameter() {
		return formulaParameter;
	}

	public void setFormulaParameter(FormulaParameter formulaParameter) {
		this.formulaParameter = formulaParameter;
	}
}
