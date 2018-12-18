package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Mapped to EXCESS MILEAGE table
 * @author Lizak
 */
@Entity
@Table(name = "EXCESS_MILEAGE")
public class ExcessMileage  extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 795652671424687966L;

	@Id
    @Size(min = 1, max = 12)
    @Column(name = "EXCESS_MILE_ID", unique=true, nullable=false)
    private long excessMileId;

    @Size(max = 10)
    @Column(name = "EXCESS_MILE_NAME", nullable=false)
    private String excessMileName;

    @Size(max = 80)
    @Column(name = "EXCESS_MILE_DESCRIPTION", nullable=false)
    private String excessMileDesc;

    @Size(max = 1)
    @Column(name = "EXCESS_MILE_TYPE", nullable=false)
    private String excessMileType;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_FROM", nullable=false)
	private Date effectiveFrom;

    @Temporal( TemporalType.DATE)
	@Column(name="EFFECTIVE_TO", nullable=false)
	private Date effectiveTo;

    
    public ExcessMileage() {
    }


	public long getExcessMileId() {
		return excessMileId;
	}

	public void setExcessMileId(long excessMileId) {
		this.excessMileId = excessMileId;
	}

	public String getExcessMileName() {
		return excessMileName;
	}

	public void setExcessMileName(String excessMileName) {
		this.excessMileName = excessMileName;
	}

	public String getExcessMileDesc() {
		return excessMileDesc;
	}

	public void setExcessMileDesc(String excessMileDesc) {
		this.excessMileDesc = excessMileDesc;
	}

	public String getExcessMileType() {
		return excessMileType;
	}

	public void setExcessMileType(String excessMileType) {
		this.excessMileType = excessMileType;
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
	
    @Override
    public String toString() {
        return "com.mikealbert.entity.ExcessMileage[ excessMileId=" + excessMileId + " ]";
    }


}
