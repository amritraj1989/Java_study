package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

/**
 * The persistent class for the RECLAIM_HEADERS database table.
 * 
 * @author Roshan
 *
 */
@Entity
@Table(name="RECLAIM_HEADERS")
public class ReclaimHeaders extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RCH_SEQ")
    @SequenceGenerator(name = "RCH_SEQ", sequenceName = "RCH_SEQ", allocationSize = 1)
   	@Column(name="RCH_ID",  unique=true, nullable=false)
	private Long rchId;

	@Column(name="RUN_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date runDate;

	@Column(name="STATUS")
	private String status;

	@Column(name="OP_CODE")
	private String opCode;

	@Column(name="CEL_CEL_ID")
	private Long celId;

	@Column(name="FROM_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fromDate;

	@Column(name="TO_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date toDate;

	@Column(name="DOC_ID")
	private Long docId;
	
	@Column(name="FMS_FMS_ID")
	private Long fmsId;

	@Column(name="MAKE_DESC")
	private String makeDesc;

	public Long getRchId() {
		return rchId;
	}

	//bi-directional many-to-one association to ReclaimLines.
	@OneToMany(mappedBy="reclaimHeaders", cascade = CascadeType.ALL ,orphanRemoval = true)
	private List<ReclaimLines> reclaimLines;
		
	//bi-directional many-to-one association to Doc
    @ManyToOne
	@JoinColumn(name="DOC_ID", insertable = false, updatable = false)
	private Doc doc;

	//bi-directional many-to-one association to FleetMasters
    @ManyToOne
	@JoinColumn(name="FMS_FMS_ID", insertable = false, updatable = false)
	private FleetMaster fleetMaster;
    
	public void setRchId(Long rchId) {
		this.rchId = rchId;
	}

	public Date getRunDate() {
		return runDate;
	}

	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public Long getCelId() {
		return celId;
	}

	public void setCelId(Long celId) {
		this.celId = celId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public String getMakeDesc() {
		return makeDesc;
	}

	public void setMakeDesc(String makeDesc) {
		this.makeDesc = makeDesc;
	}

	public List<ReclaimLines> getReclaimLines() {
		return reclaimLines;
	}

	public void setReclaimLines(List<ReclaimLines> reclaimLines) {
		this.reclaimLines = reclaimLines;
	}
	
}
