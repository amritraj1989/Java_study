package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the DELIVERY_COSTS database table.
 */
@Entity
@Table(name = "DELIVERY_COSTS")
public class DeliveryCost implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DCS_SEQ")    
    @SequenceGenerator(name="DCS_SEQ", sequenceName="DCS_SEQ", allocationSize=1)    
    @Column(name = "DCS_ID")
    private Long dcsId;
    
    @Column(name = "COST"	)
    private BigDecimal cost;

    @Column(name = "ACTUAL_COST"	)
    private BigDecimal actualCost;
    
    @Column(name = "DELIVERY_DISCOUNT"	)
    private BigDecimal deliveryCost;
    
    @Column(name = "EFFECTIVE_FROM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveFrom;
    
    @Column(name = "EFFECTIVE_TO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveTo;    
        
    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Model model;     

    public DeliveryCost() {}

	public Long getDcsId() {
		return dcsId;
	}

	public void setDcsId(Long dcsId) {
		this.dcsId = dcsId;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getActualCost() {
		return actualCost;
	}

	public void setActualCost(BigDecimal actualCost) {
		this.actualCost = actualCost;
	}

	public BigDecimal getDeliveryCost() {
		return deliveryCost;
	}

	public void setDeliveryCost(BigDecimal deliveryCost) {
		this.deliveryCost = deliveryCost;
	}

	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

    
}