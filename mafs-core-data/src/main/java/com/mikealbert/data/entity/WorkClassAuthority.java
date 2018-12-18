package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Mapped to WORK_CLASS_AUTHORITIES table
 */
@Entity
@Table(name = "WORK_CLASS_AUTHORITIES")
public class WorkClassAuthority implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WCA_SEQ")
    @SequenceGenerator(name = "WCA_SEQ", sequenceName = "WCA_SEQ", allocationSize = 1)
    @NotNull
    @Column(name = "WCA_ID")
    private Long id;

    @JoinColumns({ @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
	    @JoinColumn(name = "WORK_CLASS", referencedColumnName = "WORK_CLASS") })
    @ManyToOne
    private WorkClass workClass;

    @Column(name = "MODULE_NAME", nullable = false)
    private String moduleName;

    @Column(name = "LOWER_LIMIT", nullable = true)
    private BigDecimal lowerLimit;

    @Column(name = "UPPER_LIMIT", nullable = true)
    private BigDecimal upperLimit;

    @ManyToOne
    @JoinColumn(name = "PARAMETER_ID")
    private FinanceParameter financeParameter;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public WorkClass getWorkClass() {
	return workClass;
    }

    public void setWorkClass(WorkClass workClass) {
	this.workClass = workClass;
    }

    public String getModuleName() {
	return moduleName;
    }

    public void setModuleName(String moduleName) {
	this.moduleName = moduleName;
    }

    public BigDecimal getLowerLimit() {
	return lowerLimit;
    }

    public void setLowerLimit(BigDecimal lowerLimit) {
	this.lowerLimit = lowerLimit;
    }

    public BigDecimal getUpperLimit() {
	return upperLimit;
    }

    public void setUpperLimit(BigDecimal upperLimit) {
	this.upperLimit = upperLimit;
    }

    public FinanceParameter getFinanceParameter() {
	return this.financeParameter;
    }

    public void setFinanceParameter(FinanceParameter financeParameter) {
	this.financeParameter = financeParameter;
    }

}
