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

/**
 * Base class for a vehicle's order fee
 * @author sibley
 */

@Entity
@Table(name="VEHICLE_ORDERING_FEES")
public class VehicleOrderingFee implements Serializable {
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VOF_SEQ")    
    @SequenceGenerator(name="VOF_SEQ", sequenceName="VOF_SEQ", allocationSize=1)
    @Column(name = "VOF_ID")
    private Long vofId;
    
    @Column(name="ORDERING_FEE")
    private BigDecimal orderingFee;
    	
	@Column(name="EFFECTIVE_DATE")
	private Date effectiveDate;
	
    @JoinColumn(name = "MAK_MAK_ID", referencedColumnName = "MAK_ID")
    @ManyToOne(optional = false, fetch=FetchType.LAZY)
    private Make make;

	public Long getVofId() {
		return vofId;
	}

	public void setVofId(Long vofId) {
		this.vofId = vofId;
	}

	public BigDecimal getOrderingFee() {
		return orderingFee;
	}

	public void setOrderingFee(BigDecimal orderingFee) {
		this.orderingFee = orderingFee;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Make getMake() {
		return make;
	}

	public void setMake(Make make) {
		this.make = make;
	}		
    
    
}
