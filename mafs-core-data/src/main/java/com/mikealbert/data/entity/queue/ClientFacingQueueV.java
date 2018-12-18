package com.mikealbert.data.entity.queue;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "CLIENT_FACING_QUEUE_V")
public class ClientFacingQueueV implements Serializable {
	private static final long serialVersionUID = -5679055910751292814L;

	@Id
    @Column(name = "QMD_QMD_ID")
	private Long qmdId;
	
	@Column(name = "UNIT_NO")
	private String unitNo;
	
	@Column(name = "VARIANT")
	private String modelDesc;
	
	@Column(name = "CLIENT_ACCOUNT")
	private String accountCode;
	
	@Column(name = "CLIENT_ACCOUNT_NAME")
	private String accountName;
	
	@Column(name = "CLIENT_ACCOUNT_C_ID")
	private Long cId;
	
	@Column(name = "CLIENT_ACCOUNT_TYPE")
	private String accountType;
	
	@Column(name = "CLIENT_REQUEST_DATE")
	private String requestedDate;
	
	@Column(name = "EXPECTED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
	private Date expectedDate;
	
	@Column(name = "ETA")
    @Temporal(TemporalType.TIMESTAMP)
	private Date clientETADate;
	
	@Column(name = "FOLLOW_UP_DATE")
    @Temporal(TemporalType.TIMESTAMP)
	private Date followUpDate;
	
	@Column(name = "CSS_OR_TM")
	private String cssOrTm;
	
	@Column(name = "REASON")
	private String reason;
	
	@Column(name = "TOLERANCE_YN")
	private String toleranceYN;
	
    @Column(name = "TOLERANCE_MESSAGE")
	private String toleranceMessage;
	
    @Column(name = "PSO_ID")
	private Long psoId;
    
	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Long getCId() {
		return cId;
	}

	public void setCId(Long cId) {
		this.cId = cId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getRequestedDate() {
		return requestedDate;
	}

	public void setRequestedDate(String requestedDate) {
		this.requestedDate = requestedDate;
	}

	public Date getExpectedDate() {
		return expectedDate;
	}

	public void setExpectedDate(Date expectedDate) {
		this.expectedDate = expectedDate;
	}

	public Date getClientETADate() {
		return clientETADate;
	}

	public void setClientETADate(Date clientETADate) {
		this.clientETADate = clientETADate;
	}

	public Date getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}

	public String getCssOrTm() {
		return cssOrTm;
	}

	public void setCssOrTm(String cssOrTm) {
		this.cssOrTm = cssOrTm;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getToleranceYN() {
		return toleranceYN;
	}

	public void setToleranceYN(String toleranceYN) {
		this.toleranceYN = toleranceYN;
	}

	public String getToleranceMessage() {
		return toleranceMessage;
	}

	public void setToleranceMessage(String toleranceMessage) {
		this.toleranceMessage = toleranceMessage;
	}

	public Long getPsoId() {
		return psoId;
	}

	public void setPsoId(Long psoId) {
		this.psoId = psoId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unitNo == null) ? 0 : unitNo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ClientFacingQueueV)) {
            return false;
        }
        ClientFacingQueueV other = (ClientFacingQueueV) object;
        if ((this.unitNo == null && other.unitNo != null) || (this.unitNo != null && !this.unitNo.equals(other.unitNo))) {
            return false;
        }
        return true;
	}

}

