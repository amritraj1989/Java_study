package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Mapped to hire_period_distances table
 * @author Amritraj
 */
@Entity
@Table(name = "hire_period_distances")
public class HirePeriodDistances implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "HPD_ID")
    private Long hpdId;

    @Column(name = "DISTANCE")
    private Long distance;
    
    @Column(name = "HPD_DIST_ID")
    private Long hpdDistId;

	public Long getHpdId() {
		return hpdId;
	}

	public void setHpdId(Long hpdId) {
		this.hpdId = hpdId;
	}

	public Long getDistance() {
		return distance;
	}

	public void setDistance(Long distance) {
		this.distance = distance;
	}

	public Long getHpdDistId() {
		return hpdDistId;
	}

	public void setHpdDistId(Long hpdDistId) {
		this.hpdDistId = hpdDistId;
	}
    

}
