package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the RECLAIM_LINES database table.
 * 
 * @author Roshan
 *
 */
@Entity
@Table(name="RECLAIM_LINES")
public class ReclaimLines extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RCL_SEQ")
    @SequenceGenerator(name = "RCL_SEQ", sequenceName = "RCL_SEQ", allocationSize = 1)
   
	@Column(name="RCL_ID",  unique=true, nullable=false)
	private Long rclId;

	/*@Column(name="RCH_RCH_ID",  nullable=false)
	private Long rchId;
*/
	@Column(name="RECLAIM_AMOUNT")
	private BigDecimal reclaimAmount;

	@Column(name="CLAIM_AGAINST")
	private String claimAgainst;

	@Column(name="INCLUDE")
	private String include;

	@Column(name="QCE_QCE_ID")
	private Long qceId;

	@Column(name="EXCLUDE")
	private String exclude;

	@Column(name="DOCL_DOC_ID")
	private Long doclDocId;

	@Column(name="DOCL_LINE_ID")
	private Long doclLineId;
	
	@Transient
	private  DoclPK referenceDoclPK;

	@ManyToOne
	@JoinColumn(name="RCH_RCH_ID" )
	private ReclaimHeaders reclaimHeaders;

	@ManyToOne
	@JoinColumn(name="FMS_FMS_ID")
	private FleetMaster fleetMaster;    
    
	public Long getRclId() {
		return rclId;
	}

	public void setRclId(Long rclId) {
		this.rclId = rclId;
	}
/*
	public Long getRchId() {
		return rchId;
	}

	public void setRchId(Long rchId) {
		this.rchId = rchId;
	}*/

	public BigDecimal getReclaimAmount() {
		return reclaimAmount;
	}

	public void setReclaimAmount(BigDecimal reclaimAmount) {
		this.reclaimAmount = reclaimAmount;
	}

	public String getClaimAgainst() {
		return claimAgainst;
	}

	public void setClaimAgainst(String claimAgainst) {
		this.claimAgainst = claimAgainst;
	}

	public String getInclude() {
		return include;
	}

	public void setInclude(String include) {
		this.include = include;
	}

	public Long getQceId() {
		return qceId;
	}

	public void setQceId(Long qceId) {
		this.qceId = qceId;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

	public Long getDoclDocId() {
		return doclDocId;
	}

	public void setDoclDocId(Long docId) {
		this.doclDocId = docId;
	}

	public Long getDocLineId() {
		return doclLineId;
	}

	public void setDocLineId(Long docLineId) {
		this.doclLineId = docLineId;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}
	public DoclPK getReferenceDoclPK() {
		return referenceDoclPK;
	}

	public void setReferenceDoclPK(DoclPK referenceDoclPK) {
		this.referenceDoclPK = referenceDoclPK;
	}
	

	public ReclaimHeaders getReclaimHeaders() {
		return reclaimHeaders;
	}

	public void setReclaimHeaders(ReclaimHeaders reclaimHeaders) {
		this.reclaimHeaders = reclaimHeaders;
	}

	@Override
	public String toString() {
		return "ReclaimLines [rclId=" + rclId +"]";
	}

	
}
