package com.mikealbert.vision.vo;

import com.mikealbert.data.entity.MaintSchedulesProcessed;
import com.mikealbert.data.vo.ClientContactVO;


public class MaintScheduleQueueVO {

	private MaintSchedulesProcessed maintScheduleProcessed;
	private String unitDescription;
	private String fuelTypeGroupDescription;
	private String accountCode;
	private String accountName;
	private long fmsId;
	private ClientContactVO clientContactVO;
	private String formattedSchedule;
	private String formattedName;
	
	private String status;
	private boolean overDueFlag;
	
	public MaintSchedulesProcessed getMaintScheduleProcessed() {
		return maintScheduleProcessed;
	}
	public void setMaintScheduleProcessed(MaintSchedulesProcessed maintScheduleProcessed) {
		this.maintScheduleProcessed = maintScheduleProcessed;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUnitDescription() {
		return unitDescription;
	}
	public void setUnitDescription(String unitDescription) {
		this.unitDescription = unitDescription;
	}
	public String getFuelTypeGroupDescription() {
		return fuelTypeGroupDescription;
	}
	public void setFuelTypeGroupDescription(String fuelTypeGroupDescription) {
		this.fuelTypeGroupDescription = fuelTypeGroupDescription;
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
	public long getFmsId() {
		return fmsId;
	}
	public void setFmsId(long fmsId) {
		this.fmsId = fmsId;
	}
	public ClientContactVO getClientContactVO() {
		return clientContactVO;
	}
	public void setClientContactVO(ClientContactVO clientContactVO) {
		this.clientContactVO = clientContactVO;
	}
	public String getFormattedSchedule() {
		return formattedSchedule;
	}
	public void setFormattedSchedule(String formattedSchedule) {
		this.formattedSchedule = formattedSchedule;
	}
	public String getFormattedName() {
		return formattedName;
	}
	public void setFormattedName(String formattedName) {
		this.formattedName = formattedName;
	}
	public boolean isOverDueFlag() {
		return overDueFlag;
	}
	public void setOverDueFlag(boolean overDueFlag) {
		this.overDueFlag = overDueFlag;
	}

	

}
