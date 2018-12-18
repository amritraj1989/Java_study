package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "VEHICLE_MOVEMENTS")
public class VehicleMovement  extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -3647895460905514325L;

	@Id
	@Column(name="VMOV_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VMOV_SEQ")
    @SequenceGenerator(name = "VMOV_SEQ", sequenceName = "VMOV_SEQ", allocationSize = 1)
	private Long vmovId;

	@Column(name="VTC_CODE")
	private String vehTranTypeCode;
	
	@Column(name="VSC_CODE")
	private String vehStatusCode;
	
	@JoinColumn(name = "MOVED_FROM", referencedColumnName = "VMAL_ID")
    @OneToOne(fetch = FetchType.LAZY)
	private VehicleMovementAddrLink movedFrom;
	
	@JoinColumn(name = "MOVED_TO", referencedColumnName = "VMAL_ID")
    @OneToOne(fetch = FetchType.LAZY)
	private VehicleMovementAddrLink movedTo;
	
	@JoinColumn(name = "FMS_ID", referencedColumnName = "FMS_ID")
    @OneToOne(fetch = FetchType.LAZY)
	private FleetMaster fleetMaster;
	
	@Column(name="PLANNED_START_DATE")
	@Temporal( TemporalType.DATE)
	private Date plannedStartDate;
	
	@Column(name="PB_EMPLOYEE_NO")
	private String employeeNo;
	
	@Column(name="DATE_CREATED")
	@Temporal( TemporalType.DATE)
	private Date dateCreated;
	
	@Column(name="TPC_TRANS_PRIORITY")
	private String transPriority;
	
	@Column(name = "TRRC_TRANSPORT_REASON")
    private String transportReason;
	
	@JoinColumn(name = "PCC_PCC_ID", referencedColumnName = "PCC_ID")
    @OneToOne(fetch = FetchType.LAZY)
	private PreCollectionCheck preCollectionCheck;
	
	@Column(name = "NRC_NON_RUNNING_CODE")
    private String nonRunningCode;
	
	@Column(name = "GROUP_IND")
    private String groupInd;
	
	@Column(name="cust_req_date")
	@Temporal( TemporalType.DATE)
	private Date customerRequiredDate;
	
	@Column(name = "LOCAL_IND")
    private String localInd;
	
	public VehicleMovement() {
	}

	public Long getVmovId() {
		return vmovId;
	}

	public void setVmovId(Long vmovId) {
		this.vmovId = vmovId;
	}

	public String getVehTranTypeCode() {
		return vehTranTypeCode;
	}

	public void setVehTranTypeCode(String vehTranTypeCode) {
		this.vehTranTypeCode = vehTranTypeCode;
	}

	public String getVehStatusCode() {
		return vehStatusCode;
	}

	public void setVehStatusCode(String vehStatusCode) {
		this.vehStatusCode = vehStatusCode;
	}

	public VehicleMovementAddrLink getMovedFrom() {
		return movedFrom;
	}

	public void setMovedFrom(VehicleMovementAddrLink movedFrom) {
		this.movedFrom = movedFrom;
	}

	public VehicleMovementAddrLink getMovedTo() {
		return movedTo;
	}

	public void setMovedTo(VehicleMovementAddrLink movedTo) {
		this.movedTo = movedTo;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getTransPriority() {
		return transPriority;
	}

	public void setTransPriority(String transPriority) {
		this.transPriority = transPriority;
	}

	public String getTransportReason() {
		return transportReason;
	}

	public void setTransportReason(String transportReason) {
		this.transportReason = transportReason;
	}

	public PreCollectionCheck getPreCollectionCheck() {
		return preCollectionCheck;
	}

	public void setPreCollectionCheck(PreCollectionCheck preCollectionCheck) {
		this.preCollectionCheck = preCollectionCheck;
	}

	public String getNonRunningCode() {
		return nonRunningCode;
	}

	public void setNonRunningCode(String nonRunningCode) {
		this.nonRunningCode = nonRunningCode;
	}

	public String getGroupInd() {
		return groupInd;
	}

	public void setGroupInd(String groupInd) {
		this.groupInd = groupInd;
	}

	public Date getCustomerRequiredDate() {
		return customerRequiredDate;
	}

	public void setCustomerRequiredDate(Date customerRequiredDate) {
		this.customerRequiredDate = customerRequiredDate;
	}

	public String getLocalInd() {
		return localInd;
	}

	public void setLocalInd(String localInd) {
		this.localInd = localInd;
	}

	@Override
	public int hashCode() {
		int hash = 0;
        hash += (vmovId != null ? vmovId.hashCode() : 0);
        return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof VehicleMovement)) {
            return false;
        }
        VehicleMovement other = (VehicleMovement) object;
        if ((this.vmovId == null && other.vmovId != null) || (this.vmovId != null && !this.vmovId.equals(other.vmovId))) {
            return false;
        }
        return true;
	}

}