package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the SERVICES_DUE database table.
 * 
 */
@Entity
@Table(name="SERVICES_DUE")
public class ServicesDue extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SVD_SEQ")    
    @SequenceGenerator(name="SVD_SEQ", sequenceName="SVD_SEQ", allocationSize=1)
    @Basic(optional = false)	
	@Column(name="SVD_ID")
	private long svdId;

    @Temporal( TemporalType.DATE)
	@Column(name="ACTUAL_SERVICE_DATE")
	private Date actualServiceDate;

	@Column(name="ACTUAL_SERVICE_ODO")
	private BigDecimal actualServiceOdo;

	@Column(name="CERTIFICATE_RECEIVED_FLAG")
	private String certificateReceivedFlag;

	@Column(name="FMS_FMS_ID")
	private BigDecimal fmsFmsId;

	@Column(name="FOC_IND")
	private String focInd;

	@Column(name="JOB_DONE")
	private String jobDone;

	@ManyToOne(fetch=FetchType.LAZY, optional = true)
	@JoinColumn(name="MRQ_MRQ_ID", referencedColumnName = "MRQ_ID")
	private MaintenanceRequest maintenanceRequest;

	@Column(name="MRSTY_MRSTY_ID")
	private BigDecimal mrstyMrstyId;

	@Column(name="MSTYP_MSTYP_ID")
	private BigDecimal mstypMstypId;

    @Temporal( TemporalType.DATE)
	@Column(name="PLANNED_SERVICE_DATE")
	private Date plannedServiceDate;

	@Column(name="PLANNED_SERVICE_ODO")
	private BigDecimal plannedServiceOdo;

	@Column(name="SERVICE_TYPE_CODES")
	private String serviceTypeCodes;

	@Column(name="SYSTEM_FLAG")
	private String systemFlag;

	@Column(name="USER_NAME")
	private String userName;

    public ServicesDue() {
    }

	public long getSvdId() {
		return this.svdId;
	}

	public void setSvdId(long svdId) {
		this.svdId = svdId;
	}

	public Date getActualServiceDate() {
		return this.actualServiceDate;
	}

	public void setActualServiceDate(Date actualServiceDate) {
		this.actualServiceDate = actualServiceDate;
	}

	public BigDecimal getActualServiceOdo() {
		return this.actualServiceOdo;
	}

	public void setActualServiceOdo(BigDecimal actualServiceOdo) {
		this.actualServiceOdo = actualServiceOdo;
	}

	public String getCertificateReceivedFlag() {
		return this.certificateReceivedFlag;
	}

	public void setCertificateReceivedFlag(String certificateReceivedFlag) {
		this.certificateReceivedFlag = certificateReceivedFlag;
	}

	public BigDecimal getFmsFmsId() {
		return this.fmsFmsId;
	}

	public void setFmsFmsId(BigDecimal fmsFmsId) {
		this.fmsFmsId = fmsFmsId;
	}

	public String getFocInd() {
		return this.focInd;
	}

	public void setFocInd(String focInd) {
		this.focInd = focInd;
	}

	public String getJobDone() {
		return this.jobDone;
	}

	public void setJobDone(String jobDone) {
		this.jobDone = jobDone;
	}

	public MaintenanceRequest getMaintenanceRequest() {
		return maintenanceRequest;
	}

	public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
		this.maintenanceRequest = maintenanceRequest;
	}

	public BigDecimal getMrstyMrstyId() {
		return this.mrstyMrstyId;
	}

	public void setMrstyMrstyId(BigDecimal mrstyMrstyId) {
		this.mrstyMrstyId = mrstyMrstyId;
	}

	public BigDecimal getMstypMstypId() {
		return this.mstypMstypId;
	}

	public void setMstypMstypId(BigDecimal mstypMstypId) {
		this.mstypMstypId = mstypMstypId;
	}

	public Date getPlannedServiceDate() {
		return this.plannedServiceDate;
	}

	public void setPlannedServiceDate(Date plannedServiceDate) {
		this.plannedServiceDate = plannedServiceDate;
	}

	public BigDecimal getPlannedServiceOdo() {
		return this.plannedServiceOdo;
	}

	public void setPlannedServiceOdo(BigDecimal plannedServiceOdo) {
		this.plannedServiceOdo = plannedServiceOdo;
	}

	public String getServiceTypeCodes() {
		return this.serviceTypeCodes;
	}

	public void setServiceTypeCodes(String serviceTypeCodes) {
		this.serviceTypeCodes = serviceTypeCodes;
	}

	public String getSystemFlag() {
		return this.systemFlag;
	}

	public void setSystemFlag(String systemFlag) {
		this.systemFlag = systemFlag;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}