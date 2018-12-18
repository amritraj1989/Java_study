package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The persistent class for the OPTION_PACK_COSTS database table.
 * @author sibley
 */
@Entity
@Table(name="OPTION_PACK_COSTS")
public class OptionPackCost extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 6254874864214801901L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OPC_SEQ")    
    @SequenceGenerator(name="OPC_SEQ", sequenceName="OPC_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "OPC_ID")
    private Long opcId;
    
	@NotNull
    @Column(name = "PACK_COST")
    private BigDecimal cost;
	
	@NotNull
    @Column(name = "PACK_MSRP")
    private BigDecimal msrp;
	
    @Column(name = "HOLDBACK")
    private BigDecimal holdback;	

    @Column(name = "EFFECTIVE_FROM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveDate;
    
    @JoinColumn(name = "OPH_OPH_ID", referencedColumnName = "OPH_ID")
    @ManyToOne
    private OptionPackHeader optionPackHeader;  
    
    public OptionPackCost() {}

	public Long getOpcId() {
		return opcId;
	}

	public void setOpcId(Long opcId) {
		this.opcId = opcId;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getMsrp() {
		return msrp;
	}

	public void setMsrp(BigDecimal msrp) {
		this.msrp = msrp;
	}

	public BigDecimal getHoldback() {
		return holdback;
	}

	public void setHoldback(BigDecimal holdback) {
		this.holdback = holdback;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public OptionPackHeader getOptionPackHeader() {
		return optionPackHeader;
	}

	public void setOptionPackHeader(OptionPackHeader optionPackHeader) {
		this.optionPackHeader = optionPackHeader;
	}

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.OptionPackCost[ opcId=" + this.getOpcId() + " ]";
    }
	
}