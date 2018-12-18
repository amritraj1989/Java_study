package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name="SUPPLIER_PROGRESS_HISTORY")
public class SupplierProgressHistory extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SPH_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SPH_SEQ")
    @SequenceGenerator(name = "SPH_SEQ", sequenceName = "SPH_SEQ", allocationSize = 1)
    @NotNull
	private Long sphId;

	@Temporal(TemporalType.DATE)
	@Column(name="ACTION_DATE")
	private Date actionDate;

	@Column(name="BUILD_STATUS_DAYS")
	private BigDecimal buildStatusDays;

	@Column(name="DOC_ID")
	private Long docId;

	@Temporal(TemporalType.DATE)
	@Column(name="ENTERED_DATE")
	private Date enteredDate;

	@Column(name="OP_CODE")
	private String opCode;

	@Column(name="PROGRESS_NOTE")
	private String progressNote;

	@Column(name="PROGRESS_TYPE")
	private String progressType;

	@Column(name="SPSC_PROGRESS_TYPE")
	private String spscProgressType;

	private String supplier;

	@Column(name="SUPPLIER_PROGRESS_CODE")
	private String supplierProgressCode;

    @ManyToOne(optional = false)
	@JoinColumn(name="PROGRESS_TYPE", insertable = false, updatable = false)
	private ProgressTypeCode progressTypeCode;
	
    @Transient
    private boolean	isUpdateMode;//created for UI

	
	public SupplierProgressHistory() {
	}

	public Long getSphId() {
		return this.sphId;
	}

	public void setSphId(Long sphId) {
		this.sphId = sphId;
	}

	public Date getActionDate() {
		return this.actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public BigDecimal getBuildStatusDays() {
		return this.buildStatusDays;
	}

	public void setBuildStatusDays(BigDecimal buildStatusDays) {
		this.buildStatusDays = buildStatusDays;
	}

	public Long getDocId() {
		return this.docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public Date getEnteredDate() {
		return this.enteredDate;
	}

	public void setEnteredDate(Date enteredDate) {
		this.enteredDate = enteredDate;
	}

	public String getOpCode() {
		return this.opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public String getProgressNote() {
		return this.progressNote;
	}

	public void setProgressNote(String progressNote) {
		this.progressNote = progressNote;
	}

	public String getProgressType() {
		return this.progressType;
	}

	public void setProgressType(String progressType) {
		this.progressType = progressType;
	}

	public String getSpscProgressType() {
		return this.spscProgressType;
	}

	public void setSpscProgressType(String spscProgressType) {
		this.spscProgressType = spscProgressType;
	}

	public String getSupplier() {
		return this.supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getSupplierProgressCode() {
		return this.supplierProgressCode;
	}

	public void setSupplierProgressCode(String supplierProgressCode) {
		this.supplierProgressCode = supplierProgressCode;
	}

	public ProgressTypeCode getProgressTypeCode() {
		return progressTypeCode;
	}

	public void setProgressTypeCode(ProgressTypeCode progressTypeCode) {
		this.progressTypeCode = progressTypeCode;
	}
	
	public boolean isUpdateMode() {
		return isUpdateMode;
	}

	public void setUpdateMode(boolean isUpdateMode) {
		this.isUpdateMode = isUpdateMode;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sphId == null) ? 0 : sphId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierProgressHistory other = (SupplierProgressHistory) obj;
		if (sphId == null) {
			if (other.sphId != null)
				return false;
		} else if (!sphId.equals(other.sphId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SupplierProgressHistory [sphId=" + sphId + "]";
	}
	

}